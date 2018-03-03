package com.smart.util.to;

public class TrafficInfoTO  {
	
	private String month;
	private String date;
	private String year;
	
	private String time;
	private String trafficCongestionIndex;
	
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTrafficCongestionIndex() {
		return trafficCongestionIndex;
	}
	public void setTrafficCongestionIndex(String trafficCongestionIndex) {
		this.trafficCongestionIndex = trafficCongestionIndex;
	}
	
	
	@Override
	public String toString() {
		return "TrafficInfoTO [month=" + month + ", date=" + date + ", year=" + year + ", time=" + time
				+ ", trafficCongestionIndex=" + trafficCongestionIndex + ", additionalInfo=" + additionalInfo + "]";
	}
	public String printFactors() {
		return "Traffic Factors [Traffic Congestion Index=" + trafficCongestionIndex + ", additionalInfo=" + additionalInfo + "]";
	}
	
	
	
}
