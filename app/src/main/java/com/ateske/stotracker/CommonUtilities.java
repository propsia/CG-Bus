package com.ateske.stotracker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.ateske.stotracker.ApplicationController.Days;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;

public class CommonUtilities {

    private static SharedPreferences m_preferenceManager;

    private static final String FAVORITE_STOP_LIST = "favorite_stop_list";
    private static final String SHOW_ONLY_FAVORITE_STOP = "show_only_favorite_stop";

    public enum ICONS{
		SHOW_ALL,
		SHOW_FAVORITES,
		FAVORITE,
		NOT_FAVORITE,
		SETTINGS
	}

    public static void setPreferenceManager(SharedPreferences preferenceManager)
    {
        m_preferenceManager = preferenceManager;
    }
    
	public static int getIcon(ICONS icon)
	{
		boolean whiteStatusBarIcon =  getStatusbarColor() != Color.WHITE;
		boolean whiteIcon = getEnabledTextColor() == Color.WHITE;
		switch (icon) {
			case SHOW_ALL:
				return whiteStatusBarIcon? R.drawable.ic_star_half_white_24dp : R.drawable.ic_star_half_black_24dp;
			case SHOW_FAVORITES:
				return whiteStatusBarIcon? R.drawable.ic_star_white_24dp : R.drawable.ic_star_black_24dp;
			case FAVORITE:
				return whiteIcon? R.drawable.ic_star_white_24dp : R.drawable.ic_star_black_24dp;
			case NOT_FAVORITE:
				return whiteIcon? R.drawable.ic_star_border_white_24dp : R.drawable.ic_star_border_black_24dp;
			case SETTINGS:
				return whiteStatusBarIcon? R.drawable.ic_settings_white_24dp : R.drawable.ic_settings_black_24dp;
		}
		return R.drawable.ic_launcher;
	}

	public static void toggleFavoriteView()
	{
		//Get favorite settings
		Boolean favoriteSetting = m_preferenceManager.getBoolean("SHOW_ONLY_FAVORITE_STOP", false);
		SharedPreferences.Editor editor = m_preferenceManager.edit();
		editor.putBoolean("SHOW_ONLY_FAVORITE_STOP", !favoriteSetting);
		editor.commit();
	}

	public static boolean showFavorites()
	{
		return m_preferenceManager.getBoolean("SHOW_ONLY_FAVORITE_STOP", false);
	}

	public static void setFavorite(String text, boolean favorite)
	{
		//Prepare text for storage in settings
		text = text.replace("\n", "");
		text = text + ";";

		//Get favorite settings
		String favoriteSetting = m_preferenceManager.getString(FAVORITE_STOP_LIST, "");

		//Add or remove text from the settings
		favoriteSetting = favoriteSetting.replace(text,"");
		if (favorite)
		{
			favoriteSetting = favoriteSetting + text;
		}

		//Save settings
		SharedPreferences.Editor editor = m_preferenceManager.edit();
		editor.putString(FAVORITE_STOP_LIST, favoriteSetting);
		editor.commit();
	}

	public static boolean isFavorite(String text)
	{
		text = text.replace("\n", "");
		String favoriteSetting = m_preferenceManager.getString(FAVORITE_STOP_LIST, "");
		return favoriteSetting.contains(text);
	}
	
	public static boolean getShowRouteDirections()
	{
		return m_preferenceManager.getBoolean("show_route_directions", true);
	}
	
	@SuppressLint("InlinedApi")
	public static int getSelectedTheme()
	{
        String theme = m_preferenceManager.getString("example_list", "0");
        
        switch (theme)
        {
        case "0":
        	return android.R.style.Theme_Holo_Light;
        case "1":
        	return android.R.style.Theme_Holo;
        case "2":
        	if (android.os.Build.VERSION.SDK_INT >= 14)
        		return android.R.style.Theme_Holo_Light_DarkActionBar;
        }
        
        //We should never reach this code... but if something goes wrong,
        //default to holo light.
        return android.R.style.Theme_Holo_Light;
	}
	
	public static int getEnabledTextColor()
	{
		if (isDarkThemeSelected())
			return Color.WHITE;
		
		return Color.BLACK;
	}
	
	//Returns true if a string represents a time in the format xx:xx [am/pm]
	public static boolean isStringTime(String string)
	{
		String[] text = string.split(" ");
		if (text.length == 2 && (text[1].equals("AM") || text[1].equals("PM")))
			return true;
		return false;
	}
	
	public static Days getCurrentDay()
	{
		int currentDay_int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		
		if (currentDay_int == Calendar.SATURDAY)
			return Days.SATURDAY;
		if (currentDay_int == Calendar.SUNDAY)
			return Days.SUNDAY;
		
		return Days.WEEKDAY;
	}
	
	public static boolean isBusTimeInPast(String time, Days busDay)
	{			
		String[] text = time.split(" ");
		if (isStringTime(time))
		{
			String[] timeComponents = text[0].split(":");
			
			int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
			Days currentDay = getCurrentDay();
			
			if (!currentDay.equals(busDay))
				return true;
			
			int busHour = Integer.parseInt(timeComponents[0]);
			int busMinute = Integer.parseInt(timeComponents[1]);
			
			if (text[1].equals("PM") && busHour != 12)
			{
				busHour = busHour +  12;
			}
			
			if (currentHour == 0)
			{
				if ( busHour == 12 && text[1].equals("AM") && currentMinute < busMinute)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else if (busHour == 12 && text[1].equals("AM"))
			{
				return false;
			}
			else
			{
				if  (currentHour >  busHour || (currentHour == busHour && currentMinute > busMinute))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			
		}
		
		return false;
	}
	
	private static boolean isDarkThemeSelected()
	{
		return m_preferenceManager.getString("example_list", "0").equals("1");
	}

	private static int getStatusbarColor(){
		if (getSelectedTheme() == android.R.style.Theme_Holo_Light)
			return Color.WHITE;
		return Color.BLACK;
	}
	
	
}
