package com.smart.util.to;

public class VehicleInfoTO  {
	
	private String route;
	private String stopID;
	private String TripID;
	private String scheduledDepartureTime;
	private String scheduledArrivalTime;
	private String month;
	private String date;
	private String year;
	private String dayOfweek;
	private String adjHolidayInd;
	private String arrivalDelay;
	private String additionalInfo;
	
	
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getDayOfweek() {
		return dayOfweek;
	}
	public void setDayOfweek(String dayOfweek) {
		this.dayOfweek = dayOfweek;
	}
	public String getAdjHolidayInd() {
		return adjHolidayInd;
	}
	public void setAdjHolidayInd(String adjHolidayInd) {
		this.adjHolidayInd = adjHolidayInd;
	}
	public String getArrivalDelay() {
		return arrivalDelay;
	}
	public void setArrivalDelay(String arrivalDelay) {
		this.arrivalDelay = arrivalDelay;
	}
	public String getStopID() {
		return stopID;
	}
	public void setStopID(String stopID) {
		this.stopID = stopID;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getTripID() {
		return TripID;
	}
	public void setTripID(String tripID) {
		TripID = tripID;
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
	@Override
	public String toString() {
		return "VehicleInfoTO [route=" + route + ", stopID=" + stopID + ", TripID=" + TripID
				+ ", scheduledDepartureTime=" + scheduledDepartureTime + ", scheduledArrivalTime="
				+ scheduledArrivalTime + ", month=" + month + ", date=" + date + ", year=" + year + ", dayOfweek="
				+ dayOfweek + ", adjHolidayInd=" + adjHolidayInd + ", arrivalDelay=" + arrivalDelay
				+ ", additionalInfo=" + additionalInfo + "]";
	}
	public String printFactors() {
		return "VehicleInfoTO [Route=" + route + ", stopID=" + stopID + ", TripID=" + TripID
				+ ", scheduledDepartureTime=" + scheduledDepartureTime + ", scheduledArrivalTime="
				+ scheduledArrivalTime + ", month=" + month + ", date=" + date + ", year=" + year + ", dayOfweek="
				+ dayOfweek + ", adjHolidayInd=" + adjHolidayInd + ", arrivalDelay=" + arrivalDelay
				+ ", additionalInfo=" + additionalInfo + "]";
	}
	
		
	
}
