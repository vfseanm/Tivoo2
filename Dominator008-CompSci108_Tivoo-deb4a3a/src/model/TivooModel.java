package model;
import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.joda.time.*;
import parsers.*;
import filters.*;

public class TivooModel {

    private List<TivooEvent> eventlist;
    private List<TivooEvent> filteredlist;
    private Set<TivooEventType> seentypes;
    
    public TivooModel() {
	eventlist = new ArrayList<TivooEvent>();
	filteredlist = new ArrayList<TivooEvent>();
	seentypes = new HashSet<TivooEventType>();
    }

    public List<TivooEvent> getFilteredList() {
	return new ArrayList<TivooEvent>(filteredlist);
    }
    
    public List<TivooEvent> getOriginalList() {
	return Collections.unmodifiableList(eventlist);
    }
    
    public Set<TivooEventType> getSeenTypes() {
	return Collections.unmodifiableSet(seentypes);
    }
    
    public TivooEventType[] getSeenTypesArray() {
	TivooEventType[] toreturn = new TivooEventType[seentypes.size()];
	int j = 0;
	for (Iterator<TivooEventType> i = seentypes.iterator(); i.hasNext(); j++)
	    toreturn[j] = i.next();
	Arrays.sort(toreturn);
	return toreturn;
    }
    

    
    public void clearFilter() {
	filteredlist.clear();
    	Collections.copy(filteredlist, eventlist);
    }
    
    public void reset() {
	eventlist.clear();
	filteredlist.clear();
	seentypes.clear();
    }
    
    public void read(File input) throws DocumentException {
	TivooParser p = new TivooReader().read(input);
	seentypes.add(p.getEventType());
    	eventlist.addAll(p.convertToList(input));
    	filteredlist = new ArrayList<TivooEvent>(eventlist);
    }
    
    public void filterByTime(DateTime startdate, DateTime enddate) {
	filteredlist = Filter.filterByTime(filteredlist, startdate, enddate);
    }
    
    public void filterByKeywordTitle(String keyword) {
	filteredlist = Filter.filterByKeywordTitle(filteredlist, keyword);
    }
    
    public void filterByKeywordsAttributes(Set<String> keywords, boolean retain) {
	filteredlist = Filter.filterByKeywordsAttributes(filteredlist, keywords, retain);
    }
    
}