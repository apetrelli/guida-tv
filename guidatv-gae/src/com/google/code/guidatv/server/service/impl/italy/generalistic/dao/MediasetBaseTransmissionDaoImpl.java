package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.GuidaTvException;

public class MediasetBaseTransmissionDaoImpl implements MediasetBaseTransmissionDao {

    private final static String TRANSMISSIONS_URL = "http://www.tv.mediaset.it/dati/palinsesto/palinsesto-mondotv.xml";
    
    @Override
    public Map<Date, Map<String, List<Transmission>>> getTransmissions() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        format.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        
        Reader reader = null;
        Map<Date, Map<String, List<Transmission>>> retValue = new LinkedHashMap<Date, Map<String,List<Transmission>>>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(TRANSMISSIONS_URL);
            NodeList nodelist = document.getElementsByTagName("programma");
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element element = (Element) nodelist.item(i);
                Date timestamp = format.parse(element.getAttribute("timestamp"));
                cal.setTime(timestamp);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date day = cal.getTime();
                Map<String, List<Transmission>> channelMap = retValue.get(day);
                if (channelMap == null) {
                    channelMap = new HashMap<String, List<Transmission>>();
                    retValue.put(day, channelMap);
                }
                String channel = element.getAttribute("idref");
                List<Transmission> transmissions = channelMap.get(channel);
                if (transmissions == null) {
                    transmissions = new ArrayList<Transmission>();
                    channelMap.put(channel, transmissions);
                }
                transmissions.add(new Transmission(element.getAttribute("titolo"), null, timestamp, null, null));
            }
        } catch (MalformedURLException e) {
            throw new GuidaTvException(e);
        } catch (IOException e) {
            throw new GuidaTvException(e);
        } catch (SAXException e) {
            throw new GuidaTvException(e);
        } catch (ParserConfigurationException e) {
            throw new GuidaTvException(e);
        } catch (ParseException e) {
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
        return retValue;
    }

}
