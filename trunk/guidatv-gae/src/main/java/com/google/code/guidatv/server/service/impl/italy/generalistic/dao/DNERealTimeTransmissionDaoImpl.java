package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.html.HTMLDayParserDao;
import com.google.code.guidatv.server.html.PreprocessingRule;
import com.google.code.guidatv.server.html.TransmissionBuilder;

public class DNERealTimeTransmissionDaoImpl extends HTMLDayParserDao implements DNERealTimeTransmissionDao {

    private String pattern = "http://www.realtimetv.it/tvl-fe/day/?type=day&channel_code=RTIT-IT&date={0}&filter=0600&style=hh_day";

    @Override
	protected String getUrl(Channel channel, Date day) {
		MessageFormat format = new MessageFormat(pattern);
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        dateFormat.setTimeZone(timeZone);
		String urlString = format.format(new Object[] { dateFormat.format(day) });
		return urlString;
	}

	@Override
	protected void configure(final List<Transmission> transmissions,
			Digester digester, final TransmissionBuilder builder) {
		final Deque<Boolean> processingStack = new ArrayDeque<Boolean>();
		digester.addRule("*/TABLE/TBODY/TR", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) {
				String clazz = attributes.getValue("class");
				boolean toProcess = clazz != null && clazz.contains("listing");
				processingStack.push(toProcess);
				if (toProcess) {
					builder.prepareForNextTransmission();
				}
			}
			@Override
			public void end(String namespace, String name) throws Exception {
				if (processingStack.pop()) {
					transmissions.add(builder.build());
				}
			}
		});
		digester.addRule("*/TABLE/TBODY/TR/TD/TIME/SPAN", new PreprocessingRule(processingStack) {
			
			private DecimalFormat numberFormat;
			
			{
				numberFormat= (DecimalFormat) NumberFormat.getInstance();
				numberFormat.applyPattern("00");
			}
			
			@Override
			public boolean isProcessable(Attributes attributes) {
				return "listing-time".equals(attributes.getValue("class"));
			}
			
			@Override
			public void body(String namespace, String name, String text) throws ParseException {
				if (processingStack.peek()) {
		            String timeString = text.trim();
		            String hourString = timeString.substring(0, 2);
		            String minutesString = timeString.substring(3, 5);
		            int hour = numberFormat.parse(hourString).intValue();
		            int minutes = numberFormat.parse(minutesString).intValue();
		            builder.setTime(hour, minutes);
				}
			}
		});
		
		digester.addRule("*/TABLE/TBODY/TR/TD/DIV/DIV",
				new PreprocessingRule(processingStack) {

					@Override
					public boolean isProcessable(Attributes attributes) {
						return "module".equals(attributes.getValue("class"));
					}
				});

		digester.addRule("*/TABLE/TBODY/TR/TD/DIV/DIV/H3/A", new Rule() {
			@Override
			public void body(String namespace, String name, String text)
					throws Exception {
				if (processingStack.peek()) {
					builder.setTitle(text.trim());
				}
			}
		});

		digester.addRule("*/TABLE/TBODY/TR/TD/DIV/DIV/P", new Rule() {
			
			private int index = 0;
			
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) throws Exception {
				String clazz = attributes.getValue("class");
				if (clazz != null && clazz.contains("module-content")) {
					if (clazz.contains("episode-title")) {
						index = 1;
					} else {
						index = 2;
					}
				}
			}
			
			@Override
			public void body(String namespace, String name, String text)
					throws Exception {
				if (processingStack.peek() && index > 0) {
					String trimmedText = text.trim();
					if (index == 1) {
						if (!trimmedText.isEmpty()) {
							builder.setEpisode(trimmedText);
						}
					} else {
						builder.setDescription(trimmedText);
					}
				}
			}
			
			@Override
			public void end(String namespace, String name) throws Exception {
				index = 0;
			}
		});
	}
}
