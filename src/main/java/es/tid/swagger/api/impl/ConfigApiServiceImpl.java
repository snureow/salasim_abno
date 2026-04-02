package es.tid.swagger.api.impl;

import com.sun.jersey.multipart.FormDataParam;

import es.tid.abno.modules.ABNOCOPController;
import es.tid.abno.modules.ABNOParameters;
import es.tid.abno.modules.workflows.Workflow;
import es.tid.abno.modules.workflows.WorkflowCOP;
import es.tid.swagger.api.*;
import es.tid.swagger.model.*;
import es.tid.util.UtilsFunctions;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-29T10:48:30.233+01:00")
public class ConfigApiServiceImpl extends ConfigApiService {
	

	private Logger log=LoggerFactory.getLogger("ConfigApiServiceImpl");

	private Integer resolveSla(TrafficParams trafficParams) {
		if (trafficParams == null || trafficParams.getSla() == null) {
			return Integer.valueOf(1);
		}
		Integer sla = trafficParams.getSla();
		if (sla.intValue() < 1 || sla.intValue() > 4) {
			throw new IllegalArgumentException("trafficParams.sla must be between 1 and 4");
		}
		return sla;
	}

	private Response badRequestResponse(String message) {
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(new ApiResponseMessage(ApiResponseMessage.ERROR, message))
				.build();
	}
  
