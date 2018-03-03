package com.smart.util.to;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class VehicleRouteEntity extends TableServiceEntity {

	public VehicleRouteEntity(String partitionKey, String rowKey) {
        this.partitionKey = partitionKey;
        this.rowKey = rowKey;
    }
	
	public VehicleRouteEntity() {}
	
	private String destinationID;
	private String route;
	private String routeTripKey;
	private String scheduledArrivalTime;
	private String scheduledDepartureTime;
	private String sourceID;
	public String getDestinationID() {
		return destinationID;
	}

	public void setDestinationID(String destinationID) {
		this.destinationID = destinationID;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getRouteTripKey() {
		return routeTripKey;
	}

	public void setRouteTripKey(String routeTripKey) {
		this.routeTripKey = routeTripKey;
	}

	public String getScheduledArrivalTime() {
		return scheduledArrivalTime;
	}

	public void setScheduledArrivalTime(String scheduledArrivalTime) {
		this.scheduledArrivalTime = scheduledArrivalTime;
	}

	public String getScheduledDepartureTime() {
		return scheduledDepartureTime;
	}

	public void setScheduledDepartureTime(String scheduledDepartureTime) {
		this.scheduledDepartureTime = scheduledDepartureTime;
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	
	
}
