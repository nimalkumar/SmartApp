package com.smart.util.to;

public class RouteInfoTO  {
	
	private String route;
	private String stopID;
	private String TripID;
	private String scheduledDepartureTime;
	private String scheduledArrivalTime;
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getStopID() {
		return stopID;
	}
	public void setStopID(String stopID) {
		this.stopID = stopID;
	}
	public String getTripID() {
		return TripID;
	}
	public void setTripID(String tripID) {
		TripID = tripID;
	}
	public String getScheduledDepartureTime() {
		return scheduledDepartureTime;
	}
	public void setScheduledDepartureTime(String scheduledDepartureTime) {
		this.scheduledDepartureTime = scheduledDepartureTime;
	}
	public String getScheduledArrivalTime() {
		return scheduledArrivalTime;
	}
	public void setScheduledArrivalTime(String scheduledArrivalTime) {
		this.scheduledArrivalTime = scheduledArrivalTime;
	}
	@Override
	public String toString() {
		return "RouteInfoTO [route=" + route + ", stopID=" + stopID + ", TripID=" + TripID + ", scheduledDepartureTime="
				+ scheduledDepartureTime + ", scheduledArrivalTime=" + scheduledArrivalTime + "]";
	}
	
	
	
}
