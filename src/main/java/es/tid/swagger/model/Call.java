package es.tid.swagger.model;

import java.util.*;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import es.tid.swagger.model.Connection;
import es.tid.swagger.model.Endpoint;
import es.tid.swagger.model.MatchRules;
import es.tid.swagger.model.TrafficParams;
import es.tid.swagger.model.TransportLayerType;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-29T10:48:30.233+01:00")
public class Call  {
  
  public enum OperStatusEnum {
     down,  up, 
  };
  private OperStatusEnum operStatus = null;
  private String callId = null;
  private Endpoint zEnd = null;
  private List<Connection> connections = new ArrayList<Connection>();
  private TrafficParams trafficParams = null;
  private Integer duration = null;
  private Boolean protectionPath = null;
  private Endpoint aEnd = null;
  private TransportLayerType transportLayer = null;
  private MatchRules match = null;

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("operStatus")
  public OperStatusEnum getOperStatus() {
    return operStatus;
  }
  public void setOperStatus(OperStatusEnum operStatus) {
    this.operStatus = operStatus;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("callId")
  public String getCallId() {
    return callId;
  }
  public void setCallId(String callId) {
    this.callId = callId;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("zEnd")
  public Endpoint getZEnd() {
    return zEnd;
  }
  public void setZEnd(Endpoint zEnd) {
    this.zEnd = zEnd;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("connections")
  public List<Connection> getConnections() {
    return connections;
  }
  public void setConnections(List<Connection> connections) {
    this.connections = connections;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("trafficParams")
  public TrafficParams getTrafficParams() {
    return trafficParams;
  }
  public void setTrafficParams(TrafficParams trafficParams) {
    this.trafficParams = trafficParams;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("duration")
  public Integer getDuration() {
    return duration;
  }
  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  
  /**
   * Whether this call should be treated as a protected/lossless handover service.
   **/
  @ApiModelProperty(value = "Whether this call should be treated as a protected/lossless handover service.")
  @JsonProperty("protectionPath")
  public Boolean getProtectionPath() {
    return protectionPath;
  }
  public void setProtectionPath(Boolean protectionPath) {
    this.protectionPath = protectionPath;
  }

  @JsonProperty("losslessHandover")
  public void setLosslessHandover(Boolean losslessHandover) {
    this.protectionPath = losslessHandover;
  }

  @JsonProperty("isProtectionPath")
  public void setIsProtectionPath(Boolean isProtectionPath) {
    this.protectionPath = isProtectionPath;
  }

  @JsonProperty("protection")
  public void setProtection(Boolean protection) {
    this.protectionPath = protection;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("aEnd")
  public Endpoint getAEnd() {
    return aEnd;
  }
  public void setAEnd(Endpoint aEnd) {
    this.aEnd = aEnd;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("transportLayer")
  public TransportLayerType getTransportLayer() {
    return transportLayer;
  }
  public void setTransportLayer(TransportLayerType transportLayer) {
    this.transportLayer = transportLayer;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("match")
  public MatchRules getMatch() {
    return match;
  }
  public void setMatch(MatchRules match) {
    this.match = match;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Call {\n");
    
    sb.append("  operStatus: ").append(operStatus).append("\n");
    sb.append("  callId: ").append(callId).append("\n");
    sb.append("  zEnd: ").append(zEnd).append("\n");
    sb.append("  connections: ").append(connections).append("\n");
    sb.append("  trafficParams: ").append(trafficParams).append("\n");
    sb.append("  duration: ").append(duration).append("\n");
    sb.append("  protectionPath: ").append(protectionPath).append("\n");
    sb.append("  aEnd: ").append(aEnd).append("\n");
    sb.append("  transportLayer: ").append(transportLayer).append("\n");
    sb.append("  match: ").append(match).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
