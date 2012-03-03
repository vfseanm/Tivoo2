package writers;

import static org.rendersnake.HtmlAttributesFactory.*;
import java.io.*;
import model.*;
import org.rendersnake.*;
import java.util.*;

public abstract class TivooWriter {

    public abstract void write(List<TivooEvent> eventlist, String outputsummary, String outputdetails)
	    throws IOException;
    
    protected FileWriter getSummaryFileWriter(String outputdetails, String outputsummary) 
	    throws IOException {
	if (!new File(outputdetails).isDirectory())
	    throw new TivooException("Output path not a directory!");
	TivooUtils.clearDirectory(outputdetails);
	return new FileWriter(outputsummary);
    }
    
    protected String buildDetailURL(List<TivooEvent> eventlist, TivooEvent e) {
        return e.getTitle()
        	.replaceAll("[^A-z0-9]", "").replaceAll("\\s+", "_").trim()
        	.concat(Integer.toString(eventlist.indexOf(e))).concat(".html");
    }
    
    protected String formatDetailURL(List<TivooEvent> eventlist, TivooEvent e, String outputdetails) {
	 String s = outputdetails + buildDetailURL(eventlist, e);
	 return s.substring(outputdetails.indexOf("/") + 1);
	//return outputdetails + buildDetailURL(e);
    }
    
    protected void doWriteDetailPage(List<TivooEvent> eventlist,
	    TivooEvent e, String outputsummary, String outputdetails)
	    throws IOException {
	List<TivooEvent> oneevent = new ArrayList<TivooEvent>();
	oneevent.add(e);
	new DetailPageWriter().write(oneevent, outputsummary, outputdetails + buildDetailURL(eventlist, e));
    }
    
    protected void writeHeadWithCSS(HtmlCanvas target, String stylefile) throws IOException {
	target.head()
	  .link(href(stylefile).type("text/css").rel("stylesheet").media("screen"))
	._head().write("\n");
    }
    
    protected void startHtml(HtmlCanvas target) throws IOException {
	target.html().write("\n");
    }
    
    protected void endHtml(HtmlCanvas target) throws IOException {
	target._html().write("\n");
    }
    
    protected void startBody(HtmlCanvas target) throws IOException {
	target.body().write("\n");
    }
    
    protected void endBody(HtmlCanvas target) throws IOException {
	target._body().write("\n");
    }
    
    protected void startTable(HtmlCanvas target, String cssclass, 
	    String width, String align, String border, 
	    String cellpadding, String cellspacing) throws IOException {
	target.table(class_(cssclass).width(width).align(align).border(border)
		.cellpadding(cellpadding).cellspacing(cellspacing)).write("\n");
    }
    
    protected void endTable(HtmlCanvas target) throws IOException {
	target._table().write("\n");
    }
    
    protected void startRow(HtmlCanvas target) throws IOException {
	target.tr().write("\n");
    }
    
    protected void endRow(HtmlCanvas target) throws IOException {
	target._tr().write("\n");
    }
    
    protected void writeTableHead(HtmlCanvas target, String cssclass, String width,
	    String rowspan, String colspan, String content, String link) 
	    throws IOException {
	target
	.th(class_(cssclass).width(width).rowspan(rowspan).colspan(colspan)).write("\n");
	  if (!link.equals("")) target.a(href(link));
	  else target.a();
	  target.write(content)._a()
	._th().write("\n");
    }
    
    protected void writeTableCellLink(HtmlCanvas target, String cssclass, String width, String rowspan, String colspan, 
	    String content, String link) 
	    throws IOException {
	target
	.td(class_(cssclass).width(width).rowspan(rowspan).colspan(colspan)).write("\n")
	.a(href(link)).write(content)._a()
	._td().write("\n");
    }
    
    protected void writeTableCellLiteral(HtmlCanvas target, String cssclass, String width, String rowspan, String colspan, 
	    String content) 
	    throws IOException {
	target.td(class_(cssclass).width(width).rowspan(rowspan).colspan(colspan)).write("\n")
	   .write(content)._td().write("\n");
    }
    
    protected void writeParagraph(HtmlCanvas target, String cssclass, String content, String link) 
	    throws IOException {
	target
	.p(class_(cssclass)).write("\n");
	  if (!link.equals("")) target.a(href(link));
	  else target.a();
	  target.write(content)._a()
	._p().write("\n");
    }
    
}