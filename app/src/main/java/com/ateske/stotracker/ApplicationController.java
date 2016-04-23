package com.ateske.stotracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.util.Xml;

public class ApplicationController extends Activity {
	
	public enum Days { WEEKDAY, SATURDAY, SUNDAY }
	
	private class Stop
	{
		public LinkedHashMap<String, String> stops = new LinkedHashMap<String, String>();
	}
	private class Day
	{
		//Day List -> Stop list (e.g. Saturday --> [stop1, stop2...])
		public LinkedHashMap<Days, Stop> day = new LinkedHashMap<Days, Stop>();
	}
	private class DirectionList
	{
		//Direction name -> daylist (e.g. Ottawa via portage --> [weekday, sat, sun]
		public LinkedHashMap<String, Day> direction = new LinkedHashMap<String, Day>();
	}
	private class BusSchedule
	{
		//Route -> Direction (e.g. 11 --> [dir1, dir2]
		public LinkedHashMap<String, DirectionList> routes = new LinkedHashMap<String, DirectionList>();
		
	}
	
	private BusSchedule schedule;
	
	private Context m_context;
	
    private String selectedRoute = null;
    private String selectedDirection = null;
    private String selectedStop = null;
    private String selectedTab = null;
    private int selectedTabIndex = 0;
    
    private int m_previouslySelectedDirection = 0;
    private int m_selectedRoutePosition = 0;
    private int m_selectedStopPosition = 0;
    
    public ApplicationController(Context context)
    {
    	m_context = context;
    	
    	try {
			parseXml();
		} catch (XmlPullParserException | IOException e) {
			e.printStackTrace();
		}
	}
    
