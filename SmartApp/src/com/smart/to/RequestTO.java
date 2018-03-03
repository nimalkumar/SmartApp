package com.smart.to;

import java.util.Date;

public class RequestTO {

	private String routeKey;
	private String travelDate;
	public String getRouteKey() {
		return routeKey;
	}
	public void setRouteKey(String routeKey) {
		this.routeKey = routeKey;
	}
	public String getTravelDate() {
		return travelDate;
	}
	public void setTravelDate(String travelDate) {
		this.travelDate = travelDate;
	}
	@Override
	public String toString() {
		return "RequestTO [routeKey=" + routeKey + ", travelDate=" + travelDate + "]";
	}
	
	
}
