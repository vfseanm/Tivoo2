package model;

import org.joda.time.*;
import org.joda.time.format.*;

public class TivooTimeHandler {
    
    public static DateTime createTimeUTC(String UTCrepresentation) {
	DateTimeFormatter UTCformat = null;
	try {
	    UTCformat = DateTimeFormat.forPattern("YYYYMMdd'T'HHmmssZ");
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	}
	return UTCformat.parseDateTime(UTCrepresentation);
    }    
    
    public static DateTime createUTC(String time)
    {
    	DateTimeFormatter UTCformat = null;
    	try {
    	    UTCformat = DateTimeFormat.forPattern("MMddYYYY");
    	} catch (IllegalArgumentException e) {
    	    e.printStackTrace();
    	}
    	return UTCformat.parseDateTime(time);
    }
    public static DateTime createLocalTime(DateTime dt) {
	return dt.toDateTime(DateTimeZone.getDefault());
    }
    
}