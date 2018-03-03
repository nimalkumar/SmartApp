package com.smart.to;

public class ResultTO {

	private String route;
	private String tripID;
	private String stopID;
	private String destination;
	private String source;
	private String trafficFactor;
	private String weatherFactor;
	private String vehicleFactor;
	private String otherFactor;
	private String additionalInfo;
	private String arrivalDelay;
	private String eta;
	private String generatedTs;
	private String scheduledArrivalTime;
	
	public String getVehicleFactor() {
		return vehicleFactor;
	}
	public void setVehicleFactor(String vehicleFactor) {
		this.vehicleFactor = vehicleFactor;
	}
	public String getScheduledArrivalTime() {
		return scheduledArrivalTime;
	}
	public void setScheduledArrivalTime(String scheduledArrivalTime) {
		this.scheduledArrivalTime = scheduledArrivalTime;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getArrivalDelay() {
		return arrivalDelay;
	}
	public void setArrivalDelay(String arrivalDelay) {
		this.arrivalDelay = arrivalDelay;
	}
	public String getEta() {
		return eta;
	}
	public void setEta(String eta) {
		this.eta = eta;
	}
	public String getTripID() {
		return tripID;
	}
	public void setTripID(String tripID) {
		this.tripID = tripID;
	}
	public String getStopID() {
		return stopID;
	}
	public void setStopID(String stopID) {
		this.stopID = stopID;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTrafficFactor() {
		return trafficFactor;
	}
	public void setTrafficFactor(String trafficFactor) {
		this.trafficFactor = trafficFactor;
	}
	public String getWeatherFactor() {
		return weatherFactor;
	}
	public void setWeatherFactor(String weatherFactor) {
		this.weatherFactor = weatherFactor;
	}
	public String getOtherFactor() {
		return otherFactor;
	}
	public void setOtherFactor(String otherFactor) {
		this.otherFactor = otherFactor;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getGeneratedTs() {
		return generatedTs;
	}
	public void setGeneratedTs(String generatedTs) {
		this.generatedTs = generatedTs;
	}
	@Override
	public String toString() {
		return "ResultTO [route=" + route + ", tripID=" + tripID + ", stopID=" + stopID + ", destination=" + destination
				+ ", source=" + source + ", trafficFactor=" + trafficFactor + ", weatherFactor=" + weatherFactor
				+ ", vehicleFactor=" + vehicleFactor + ", otherFactor=" + otherFactor + ", additionalInfo="
				+ additionalInfo + ", arrivalDelay=" + arrivalDelay + ", eta=" + eta + ", generatedTs=" + generatedTs
				+ ", scheduledArrivalTime=" + scheduledArrivalTime + "]";
	}
	
	
	
	
	
	
}
