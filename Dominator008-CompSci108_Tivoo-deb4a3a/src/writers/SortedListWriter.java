package writers;

import java.io.*;
import java.util.*;
import org.rendersnake.*;
import model.*;

public class SortedListWriter extends TivooWriter {

    public void write(List<TivooEvent> eventlist, String outputsummary,
	    String outputdetails) throws IOException {
	FileWriter fw = getSummaryFileWriter(outputdetails, outputsummary);
	HtmlCanvas summary = new HtmlCanvas(fw);
	Collections.sort(eventlist, TivooEvent.EventTimeComparator);
	startHtml(summary);
	writeHeadWithCSS(summary, "styles/list_view.css");
	startBody(summary);
	startTable(summary, "", "80%", "center", "0", "0", "0");
	for (TivooEvent e : eventlist) {
	    startRow(summary);
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

}