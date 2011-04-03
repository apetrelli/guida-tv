package com.google.code.guidatv.android;

import java.util.Date;

import android.app.Application;

public class GuidaTvApplication extends Application {

	private Date currentDate = new Date();
	
	public Date getCurrentDate() {
		return currentDate;
	}
	
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
}
