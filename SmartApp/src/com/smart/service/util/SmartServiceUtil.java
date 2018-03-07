package com.smart.service.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.smart.util.to.RouteInfoTO;
import com.smart.util.to.VehicleLiveInfoTO;


public class SmartServiceUtil {

	private static Map routeInfoMap;
	private static List holidayInfoList;
	private static Map ticketMap;
	
	private static final String storageConnectionString =
		    "DefaultEndpointsProtocol=http;" +
		    "AccountName=mytrafficdatablobstorage;" +
		    "AccountKey=69Y96ni2hzPrc+8u2cYyxyJekHO2WRKHvV8KDsXmP3/2wnsR0WJiS69Rrh99gF6uC0ftVXJREyq78TwQ9U+yqg==";
	
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
	
	public static Map getTicketMap() {
		if (ticketMap == null)
		{
			loadReferenceMaps();
		}
		return ticketMap;
	}

	private static void loadReferenceMaps()
	{
		if (routeInfoMap == null || holidayInfoList == null)
		{
			routeInfoMap = new HashMap(); 
			holidayInfoList = new ArrayList();
			ticketMap = new HashMap();
			
			//load the map
			try
			{
			    //Retrieve storage account from connection-string.
			    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			    // Create the blob client.
			    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			    // Get a reference to a container.
			    // The container name must be lower case
			    CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

			    //HOLIDAY REFERENCE LIST
			    //Write the contents of the file to the console.
			    CloudBlockBlob blob = container.getBlockBlobReference("HolidayReference.json");
			    String contentFromBlob = blob.downloadText();
			    
			    JSONArray jsonObjArray = new JSONArray(contentFromBlob);
			    
			    //System.out.println("jsonObjArray:" + jsonObjArray);
			    
			    int length = jsonObjArray.length();
			    SimpleDateFormat fmt = new SimpleDateFormat("MMMM/dd/yyyy");
	            SimpleDateFormat fmt2 = new SimpleDateFormat("MM/dd/yyyy");
	            
			    for (int i=0; i < length; i++)
			    {
			    	JSONObject jsonObj = jsonObjArray.getJSONObject(i);
			    	if (jsonObj != null)
			    	{
				    	String dateStr = jsonObj.get("MonthValue") + "/" + jsonObj.get("DateValue") + "/" + jsonObj.get("YearValue");
			            holidayInfoList.add(fmt2.format(fmt.parse(dateStr)));
			    	}
			    }
		        
		      //ROUTE REFERENCE LIST
			    //Write the contents of the file to the console.
			    blob = container.getBlockBlobReference("TimecalculatorReference.json");
			    contentFromBlob = blob.downloadText();
			    
			    jsonObjArray = new JSONArray(contentFromBlob);
			    
			    //System.out.println("jsonObjArray:" + jsonObjArray);
			    
			    length = jsonObjArray.length();
			    
			    for (int i=0; i < length; i++)
			    {
			    	JSONObject jsonObj = jsonObjArray.getJSONObject(i);
			    	//System.out.println("jsonObj:" + jsonObj);
			    	
			    	if (jsonObj != null)
			    	{
				    	//RouteID-TripID-StopID
			            String key = jsonObj.getString("RouteName") + "-" + jsonObj.getString("TripID") + "-" + jsonObj.getString("StopID");
			            
			            RouteInfoTO to = new RouteInfoTO();
			            to.setRoute(jsonObj.getString("RouteName"));
			            to.setScheduledArrivalTime(jsonObj.getString("ScheduledArrival"));
			            to.setScheduledDepartureTime(jsonObj.getString("ScheduledDeparture"));
			            to.setStopID(jsonObj.getString("StopID"));
			            to.setTripID(jsonObj.getString("TripID"));
			            
			            routeInfoMap.put(key, to);	
			    	}
			    }
			    
			    blob = container.getBlockBlobReference("TicketReference.json");
			    contentFromBlob = blob.downloadText();
			    
			    jsonObjArray = new JSONArray(contentFromBlob);
			    
			    //System.out.println("jsonObjArray:" + jsonObjArray);
			    
			    length = jsonObjArray.length();
			    
			    for (int i=0; i < length; i++)
			    {
			    	JSONObject jsonObj = jsonObjArray.getJSONObject(i);
			    	//System.out.println("jsonObj:" + jsonObj);
			    	
			    	if (jsonObj != null)
			    	{
				    	
			            String ticketNumber = jsonObj.getString("TicketNumber");
			            //RouteID-TripID-StopID
			            String routeKey = jsonObj.getString("RouteName") + "-" + jsonObj.getString("TripID") + "-" + jsonObj.getString("StopID");
			            
			            ticketMap.put(ticketNumber, routeKey);	
			    	}
			    }
			    
		        //System.out.println("HolidayInfoList:" + holidayInfoList);
		        //System.out.println("RouteInfoMap:" + routeInfoMap);
			  //System.out.println("ticketMap:" + ticketMap);
			    
			}
			catch (Exception e)
			{
			    // Output the stack trace.
			    e.printStackTrace();
			}
		}
	}
		
