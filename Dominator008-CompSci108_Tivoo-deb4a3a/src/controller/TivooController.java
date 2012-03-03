package controller;


import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.dom4j.DocumentException;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import view.TivooView;
import writers.*;
import model.*;
//import view.*;

public class TivooController {

    
    private TivooModel myModel;
    private TivooView myView;
    public static final String TITLE = "Tivoo: XML to HTML";
    private static HashMap<String, TivooWriter> myMap;

    public TivooController() {
	myModel = new TivooModel();
	myView = new TivooView(myModel);
	myView.giveController(this);

    }
    static
    {
    	myMap = new HashMap<String, TivooWriter>();
    	myMap.put("Daily Calendar", new DailyCalendarWriter());
    	myMap.put("Weekly Calendar", new WeeklyCalendarWriter());
    	myMap.put("Monthly Calendar", new MonthlyCalendarWriter());
    	myMap.put("Conflicting Events", new ConflictingEventsWriter());
    }
    
    public void view()
    {
    	File file = myView.getFile();
    	

        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(myView);
        frame.pack();
        frame.setVisible(true);

               doRead(file);
        }
    
    public void test()
    {
    	System.out.println("this is a test");
    }
    
    public void go(String destination, String detailDest, String writer, String startdate, String enddate, String key1, String key2, String key3)
    {
    	TivooWriter w = myMap.get(writer);
    	if (!startdate.isEmpty() && !enddate.isEmpty())
    	{
    		DateTime start = TivooTimeHandler.createUTC(startdate);
    		DateTime end = TivooTimeHandler.createUTC(enddate);
    		doFilterByTime(start, end);
    	}
    	if (!key1.isEmpty())
    	{
    		doFilterByKeywordTitle(key1);
    	}
    	if (!key2.isEmpty())
    	{
    		doFilterByKeywordTitle(key2);
    	}
    	if (!key3.isEmpty())
    	{
    		doFilterByKeywordTitle(key3);
    	}
    	
    	doWrite(w, destination, detailDest);
    }
   
    public void read(File file)
    {
    	doRead(file);
    }
    
    public void dailyCalendar(String outputsummary, String outputdetails)
    {
    	doWrite(new DailyCalendarWriter(), outputsummary, outputdetails);
    }
    
    public void doRead(File input) {
	try {
	    myModel.read(input);
	} catch (DocumentException e) {
	    e.printStackTrace();
	}
    }
    
    public void doFilterByTime(DateTime startdate, DateTime enddate) {
    	myModel.filterByTime(startdate, enddate);
    }
    
    public void doFilterByKeywordTitle(String keyword) {
    	myModel.filterByKeywordTitle(keyword);

    }
    
    public void doFilterByKeywordsAttributes(Set<String> keywords, boolean retain) {
	myModel.filterByKeywordsAttributes(keywords, retain);
    }
    
    public void doWrite(TivooWriter writer, String outputsummary, 
	    String outputdetails) {
	try {
	    writer.write(myModel.getFilteredList(), outputsummary, outputdetails);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
}