      @Override
      public Response retrieveCalls()
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsById(List<Call> calls)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsById(List<Call> calls)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsById()
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallCallById(String callId)
      throws NotFoundException {
      // do some magicoo1!
    	  log.info("callId received: "+callId);
    	  
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "retrieveCallsCallCallById!")).build();
  }
  
      @Override
      public Response updateCallsCallCallById(String callId,Call call)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallCallById(String callId,Call call)
      throws NotFoundException {
      // do some magicoo1!
    	  
    	  
 
  
    	  log.info("recieved retrieveCallsCallCallById request.");
		  log.info("callId : "+callId);
		     	  
	
  		   
  		String workflowParam = "L0ProvisioningCOPWF";
		Integer slaLevel;
		try {
			slaLevel = resolveSla(call.getTrafficParams());
		} catch (IllegalArgumentException exc) {
			log.warn("Invalid SLA for call {}: {}", callId, exc.getMessage());
			return badRequestResponse(exc.getMessage());
		}
  		
  		
  		Hashtable<String, String> request = new Hashtable<String, String>();
  		request.put("ID_Operation", "1234");
  		request.put("Operation", "add");
  		//request.put("Exclude_Node", null);
  		request.put("OF", "1002");
  		//request.put("m", null);
  		//request.put("ERO", null);
  		request.put("remoteAddr", "COP Protocol");
  		request.put("Bandwidth", call.getTrafficParams().getReservedBandwidth().toString());
  		request.put("Source_Node", call.getAEnd().getRouterId());
  		request.put("Destination_Node", call.getZEnd().getRouterId());
  		request.put("Duration", call.getDuration().toString());
		request.put("SLA", slaLevel.toString());
  		
  		if (call.getAEnd().getInterfaceId()!= null){
  			request.put("source_interface", call.getAEnd().getInterfaceId());
  		}
  		
  		if (call.getZEnd().getInterfaceId()!= null){
  			request.put("destination_interface", call.getZEnd().getInterfaceId());
  		}
  		
  		
		String response = null;
  		
		
		Class<?> act;
		try {
			act = Class.forName("es.tid.abno.modules.workflows."+workflowParam);
		
		
			@SuppressWarnings("rawtypes")
			Class[] cArg = new Class[5];
			
			cArg[0] = Hashtable.class;
			cArg[1] = String.class;
			cArg[2] = LinkedList.class;
			cArg[3] = ABNOParameters.class;
			cArg[4] = HashMap.class;
			
			Object[] args = new Object[5];
			args[0] = request;
			args[1] = response;
			args[2] = ABNOCOPController.getPath_Computationlist();
			args[3] = ABNOCOPController.getParams();
			args[4] = ABNOCOPController.getOPtable();
			
			WorkflowCOP workflow = (WorkflowCOP)act.getDeclaredConstructor(cArg).newInstance(args);
			
			workflow.handleRequest();
			response = workflow.getResponse();
		}
		catch (Exception e1)
			{
				log.info(UtilsFunctions.exceptionToString(e1));			
//				response.sendError(HttpServletResponse.SC_NOT_FOUND);
//				return;
			}
    	  log.info("response: "+response);
    	  
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Created OK")).build();
  }
  
      @Override
      public Response deleteCallsCallCallById(String callId)
      throws NotFoundException {
          // todo FIXME: 这里的删除逻辑需要完善，至少要根据 callId 查出原来建路时的 Source 和 Dest，才能正确触发底层 Workflow 的删除操作。
          log.info("Received DELETE request for callId: " + callId);

          String workflowParam = "L0ProvisioningCOPWF";

          // 组装删除请求参数
          Hashtable<String, String> request = new Hashtable<String, String>();
          // 注意：ID_Operation 需要和创建时保持一致，或者从数据库/OPTable中根据 callId 查出来
          request.put("ID_Operation", "1234");

          // 【关键】将操作类型从 "add" 改为 "del"
          request.put("Operation", "del");
          request.put("OF", "1002");
          request.put("remoteAddr", "COP Protocol");

          // 注意：删除时，底层 Workflow 可能依然需要 Source 和 Dest 节点信息。
          // 在完善的系统中，你应该通过 callId 去 OPtable 里查出原来建路时的 Source 和 Dest。
          // 这里为了测试，先写死或假设 Workflow 支持仅靠 ID 删除。
          request.put("Source_Node", "172.18.1.4");
          request.put("Destination_Node", "172.18.2.5");

          String response = null;
          Class<?> act;
          try {
              act = Class.forName("es.tid.abno.modules.workflows." + workflowParam);

              @SuppressWarnings("rawtypes")
              Class[] cArg = new Class[5];
              cArg[0] = Hashtable.class;
              cArg[1] = String.class;
              cArg[2] = LinkedList.class;
              cArg[3] = ABNOParameters.class;
              cArg[4] = HashMap.class;

              Object[] args = new Object[5];
              args[0] = request;
              args[1] = response;
              args[2] = ABNOCOPController.getPath_Computationlist();
              args[3] = ABNOCOPController.getParams();
              args[4] = ABNOCOPController.getOPtable();

              WorkflowCOP workflow = (WorkflowCOP) act.getDeclaredConstructor(cArg).newInstance(args);

              // 触发底层发送 PCInitiate (Remove) 报文
              workflow.handleRequest();
              response = workflow.getResponse();
          } catch (Exception e1) {
              log.error(UtilsFunctions.exceptionToString(e1));
              return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "Delete Failed")).build();
          }

          log.info("Delete response: " + response);
          return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Deleted OK")).build();
      // do some magicoo1!
