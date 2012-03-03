package parsers;

import java.io.*;
import java.util.*;
import model.*;
import org.dom4j.*;
import org.dom4j.io.*;
import sharedattributes.*;

public abstract class TivooParser {
    
    protected Map<String, TivooAttribute> noneedparsemap = new HashMap<String, TivooAttribute>();
    protected Map<TivooAttribute, Object> grabdatamap = new HashMap<TivooAttribute, Object>();
    protected List<TivooEvent> eventlist;
    protected TivooEventType eventtype;
    
    public abstract String getRootName();

    public abstract TivooEventType getEventType();

    protected abstract void setUpHandlers(SAXReader reader);
    
    protected void setEventType(TivooEventType type) {
	eventtype = type;
    }
    
    protected void updateNoNeedParseMap(String key, TivooAttribute value) {
	noneedparsemap.put(key, value);
    }
    
    @SuppressWarnings("serial")
    private static HashMap<String, String> pollutant = new HashMap<String, String>() {{
	put("&lt;br /&gt;", " "); put("<br />", " "); put("&amp;", "&"); put("&#39;", "'");
	put("&nbsp;", " ");  put("<br>", " ");
    }};

    protected String sanitizeString(String polluted) {
	for (String s: pollutant.keySet())
	    polluted = polluted.replaceAll(s, pollutant.get(s));
	return polluted;
    }
    
    public List<TivooEvent> convertToList(File input) {
	eventlist = new ArrayList<TivooEvent>();
        SAXReader reader = TivooReader.getReader();
        setUpHandlers(reader);
        try {
            reader.read(input);
            noneedparsemap.clear();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
	return new ArrayList<TivooEvent>(eventlist);
    }
    
    protected class NoNeedParseHandler implements ElementHandler {
	
	public void onStart(ElementPath elementPath) {}

	public void onEnd(ElementPath elementPath) {
	    Element e = elementPath.getCurrent();
	    String str = sanitizeString(e.getStringValue());
	    String relpath = e.getPath(e.getParent());
	    TivooAttribute attr = noneedparsemap.get(relpath);
	    if (attr == null) {
		System.out.println(noneedparsemap);
		System.out.println(relpath);
		System.out.println(str);
	    }
	    grabdatamap.put(attr, str);
	    elementPath.getCurrent().detach();
	}
	
    }
    
}