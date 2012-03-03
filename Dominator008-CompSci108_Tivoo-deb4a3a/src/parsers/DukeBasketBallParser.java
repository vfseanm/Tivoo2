package parsers;

import java.util.*;
import model.*;
import org.dom4j.*;
import org.dom4j.io.*;
import org.joda.time.*;
import org.joda.time.format.*;
import sharedattributes.*;

public class DukeBasketBallParser extends TivooParser {

    private String cachedstartdate, cachedenddate;
    
    public DukeBasketBallParser() {
	setEventType(new DukeBasketBallEventType());
	updateNoNeedParseMap("Subject", new Title());
	updateNoNeedParseMap("Description", new Description());
	updateNoNeedParseMap("Location", new Location());
    }

    protected void setUpHandlers(SAXReader reader) {
	reader.addHandler("/dataroot/Calendar/Subject", new NoNeedParseHandler());
	reader.addHandler("/dataroot/Calendar/Description", new NoNeedParseHandler());
	reader.addHandler("/dataroot/Calendar/Location", new NoNeedParseHandler());
	reader.addHandler("/dataroot/Calendar", new EventLevelHandler());
    }
    
    public TivooEventType getEventType() {
	return new DukeBasketBallEventType();
    }
	
    public String getRootName() {
    	return "dataroot";
    }
    
    private DateTime parseTime(String timestring) {
	DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/YYYY hh:mm:ss aa");
	return formatter.parseDateTime(timestring);
    }

    private class EventLevelHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {
	    elementPath.addHandler("StartDate", new TimeHandler());
	    elementPath.addHandler("StartTime", new TimeHandler());
	    elementPath.addHandler("EndDate", new TimeHandler());
	    elementPath.addHandler("EndTime", new TimeHandler());
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
	    if (e.getName().equals("StartDate"))
		cachedstartdate = e.getStringValue();
	    if (e.getName().equals("StartTime")) {
		DateTime starttime = parseTime(cachedstartdate.concat(" " + 
			e.getStringValue()));
		grabdatamap.put(new StartTime(), starttime);
	    }
	    if (e.getName().equals("EndDate"))
		cachedenddate = e.getStringValue();
	    if (e.getName().equals("EndTime")) {
		DateTime endtime = parseTime(cachedenddate.concat(" " + 
			e.getStringValue()));
		grabdatamap.put(new EndTime(), endtime);
	    }
	    elementPath.getCurrent().detach();
	}
	
    }
    
    private class DukeBasketBallEventType extends TivooEventType {

	private DukeBasketBallEventType() {
	    @SuppressWarnings("serial")
	    Set<TivooAttribute> toadd = new HashSet<TivooAttribute>() {{
		    add(new Location());
	    }};
	    addSpecialAttributes(toadd);
	}
	
	public String toString() {
	    return "Duke Basketball";
	}
	
    }

}