package es.tid.swagger.api.impl;

final class RestCallContext {

    private final String callId;
    private final int internalOperationId;
    private final String sourceNode;
    private final String destinationNode;
    private final String sourceInterface;
    private final String destinationInterface;
    private final String reservedBandwidth;
    private final String ofCode;

    RestCallContext(String callId, int internalOperationId, String sourceNode, String destinationNode,
                    String sourceInterface, String destinationInterface, String reservedBandwidth, String ofCode) {
        this.callId = callId;
        this.internalOperationId = internalOperationId;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.sourceInterface = sourceInterface;
        this.destinationInterface = destinationInterface;
        this.reservedBandwidth = reservedBandwidth;
        this.ofCode = ofCode;
    }

    String getCallId() {
        return callId;
    }

    int getInternalOperationId() {
        return internalOperationId;
    }

    String getSourceNode() {
        return sourceNode;
    }

    String getDestinationNode() {
        return destinationNode;
    }

    String getSourceInterface() {
        return sourceInterface;
    }

    String getDestinationInterface() {
        return destinationInterface;
    }

    String getReservedBandwidth() {
        return reservedBandwidth;
    }

    String getOfCode() {
        return ofCode;
    }
}
