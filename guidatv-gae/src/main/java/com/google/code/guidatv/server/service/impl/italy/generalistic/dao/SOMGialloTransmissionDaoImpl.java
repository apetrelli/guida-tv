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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.GuidaTvException;

public class SOMGialloTransmissionDaoImpl implements SOMGialloTransmissionDao {

    private String pattern = "http://www.giallotv.it/programmi/{0}";

    public List<Transmission> getTransmissions(Channel channel, Date day) {
        MessageFormat format = new MessageFormat(pattern);
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
		Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(day);
        DateFormat dateFormat = new SimpleDateFormat("EEEE-dd-MMMM", Locale.ITALY);
        dateFormat.setTimeZone(timeZone);
        String urlString = format.format(new Object[] {dateFormat.format(day).replace("ì", "i")});
        Reader reader = null;
        List<Transmission> transmissions = new ArrayList<Transmission>();
        try {
            URL url = new URL(urlString);
            InputStream is = url.openStream();
            reader = new InputStreamReader(is, "UTF-8");
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(reader));
            Document document = parser.getDocument();
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = xpath.compile("//DIV[@id='palinsesto']");
            NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            System.out.println(nodeList.getLength());
            DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getInstance();
            numberFormat.applyPattern("00");
            for (int i=0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                NodeList children = item.getChildNodes();
                Date start = null;
                String name = null;
                String link = null;
                String episode = null;
                String description = null;
                for (int j=0; j < children.getLength(); j++) {
                    Node supernode = children.item(j);
                    if (supernode instanceof Element) {
                        Element superelement = (Element) supernode;
                        if ("P".equals(superelement.getTagName())) {
                        	NodeList subchildren = superelement.getChildNodes();
                        	for (int t = 0; t < subchildren.getLength(); t++) {
                        		Node node = subchildren.item(t);
                        		if (node instanceof Element) {
                        			Element element = (Element) node;
			                        Node firstChild = element.getFirstChild();
			                        if ("SPAN".equals(element.getTagName())) {
			                            String className = element.getAttribute("class");
			                            if ("orario".equals(className)) {
			                                Text text = (Text) firstChild;
			                                String timeString = text.getWholeText().trim();
			                                String hour = timeString.substring(0, 2);
			                                String minutes = timeString.substring(3, 5);
			                                cal.setTime(day);
			                                cal.set(Calendar.HOUR_OF_DAY, 0);
			                                cal.set(Calendar.MINUTE, 0);
			                                cal.set(Calendar.SECOND, 0);
			                                cal.set(Calendar.MILLISECOND, 0);
			                                int hours = numberFormat.parse(hour).intValue();
			                                int minute = numberFormat.parse(minutes).intValue();
			                                if (hours < 6 || (hours == 6 && minute == 0 && i != 0)) {
			                                    cal.add(Calendar.DAY_OF_YEAR, 1);
			                                }
			                                cal.set(Calendar.HOUR_OF_DAY,hours);
			                                cal.set(Calendar.MINUTE, minute);
			                                start = cal.getTime();
			                            } else if ("programma".equals(className)) {
			                                NodeList items = element.getChildNodes();
			                                for (int k=0; k < items.getLength(); k++) {
			                                    Node innerItem = items.item(k);
			                                    if (innerItem instanceof Element) {
			                                        Element innerElement = (Element) innerItem;
			                                        if ("A".equals(innerElement.getTagName())) {
			                                            link = innerElement.getAttribute("href");
			                                            if (link != null && link.trim().length() <= 0) {
			                                                link = null;
			                                            }
			                                            Text innerText = (Text) innerElement.getFirstChild();
			                                            name = innerText.getWholeText();
			                                        }
			                                    }
			                                }
			                            } else if ("info".equals(className)) {
			                                Text text = (Text) firstChild;
			                                episode = text.getWholeText();
			                            } else if ("episodio".equals(className)) {
			                                Text text = (Text) firstChild;
			                                description = text.getWholeText();
			                            }
			                        }
                        		}
                        	}
                            Transmission transmission = new Transmission(name, episode + " " + description, start, null, link);
                            transmissions.add(transmission);
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            throw new GuidaTvException(e);
        } catch (IOException e) {
            throw new GuidaTvException(e);
        } catch (SAXException e) {
            throw new GuidaTvException(e);
        } catch (XPathExpressionException e) {
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
        return transmissions;
    }

}
