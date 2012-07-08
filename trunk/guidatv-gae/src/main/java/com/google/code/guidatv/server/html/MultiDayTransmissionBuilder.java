package com.google.code.guidatv.server.html;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MultiDayTransmissionBuilder extends TransmissionBuilder {

	private Calendar cal;
	
	public void setStartingDay(Date day) {
		cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
		cal.setTime(day);
	}
	
	public Date getCurrentDay() {
		return cal.getTime();
	}
	
	public void increaseDay() {
		cal.add(Calendar.DATE, 1);
	}
}