//      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallAEndAEndById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallAEndAEndById(String callId,Endpoint aEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallAEndAEndById(String callId,Endpoint aEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallAEndAEndById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsConnectionsById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsAEndAEndById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsAEndAEndById(String callId,String connectionId,Endpoint aEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsAEndAEndById(String callId,String connectionId,Endpoint aEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsAEndAEndById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsMatchMatchById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsMatchMatchById(String callId,String connectionId,MatchRules match)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsMatchMatchById(String callId,String connectionId,MatchRules match)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsMatchMatchById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsPathPathById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsPathPathById(String callId,String connectionId,PathType path)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsPathPathById(String callId,String connectionId,PathType path)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsPathPathById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsPathLabelLabelById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsPathLabelLabelById(String callId,String connectionId,Label label)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsPathLabelLabelById(String callId,String connectionId,Label label)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsPathLabelLabelById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsPathTopoComponentsTopoComponentsById(String callId,String connectionId,String endpointId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsPathTopoComponentsTopoComponentsById(String callId,String connectionId,String endpointId,Endpoint topoComponents)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsPathTopoComponentsTopoComponentsById(String callId,String connectionId,String endpointId,Endpoint topoComponents)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsPathTopoComponentsTopoComponentsById(String callId,String connectionId,String endpointId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsTrafficParamsTrafficParamsById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsTrafficParamsTrafficParamsById(String callId,String connectionId,TrafficParams trafficParams)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsTrafficParamsTrafficParamsById(String callId,String connectionId,TrafficParams trafficParams)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsTrafficParamsTrafficParamsById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsTransportLayerTransportLayerById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsTransportLayerTransportLayerById(String callId,String connectionId,TransportLayerType transportLayer)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsTransportLayerTransportLayerById(String callId,String connectionId,TransportLayerType transportLayer)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsTransportLayerTransportLayerById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallConnectionsZEndZEndById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallConnectionsZEndZEndById(String callId,String connectionId,Endpoint zEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallConnectionsZEndZEndById(String callId,String connectionId,Endpoint zEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallConnectionsZEndZEndById(String callId,String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallMatchMatchById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallMatchMatchById(String callId,MatchRules match)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallMatchMatchById(String callId,MatchRules match)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallMatchMatchById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallTrafficParamsTrafficParamsById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallTrafficParamsTrafficParamsById(String callId,TrafficParams trafficParams)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallTrafficParamsTrafficParamsById(String callId,TrafficParams trafficParams)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallTrafficParamsTrafficParamsById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallTransportLayerTransportLayerById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallTransportLayerTransportLayerById(String callId,TransportLayerType transportLayer)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallTransportLayerTransportLayerById(String callId,TransportLayerType transportLayer)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallTransportLayerTransportLayerById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveCallsCallZEndZEndById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateCallsCallZEndZEndById(String callId,Endpoint zEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createCallsCallZEndZEndById(String callId,Endpoint zEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteCallsCallZEndZEndById(String callId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnections()
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsById(List<Connection> connections)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsById(List<Connection> connections)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsById()
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionConnectionById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionConnectionById(String connectionId,Connection connection)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionConnectionById(String connectionId,Connection connection)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionConnectionById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionAEndAEndById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionAEndAEndById(String connectionId,Endpoint aEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionAEndAEndById(String connectionId,Endpoint aEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionAEndAEndById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionMatchMatchById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionMatchMatchById(String connectionId,MatchRules match)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionMatchMatchById(String connectionId,MatchRules match)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionMatchMatchById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionPathPathById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionPathPathById(String connectionId,PathType path)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionPathPathById(String connectionId,PathType path)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionPathPathById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionPathLabelLabelById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionPathLabelLabelById(String connectionId,Label label)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionPathLabelLabelById(String connectionId,Label label)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionPathLabelLabelById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionPathTopoComponentsTopoComponentsById(String connectionId,String endpointId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionPathTopoComponentsTopoComponentsById(String connectionId,String endpointId,Endpoint topoComponents)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionPathTopoComponentsTopoComponentsById(String connectionId,String endpointId,Endpoint topoComponents)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionPathTopoComponentsTopoComponentsById(String connectionId,String endpointId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionTrafficParamsTrafficParamsById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionTrafficParamsTrafficParamsById(String connectionId,TrafficParams trafficParams)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionTrafficParamsTrafficParamsById(String connectionId,TrafficParams trafficParams)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionTrafficParamsTrafficParamsById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionTransportLayerTransportLayerById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionTransportLayerTransportLayerById(String connectionId,TransportLayerType transportLayer)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionTransportLayerTransportLayerById(String connectionId,TransportLayerType transportLayer)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionTransportLayerTransportLayerById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response retrieveConnectionsConnectionZEndZEndById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response updateConnectionsConnectionZEndZEndById(String connectionId,Endpoint zEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response createConnectionsConnectionZEndZEndById(String connectionId,Endpoint zEnd)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
  }
  
      @Override
      public Response deleteConnectionsConnectionZEndZEndById(String connectionId)
      throws NotFoundException {
      // do some magicoo1!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magicoo1!")).build();
	  }
  
}
