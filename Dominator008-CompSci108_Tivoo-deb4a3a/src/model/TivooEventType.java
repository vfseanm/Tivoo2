package model;

import java.util.*;

import sharedattributes.*;

public abstract class TivooEventType implements Comparable<TivooEventType> {

    public abstract String toString();
    
    @SuppressWarnings("serial")
    private static Set<TivooAttribute> commonAttributes = new HashSet<TivooAttribute>() {{
	add(new Title()); add(new Description()); add(new StartTime()); add(new EndTime());
    }};
    
    private Set<TivooAttribute> specialAttributes = new HashSet<TivooAttribute>();
    
    public static Set<TivooAttribute> getCommonAttributes() {
	return Collections.unmodifiableSet(commonAttributes);
    }
    
    public Set<TivooAttribute> getSpecialAttributes() {
	return Collections.unmodifiableSet(specialAttributes);
    }
    
    protected void addSpecialAttributes(Set<TivooAttribute> theset) {
	specialAttributes.addAll(theset);
    }
    
    public int compareTo(TivooEventType t) {
	return toString().compareTo(t.toString());
    }
    
    public boolean equals(Object o) {
	TivooEventType t = (TivooEventType) o;
	return toString().equals(t.toString());
    }
    
    public int hashCode() {
	return toString().hashCode();
    }
    
}