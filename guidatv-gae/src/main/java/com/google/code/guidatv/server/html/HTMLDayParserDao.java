package com.google.code.guidatv.server.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.SAXException;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.GuidaTvException;

public abstract class HTMLDayParserDao {

	protected abstract void configure(final List<Transmission> transmissions, Digester digester, final TransmissionBuilder builder);

	protected abstract String getUrl(Channel channel, Date day);

	public List<Transmission> getTransmissions(Channel channel, Date day) {
	    String urlString = getUrl(channel, day);
	    Reader reader = null;
	    final List<Transmission> transmissions = new ArrayList<Transmission>();
	    try {
	        URL url = new URL(urlString);
	        InputStream is = url.openStream();
	        reader = new InputStreamReader(is, getEncoding());
	        SAXParser parser = new SAXParser();
	        Digester digester = new Digester(parser);
	        final TransmissionBuilder builder = new TransmissionBuilder();
	        builder.setStartingTime(day);
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