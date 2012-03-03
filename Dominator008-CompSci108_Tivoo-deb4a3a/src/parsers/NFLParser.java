package parsers;

import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;
import org.joda.time.*;
import org.joda.time.format.*;
import model.*;
import sharedattributes.*;

public class NFLParser extends TivooParser {

    public NFLParser() {
	setEventType(new NFLEventType());
	updateNoNeedParseMap("Col1", new Title());
	updateNoNeedParseMap("Col2", new Description());
	updateNoNeedParseMap("Col15", new Location());
    }
    
    protected void setUpHandlers(SAXReader reader) {
	reader.addHandler("/document/row/Col1", new NoNeedParseHandler());
	reader.addHandler("/document/row/Col2", new NoNeedParseHandler());
	reader.addHandler("/document/row/Col15", new NoNeedParseHandler());
	reader.addHandler("/document/row", new EventLevelHandler());
    }
    
    public String getRootName() {
    	return "document";
    }
    
    public TivooEventType getEventType() {
	return new NFLEventType();
    }

    private DateTime parseTime(Element e) {
	String timestring = e.getStringValue();
	DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
	return formatter.parseDateTime(timestring).plusHours(12);
    }
    
    private class NFLEventType extends TivooEventType {
	
	private NFLEventType() {
	    @SuppressWarnings("serial")
	    Set<TivooAttribute> localSpecialAttributes = new HashSet<TivooAttribute>() {{
		add(new Location());
	    }};
	    addSpecialAttributes(localSpecialAttributes);
	}

	public String toString() {
	    return "NFL";
	}
	
    }
    
    private class EventLevelHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {
	    elementPath.addHandler("Col8", new TimeHandler());
	    elementPath.addHandler("Col9", new TimeHandler());
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
	    if (e.getName().equals("Col8")) {
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