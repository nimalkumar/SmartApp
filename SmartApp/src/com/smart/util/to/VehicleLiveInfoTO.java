package com.smart.util.to;

public class VehicleLiveInfoTO {

	boolean isVehicleMoving;
	String timeLastKnown;
	String latitudeLastKnown;
	String longitudeLastKnown;
	
	public boolean isVehicleMoving() {
		return isVehicleMoving;
	}
	public void setVehicleMoving(boolean isVehicleMoving) {
		this.isVehicleMoving = isVehicleMoving;
	}
	public String getTimeLastKnown() {
		return timeLastKnown;
	}
	public void setTimeLastKnown(String timeLastKnown) {
		this.timeLastKnown = timeLastKnown;
	}
	public String getLatitudeLastKnown() {
		return latitudeLastKnown;
	}
	public void setLatitudeLastKnown(String latitudeLastKnown) {
		this.latitudeLastKnown = latitudeLastKnown;
	}
	public String getLongitudeLastKnown() {
		return longitudeLastKnown;
	}
	public void setLongitudeLastKnown(String longitudeLastKnown) {
		this.longitudeLastKnown = longitudeLastKnown;
	}
	@Override
	public String toString() {
		return "VehicleLiveInfoTO [isVehicleMoving=" + isVehicleMoving + ", timeLastKnown=" + timeLastKnown
				+ ", latitudeLastKnown=" + latitudeLastKnown + ", longitudeLastKnown=" + longitudeLastKnown + "]";
	}
	
}
