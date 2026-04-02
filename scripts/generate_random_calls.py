#!/usr/bin/env python3
"""Generate random COP call requests from active station nodes."""

from __future__ import annotations

import argparse
import json
import random
from pathlib import Path
from typing import Any


SCRIPT_DIR = Path(__file__).resolve().parent
DEFAULT_MAPPING = SCRIPT_DIR / "node_ip_mapping.json"
DEFAULT_OUTPUT = SCRIPT_DIR / "generated_calls.json"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Generate random call JSON from active station nodes."
    )
    parser.add_argument(
        "--mapping",
        default=str(DEFAULT_MAPPING),
        help=f"Path to node_ip_mapping.json. Default: {DEFAULT_MAPPING}",
    )
    parser.add_argument(
        "--output",
        default=str(DEFAULT_OUTPUT),
        help=f"Output JSON path. Default: {DEFAULT_OUTPUT}",
    )
    parser.add_argument(
        "--count",
        type=int,
        default=500,
        help="Number of random calls to generate. Default: 500",
    )
    parser.add_argument(
        "--bandwidth",
        type=int,
        default=4,
        help="reservedBandwidth value for every call. Default: 4",
    )
    parser.add_argument(
        "--duration-min",
        type=int,
        default=1,
        help="Minimum duration, inclusive. Default: 1",
    )
    parser.add_argument(
        "--duration-max",
        type=int,
        default=20,
        help="Maximum duration, inclusive. Default: 20",
    )
    parser.add_argument(
        "--sla-min",
        type=int,
        default=1,
        help="Minimum SLA level, inclusive. Default: 1",
    )
    parser.add_argument(
        "--sla-max",
        type=int,
        default=4,
        help="Maximum SLA level, inclusive. Default: 4",
    )
    parser.add_argument(
        "--call-id-prefix",
        default="test-connection",
        help="Prefix for generated call IDs. Default: test-connection",
    )
    parser.add_argument(
        "--seed",
        type=int,
        default=None,
        help="Optional random seed for reproducible output.",
    )
    return parser.parse_args()


def load_mapping(mapping_path: Path) -> list[dict[str, Any]]:
    with mapping_path.open("r", encoding="utf-8") as fh:
        data = json.load(fh)

    if not isinstance(data, list):
        raise ValueError("Mapping file must contain a JSON array.")
    return data


def load_active_stations(mapping: list[dict[str, Any]]) -> list[dict[str, Any]]:
    stations = []
    for item in mapping:
        if not isinstance(item, dict):
            continue
        if item.get("type") != "station":
            continue
        if item.get("is_active") is not True:
            continue
        ip = item.get("IP")
        if not ip:
            continue
        stations.append(item)

    if len(stations) < 2:
        raise ValueError("At least two active station nodes are required.")
    return stations


def build_call(
    index: int,
    a_end: dict[str, Any],
    z_end: dict[str, Any],
    bandwidth: int,
    duration_min: int,
    duration_max: int,
    sla_min: int,
    sla_max: int,
    call_id_prefix: str,
) -> dict[str, Any]:
    call_id = f"{call_id_prefix}-{index:03d}"
    return {
        "callId": call_id,
        "aEnd": {
            "routerId": a_end["IP"],
        },
        "zEnd": {
            "routerId": z_end["IP"],
        },
        "trafficParams": {
            "reservedBandwidth": bandwidth,
            "sla": random.randint(sla_min, sla_max),
        },
        "duration": random.randint(duration_min, duration_max),
    }


def main() -> int:
    args = parse_args()

    if args.count <= 0:
        raise SystemExit("--count must be > 0")
    if args.duration_min > args.duration_max:
        raise SystemExit("--duration-min cannot be greater than --duration-max")
    if args.sla_min > args.sla_max:
        raise SystemExit("--sla-min cannot be greater than --sla-max")
    if args.sla_min < 1 or args.sla_max > 4:
        raise SystemExit("--sla-min/--sla-max must stay within 1..4")

    if args.seed is not None:
        random.seed(args.seed)

    mapping_path = Path(args.mapping).expanduser().resolve()
    output_path = Path(args.output).expanduser().resolve()

    mapping = load_mapping(mapping_path)
    stations = load_active_stations(mapping)

    calls = []
    for index in range(1, args.count + 1):
        a_end, z_end = random.sample(stations, 2)
        calls.append(
            build_call(
                index=index,
                a_end=a_end,
                z_end=z_end,
                bandwidth=args.bandwidth,
                duration_min=args.duration_min,
                duration_max=args.duration_max,
                sla_min=args.sla_min,
                sla_max=args.sla_max,
                call_id_prefix=args.call_id_prefix,
            )
        )

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8") as fh:
        json.dump(calls, fh, indent=2, ensure_ascii=False)
        fh.write("\n")

    print(
        f"[DONE] generated={len(calls)} stations={len(stations)} "
        f"output={output_path}"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
