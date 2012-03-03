package writers;

import java.io.*;
import java.util.*;
import org.joda.time.*;
import org.rendersnake.*;
import model.*;

public class DailyCalendarWriter extends TivooWriter {

    public void write(List<TivooEvent> eventlist, String outputsummary, String outputdetails) 
	    throws IOException {
	FileWriter fw = getSummaryFileWriter(outputdetails, outputsummary);
	HtmlCanvas summary = new HtmlCanvas(fw);
	Collections.sort(eventlist, TivooEvent.EventTimeComparator);
	HashSet<Integer> writtenstartdate = new HashSet<Integer>();
	startHtml(summary);
	writeHeadWithCSS(summary, "styles/daily_calendar.css");
	startBody(summary);
	startTable(summary, "", "80%", "center", "0", "0", "0");
	for (TivooEvent e: eventlist) {
	    if (e.isLongEvent()) continue;
	    DateTime localstart = TivooTimeHandler.createLocalTime(e.getStart());
	    DateTime localend = TivooTimeHandler.createLocalTime(e.getEnd());
	    checkDuplicateStartDate(summary, localstart, writtenstartdate);
	    startRow(summary);
	    writeTableHead(summary, "time", null, "1", "1", formatStartEnd(localstart, localend), "");
	    writeTableCellLink(summary, "", null, "1", "1", e.getTitle(), 
		    formatDetailURL(eventlist, e, outputdetails));
	    endRow(summary);
	    doWriteDetailPage(eventlist, e, outputsummary, outputdetails);
	}
	endTable(summary);
	endBody(summary);
	endHtml(summary);
	fw.close();
    }
    
    private String formatStartEnd(DateTime start, DateTime end) {
	return start.toString("HH:mm") + "-" + end.toString("HH:mm");
    }
    
    private void checkDuplicateStartDate(HtmlCanvas target, DateTime startdate
	    , Set<Integer> writtenstartdate) throws IOException {
	if (!writtenstartdate.contains(startdate.getDayOfYear())) {
	    startRow(target);
	    writeTableHead(target, "day", null, "1", "2", startdate.toString("EEE, MMM dd"), "");
	    endRow(target);
	    writtenstartdate.add(startdate.getDayOfYear());
	}
    }
    
}