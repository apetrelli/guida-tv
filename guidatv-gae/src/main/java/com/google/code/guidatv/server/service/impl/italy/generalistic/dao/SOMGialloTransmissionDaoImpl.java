package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class SOMGialloTransmissionDaoImpl {

	private String pattern = "http://www.giallotv.it/programmi/?start=";

	public Map<Date, List<Transmission>> getTransmissions(Channel channel,
			Date day) {
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
		String urlString = pattern + Long.toString(requestCal.getTimeInMillis() / 1000);
		Reader reader = null;
		Map<Date, List<Transmission>> transmissionMap = new HashMap<Date, List<Transmission>>();
		try {
			URL url = new URL(urlString);
			InputStream is = url.openStream();
			reader = new InputStreamReader(is, "UTF-8");
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(reader));
			Document document = parser.getDocument();
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expression = xpath
					.compile("//DIV[starts-with(@id, 'giorno')]");
			NodeList dayNodeList = (NodeList) expression.evaluate(document,
					XPathConstants.NODESET);
			DecimalFormat numberFormat = (DecimalFormat) NumberFormat
					.getInstance();
			numberFormat.applyPattern("00");
			Calendar dayCal = Calendar.getInstance(timeZone);
			dayCal.setTime(day);
			Date currentDate = dayCal.getTime();
			for (int d = 0; d < dayNodeList.getLength(); d++) {
				Node dayNode = dayNodeList.item(d);
				transmissionMap.put(currentDate,
						buildDay(currentDate, dayNode, numberFormat));
				dayCal.add(Calendar.DATE, 1);
				currentDate = dayCal.getTime();
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
		return transmissionMap;
	}

	private List<Transmission> buildDay(Date currentDate, Node dayNode,
			DecimalFormat numberFormat) throws ParseException {
		NodeList superNodeList = dayNode.getChildNodes();
		List<Transmission> transmissions = new ArrayList<Transmission>();
		for (int v = 0; v < superNodeList.getLength(); v++) {
			Node partDayNode = superNodeList.item(v);
			if (partDayNode instanceof Element) {
				Element partDayElement = (Element) partDayNode;
				if ("DIV".equals(partDayElement.getTagName())
						&& "tabdiv"
								.equals(partDayElement.getAttribute("class"))) {
					TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
					Calendar cal = Calendar.getInstance(timeZone);
					cal.setTime(currentDate);
					NodeList nodeList = partDayElement.getChildNodes();
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node item = nodeList.item(i);
						if (item instanceof Element) {
							Element superelement = (Element) item;
							if ("P".equals(superelement.getTagName())) {
								Transmission transmission = buildTransmission(superelement,
										currentDate, cal, i, numberFormat);
								transmissions.add(transmission);
							}
						}
					}
				}
			}
		}
		return transmissions;
	}

	private void loadDayPart(Node item, Date currentDate,
			DecimalFormat numberFormat, List<Transmission> transmissions, int i)
			throws ParseException {
		TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(currentDate);
		NodeList children = item.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			Node supernode = children.item(j);
			if (supernode instanceof Element) {
				Element superelement = (Element) supernode;
				if ("P".equals(superelement.getTagName())) {
					Transmission transmission = buildTransmission(superelement,
							currentDate, cal, i, numberFormat);
					transmissions.add(transmission);
				}
			}
		}
	}

	private Transmission buildTransmission(Element superelement,
			Date currentDate, Calendar cal, int i, DecimalFormat numberFormat)
			throws ParseException {
		Date start = null;
		String name = null;
		String link = null;
		String episode = null;
		String description = null;
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
						cal.setTime(currentDate);
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);
						int hours = numberFormat.parse(hour).intValue();
						int minute = numberFormat.parse(minutes).intValue();
						if (hours < 6 || (hours == 6 && minute == 0 && i != 0)) {
							cal.add(Calendar.DAY_OF_YEAR, 1);
						}
						cal.set(Calendar.HOUR_OF_DAY, hours);
						cal.set(Calendar.MINUTE, minute);
						start = cal.getTime();
					} else if ("programma".equals(className)) {
						Text text = (Text) firstChild;
						name = text.getWholeText();
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
		Transmission transmission = new Transmission(name, episode + " "
				+ description, start, null, link);
		return transmission;
	}

}
