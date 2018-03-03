package com.smart.util.to;

public class WeatherInfoTO  {
	
	private String month;
	private String date;
	private String year;
	
	private String tempAvg;
	private String visibility;
	private String windSpeed;
	private String precipitation;
	private String event;
	private String additionalInfo;
	
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
	public String getTempAvg() {
		return tempAvg;
	}
	public void setTempAvg(String tempAvg) {
		this.tempAvg = tempAvg;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public String getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}
	public String getPrecipitation() {
		return precipitation;
	}
	public void setPrecipitation(String precipitation) {
		this.precipitation = precipitation;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	
	@Override
	public String toString() {
		return "WeatherInfoTO [month=" + month + ", date=" + date + ", year=" + year + ", tempAvg=" + tempAvg
				+ ", visibility=" + visibility + ", windSpeed=" + windSpeed + ", precipitation=" + precipitation
				+ ", event=" + event + ", additionalInfo=" + additionalInfo + "]";
	}
	public String printFactors() {
		return "Weather Factors [Temperature=" + tempAvg
				+ ", Visibility Range (km)=" + visibility + ", Wind Speed (kmph)=" + windSpeed + ", Precipitation (mm)=" + precipitation
				+ ", Event=" + event + ", additionalInfo=" + additionalInfo + "]";
	}
		
}
