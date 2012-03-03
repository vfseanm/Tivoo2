package parsers;

import java.util.*;
import org.dom4j.*;
import org.dom4j.io.*;
import org.joda.time.*;
import sharedattributes.*;
import model.*;

public class DukeCalParser extends TivooParser {
    
    public DukeCalParser() {
	setEventType(new DukeCalEventType());
	updateNoNeedParseMap("summary", new Title());
	updateNoNeedParseMap("description", new Description());
	updateNoNeedParseMap("address", new Location());
    }
    
    protected void setUpHandlers(SAXReader reader) {
	reader.addHandler("/events/event/summary", new NoNeedParseHandler());
	reader.addHandler("/events/event/description", new NoNeedParseHandler());
	reader.addHandler("/events/event/location/address", new NoNeedParseHandler());
	reader.addHandler("/events/event", new EventLevelHandler());
    }
    
    public TivooEventType getEventType() {
	return new DukeCalEventType();
    }
    
    public String getRootName() {
    	return "events";
    }
    
    private DateTime parseTime(Element e) {
	String timestring = e.getStringValue();
	return TivooTimeHandler.createTimeUTC(timestring);
    }

    private class DukeCalEventType extends TivooEventType {
	
	private DukeCalEventType() {
	    @SuppressWarnings("serial")
	    Set<TivooAttribute> localSpecialAttributes = new HashSet<TivooAttribute>() {{
		add(new Location());
	    }};
	    addSpecialAttributes(localSpecialAttributes);
	}
	
	public String toString() {
	    return "Duke Calendar";
	}
	
    }
    
    private class EventLevelHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {
	    elementPath.addHandler("start/utcdate", new TimeHandler());
	    elementPath.addHandler("end/utcdate", new TimeHandler());
	}

	public void onEnd(ElementPath elementPath) {
            eventlist.add(new TivooEvent(eventtype, 
        	    new HashMap<TivooAttribute, Object>(grabdatamap)));
	    grabdatamap.clear();
	    elementPath.getCurrent().detach();
	}
	
    }

    private class TimeHandler implements ElementHandler {
	
	public void onStart(ElementPath elementPath) {}

	public void onEnd(ElementPath elementPath) {
	    Element e = elementPath.getCurrent();
	    if (e.getPath().contains("start")) {
		DateTime starttime = parseTime(e);
		grabdatamap.put(new StartTime(), starttime);
	    }
	    else {
		DateTime endtime = parseTime(e);
		grabdatamap.put(new EndTime(), endtime);
	    }
	    elementPath.getCurrent().detach();
	}
	
    }
    
}