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

public class BoingTransmissionDaoImpl implements BoingTransmissionDao {

    private final static String TRANSMISSIONS_URL = "http://www.boingtv.it/xml/palinsesto.xml";
    
    @Override
    public Map<Date, List<Transmission>> getTransmissions() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        format.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        
        Reader reader = null;
        Map<Date, List<Transmission>> retValue = new LinkedHashMap<Date, List<Transmission>>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(TRANSMISSIONS_URL);
            NodeList nodelist = document.getElementsByTagName("EVENT");
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element element = (Element) nodelist.item(i);
                Date timestamp = format.parse(element.getAttribute("timestamp"));
                cal.setTime(timestamp);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date day = cal.getTime();
                List<Transmission> transmissions = retValue.get(day);
                if (transmissions == null) {
                    transmissions = new ArrayList<Transmission>();
                    retValue.put(day, transmissions);
                }
                transmissions.add(new Transmission(
                        element.getAttribute("name"), element
                                .getAttribute("description"), timestamp, null,
                        null));
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
