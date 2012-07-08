package com.google.code.guidatv.server.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.SAXException;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.GuidaTvException;

public abstract class HTMLMultiDayParserDao {

	protected abstract void configure(
			final Map<Date, List<Transmission>> transmissionMap,
			Digester digester, final MultiDayTransmissionBuilder builder);

	protected abstract String getUrl(Channel channel, Date day);

	public Map<Date, List<Transmission>> getTransmissions(Channel channel, Date day) {
	    String urlString = getUrl(channel, day);
	    Reader reader = null;
	    final Map<Date, List<Transmission>> transmissions = new LinkedHashMap<Date, List<Transmission>>();
	    try {
	        URL url = new URL(urlString);
	        InputStream is = url.openStream();
	        reader = new InputStreamReader(is, getEncoding());
	        SAXParser parser = new SAXParser();
	        Digester digester = new Digester(parser);
	        final MultiDayTransmissionBuilder builder = new MultiDayTransmissionBuilder();
	        builder.setStartingTime(day);
	        builder.setStartingDay(day);
	        configure(transmissions, digester, builder);
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

	protected String getEncoding() {
		return "UTF-8";
	}

}