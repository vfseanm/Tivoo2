package model;
import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.*;
import org.xml.sax.*;

import parsers.*;

public class TivooReader {

    private TivooParser pickedparser;
    private static Map<String, TivooParser> parsermap;
    
    private static Set<TivooParser> parserset = new HashSet<TivooParser>();
    static {
	parserset.add(new DukeCalParser());
	parserset.add(new GoogleCalParser());
	parserset.add(new DukeBasketBallParser());
	parserset.add(new NFLParser());
	parserset.add(new TVParser());
	parsermap = new HashMap<String, TivooParser>();
	for (TivooParser p: parserset)
	    parsermap.put(p.getRootName(), p);
    }
    
    public static SAXReader getReader() {
        SAXReader reader = new SAXReader();
	reader.setEntityResolver(new EntityResolver() {
	    public InputSource resolveEntity(String publicID, String systemID) {
		return new InputSource(new ByteArrayInputStream
			("<?xml version='1.0' encoding='GBK'?>".getBytes()));  
	    }
	});
	return reader;
    }
    
    public TivooParser read(File input) throws DocumentException {
	SAXReader reader = getReader();
	for (String rootname: parsermap.keySet()) {
	    reader.addHandler("/" + rootname, new TypeCheckHandler());
	}
	reader.read(input);
	return pickedparser;
    }
    
    private class TypeCheckHandler implements ElementHandler {

	public void onStart(ElementPath elementPath) {
	    String rootstring = elementPath.getCurrent().getName();
	    pickedparser = parsermap.get(rootstring);
	    elementPath.getCurrent().detach();
	}

	public void onEnd(ElementPath elementPath) {
	    elementPath.getCurrent().detach();
	}
	
    }   
    
}