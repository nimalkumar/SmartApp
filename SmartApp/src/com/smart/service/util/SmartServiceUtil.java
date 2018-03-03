package com.smart.service.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.smart.util.to.RouteInfoTO;


public class SmartServiceUtil {

	private static Map routeInfoMap;
	private static List holidayInfoList;
	
	
	
	public static Map getRouteInfoMap() {
		if (routeInfoMap == null)
		{
			loadReferenceMaps();
		}
		return routeInfoMap;
	}

	public static List getHolidayInfoList() {
		if (holidayInfoList == null)
		{
			loadReferenceMaps();
		}
		return holidayInfoList;
	}

	private static Map loadReferenceMaps()
	{
		if (routeInfoMap == null || holidayInfoList == null)
		{
			routeInfoMap = new HashMap(); 
			holidayInfoList = new ArrayList();
			String storageConnectionString =
				    "DefaultEndpointsProtocol=http;" +
				    "AccountName=mytrafficdatablobstorage;" +
				    "AccountKey=69Y96ni2hzPrc+8u2cYyxyJekHO2WRKHvV8KDsXmP3/2wnsR0WJiS69Rrh99gF6uC0ftVXJREyq78TwQ9U+yqg==";
			
			//load the map
			try
			{
			    // Define constants for filters.
			    final String PARTITION_KEY = "PartitionKey";
			    final String ROW_KEY = "RowKey";
			    
			    
			 // Retrieve storage account from connection-string.
			    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			    // Create the blob client.
			    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			    // Get a reference to a container.
			    // The container name must be lower case
			    CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

			    //HOLIDAY REFERENCE LIST
			    //Write the contents of the file to the console.
			    CloudBlockBlob blob = container.getBlockBlobReference("HolidayReferenceData.csv");
			    String contentFromBlob = blob.downloadText();
			    
			    List<String> tempLines = Arrays.asList(contentFromBlob.split("\n"));
		        
		        boolean isFirstLine = true;
		        for (String line : tempLines) {
		        	
		        	//skip first line header
		        	if (isFirstLine)
		        	{
		        		isFirstLine = false;
		        	}
		        	else
		        	{	
		        		line = line.replaceAll("\"", "");
		        		
			        	//Remove whitespace and split by comma
			            List<String> eachLine = Arrays.asList(line.split("\\s*,\\s*"));
			            String dateStr = eachLine.get(1) + "/" + eachLine.get(2) + "/" + eachLine.get(0);
			            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
			            holidayInfoList.add(fmt.format(fmt.parse(dateStr)));
			            
		        	}	
		        }
		        
		      //ROUTE REFERENCE LIST
			    //Write the contents of the file to the console.
			    blob = container.getBlockBlobReference("TimeCalculatorReferenceData.csv");
			    contentFromBlob = blob.downloadText();
			    
			    tempLines = Arrays.asList(contentFromBlob.split("\n"));
		        
		        isFirstLine = true;
		        for (String line : tempLines) {
		        	
		        	//skip first line header
		        	if (isFirstLine)
		        	{
		        		isFirstLine = false;
		        	}
		        	else
		        	{	
		        		line = line.replaceAll("\"", "");
		        		
			        	//Remove whitespace and split by comma
			            List<String> eachLine = Arrays.asList(line.split("\\s*,\\s*"));
			            //RouteID-TripID-StopID
			            String key = eachLine.get(0) + "-" + eachLine.get(2) + "-" + eachLine.get(1);
			            //System.out.println(key);
			            
			            RouteInfoTO to = new RouteInfoTO();
			            to.setRoute(eachLine.get(0));
			            to.setScheduledArrivalTime(eachLine.get(3));
			            to.setScheduledDepartureTime(eachLine.get(5).replace("\n", "").replace("\r", ""));
			            to.setStopID(eachLine.get(1));
			            to.setTripID(eachLine.get(2));
			            
			            routeInfoMap.put(key, to);
			            
		        	}	
		        }
			    
		        //System.out.println("HolidayInfoList:" + holidayInfoList);
		        //System.out.println("RouteInfoMap:" + routeInfoMap);
			    
			}
			catch (Exception e)
			{
			    // Output the stack trace.
			    e.printStackTrace();
			}
		}
		return routeInfoMap;
	}
		
	
	private void callTrafficService()
	{
		//Koyambed lat-long 13.069166,80.191388
		//Guindy lat-long 13.010236, 80.215651
		//Velachery lat-long 12.975971, 80.221209
				
		Client client = ClientBuilder.newClient();

		WebTarget resource = client.target("https://traffic.cit.api.here.com/traffic/6.1/flow.json?minjamfactor=7&corridor=13.0691%2C80.1913%3B13.0102%2C80.2156%3B12.9759%2C80.2212%3B1000&app_id=tf3yQfmbpouRPrZKlgHR&app_code=GEbZOtQGURCbtgAxlJywFA");

		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);

		Response response = request.get();
		if (response.getStatus() == 200) {
		    System.out.println("Success! " + response.getStatus());
		     System.out.println(response.getEntity());
		    
		} else {
		    System.out.println("ERROR! " + response.getStatus());
		    System.out.println(response.getEntity());
		}
		
		System.out.println(response.readEntity(String.class));
		
		/*Map jsonMap = response.readEntity(Map.class);
		System.out.println("jsonMap:" + jsonMap);
		
		BigDecimal speed = (BigDecimal) ((Map)jsonMap.get("wind")).get("speed");
		
		System.out.println("windspeed:" + speed);
	*/	
		//System.out.println("response:" + response);
	}
	
	public static void main (String arg[]) throws Exception
	{
		/*loadReferenceMaps();
		System.out.println("Self Test - Load Reference Data from Azure Blob");
		System.out.println("Route Info Map:" + routeInfoMap);
		System.out.println("Holiday Info List:" + holidayInfoList);*/

		String adjHolidayInd = "No";
		List holidayList = SmartServiceUtil.getHolidayInfoList();
		
		System.out.println(holidayList);
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		//Check if Next day holiday
		Date travelDate = sdf.parse("01/02/2018");
		c.setTime(travelDate);
		c.add(Calendar.DATE, 1); 
		System.out.println("next day:" + sdf.format(c.getTime()));
		adjHolidayInd = (holidayList.contains(sdf.format(c.getTime())))?"Yes":"No";
		
		//Check if Previous day holiday
		if  (adjHolidayInd.equals("No"))
		{
			c.setTime(travelDate);
			c.add(Calendar.DATE, -1); 
			System.out.println("prev day:" + sdf.format(c.getTime()));
			adjHolidayInd = (holidayList.contains(sdf.format(c.getTime())))?"Yes":"No";
		}
		System.out.println(adjHolidayInd);
		
		//constructPredictServiceRequest();
	}

	
}
