package com.nadeem.cgbus;

import java.util.Calendar;

import com.nadeem.cgbus.ApplicationController.Days;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;

public class CommonUtilities {

    private static SharedPreferences m_preferenceManager;

    private static final String FAVORITE_STOP_LIST = "favorite_stop_list";
    private static final String SHOW_ONLY_FAVORITE_STOP_MAIN = "show_only_favorite_stop_main";
	private static final String SHOW_ONLY_FAVORITE_STOP_STOPS = "show_only_favorite_stop_stops";

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
		switch (icon) {
			case SHOW_ALL:
				return R.drawable.ic_star_half_white_24dp;
			case SHOW_FAVORITES:
				return R.drawable.ic_star_white_24dp;
			case FAVORITE:
				return R.drawable.ic_star_white_24dp;
			case NOT_FAVORITE:
				return R.drawable.ic_star_border_white_24dp;
			case SETTINGS:
				return R.drawable.ic_settings_white_24dp;
		}
		return R.drawable.ic_launcher;
	}

    public static String generateKey(String selectedRoute, String selectedDirection, String elem)
    {
        if (selectedDirection == null || selectedRoute == null)
        {
            return elem;
        }
        return selectedRoute + ":" + selectedDirection + ":" + elem;
    }

	public static void toggleFavoriteView(String key)
	{
		if (key.equals(""))
		{
			//Get favorite settings
			Boolean favoriteSetting = m_preferenceManager.getBoolean(SHOW_ONLY_FAVORITE_STOP_MAIN, false);
			SharedPreferences.Editor editor = m_preferenceManager.edit();
			editor.putBoolean(SHOW_ONLY_FAVORITE_STOP_MAIN, !favoriteSetting);
			editor.commit();
		}
		else
		{
			//Prepare text for storage in settings
			key = key.replace("\n", "");
			key = key + ";";

			//Get favorite settings
			String favoriteSetting = m_preferenceManager.getString(SHOW_ONLY_FAVORITE_STOP_STOPS, "");
			boolean favorite = favoriteSetting.contains(key);

			//Add or remove text from the settings
			favoriteSetting = favoriteSetting.replace(key,"");
			if (!favorite)
				favoriteSetting = favoriteSetting + key;

			//Save settings
			SharedPreferences.Editor editor = m_preferenceManager.edit();
			editor.putString(SHOW_ONLY_FAVORITE_STOP_STOPS, favoriteSetting);
			editor.commit();
		}

	}

	public static boolean showFavorites(String key)
	{
		if (key.equals(""))
		{
			return m_preferenceManager.getBoolean(SHOW_ONLY_FAVORITE_STOP_MAIN, false);
		}
		else
		{
			return m_preferenceManager.getString(SHOW_ONLY_FAVORITE_STOP_STOPS, "").contains(key);
		}
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


	public static int getEnabledTextColor()
	{
			return Color.BLACK;
	}

	//Returns true if a string represents a time in the format xx:xx [am/pm]
	public static boolean isStringTime(String string)
	{
		String[] text = string.split(" ");
		return text.length == 2 && (text[1].equals("AM") || text[1].equals("PM"));
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
				return !(busHour == 12 && text[1].equals("AM") && currentMinute < busMinute);
			}
			else if (busHour == 12 && text[1].equals("AM"))
			{
				return false;
			}
			else
			{
				return currentHour > busHour || (currentHour == busHour && currentMinute > busMinute);
			}
			
		}
		
		return false;
	}
	
	public static boolean isFontSizeOverride()
	{
		return m_preferenceManager.getBoolean("override_font_size",false);
	}

	public static int getFontSize()
	{
		String fontSize = m_preferenceManager.getString("font_sizes_list","18");
		return Integer.parseInt(fontSize);
	}
	
}
