package parsers;

import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;
import org.joda.time.*;
import org.joda.time.format.*;
import sharedattributes.*;
import model.*;

public class GoogleCalParser extends TivooParser {

    private DateTimeZone timezone;
    private List<List<DateTime>> recurringstartend;

    public GoogleCalParser() {
	recurringstartend = new ArrayList<List<DateTime>>();
	setEventType(new GoogleCalEventType());
	updateNoNeedParseMap("*[name()='title']", new Title());
	updateNoNeedParseMap("*[name()='content']", new Description());
    }
    
    protected void setUpHandlers(SAXReader reader) {
	reader.addHandler("/feed/entry/title", new NoNeedParseHandler());
	reader.addHandler("/feed/entry/content", new NoNeedParseHandler());
	reader.addHandler("/feed/gCal:timezone", new TopLevelHandler());
	reader.addHandler("/feed/entry", new EventLevelHandler());
    }
    
    public TivooEventType getEventType() {
	return new GoogleCalEventType();
    }
    
    public String getRootName() {
	return "feed";
    }
    
    private List<DateTime> parseOneTimeEvent(String timestring) {
	String[] splitted = timestring.split("\\s+");
	if (splitted.length < 12)
	    splitted = Arrays.copyOf(fixTime(splitted), 8);
	String month = splitted[2], 
		date = splitted[3].replaceAll(",", ""), 
		year = splitted[4], 
		starttime = splitted[5], 
		endtime = splitted[7];
	DateTimeFormatter monthformat = DateTimeFormat.forPattern("MMM");
	int _month = monthformat.parseDateTime(month).getMonthOfYear();
	int _date = Integer.parseInt(date);
	int _year = Integer.parseInt(year);
	DateTimeParser[] parsers = { 
	        DateTimeFormat.forPattern("hh:mmaa").getParser(),
	        DateTimeFormat.forPattern("hhaa").getParser() };
	DateTimeFormatter hourformat = new DateTimeFormatterBuilder()
		.append(null, parsers).toFormatter();
	int _starthour = hourformat.parseDateTime(starttime).getHourOfDay();
	int _startminute = hourformat.parseDateTime(starttime).getMinuteOfHour();
	int _endhour = hourformat.parseDateTime(endtime).getHourOfDay();
	int _endminute = hourformat.parseDateTime(endtime).getMinuteOfHour();
	DateTime start = new DateTime(_year, _month, 
		_date, _starthour, _startminute, timezone);
	DateTime end = new DateTime(_year, _month, 
		_date, _endhour, _endminute, timezone);
	List<DateTime> toreturn = new ArrayList<DateTime>();
	toreturn.add(start); toreturn.add(end);
	return toreturn;
    }
    
    private void parseRecurringEvent (String timestring) {
	List<DateTime> recurringstart = new ArrayList<DateTime>();
	List<DateTime> recurringend = new ArrayList<DateTime>();
	String[] splitted = timestring.split("\\s+");
	String starttime = splitted[4].concat(splitted[5]);
	DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-ddHH:mm:ss")
		.withZone(timezone);
	DateTime firststart = formatter.parseDateTime(starttime);
	int durationsec = Integer.parseInt(splitted[8]);
	DateTime firstend = firststart.plusSeconds(durationsec);
	for (int i = 0; i < 52; i++) {
	    DateTime tempstart = new DateTime(firststart);
	    DateTime tempend = new DateTime(firstend);
	    recurringstart.add(tempstart);
	    recurringend.add(tempend);
	    firststart = firststart.plusWeeks(1);
	    firstend = firstend.plusWeeks(1);
	}
	recurringstartend.add(recurringstart); recurringstartend.add(recurringend);
    }
    
    private void buildRecurringEvents() {
	for (int i = 0; i < recurringstartend.get(0).size(); i++) {
	    Map<TivooAttribute, Object> toadd = new HashMap<TivooAttribute, Object>(grabdatamap);
	    toadd.put(new StartTime(), recurringstartend.get(0).get(i));
	    toadd.put(new EndTime(), recurringstartend.get(1).get(i));
	    eventlist.add(new TivooEvent(eventtype, toadd));
	}
    }
    
    private String[] fixTime(String[] splitted) {
	String[] fixed = new String[8];
	for (int i = 0; i < splitted.length; i++) fixed[i] = splitted[i];
	fixed[5] =  "12:01am"; fixed[7] = "11:59pm";
	return fixed;
    }
    
    private class GoogleCalEventType extends TivooEventType {

	public String toString() {
	    return "Google Calendar";
	}
	
    }
    
    private class TopLevelHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {}

	public void onEnd(ElementPath elementPath) {
	    timezone = DateTimeZone.forID(elementPath.getCurrent().attributeValue("value"));
	}
	
    }

    private class EventLevelHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {
	    elementPath.addHandler("/feed/entry/summary", new TimeHandler());
	}

	public void onEnd(ElementPath elementPath) {
	    if (recurringstartend.isEmpty()) {
		eventlist.add(new TivooEvent(eventtype,
			new HashMap<TivooAttribute, Object>(grabdatamap)));
	    }
	    else 
		buildRecurringEvents();
	    recurringstartend.clear();
	    grabdatamap.clear();
	    elementPath.getCurrent().detach();
	}
	
    }

    private class TimeHandler implements ElementHandler {
	
	public void onStart(ElementPath elementPath) {}

	public void onEnd(ElementPath elementPath) {
	    Element e = elementPath.getCurrent();
	    String timestring = sanitizeString(e.getStringValue());
	    if (timestring.startsWith("Recurring")) {
		parseRecurringEvent(timestring);
	    }
	    else {
		List<DateTime> startend = parseOneTimeEvent(timestring);
		grabdatamap.put(new StartTime(), startend.get(0));
		grabdatamap.put(new EndTime(), startend.get(1));
	    }
	    elementPath.getCurrent().detach();
	}
	
    }

}