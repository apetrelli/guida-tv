package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.html.HTMLMultiDayParserDao;
import com.google.code.guidatv.server.html.MultiDayTransmissionBuilder;

public class SOMGialloTransmissionDaoImpl extends HTMLMultiDayParserDao {

	private String pattern = "http://www.giallotv.it/programmi/?start=";

	@Override
	protected void configure(final Map<Date, List<Transmission>> transmissionMap,
			Digester digester, final MultiDayTransmissionBuilder builder) {
		final Deque<Integer> processingStack = new ArrayDeque<Integer>();
		digester.addRule("*/DIV/DIV", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) throws Exception {
				String id = attributes.getValue("id");
				processingStack.push((id != null && id.startsWith("giorno")) ? 1 : 2);
			}
			
			@Override
			public void end(String namespace, String name) throws Exception {
				if (processingStack.pop() == 1) {
					builder.increaseDay();
				}
			}
		});
		digester.addRule("*/DIV/DIV/DIV/P", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) throws Exception {
				boolean toProcess = !processingStack.isEmpty() && processingStack.peek() > 0;
				if (toProcess) {
					builder.prepareForNextTransmission();
				}
				processingStack.push(toProcess ? 1 : 0);
			}
			
			@Override
			public void end(String namespace, String name) throws Exception {
				Date currentDay = builder.getCurrentDay();
				List<Transmission> transmissions = transmissionMap.get(currentDay);
				if (transmissions == null) {
					transmissions = new ArrayList<Transmission>();
					transmissionMap.put(currentDay, transmissions);
				}
				transmissions.add(builder.build());
				processingStack.pop();
			}
		});
		digester.addRule("*/DIV/DIV/DIV/P/SPAN", new Rule() {
			
			private int mode = 0;
			private DecimalFormat numberFormat;

			{
				numberFormat = (DecimalFormat) NumberFormat.getInstance();
				numberFormat.applyPattern("##");
			}
			
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) throws Exception {
				if (processingStack.peek() > 0) {
					String clazz = attributes.getValue("class");
					if ("orario".equals(clazz)) {
						mode = 1;
					} else if ("programma".equals(clazz)) {
						mode = 2;
					} else if ("info".equals(clazz)) {
						mode = 3;
					} else if ("episodio".equals(clazz)) {
						mode = 4;
					}
				}
			}
			
			@Override
			public void body(String namespace, String name, String text)
					throws Exception {
				switch (mode) {
					case 1:
					String[] timeStrings = text.trim().split(":");
					int hour = numberFormat.parse(timeStrings[0]).intValue();
						int minutes = numberFormat.parse(timeStrings[1]).intValue();
						builder.setTime(hour, minutes);
						break;
					case 2:
						builder.setTitle(text.replaceAll("\\s*-\\s*$", ""));
						break;
					case 3:
						builder.setDescription(text);
						break;
					case 4:
						builder.setEpisode(text);
				}
			}
			
			@Override
			public void end(String namespace, String name) throws Exception {
				mode = 0;
			}
		});
		
		digester.addRule("*/DIV/DIV/DIV/P/SPAN/SPAN", new Rule() {
			private boolean toProcess = false;
			
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) throws Exception {
				toProcess = processingStack.peek() > 0 && "sottotitolo".equals(attributes.getValue("class"));
			}
			
			@Override
			public void body(String namespace, String name, String text)
					throws Exception {
				if (toProcess) {
					builder.setSubtitle(text);
				}
			}
			
			@Override
			public void end(String namespace, String name) throws Exception {
				toProcess = false;
			}
		});
		
	}

	@Override
	protected String getUrl(Channel channel, Date day) {
		TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
		TimeZone requestTimeZone = TimeZone.getTimeZone("UTC");
		Calendar currentCal = Calendar.getInstance(timeZone);
		Calendar requestCal = Calendar.getInstance(requestTimeZone);
		currentCal.setTime(day);
		requestCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));
		requestCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH));
		requestCal.set(Calendar.DATE, currentCal.get(Calendar.DATE));
		requestCal.set(Calendar.HOUR_OF_DAY, 0);
		requestCal.set(Calendar.MINUTE, 0);
		requestCal.set(Calendar.SECOND, 0);
		requestCal.set(Calendar.MILLISECOND, 0);
		return pattern + Long.toString(requestCal.getTimeInMillis() / 1000);
	}

}
