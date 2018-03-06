package com.smart.to;

import java.util.Date;

public class RequestTO {

	private String userInput;
	private String travelDate;
	
	public String getUserInput() {
		return userInput;
	}
	public void setUserInput(String userInput) {
		this.userInput = userInput;
	}
	public String getTravelDate() {
		return travelDate;
	}
	public void setTravelDate(String travelDate) {
		this.travelDate = travelDate;
	}
	@Override
	public String toString() {
		return "RequestTO [userInput=" + userInput + ", travelDate=" + travelDate + "]";
	}
	
	
}
