#!/usr/bin/env python3
"""Send COP call requests to the ABNO RESTCONF API.

Input can be either:
1. A single JSON object describing one call
2. A JSON array of call objects

Each call object should follow the repo's `Call` model, for example:
{
  "aEnd": {"routerId": "172.18.1.4"},
  "zEnd": {"routerId": "172.18.2.5"},
  "trafficParams": {"reservedBandwidth": 100, "sla": 1},
  "duration": 1
}
"""

from __future__ import annotations

import argparse
import json
import sys
import time
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Iterable
from urllib.parse import urlparse
from urllib import error, request
from xml.etree import ElementTree


DEFAULT_BASE_URL = "http://127.0.0.1:4445/restconf"
DEFAULT_ABNO_CONFIG_PATH = Path(__file__).resolve().parents[1] / "ABNOConfiguration.xml"
DEFAULT_BURST_WINDOW_SECONDS = 1.0


@dataclass
class SendResult:
    call_id: str
    status_code: int | None
    ok: bool
    response_body: str
    request_body: dict[str, Any]
    error: str | None = None


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Batch send COP call requests to the ABNO controller."
    )
    parser.add_argument(
        "-i",
        "--input",
        required=True,
        help="Path to a JSON file containing one call object or a list of call objects.",
    )
    parser.add_argument(
        "-u",
        "--base-url",
        default=None,
        help=(
            "Base RESTCONF URL. If omitted, try ABNOConfiguration.xml first, "
            f"then fall back to {DEFAULT_BASE_URL}."
        ),
    )
    parser.add_argument(
        "--abno-config",
        default=str(DEFAULT_ABNO_CONFIG_PATH),
        help=(
            "ABNO configuration XML used to infer the default port when "
            f"--base-url is omitted. Default: {DEFAULT_ABNO_CONFIG_PATH}"
        ),
    )
    parser.add_argument(
        "--call-id-prefix",
        default="call",
        help="Prefix used when auto-generating call IDs. Default: call",
    )
    parser.add_argument(
        "--start-index",
        type=int,
        default=1,
        help="Start index for generated call IDs. Default: 1",
    )
    parser.add_argument(
        "--interval-seconds",
        type=float,
        default=0.0,
        help=(
            "Legacy mode: sleep time between requests. "
            "When > 0, burst mode is disabled. Default: 0"
        ),
    )
    parser.add_argument(
        "--calls-per-burst",
        type=int,
        default=10,
        help="Number of calls to send within one second. Default: 10",
    )
    parser.add_argument(
        "--burst-gap-seconds",
        type=float,
        default=60.0,
        help="Sleep time between burst windows in seconds. Default: 60",
    )
    parser.add_argument(
        "--timeout",
        type=float,
        default=10.0,
        help="Per-request timeout in seconds. Default: 10",
    )
    parser.add_argument(
        "--output",
        default=None,
        help=(
            "Optional result JSON file. Default: "
            "<input-dir>/send_results_<timestamp>.json"
        ),
    )
    return parser.parse_args()


def load_calls(input_path: Path) -> list[dict[str, Any]]:
    with input_path.open("r", encoding="utf-8") as fh:
        payload = json.load(fh)

    if isinstance(payload, dict):
        return [payload]
    if isinstance(payload, list) and all(isinstance(item, dict) for item in payload):
        return payload
    raise ValueError("Input JSON must be an object or an array of objects.")


def ensure_required_fields(call: dict[str, Any], index: int) -> None:
    missing: list[str] = []
    traffic_params = call.get("trafficParams")

    if not isinstance(call.get("aEnd"), dict) or not call["aEnd"].get("routerId"):
        missing.append("aEnd.routerId")
    if not isinstance(call.get("zEnd"), dict) or not call["zEnd"].get("routerId"):
        missing.append("zEnd.routerId")
    if (
        not isinstance(traffic_params, dict)
        or traffic_params.get("reservedBandwidth") is None
    ):
        missing.append("trafficParams.reservedBandwidth")
    if call.get("duration") is None:
        missing.append("duration")

    if missing:
        joined = ", ".join(missing)
        raise ValueError(f"Call #{index} is missing required fields: {joined}")

    sla = traffic_params.get("sla") if isinstance(traffic_params, dict) else None
    if sla is not None and (not isinstance(sla, int) or sla < 1 or sla > 4):
        raise ValueError(f"Call #{index} has invalid trafficParams.sla: {sla}")


