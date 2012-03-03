package writers;

import java.io.*;
import java.util.*;
import org.joda.time.*;
import org.rendersnake.*;
import model.*;

public class MonthlyCalendarWriter extends TivooWriter {

    private ArrayList<TivooEvent> event;
    private ArrayList<Integer> date;
    private ArrayList<Integer> week;

    private void clearList() {
	event = new ArrayList<TivooEvent>();
	date = new ArrayList<Integer>();
	week = new ArrayList<Integer>();
    }

    public void write(List<TivooEvent> eventlist, String outputsummary,
	    String outputdetails) throws IOException {
	FileWriter fw = getSummaryFileWriter(outputdetails, outputsummary);
	HtmlCanvas summary = new HtmlCanvas(fw);
	Collections.sort(eventlist, TivooEvent.EventTimeComparator);
	startHtml(summary);
	writeHeadWithCSS(summary, "styles/monthly_calendar.css");
	startBody(summary);
	startTable(summary, "", "90%", "center", "1", "0", "0");
	DateTime current = TivooTimeHandler.createLocalTime(eventlist.get(0).getStart());
	clearList();
	boolean flag = true;
	int count = 0;
	for (TivooEvent e: eventlist) {
	    count++;
	    boolean addedThisTime = false;
	    //if (e.isLongEvent()) continue;
	    DateTime localstart = TivooTimeHandler.createLocalTime(e.getStart());
	    // DateTime localend = TivooTimeHandler.createLocalTime(e.getEnd());
	    if (localstart.getMonthOfYear() != current.getMonthOfYear()) {
		current = localstart;
		flag = false;
	    } 
	    else {
		date.add(localstart.getDayOfWeek() - 1);
		week.add(localstart.getWeekOfWeekyear());
		event.add(e);
		addedThisTime = true;
	    }
	    if (!event.isEmpty() && (!flag || count == eventlist.size())) {
		startRow(summary);
		String header = TivooTimeHandler.createLocalTime(event.get(0).getStart())
			.toString("MMMM YYYY");
		writeTableHead(summary, "", null, "1", "8", header, "");
		endRow(summary);
		startRow(summary);
		String[] firstrow = {" ", "Monday", "Tuesday", "Wednesday", 
			"Thursday", "Friday", "Saturday", "Sunday"};
		for (String s: firstrow)
		    writeTableHead(summary, "", "11.25%", "1", "1", s, "");
		endRow(summary);
		DateTime currentMonth = TivooTimeHandler.createLocalTime(event.get(0).getStart());
		DateTime monthStart = currentMonth.minusDays(currentMonth.getDayOfMonth() - 1);
		int startWeek = monthStart.getWeekOfWeekyear();
		DateTime weekStart = monthStart.minusDays(monthStart.getDayOfWeek() - 1);
		for (int i = 0; i < 5; i++) {
		    startRow(summary);
		    String rowhead = weekStart.plusDays(i*7).toString("MM.dd")+ 
			    "-" + weekStart.plusDays(i*7+6).toString("MM.dd");
		    writeTableCellLiteral(summary, "", null, "1", "1", rowhead);
		    for (int j = 0; j < 7; j++) {
			summary.td();
			for (int k = 0; k < date.size(); k++) {
			    if (date.get(k) == j && week.get(k) == i + startWeek) {
				writeParagraph(summary, "", event.get(k).getTitle(),
					formatDetailURL(eventlist, event.get(k), outputdetails));
				doWriteDetailPage(eventlist, event.get(k),	outputsummary, outputdetails);
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
		week.add(localstart.getWeekOfWeekyear());
		event.add(e);
	    }
	}
	endTable(summary);
	endBody(summary);
	endHtml(summary);
	fw.close();
    }

}