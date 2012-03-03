package writers;

import java.io.*;
import java.util.*;
import model.*;
import org.rendersnake.HtmlCanvas;

public class ConflictingEventsWriter extends TivooWriter {

    public void write(List<TivooEvent> eventlist, String outputsummary,
	    String outputdetails) throws IOException {
	FileWriter fw = getSummaryFileWriter(outputdetails, outputsummary);
	HtmlCanvas summary = new HtmlCanvas(fw);
	Collections.sort(eventlist, TivooEvent.EventTimeComparator);
	startHtml(summary);
	writeHeadWithCSS(summary, "styles/conflict_view.css");
	startBody(summary);
	startTable(summary, "", "80%", "center", "0", "0", "0");
	for (TivooEvent e : eventlist) {
	    if (e.isLongEvent()) continue;
	    List<TivooEvent> conflicts = new ArrayList<TivooEvent>();
	    for (TivooEvent o: eventlist)
		if (e.hasConflict(o) && !o.isLongEvent())
		    conflicts.add(o);
	    if (!conflicts.isEmpty()) {
		startRow(summary);
		writeTableCellLink(summary, "conflict", null, "1", "1", e.getTitle(), 
			formatDetailURL(eventlist, e, outputdetails));
		endRow(summary);
		for (TivooEvent o: conflicts) {
		    startRow(summary);
		    writeTableCellLiteral(summary, "", null, "1", "1", o.toString());
		    endRow(summary);
		}
	    }
	    doWriteDetailPage(eventlist, e, outputsummary, outputdetails);
	}
	endTable(summary);
	endBody(summary);
	endHtml(summary);
	fw.close();
    }
}