def build_call_id(prefix: str, index: int) -> str:
    return f"{prefix}-{index:04d}"


def build_endpoint(base_url: str, call_id: str) -> str:
    trimmed = base_url.rstrip("/")
    return f"{trimmed}/calls/call/{call_id}/"


def send_call(
    endpoint: str,
    call_id: str,
    call: dict[str, Any],
    timeout: float,
) -> SendResult:
    body_bytes = json.dumps(call).encode("utf-8")
    http_request = request.Request(
        endpoint,
        data=body_bytes,
        headers={
            "Content-Type": "application/json",
            "Accept": "application/json",
        },
        method="POST",
    )

    try:
        with request.urlopen(http_request, timeout=timeout) as response:
            body = response.read().decode("utf-8", errors="replace")
            return SendResult(
                call_id=call_id,
                status_code=response.status,
                ok=200 <= response.status < 300,
                response_body=body,
                request_body=call,
            )
    except error.HTTPError as exc:
        body = exc.read().decode("utf-8", errors="replace")
        return SendResult(
            call_id=call_id,
            status_code=exc.code,
            ok=False,
            response_body=body,
            request_body=call,
            error=str(exc),
        )
    except error.URLError as exc:
        return SendResult(
            call_id=call_id,
            status_code=None,
            ok=False,
            response_body="",
            request_body=call,
            error=str(exc.reason),
        )


def save_results(results: Iterable[SendResult], output_path: Path) -> None:
    serializable = []
    for item in results:
        serializable.append(
            {
                "callId": item.call_id,
                "ok": item.ok,
                "statusCode": item.status_code,
                "error": item.error,
                "requestBody": item.request_body,
                "responseBody": item.response_body,
            }
        )

    with output_path.open("w", encoding="utf-8") as fh:
        json.dump(serializable, fh, indent=2, ensure_ascii=False)
        fh.write("\n")


def default_output_path(input_path: Path) -> Path:
    timestamp = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ")
    return input_path.parent / f"send_results_{timestamp}.json"


def load_base_url_from_config(config_path: Path) -> str | None:
    if not config_path.exists():
        return None

    tree = ElementTree.parse(config_path)
    root = tree.getroot()
    port_text = root.findtext("ABNOPort")
    if port_text is None:
        return None

    port = int(port_text.strip())
    return f"http://127.0.0.1:{port}/restconf"


def resolve_base_url(base_url_arg: str | None, config_path: Path) -> tuple[str, str]:
    if base_url_arg:
        return base_url_arg, "cli"

    config_base_url = load_base_url_from_config(config_path)
    if config_base_url:
        return config_base_url, str(config_path)

    return DEFAULT_BASE_URL, "fallback"


def format_connection_refused_hint(endpoint: str, config_path: Path) -> str:
    parsed = urlparse(endpoint)
    host = parsed.hostname or "unknown-host"
    port = parsed.port or ("443" if parsed.scheme == "https" else "80")
    hint = f"connection refused: no service is listening on {host}:{port}"

    try:
        configured_base_url = load_base_url_from_config(config_path)
    except Exception:
        configured_base_url = None

    if configured_base_url:
        hint = (
            f"{hint}; repo config suggests {configured_base_url}. "
            "Use --base-url to override if needed."
        )

    return hint


def send_one_call(
    *,
    call: dict[str, Any],
    index: int,
    base_url: str,
    call_id_prefix: str,
    config_path: Path,
    timeout: float,
) -> SendResult:
    call_id = call.get("callId") or build_call_id(call_id_prefix, index)
    endpoint = build_endpoint(base_url, call_id)

    print(f"[SEND] {call_id} -> {endpoint}")
    result = send_call(endpoint, call_id, call, timeout)
    if result.status_code is None and result.error:
        error_text = result.error.lower()
        if "connection refused" in error_text:
            result.error = format_connection_refused_hint(endpoint, config_path)

    status = result.status_code if result.status_code is not None else "N/A"
    if result.ok:
        print(f"[ OK ] {call_id} status={status}")
    else:
        print(f"[FAIL] {call_id} status={status} error={result.error}", file=sys.stderr)

    return result


