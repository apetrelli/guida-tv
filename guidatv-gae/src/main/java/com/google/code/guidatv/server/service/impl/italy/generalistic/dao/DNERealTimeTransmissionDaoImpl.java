package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.GuidaTvException;

public class DNERealTimeTransmissionDaoImpl implements DNERealTimeTransmissionDao {

    private String pattern = "http://www.realtimetv.it/tvl-fe/day/?type=day&channel_code=RTIT-IT&date={0}&filter=0600&style=hh_day";

    public List<Transmission> getTransmissions(Date day) {
        MessageFormat format = new MessageFormat(pattern);
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        dateFormat.setTimeZone(timeZone);
		String urlString = format.format(new Object[] { dateFormat.format(day) });
        Reader reader = null;
        final List<Transmission> transmissions = new ArrayList<Transmission>();
        try {
            URL url = new URL(urlString);
            InputStream is = url.openStream();
            reader = new InputStreamReader(is, "UTF-8");
            SAXParser parser = new SAXParser();
            Digester digester = new Digester(parser);
            final TransmissionBuilder builder = new TransmissionBuilder();
            builder.setStartingTime(day);
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
            
            digester.parse(reader);
        } catch (MalformedURLException e) {
            throw new GuidaTvException(e);
        } catch (IOException e) {
            throw new GuidaTvException(e);
        } catch (SAXException e) {
            throw new GuidaTvException(e);
		} finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return transmissions;
    }
    
    private abstract class PreprocessingRule extends Rule {
		private final Deque<Boolean> processingStack;

		private PreprocessingRule(Deque<Boolean> processingStack) {
			this.processingStack = processingStack;
		}

		@Override
		public void begin(String namespace, String name,
				Attributes attributes) throws Exception {
			boolean toProcess = isProcessable(attributes);
			processingStack.push(toProcess);
		}

		public abstract boolean isProcessable(Attributes attributes);

		@Override
		public void end(String namespace, String name) {
			processingStack.pop();
		}
	}

	private static class TransmissionBuilder {
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
    		return new Transmission(title, (episode != null ? episode + " - " : "") + description, time, null, url);
    	}
    }
}
