package com.smart.to;

import java.util.Date;

public class RequestTO {

	private String route;
	private Date travelDateTime;
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public Date getTravelDateTime() {
		return travelDateTime;
	}
	public void setTravelDateTime(Date travelDateTime) {
		this.travelDateTime = travelDateTime;
	}
	
}