    private void parseXml() throws XmlPullParserException, IOException{
    	
    	XmlPullParser xpp = getParser();
    	int eventType = xpp.getEventType();
    	
    	schedule = new BusSchedule();	 
    	
    	String currentBusRoute = null;
    	String currentDirection = null;
    	Days currentDay = null;
    	String currentStop = null;
    	boolean expectStopInfo = false;

    	while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals("bus_schedule"))){
    		if (eventType == XmlPullParser.START_TAG){
    			String name = xpp.getAttributeValue(null, "name");
    			switch (xpp.getName())
    			{
    			case "route":
    				currentBusRoute = name;
    				schedule.routes.put(name, new DirectionList());
    				break;
    			case "direction":
    				currentDirection = name;
    				schedule.routes.get(currentBusRoute).direction.put(currentDirection, new Day());
    				break;
    			case "day":	    				
    				switch (name)
    				{
    				case "Weekday":
    					currentDay = Days.WEEKDAY;
    					break;
    				case "Saturday":
    					currentDay = Days.SATURDAY;
    					break;
    				case "Sunday":
    					currentDay = Days.SUNDAY;  
    					break;	
    				default:
    					throw new XmlPullParserException("Invalid day");
    				}
    				schedule.routes.get(currentBusRoute).direction.get(currentDirection).day.put(currentDay, new Stop());	    				
    				break;
    			case "stop":
    				currentStop = name;
    				expectStopInfo = true;
    				break;
    			}	  	
    		}
    		if (eventType == XmlPullParser.TEXT && expectStopInfo)
    		{
    			expectStopInfo = false;
    			schedule.routes.get(currentBusRoute).direction.get(currentDirection).day.get(currentDay).stops.put(currentStop,  xpp.getText());
    		}
    		eventType = xpp.next();
    	}	
    }
	    
    public void setSelectedTab(String tabTitle, int tabIndex)
    {
    	selectedTab = tabTitle;
    	selectedTabIndex = tabIndex;
    }
    
    public boolean back()
    {
    	if (selectedStop != null){
    		selectedStop = null;
    		selectedDirection = null;
    	}
    	else if (selectedRoute != null){
    		selectedRoute = null;
    		m_previouslySelectedDirection = 0;
    		m_selectedStopPosition = 0;
    	}
    	else
    		return false;
    	
    	return true;
    }
    
    public boolean addSelectionInfo(String selectionInfo, int selectionIndex)
    {    	
    	if (selectedRoute == null)
    	{
    		selectedRoute = selectionInfo.split("\\s+")[0];
    		m_selectedRoutePosition = selectionIndex;
    	}
    	else if (selectedDirection == null)
    	{
    		selectedStop = selectionInfo;
    		selectedDirection = selectedTab;
    		m_previouslySelectedDirection = selectedTabIndex;
    		m_selectedStopPosition = selectionIndex;
    	}
    	else
    		return false;
    	
    	return true;
    } 
    
	public ViewContext getCurrentView() throws XmlPullParserException
	{
 
    	if (selectedRoute == null)
    	{
    		return getBusRoutes();
    	}
    	else if (selectedStop == null || selectedDirection == null)
    	{
    		return getStops();
    	}
    	else
    	{
    		return getTimes();
    	}
    	
    }
   
    private XmlPullParser getParser() {
    	
    	String fileName = "sto-complete.xml";
    	
        try {
        	InputStream in = m_context.getAssets().open(fileName);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return parser;
        } catch (Exception e){
        	System.err.println("CAUGHT EXCEPTION: " + e);
        	return null;
        }
    }
    
	private ViewContext getBusRoutes() throws XmlPullParserException {
		
		Set<String> keys = schedule.routes.keySet();
		
		List<String> result = new ArrayList<String>();
		
		for (String route : keys)
		{	
			if (CommonUtilities.getShowRouteDirections())
			{
				DirectionList directions = schedule.routes.get(route);
				Set<String> direction = directions.direction.keySet();
				String[] test = direction.toArray(new String[direction.size()]);
				
				String dir = test[0];
				if (test.length > 1)
					dir += " ↔ " + test[1];
				
				route += "\n" + dir;
			}
			
			result.add(route);
		}
		
		ViewContext context = new ViewContext();
		context.listViews = new ArrayList<String[]>();
		context.listViews.add(result.toArray(new String[1]));
		context.tabTitles = new String[0];
		context.viewTitle = m_context.getString(R.string.route_page_title);
		context.backPossible = false;
		context.listScrollPosition[0] = m_selectedRoutePosition;
		context.favoritesEnabled = true;
		
		return context;
		
	}
	
	private ViewContext getStops(){
		String[] directions = getDirections();
		List<String[]> stops = new ArrayList<String[]>();
		
		for (String direction : directions)
		{
			Stop stop = schedule.routes.get(selectedRoute).direction.get(direction).day.get(Days.WEEKDAY);
			String[] theStops = stop.stops.keySet().toArray(new String[1]);
			stops.add(theStops);
		}
		
		ViewContext context = new ViewContext();
		
		context.listViews = stops;
		context.tabTitles = directions;
		context.viewTitle = m_context.getString(R.string.route_prefix) + " " +  selectedRoute;
		context.tabToSelect = m_previouslySelectedDirection;
		context.listScrollPosition[m_previouslySelectedDirection] = m_selectedStopPosition;
		
    	return context;
	}
	
    public String[] getDirections(){
    	
    	DirectionList directions = schedule.routes.get(selectedRoute);
    	
    	List<String> direction = new ArrayList<String>();
    	
    	for (Map.Entry<String, Day> entry : directions.direction.entrySet())
    	{
    		direction.add(entry.getKey());
    	}
    	
    	return direction.toArray(new String[1]);
    }
	
	private ViewContext getTimes(){
		List<String[]> timeViews = new ArrayList<String[]>();
		String[] tabTitles = new String[] {
				m_context.getString(R.string.weekday_label),  
				m_context.getString(R.string.saturday_label),
				m_context.getString(R.string.sunday_label)};
		
		timeViews.add(getTimesForDay(Days.WEEKDAY));
		timeViews.add(getTimesForDay(Days.SATURDAY));
		timeViews.add(getTimesForDay(Days.SUNDAY));
		
		ViewContext context = new ViewContext();
		
		context.listViews = timeViews;
		context.tabTitles = tabTitles;
		context.viewTitle = selectedStop;
		context.tabToSelect = CommonUtilities.getCurrentDay().ordinal();
		
		Days[] days = new Days[] {Days.WEEKDAY, Days.SATURDAY, Days.SUNDAY};
		
		for ( int day = 0; day < days.length; ++day)
		{
			String[] times = timeViews.get(day);
			for (int i = 0; i < times.length; ++i)
			{
				if (!CommonUtilities.isBusTimeInPast(times[i], days[day]))
				{
					context.listScrollPosition[day] = i;
					break;
				}
				
			}
		}
		
    	return context;
	}
	
	private String[] getTimesForDay(Days day){
		
    	Stop stop = schedule.routes.get(selectedRoute).direction.get(selectedDirection).day.get(day);
    	String msg = stop == null? null : stop.stops.get(selectedStop);
    	if (msg == null) msg = m_context.getString(R.string.no_service_message);
    	List<String> times = Arrays.asList(msg.split(","));
    	
    	List<String> results = new ArrayList<String>();
    	
    	for (int i = 0; i < times.size(); ++i)
    	{
    		if (!times.get(i).equals(""))
    			results.add(times.get(i));
    	}
    	
    	return results.toArray(new String[1]);
	}
	
	public Days getSelectedDay()
	{
		if (selectedStop == null)
			return null;
		
		if (selectedTab.equals(m_context.getString(R.string.weekday_label)))
			return Days.WEEKDAY;
		if (selectedTab.equals(m_context.getString(R.string.saturday_label)))
			return Days.SATURDAY;
		
		return Days.SUNDAY;	
	}

}
