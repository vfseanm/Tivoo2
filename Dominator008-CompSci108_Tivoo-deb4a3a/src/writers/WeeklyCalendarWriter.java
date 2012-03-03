package writers;

import java.io.*;
import java.util.*;
import org.joda.time.*;
import org.rendersnake.*;
import model.*;

public class WeeklyCalendarWriter extends TivooWriter {

    private ArrayList<TivooEvent> event;
    private ArrayList<Integer> date;
    private ArrayList<Integer> time;

    private void clearList() {
	event = new ArrayList<TivooEvent>();
	date = new ArrayList<Integer>();
	time = new ArrayList<Integer>();
    }

    public void write(List<TivooEvent> eventlist, String outputsummary,
	    String outputdetails) throws IOException {
	FileWriter fw = getSummaryFileWriter(outputdetails, outputsummary);
	HtmlCanvas summary = new HtmlCanvas(fw);
	Collections.sort(eventlist, TivooEvent.EventTimeComparator);
	startHtml(summary);
	writeHeadWithCSS(summary, "styles/weekly_calendar.css");
	startBody(summary);
	startTable(summary, "", "90%", "center", "1", "0", "0");
	DateTime current = TivooTimeHandler.createLocalTime(eventlist.get(0).getStart());
	clearList();
	boolean flag = true;
	int count = 0;
	for (TivooEvent e : eventlist) {
	    count++;
	    boolean addedThisTime = false;
	    //if (e.isLongEvent()) continue;
	    DateTime localstart = TivooTimeHandler.createLocalTime(e.getStart());
	    // DateTime localend = TivooTimeHandler.createLocalTime(e.getEnd());
	    if (localstart.getWeekOfWeekyear() != current.getWeekOfWeekyear()) {
		current = localstart;
		flag = false;
	    } 
	    else {
		date.add(localstart.getDayOfWeek() - 1);
		time.add(localstart.getHourOfDay());
		event.add(e);
		addedThisTime = true;
	    }
	    if (!event.isEmpty() && (!flag || count == eventlist.size())) {
		DateTime currentWeek = TivooTimeHandler.createLocalTime(event.get(0).getStart());
		DateTime weekStart = currentWeek.minusDays(currentWeek.getDayOfWeek() - 1);
		startRow(summary);
		String header = weekStart.toString("MMM dd, YYYY") + " ~ " 
			+ weekStart.plusDays(6).toString("MMM dd, YYYY");
		writeTableHead(summary, "", null, "1", "8", header, "");
		endRow(summary);
		startRow(summary);
		String[] firstrow = {" ", "Monday", "Tuesday", "Wednesday", 
			"Thursday", "Friday", "Saturday", "Sunday"};
		for (String s: firstrow)
		    writeTableHead(summary, "", "11.25%", "1", "1", s, "");
		endRow(summary);
		for (int i = 0; i < 24; i++) {
		    startRow(summary);
		    writeTableCellLiteral(summary, "", null, "1", "1", i + ":00");
		    for (int j = 0; j < 7; j++) {
			summary.td();
			for (int k = 0; k < date.size(); k++) {
			    if (date.get(k) == j && time.get(k) == i) {
				writeParagraph(summary, "", event.get(k).getTitle(),
					formatDetailURL(eventlist, event.get(k), outputdetails));
				doWriteDetailPage(eventlist, event.get(k), outputsummary, outputdetails);
			    }
			}
			summary._td();
		    }
		    endRow(summary);
		}
		clearList();
		flag = true;
	    }
	    if (!addedThisTime) {
		date.add(localstart.getDayOfWeek() - 1);
		time.add(localstart.getHourOfDay());
		event.add(e);
	    }
	}
	endTable(summary);
	endBody(summary);
	endHtml(summary);
	fw.close();
    }

}