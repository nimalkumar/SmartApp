package com.smart.service.util;

import java.util.HashMap;
import java.util.Map;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import com.smart.util.to.VehicleRouteEntity;


public class SmartServiceUtil {

	private static Map routeInfoMap;
	
	private static Map getRouteInfoMap()
	{
		if (routeInfoMap == null)
		{
			routeInfoMap = new HashMap(); 
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
			    CloudStorageAccount storageAccount =
			        CloudStorageAccount.parse(storageConnectionString);

			    // Create the table client.
			    CloudTableClient tableClient = storageAccount.createCloudTableClient();

			    // Create a cloud table object for the table.
			    CloudTable cloudTable = tableClient.getTableReference("VehicleRouteReference");

			    // Create a filter condition where the partition key is "Smith".
			    String partitionFilter = TableQuery.generateFilterCondition(
			        PARTITION_KEY,
			        QueryComparisons.EQUAL,
			        "RouteInfo_2018");

			    // Specify a partition query, using "Smith" as the partition key filter.
			    TableQuery<VehicleRouteEntity> partitionQuery =
			        TableQuery.from(VehicleRouteEntity.class)
			        .where(partitionFilter);

			    // Loop through the results, displaying information about the entity.
			    for (VehicleRouteEntity entity : cloudTable.execute(partitionQuery)) {
			        
			    	routeInfoMap.put(entity.getRouteTripKey(), entity);
			    	/*System.out.println(entity.getPartitionKey() +
			            " " + entity.getRowKey() +
			            "\t" + entity.getRouteTripKey() +
			            "\t" + entity.getScheduledArrivalTime());*/
			    }
			}
			catch (Exception e)
			{
			    // Output the stack trace.
			    e.printStackTrace();
			}
		}
		return routeInfoMap;
	}
	
	public static VehicleRouteEntity getRouteInfo(String routeKey)
	{
		VehicleRouteEntity entity = (VehicleRouteEntity) getRouteInfoMap().get(routeKey);
		//TODO: Test only for invalid inputs
		if (entity == null)
		{
			entity = new VehicleRouteEntity();
			entity.setDestinationID("T1");
			entity.setEtag("T1");
			entity.setPartitionKey("T1");
			entity.setRoute("T1");
			entity.setRouteTripKey("T1");
			entity.setRowKey("T1");
			entity.setScheduledArrivalTime("T1");
			entity.setScheduledDepartureTime("T1");
			entity.setSourceID("T1");
			
		}
		return entity;
		
	}
	
	public static void main (String arg[])
	{
		getRouteInfoMap();
	}
}
