package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Calendar;
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
import com.google.code.guidatv.server.html.TransmissionBuilder;

public class RaiTransmissionDaoImpl extends HTMLDayParserDao implements
		RaiTransmissionDao {

	private String pattern = "http://www.rai.it/dl/portale/html/palinsesti/guidatv/static/{0}_{1,number,0000}_{2,number,00}_{3,number,00}.html";

	@Override
	protected void configure(final List<Transmission> transmissions,
			Digester digester, final TransmissionBuilder builder) {
		final Deque<Boolean> processingStack = new ArrayDeque<Boolean>();
		digester.addRule("*/DIV/DIV/DIV", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) {
				String clazz = attributes.getValue("class");
				boolean toProcess = "intG".equals(clazz);
				processingStack.push(toProcess);
				if (toProcess) {
					builder.prepareForNextTransmission();
				}
			}

			@Override
			public void end(String namespace, String name) {
				if (processingStack.pop()) {
					transmissions.add(builder.build());
				}
			}
		});

		digester.addRule("*/DIV/DIV/DIV/SPAN", new Rule() {

			private int mode = 0;
			private DecimalFormat numberFormat;

			{
				numberFormat = (DecimalFormat) NumberFormat.getInstance();
				numberFormat.applyPattern("00");
			}

			@Override
			public void begin(String namespace, String name,
					Attributes attributes) {
				if (processingStack.peek()) {
					String clazz = attributes.getValue("class");
					if ("ora".equals(clazz)) {
						processingStack.push(false);
						mode = 1;
					} else if ("info".equals(clazz)) {
						mode = 2;
						processingStack.push(true);
					} else {
						processingStack.push(false);
					}
				} else {
					processingStack.push(false);
				}
			}

			@Override
			public void body(String namespace, String name, String text) throws ParseException {
				if (mode == 1) {
					String timeString = text.trim();
					String hourString = timeString.substring(0, 2);
					String minutesString = timeString.substring(3, 5);
					int hour = numberFormat.parse(hourString).intValue();
					int minutes = numberFormat.parse(minutesString).intValue();
					builder.setTime(hour, minutes);
				}
			}

			@Override
			public void end(String namespace, String name) {
				processingStack.pop();
				mode = 0;
			}

		});

		digester.addRule("*/DIV/DIV/DIV/SPAN/A", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) {
				if (processingStack.peek()) {
					String href = attributes.getValue("href");
					if (href != null) {
						builder.setUrl(href);
					}
				}
			}

			@Override
			public void body(String namespace, String name, String text) {
				if (processingStack.peek()) {
					builder.setTitle(text.trim());
				}
			}

		});

		digester.addRule("*/DIV/DIV/DIV/DIV", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) {
				boolean toProcess = processingStack.peek()
						&& "eventDescription".equals(attributes
								.getValue("class"));
				processingStack.push(toProcess);
			}

			@Override
			public void body(String namespace, String name, String text) {
				if (processingStack.peek()) {
					String trimmedText = text.trim();
					if (!trimmedText.isEmpty()) {
						builder.setDescription(trimmedText);
					}
				}
			}

			@Override
			public void end(String namespace, String name) {
				processingStack.pop();
			}
		});

		digester.addRule("*/DIV/DIV/DIV/DIV/SPAN", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) {
				boolean toProcess = processingStack.peek()
						&& "solotesto".equals(attributes.getValue("class"));
				processingStack.push(toProcess);
			}

			@Override
			public void body(String namespace, String name, String text) {
				if (processingStack.peek()) {
					String trimmedText = text.trim();
					if (!trimmedText.isEmpty()) {
						builder.setEpisode(trimmedText);
					}
				}
			}

			@Override
			public void end(String namespace, String name) {
				processingStack.pop();
			}
		});

	}

	@Override
	protected String getUrl(Channel channel, Date day) {
		MessageFormat format = new MessageFormat(pattern);
		Calendar cal = Calendar
				.getInstance(TimeZone.getTimeZone("Europe/Rome"));
		cal.setTime(day);
		return format.format(new Object[] { channel.getCode(),
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DATE) });
	}

}
