package com.smart.service.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.Operators;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import com.smart.util.to.LiveFeedEntity;
import com.smart.util.to.RouteInfoTO;
import com.smart.util.to.VehicleLiveInfoTO;


public class SmartServiceUtil {

	private static Map routeInfoMap;
	private static List holidayInfoList;
	//to find out the route-trip-stop key based on ticket #
	private static Map ticketMap;
	//to identify source, destination names
	private static Map stopNameMap;
	
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
	
	public static Map getStopNameMap() {
		if (stopNameMap == null)
		{
			loadReferenceMaps();
		}
		return stopNameMap;
	}

	private static void loadReferenceMaps()
	{
		if (routeInfoMap == null || holidayInfoList == null)
		{
			routeInfoMap = new HashMap(); 
			holidayInfoList = new ArrayList();
			ticketMap = new HashMap();
			stopNameMap = new HashMap();
			
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
			    
			    blob = container.getBlockBlobReference("RouteReference.json");
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
				    	
			            //RouteID-StopID
			            String stopKey = jsonObj.getString("RouteName") + "-" + jsonObj.getString("StopID");
			            String stopName = jsonObj.getString("StopName");
			            
			            
			            stopNameMap.put(stopKey, stopName);	
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
		System.out.println(new Date() + "start:");
		
		VehicleLiveInfoTO vehicleLiveInfoTO = new VehicleLiveInfoTO();
		boolean isVehicleMoving = true;
		Date timeLastKnownDt = null;
		String latitudeLastKnown = "-1";
		String longitudeLastKnown = "-1";
		
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
			    
			    System.out.println(new Date() + ":container created" );
				
			    
			    CloudBlobDirectory cloudDir = container.getDirectoryReference("VehiclePositionLiveFeed");
			    System.out.println(new Date() + ":directory created" );
			    
			    CloudBlockBlob latestBlob = null;
			    for (ListBlobItem blobItem : cloudDir.listBlobs())
			    {
			    	System.out.println(new Date() + ":rotating blob files" );
			    	if (blobItem != null)
			    	{
			    		
			    		CloudBlockBlob blob = (CloudBlockBlob) blobItem;
			    		
			    		if (latestBlob == null)
			    		{
			    			latestBlob = blob;
			    		}
			    		else if (latestBlob.getProperties().getLastModified().compareTo(blob.getProperties().getLastModified()) < 0)
			    		{
			    			latestBlob = blob;
			    		}
			    	}
			    	
			    }
			    System.out.println(new Date() + ":found latest file" );
			    System.out.println(latestBlob.getUri());
		    	System.out.println(latestBlob.getProperties().getLastModified());
		    	
			    
			    //Write the contents of the file to the console.
			    //CloudBlockBlob blob = container.getBlockBlobReference("VehiclePositionLiveFeed/0_d840691a587c4f2883056b552fbdcd53_1.json");
			    String contentFromBlob = latestBlob.downloadText();
			    System.out.println(new Date() + ":content downloaded" );
			    String[] lines = contentFromBlob.split(System.getProperty("line.separator"));
	            
			    int length=lines.length;
			    System.out.println(new Date() + ":reading file" );
			    for (int i=0; i < length; i++)
			    {
			    	JSONObject jsonObj = new JSONObject(lines[i]);
			    	if (jsonObj != null && routeName.equalsIgnoreCase(jsonObj.getString("routename")) 
			    			&& tripID.equalsIgnoreCase(jsonObj.getString("tripid")))
			    	{
			    		String key = jsonObj.getString("routename") + "-" + jsonObj.getString("tripid") + "-" + jsonObj.getString("geolocation");
			            
			    		String feedTimeStr = "" + jsonObj.get("hourvalue") + ":" + jsonObj.get("minutevalue") + ":" + jsonObj.get("secondvalue");
			    		
			    		if (feedTimeStr.indexOf("null") == -1)
			    		{
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
			    }
			    System.out.println(new Date() + ":done with method" );
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
	
	public static VehicleLiveInfoTO getVehicleLiveInfoFrmTable(String routeName, String tripID)
	{
		System.out.println(new Date() + "start:");
		
		VehicleLiveInfoTO vehicleLiveInfoTO = new VehicleLiveInfoTO();
		boolean isVehicleMoving = true;
		Date timeLastKnownDt = null;
		String latitudeLastKnown = "-1";
		String longitudeLastKnown = "-1";
		//03-12-2018 09:53:00	
        SimpleDateFormat fmt = new SimpleDateFormat("MM-DD-yyyy HH:mm:ss");
			try
			{
			    Map vehiclePositioningInfoMap = new HashMap();
				CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			    // Create the blob client.
			    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			    
			 // Create the table client.
			    CloudTableClient tableClient = storageAccount.createCloudTableClient();

			    System.out.println("creating table");
			    // Create a cloud table object for the table.
			    CloudTable cloudTable = tableClient.getTableReference("LiveTrafficDataOutputDumpTable");
			    
		    	
			    TableQuery<LiveFeedEntity> partitionScanQuery = TableQuery.from(LiveFeedEntity.class).where(
			            (TableQuery.generateFilterCondition("PartitionKey", QueryComparisons.EQUAL, routeName+"-"+tripID)));

			        // Iterate through the results
			        for (LiveFeedEntity entity : cloudTable.execute(partitionScanQuery)) 
			        {
			    
			    String key = entity.getroutename() + "-" + entity.gettripid() + "-" + entity.getgeolocation();
			            
			    		
				    		Date feedTime = fmt.parse(entity.getRowKey());
				    		
				    		if (timeLastKnownDt == null || timeLastKnownDt.before(feedTime))
				    		{
				    			timeLastKnownDt = feedTime;
				    			String geolocation = entity.getgeolocation();
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
			    	
			    
			    System.out.println(new Date() + ":done with method" );
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

			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		    /*// Create the blob client.
		    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		    // Get a reference to a container.
		    // The container name must be lower case
		    CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

		    CloudBlobDirectory cloudDir = container.getDirectoryReference("VehiclePositionLiveFeed");
		    
		    CloudBlockBlob latestBlob = null;
		    for (ListBlobItem blobItem : cloudDir.listBlobs())
		    {
		    	if (blobItem != null)
		    	{
		    		
		    		CloudBlockBlob blob = (CloudBlockBlob) blobItem;
		    		
		    		if (latestBlob == null)
		    		{
		    			latestBlob = blob;
		    		}
		    		else if (latestBlob.getProperties().getLastModified().compareTo(blob.getProperties().getLastModified()) < 0)
		    		{
		    			latestBlob = blob;
		    		}
		    	}
		    	
		    }
		    System.out.println(latestBlob.getUri());
	    	System.out.println(latestBlob.getProperties().getLastModified());
	    	System.out.println(new Date() + ":start downloaded" );
	    	//Write the contents of the file to the console.
		    //CloudBlockBlob blob = container.getBlockBlobReference("VehiclePositionLiveFeed/0_d840691a587c4f2883056b552fbdcd53_1.json");
		    String contentFromBlob = latestBlob.downloadText();
		    System.out.println(new Date() + ":content downloaded" );
		    System.out.println(new Date() + ":start stream " );
		    BlobInputStream inputStream =  latestBlob.openInputStream();
		    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		    BufferedReader bufReader = new BufferedReader(inputStreamReader);
		    String line;
		    while ((line = bufReader.readLine()) != null) {
		        //System.out.println(line);
		    }
		    
		    System.out.println(new Date() + ":stream done" );
		    String[] lines = contentFromBlob.split(System.getProperty("line.separator"));
            
		    int length=lines.length;
		    System.out.println(new Date() + ":reading file" );
		    for (int i=0; i < length; i++)
		    {
		    	JSONObject jsonObj = new JSONObject(lines[i]);
		    	if (jsonObj != null && "BeachTmb".equalsIgnoreCase(jsonObj.getString("routename")) 
		    			&& "7".equalsIgnoreCase(jsonObj.getString("tripid")))
		    	{
		    		System.out.println(new Date() + ":line reading" );
		    		String key = jsonObj.getString("routename") + "-" + jsonObj.getString("tripid") + "-" + jsonObj.getString("geolocation");
		            
		    		String feedTimeStr = "" + jsonObj.get("hourvalue") + ":" + jsonObj.get("minutevalue") + ":" + jsonObj.get("secondvalue");
		    		
		    		if (feedTimeStr.indexOf("null") == -1)
		    		{
		    			
		    		}
		    	}
		    }
		    System.out.println(new Date() + ":done with method" );*/
		   
			// Create the table client.
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();

		    System.out.println("creating table");
		    // Create a cloud table object for the table.
		    CloudTable cloudTable = tableClient.getTableReference("LiveTrafficDataOutputDumpTable");

		   /* System.out.println("creating filter");
		    // Create a filter condition where the partition key is "Smith".
		    String partitionFilter = TableQuery.generateFilterCondition(
		        "PartitionKey",
		        QueryComparisons.EQUAL,
		        "D70-8");
		    
		    System.out.println("filter:" + partitionFilter);

		    // Create a filter condition where the row key is less than the letter "E".
		    String rowFilter = TableQuery.generateFilterCondition(
		        ROW_KEY,
		        QueryComparisons.LESS_THAN,
		        "E");

		    // Combine the two conditions into a filter expression.
		    String combinedFilter = TableQuery.combineFilters(partitionFilter,
		        Operators.AND, rowFilter);

		    // Specify a range query, using "Smith" as the partition key,
		    // with the row key being up to the letter "E".
		    TableQuery<LiveFeedEntity> rangeQuery =
		        TableQuery.from(LiveFeedEntity.class)
		        .where(partitionFilter);
		    System.out.println("created range query:" + rangeQuery);
		    // Loop through the results, displaying information about the entity
		    for (LiveFeedEntity entity : cloudTable.execute(rangeQuery)) {
		        System.out.println(entity.getPartitionKey() +
		            " " + entity.getRowKey() +
		            "\t" + entity.getGeolocation() +
		            "\t" + entity.getDayvalue() +
		            "\t" + entity.getEtag() +
		            "\t" + entity.getHourvalue() +
		            "\t" + entity.getMinutevalue() +
		            "\t" + entity.getMonthvalue() +
		            "\t" + entity.getRoutename() +
		            "\t" + entity.getSecondvalue() +
		            "\t" + entity.getYearvalue() +
		            "\t" + entity.getTripid());
		    }*/
		    
		 
		 // Create the partition scan query
	        TableQuery<LiveFeedEntity> partitionScanQuery = TableQuery.from(LiveFeedEntity.class).where(
	            (TableQuery.generateFilterCondition("PartitionKey", QueryComparisons.EQUAL, "BeachTmb-7")));

	        Date lastKnownTime = null;
	        //03-12-2018 09:53:00	
	        SimpleDateFormat fmt = new SimpleDateFormat("MM-DD-yyyy HH:mm:ss");
	        // Iterate through the results
	        for (LiveFeedEntity entity : cloudTable.execute(partitionScanQuery)) {
	            System.out.println(String.format("\tCustomer: %s,%s\t%s\t%s\t%s", entity.getPartitionKey(), entity.getRowKey(), entity.getgeolocation(), entity.gettripid(), entity.getroutename()));
	            
	            Date feedTime = fmt.parse(entity.getRowKey());
	            if (lastKnownTime == null || lastKnownTime.before(feedTime))
	            {
	            	lastKnownTime = feedTime;
	            }
	        }
	        System.out.println(lastKnownTime);
		    System.out.println("done");

	}

	
}
