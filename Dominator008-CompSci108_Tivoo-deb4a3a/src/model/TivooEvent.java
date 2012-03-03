package model;

import java.util.*;
import org.joda.time.*;
import sharedattributes.*;

public class TivooEvent {

    private TivooEventType myType;
    private Map<TivooAttribute, Object> myCommonAttributes;
    private Map<TivooAttribute, Object> mySpecialAttributes;
    private String myTitle;
    private String myDescription;
    private DateTime myStart;
    private DateTime myEnd;
    
    public TivooEvent(TivooEventType type, Map<TivooAttribute, Object> attributes) {
	myType = type;
	myCommonAttributes = new HashMap<TivooAttribute, Object>();
	mySpecialAttributes = new HashMap<TivooAttribute, Object>();
	Set<TivooAttribute> specialattributes = type.getSpecialAttributes();
	for (TivooAttribute t: attributes.keySet()) {
	    if (specialattributes.contains(t))
		mySpecialAttributes.put(t, attributes.get(t));
	    else
		myCommonAttributes.put(t, attributes.get(t));
	}
	myTitle = (String) myCommonAttributes.get(new Title());
	myDescription = (String) myCommonAttributes.get(new Description());
	myStart = (DateTime) myCommonAttributes.get(new StartTime());
	myEnd = (DateTime) myCommonAttributes.get(new EndTime());
    }
    
    public Map<TivooAttribute, Object> getSpecialAttributes() {
	return Collections.unmodifiableMap(mySpecialAttributes);
    }
    
    public TivooEventType getType() {
	return myType;
    }
    
    public String getTitle() {
	return myTitle;
    }
    
    public DateTime getStart() {
	return myStart;
    }
    
    public DateTime getEnd() {
	return myEnd;
    }
    
    public String getDescription() {
	return myDescription;
    }
    
    public Interval getInterval() {
	return new Interval(myStart, myEnd);
    }
    
    public boolean hasConflict(TivooEvent other) {
	if (this.equals(other)) return false;
	return getInterval().overlaps(other.getInterval());
	/*return ((other.getStart().compareTo(getEnd()) < 0 && getEnd().compareTo(other.getEnd()) <= 0) ||
		(getStart().compareTo(other.getEnd()) < 0 && other.getEnd().compareTo(getEnd()) <= 0));*/
    }
    
    public boolean hasKeyWord(String keyword, boolean special) {
	String lower = keyword.toLowerCase();
	Map<TivooAttribute, Object> usedmap = (special == true ?
		mySpecialAttributes : myCommonAttributes);
	for (TivooAttribute t: usedmap.keySet())
	    if (usedmap.get(t).toString().contains(lower)) return true;
	return false;
    }
    
    public boolean isLongEvent() {
	if (Hours.hoursBetween(getStart(), getEnd()).getHours() > 24)
	    return true;
	return false;
    }
    
    public boolean equals(Object o) {
	TivooEvent other = (TivooEvent) o;
	return (myType.equals(other.getType()) &&
		myTitle.equals(other.getTitle()) &&
		myStart.equals(other.getStart()) &&
		myEnd.equals(other.getEnd()));
    }
    
    public static final Comparator<TivooEvent> EventTimeComparator = new Comparator<TivooEvent>() {
	public int compare(TivooEvent e1, TivooEvent e2) {
	    int startcomp = EventStartComparator.compare(e1, e2);
	    if (startcomp != 0) return startcomp;
	    Integer duration1 = Seconds.secondsBetween(e1.getStart(), e1.getEnd()).getSeconds();
	    Integer duration2 = Seconds.secondsBetween(e2.getStart(), e2.getEnd()).getSeconds();
	    int durationdiff = duration1.compareTo(duration2);
	    if (durationdiff != 0) return durationdiff;
	    return EventTitleComparator.compare(e1, e2);
	}
    };
    
    public static final Comparator<TivooEvent> EventStartComparator = new Comparator<TivooEvent>() {
	public int compare(TivooEvent e1, TivooEvent e2) {
	    return e1.getStart().compareTo(e2.getStart());
	}
    };
    
    public static final Comparator<TivooEvent> EventEndComparator = new Comparator<TivooEvent>() {
	public int compare(TivooEvent e1, TivooEvent e2) {
	    return e1.getEnd().compareTo(e2.getEnd());
	}
    };
    
    public static final Comparator<TivooEvent> EventTitleComparator = new Comparator<TivooEvent>() {
	public int compare(TivooEvent e1, TivooEvent e2) {
	    return e1.getTitle().compareTo(e2.getTitle());
	}
    };
    
    public String toString() {
	return myTitle;
    }
    
}