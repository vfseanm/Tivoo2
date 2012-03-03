package writers;

import java.io.*;
import java.util.*;
import org.joda.time.*;
import org.rendersnake.*;
import sharedattributes.*;
import model.*;

public class DetailPageWriter extends TivooWriter {

    public void write(List<TivooEvent> eventlist, String outputsummary,
	    String outputdetails)
	    throws IOException {
	if (new File(outputdetails).isDirectory()) {
	    for (TivooEvent e: eventlist) {
		writeOneDetailPage(e, outputsummary, buildDetailURL(eventlist, e));
	    }
	}
	else writeOneDetailPage(eventlist.get(0), outputsummary, outputdetails);
    }
    
    private void writeOneDetailPage(TivooEvent e, String outputsummary, String detailURL)
	    throws IOException {
	FileWriter fw = new FileWriter(detailURL);
	HtmlCanvas detail = new HtmlCanvas(fw);
	startHtml(detail);
	writeHeadWithCSS(detail, "../styles/detail_page.css");
	startBody(detail);
	startTable(detail, "", "70%", "center", "0", "0", "0");
	startRow(detail);
	writeTableHead(detail, "title", null,"1", "2", e.getTitle(), "");
	endRow(detail);
	startRow(detail);
	writeTableCellLiteral(detail, "time", null, "1", "2", formatStartEnd(e.getStart(), e.getEnd()));
	endRow(detail);
	startRow(detail);
	writeTableCellLiteral(detail, "", null, "1", "1", e.getDescription());
	endRow(detail);
	Map<TivooAttribute, Object> specialAttributes = e.getSpecialAttributes();
	for (TivooAttribute attr: specialAttributes.keySet()) {
	    startRow(detail);
	    String towrite = attr.toString() + ": " +
		    specialAttributes.get(attr).toString().replaceAll("\\]", "")
	    		.replaceAll("\\[", "");
	    writeTableCellLiteral(detail, "", null, "1", "1", towrite);
	    endRow(detail);
	}
	startRow(detail);
	writeTableCellLink(detail, "back", null, "1", "1", "Back to summary", 
		"../../" + outputsummary);
	endRow(detail);
	endTable(detail);
	endBody(detail);
	endHtml(detail);
	fw.close();
    }
    
    private String formatStartEnd(DateTime start, DateTime end) {
	return "Start: " + start.toString("HH:mm") + " End: " + end.toString("HH:mm");
    }
    
}