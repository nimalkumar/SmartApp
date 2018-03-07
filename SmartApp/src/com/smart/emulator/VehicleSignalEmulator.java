package com.smart.emulator;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value="/")

@Path("/emulator") 
public class VehicleSignalEmulator {

	@POST 
	   @Path("/postEvents") 
		@Consumes(MediaType.APPLICATION_JSON) 
	   @Produces(MediaType.APPLICATION_JSON) 
		@ApiOperation(value="Post events from emulator", response=String.class)
		@ApiResponses({ @ApiResponse(code = 200, response = String.class, message = "Success Count from emulator") })
	public static String postVehicleStatus(String inputString) throws Exception
	{
		int successMsgCount = 0;
		
		final String namespaceName = "livetrafficdataeventhub";
	    final String eventHubName = "trafficdataeventhub";
	    final String sasKeyName = "RootManageSharedAccessKey";
	    final String sasKey = "PAmj+dy6dGuHXAajDltMD0wwtf8j+OYH8S92X2j9Ua4=";
	    //Endpoint=sb://livetrafficdataeventhub.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=PAmj+dy6dGuHXAajDltMD0wwtf8j+OYH8S92X2j9Ua4=
	    ConnectionStringBuilder connStr = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey);
	    EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(connStr.toString());
	    System.out.println("Posting URL:"+ connStr.toString());
	    
	    
	    JSONArray jsonArray = new JSONArray(inputString);
	    
		if (jsonArray != null)
		{
			int length = jsonArray.length();
			
			for (int i=0; i < length; i++)
			{
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				if (jsonObj != null)
				{
					System.out.println("Preparing the event:" + jsonObj);
				    EventData sendEvent = new EventData(jsonObj.toString().getBytes());

				    
				    ehClient.sendSync(sendEvent);
				    
				    successMsgCount++;

				    System.out.println("Event posted");
				    
				    System.out.println("waiting for 5 secs..");
				    Thread.sleep(5000);
				    
				}
			}
		}
		
		// close the client at the end of your program
	    ehClient.closeSync();
		
	    System.out.println("All events Posted");
	    
	    return new String("{\"successMsgCount\":\"" + successMsgCount + "\"}");
	}
	
	public static void main(String[] args) throws EventHubException, ExecutionException, InterruptedException, IOException
	{
		/*final String namespaceName = "livetrafficdataeventhub";
	    final String eventHubName = "trafficdataeventhub";
	    final String sasKeyName = "RootManageSharedAccessKey";
	    final String sasKey = "PAmj+dy6dGuHXAajDltMD0wwtf8j+OYH8S92X2j9Ua4=";
	    //Endpoint=sb://livetrafficdataeventhub.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=PAmj+dy6dGuHXAajDltMD0wwtf8j+OYH8S92X2j9Ua4=
	    ConnectionStringBuilder connStr = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey);
	    
	    ObjectMapper mapper = new ObjectMapper();
	    
        //ArrayNode arrayNode = mapper.createArrayNode();
	    
	    ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put("route", "R1");
        objectNode1.put("geoloc", "1.2,2.8");
        
	    byte[] jsonBytes = mapper.writeValueAsBytes(objectNode1);
	    
	    System.out.println("json object:" + new String(jsonBytes));
	    EventData sendEvent = new EventData(jsonBytes);

	    System.out.println("connStr:"+ connStr.toString());
	    
	    EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(connStr.toString());
	    ehClient.sendSync(sendEvent);

	    System.out.println("message posted");
	    
	    // close the client at the end of your program
	    ehClient.closeSync();*/
		
		String inputString = "[\r\n" + 
				" {\r\n" + 
				"   \"RouteName\": \"D70\",\r\n" + 
				"   \"TripID\": \"8\",\r\n" + 
				"   \"Geolocation\": \"13.11,80.14\",\r\n" + 
				"   \"Month\":\"03\",\r\n" + 
				"   \"Date\":\"06\",\r\n" + 
				"   \"Year\":\"2018\",\r\n" + 
				"   \"Hour\":\"08\",\r\n" + 
				"   \"Minute\":\"26\",\r\n" + 
				"   \"Second\":\"55\",\r\n" + 
				" },\r\n" + 
				" {\r\n" + 
				"   \"RouteName\": \"D70\",\r\n" + 
				"   \"TripID\": \"8\",\r\n" + 
				"   \"Geolocation\": \"13.11,80.14\",\r\n" + 
				"   \"Month\":\"03\",\r\n" + 
				"   \"Date\":\"06\",\r\n" + 
				"   \"Year\":\"2018\",\r\n" + 
				"   \"Hour\":\"08\",\r\n" + 
				"   \"Minute\":\"26\",\r\n" + 
				"   \"Second\":\"55\",\r\n" + 
				" }]";
		
		try {
			postVehicleStatus(inputString );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	   }
	

}