def send_with_interval_mode(
    calls: list[dict[str, Any]],
    *,
    start_index: int,
    base_url: str,
    call_id_prefix: str,
    config_path: Path,
    timeout: float,
    interval_seconds: float,
) -> list[SendResult]:
    results: list[SendResult] = []

    for offset, call in enumerate(calls):
        index = start_index + offset
        results.append(
            send_one_call(
                call=call,
                index=index,
                base_url=base_url,
                call_id_prefix=call_id_prefix,
                config_path=config_path,
                timeout=timeout,
            )
        )

        if interval_seconds > 0 and offset < len(calls) - 1:
            time.sleep(interval_seconds)

    return results


def send_with_burst_mode(
    calls: list[dict[str, Any]],
    *,
    start_index: int,
    base_url: str,
    call_id_prefix: str,
    config_path: Path,
    timeout: float,
    calls_per_burst: int,
    burst_gap_seconds: float,
) -> list[SendResult]:
    results: list[SendResult] = []
    total_calls = len(calls)
    burst_spacing_seconds = DEFAULT_BURST_WINDOW_SECONDS / calls_per_burst
    offset = 0

    while offset < total_calls:
        burst_calls = calls[offset : offset + calls_per_burst]
        burst_start_time = time.monotonic()

        for burst_position, call in enumerate(burst_calls):
            index = start_index + offset + burst_position

            if burst_position > 0:
                scheduled_send_time = (
                    burst_start_time + (burst_position * burst_spacing_seconds)
                )
                remaining = scheduled_send_time - time.monotonic()
                if remaining > 0:
                    time.sleep(remaining)

            results.append(
                send_one_call(
                    call=call,
                    index=index,
                    base_url=base_url,
                    call_id_prefix=call_id_prefix,
                    config_path=config_path,
                    timeout=timeout,
                )
            )

        offset += len(burst_calls)
        if offset >= total_calls:
            print("[STOP] all calls have been consumed; no more HTTP requests will be sent")
            break

        if burst_gap_seconds > 0:
            print(f"[WAIT] sleeping {burst_gap_seconds}s before next burst")
            time.sleep(burst_gap_seconds)

    return results


def main() -> int:
    args = parse_args()
    input_path = Path(args.input).expanduser().resolve()
    config_path = Path(args.abno_config).expanduser().resolve()
    output_path = (
        Path(args.output).expanduser().resolve()
        if args.output
        else default_output_path(input_path)
    )
    try:
        base_url, base_url_source = resolve_base_url(args.base_url, config_path)
    except Exception as exc:
        print(f"[ERROR] failed to resolve base URL: {exc}", file=sys.stderr)
        return 2

    if args.interval_seconds < 0:
        print("[ERROR] --interval-seconds must be >= 0", file=sys.stderr)
        return 2
    if args.calls_per_burst <= 0:
        print("[ERROR] --calls-per-burst must be > 0", file=sys.stderr)
        return 2
    if args.burst_gap_seconds < 0:
        print("[ERROR] --burst-gap-seconds must be >= 0", file=sys.stderr)
        return 2

    try:
        calls = load_calls(input_path)
        for idx, call in enumerate(calls, start=args.start_index):
            ensure_required_fields(call, idx)
    except Exception as exc:
        print(f"[ERROR] {exc}", file=sys.stderr)
        return 2

    print(f"[BASE] {base_url} source={base_url_source}")

    if args.interval_seconds > 0:
        print(f"[MODE] interval requests every {args.interval_seconds}s")
        results = send_with_interval_mode(
            calls,
            start_index=args.start_index,
            base_url=base_url,
            call_id_prefix=args.call_id_prefix,
            config_path=config_path,
            timeout=args.timeout,
            interval_seconds=args.interval_seconds,
        )
    else:
        print(
            "[MODE] burst "
            f"{args.calls_per_burst} calls/{DEFAULT_BURST_WINDOW_SECONDS:.0f}s, "
            f"gap={args.burst_gap_seconds}s"
        )
        results = send_with_burst_mode(
            calls,
            start_index=args.start_index,
            base_url=base_url,
            call_id_prefix=args.call_id_prefix,
            config_path=config_path,
            timeout=args.timeout,
            calls_per_burst=args.calls_per_burst,
            burst_gap_seconds=args.burst_gap_seconds,
        )

    save_results(results, output_path)

    success_count = sum(1 for item in results if item.ok)
    print(
        f"[DONE] total={len(results)} success={success_count} "
        f"failed={len(results) - success_count} results={output_path}"
    )

    return 0 if success_count == len(results) else 1


if __name__ == "__main__":
    raise SystemExit(main())
