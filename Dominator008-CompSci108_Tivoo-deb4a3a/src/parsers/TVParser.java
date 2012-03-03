package parsers;

import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;
import org.joda.time.*;
import org.joda.time.format.*;
import sharedattributes.*;
import model.*;

public class TVParser extends TivooParser {
    
    private Map<String, Set<String>> channelmap;
    private String cachedcurrentchannel;
    private Set<String> cacheddisplaynames;
    private Set<String> cachedactors;

    public TVParser() {
	channelmap = new HashMap<String, Set<String>>();
	cacheddisplaynames = new HashSet<String>();
	cachedactors = new HashSet<String>();
	setEventType(new TVEventType());
	updateNoNeedParseMap("title", new Title());
	updateNoNeedParseMap("desc", new Description());
    }
    
    protected void setUpHandlers(SAXReader reader) {
	reader.addHandler("/tv/programme/title", new NoNeedParseHandler());
	reader.addHandler("/tv/programme/desc", new NoNeedParseHandler());
	reader.addHandler("/tv/channel", new TopLevelHandler());
	reader.addHandler("/tv/programme", new EventLevelHandler());
    }
    
    public TivooEventType getEventType() {
	return new TVEventType();
    }
    
    public String getRootName() {
    	return "tv";
    }
    
    private DateTime parseTime(String timestring) {
	DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYYMMddHHmmss Z");
	return formatter.parseDateTime(timestring);
    }
    
    private class TVEventType extends TivooEventType {
	
	private TVEventType() {
	    @SuppressWarnings("serial")
	    Set<TivooAttribute> localSpecialAttributes = new HashSet<TivooAttribute>() {{
		add(new Channel()); add(new Actor()); 
	    }};
	    addSpecialAttributes(localSpecialAttributes);
	}
	
	public String toString() {
	    return "TV";
	}
	
    }
    
    private class TopLevelHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {
	    cachedcurrentchannel= elementPath.getCurrent().attributeValue("id");
	    elementPath.addHandler("display-name", new DisplayNameHandler());
	}

	public void onEnd(ElementPath elementPath) {
	    channelmap.put(cachedcurrentchannel, new HashSet<String>(cacheddisplaynames));
	    cacheddisplaynames.clear();
	    elementPath.getCurrent().detach();
	}
	
    }
    
    private class EventLevelHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {
	    Element e = elementPath.getCurrent();
	    DateTime starttime = parseTime(e.attributeValue("start"));
	    DateTime endtime = parseTime(e.attributeValue("stop"));
	    String channel = e.attributeValue("channel");
	    grabdatamap.put(new StartTime(), starttime);
	    grabdatamap.put(new EndTime(), endtime);
	    grabdatamap.put(new Channel(), channelmap.get(channel));
	    elementPath.addHandler("credits", new CreditsHandler());
	}

	public void onEnd(ElementPath elementPath) {
            eventlist.add(new TivooEvent(eventtype, 
        	    new HashMap<TivooAttribute, Object>(grabdatamap)));
	    grabdatamap.clear();
	    elementPath.getCurrent().detach();
	}
	
    }

    private class CreditsHandler implements ElementHandler {
	
	public void onStart(ElementPath elementPath) {
	    elementPath.addHandler("actor", new ActorHandler());
	}

	public void onEnd(ElementPath elementPath) {
	    grabdatamap.put(new Actor(), new HashSet<String>(cachedactors));
	    cachedactors.clear();
	    elementPath.getCurrent().detach();
	}
	
    }
    
    private class DisplayNameHandler implements ElementHandler {
	
	public void onStart(ElementPath elementPath) {}

	public void onEnd(ElementPath elementPath) {
	    cacheddisplaynames.add(elementPath.getCurrent().getStringValue());
	    elementPath.getCurrent().detach();
	}
	
    }
    
    private class ActorHandler implements ElementHandler {
	
	public void onStart(ElementPath elementPath) {}

	public void onEnd(ElementPath elementPath) {
	    cachedactors.add(elementPath.getCurrent().getStringValue());
	    elementPath.getCurrent().detach();
	}
	
    }
    
    private class Channel extends TivooAttribute {

	public String toString() {
	    return "Channel(s)";
	}
	    
    }
	
    private class Actor extends TivooAttribute {

	public String toString() {	
	    return "Actor(s)";
	}
	
    }
    
}