	public static VehicleLiveInfoTO getVehicleLiveInfo(String routeName, String tripID)
	{
		VehicleLiveInfoTO vehicleLiveInfoTO = new VehicleLiveInfoTO();
		boolean isVehicleMoving = true;
		Date timeLastKnownDt = null;
		String latitudeLastKnown = "NA";
		String longitudeLastKnown = "NA";
		
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
			try
			{
			    Map vehiclePositioningInfoMap = new HashMap();
				CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			    // Create the blob client.
			    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			    // Get a reference to a container.
			    // The container name must be lower case
			    CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

			    //HOLIDAY REFERENCE LIST
			    //Write the contents of the file to the console.
			    CloudBlockBlob blob = container.getBlockBlobReference("VehiclePositionLiveFeed/0_d840691a587c4f2883056b552fbdcd53_1.json");
			    String contentFromBlob = blob.downloadText();
			    
			    String[] lines = contentFromBlob.split(System.getProperty("line.separator"));
	            
			    int length=lines.length;
	            
			    for (int i=0; i < length; i++)
			    {
			    	JSONObject jsonObj = new JSONObject(lines[i]);
			    	if (jsonObj != null && routeName.equalsIgnoreCase(jsonObj.getString("routename")) 
			    			&& tripID.equalsIgnoreCase(jsonObj.getString("tripid")))
			    	{
			    		System.out.println(jsonObj);
			    		String key = jsonObj.getString("routename") + "-" + jsonObj.getString("tripid") + "-" + jsonObj.getString("geolocation");
			            
			    		String feedTimeStr = "" + jsonObj.get("hourvalue") + ":" + jsonObj.get("minutevalue") + ":" + jsonObj.get("secondvalue");
			    		
			    		Date feedTime = fmt.parse(feedTimeStr);
			    		
			    		if (timeLastKnownDt == null)
			    		{
			    			timeLastKnownDt = feedTime;
			    			String geolocation = jsonObj.getString("geolocation");
			    			String[] geolocationArr = geolocation.split(",");
			    			latitudeLastKnown = geolocationArr[0];
			    			longitudeLastKnown = geolocationArr[1];
			    		}
			    		else if (feedTime.compareTo(timeLastKnownDt) > 0)
			    		{
			    			timeLastKnownDt = feedTime;
			    			String geolocation = jsonObj.getString("geolocation");
			    			String[] geolocationArr = geolocation.split(",");
			    			latitudeLastKnown = geolocationArr[0];
			    			longitudeLastKnown = geolocationArr[1];
			    		}
			    		
			    		if (vehiclePositioningInfoMap.get(key) != null)
			    		{
			    			if ((Integer)vehiclePositioningInfoMap.get(key) > 6)
			    			{
			    				isVehicleMoving = false;
			    			}
			    			vehiclePositioningInfoMap.put(key, (Integer)vehiclePositioningInfoMap.get(key)+1);
			    		}
			    		else
			    		{
			    			vehiclePositioningInfoMap.put(key, 1);
			    		}
			    	}
			    }
			    System.out.println(vehiclePositioningInfoMap);
			    
			    
			}
			catch (Exception e)
			{
			    // Output the stack trace.
			    e.printStackTrace();
			}
			vehicleLiveInfoTO.setVehicleMoving(isVehicleMoving);
			if (timeLastKnownDt != null)
			{
				SimpleDateFormat fmt2 = new SimpleDateFormat("HH:mm:ss");
				vehicleLiveInfoTO.setTimeLastKnown(fmt2.format(timeLastKnownDt));
			}
			vehicleLiveInfoTO.setLatitudeLastKnown(latitudeLastKnown);
			vehicleLiveInfoTO.setLongitudeLastKnown(longitudeLastKnown);
			
			return vehicleLiveInfoTO;
	}
	
		public static void main (String arg[]) throws Exception
	{
		/*loadReferenceMaps();
		System.out.println("Self Test - Load Reference Data from Azure Blob");
		System.out.println("Route Info Map:" + routeInfoMap);
		System.out.println("Holiday Info List:" + holidayInfoList);*/

			Map vehiclePositioningInfoMap = new HashMap();
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		    // Create the blob client.
		    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		    // Get a reference to a container.
		    // The container name must be lower case
		    CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

		    //HOLIDAY REFERENCE LIST
		    //Write the contents of the file to the console.
		    CloudBlockBlob blob = container.getBlockBlobReference("VehiclePositionLiveFeed/0_d840691a587c4f2883056b552fbdcd53_1.json");
		    String contentFromBlob = blob.downloadText();
		    
		    String[] lines = contentFromBlob.split(System.getProperty("line.separator"));
            
		    int length=lines.length;
		    for (int i=0; i < length; i++)
		    {
		    	JSONObject jsonObj = new JSONObject(lines[i]);
		    	if (jsonObj != null)
		    	{
		    		System.out.println(jsonObj);
		    		String key = jsonObj.getString("routename") + "-" + jsonObj.getString("tripid") + "-" + jsonObj.getString("geolocation");
			    	
		    		if (vehiclePositioningInfoMap.get(key) != null)
		    		{
		    			vehiclePositioningInfoMap.put(key, (Integer)vehiclePositioningInfoMap.get(key)+1);
		    		}
		    		else
		    		{
		    			vehiclePositioningInfoMap.put(key, 1);
		    		}
		    	}
		    }
		    System.out.println(vehiclePositioningInfoMap);
	}

	
}
