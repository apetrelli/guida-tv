package com.google.code.guidatv.server.html;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.code.guidatv.model.Transmission;

public class TransmissionBuilder {
	Calendar previousTime;
	Date time;
	String title;
	String episode;
	String description;
	String url;
	
	public TransmissionBuilder() {
	}
	
	public TransmissionBuilder setStartingTime(Date time) {
		previousTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
		previousTime.setTime(time);
		return this;
	}
	
	public TransmissionBuilder prepareForNextTransmission() {
		time = null;
		title = null;
		episode = null;
		description = null;
		url = null;
		return this;
	}
	
	public TransmissionBuilder setTime(int hour, int minute) {
		int oldHour = previousTime.get(Calendar.HOUR);
		int oldMinute = previousTime.get(Calendar.MINUTE);
		previousTime.set(Calendar.HOUR_OF_DAY, hour);
		previousTime.set(Calendar.MINUTE, minute);
		if (oldHour > hour || (oldHour == hour && oldMinute > minute)) {
			previousTime.add(Calendar.DATE, 1);
		}
		time = previousTime.getTime();
		return this;
	}
	
	public TransmissionBuilder setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public TransmissionBuilder setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public TransmissionBuilder setEpisode(String episode) {
		this.episode = episode;
		return this;
	}
	
	public TransmissionBuilder setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public Transmission build() {
		String finalDescription = null;
		if (episode != null) {
			if (description != null) {
				finalDescription = episode + " - " + description;
			} else {
				finalDescription = episode;
			}
		} else if (description != null) {
			finalDescription = description;
		}
		return new Transmission(title, finalDescription, time, null, url);
	}
}