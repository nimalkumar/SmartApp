package com.smart.util.to;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class LiveFeedEntity extends TableServiceEntity {

	public LiveFeedEntity(String routeTripKey,String dateStr)
	{
		this.partitionKey = routeTripKey;
		this.rowKey = dateStr;
	}
	public LiveFeedEntity() {}
	
	public String dayvalue;
	public String geolocation;
	public String hourvalue;
	public String minutevalue;
	public String monthvalue;
	public String routename;
	public String secondvalue;
	public String tripid;
	public String yearvalue;
	public String getDayvalue() {
		return this.dayvalue;
	}
	public void setdayvalue(String dayvalue) {
		this.dayvalue = dayvalue;
	}
	public String getgeolocation() {
		return this.geolocation;
	}
	public void setgeolocation(String geolocation) {
		this.geolocation = geolocation;
	}
	public String gethourvalue() {
		return this.hourvalue;
	}
	public void sethourvalue(String hourvalue) {
		this.hourvalue = hourvalue;
	}
	public String getminutevalue() {
		return this.minutevalue;
	}
	public void setminutevalue(String minutevalue) {
		this.minutevalue = minutevalue;
	}
	public String getmonthvalue() {
		return this.monthvalue;
	}
	public void setmonthvalue(String monthvalue) {
		this.monthvalue = monthvalue;
	}
	public String getroutename() {
		return this.routename;
	}
	public void setroutename(String routename) {
		this.routename = routename;
	}
	public String getsecondvalue() {
		return this.secondvalue;
	}
	public void setsecondvalue(String secondvalue) {
		this.secondvalue = secondvalue;
	}
	public String gettripid() {
		return this.tripid;
	}
	public void settripid(String tripid) {
		this.tripid = tripid;
	}
	public String getyearvalue() {
		return this.yearvalue;
	}
	public void setyearvalue(String yearvalue) {
		this.yearvalue = yearvalue;
	}
	
	
}
