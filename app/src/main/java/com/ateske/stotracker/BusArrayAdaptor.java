package com.ateske.stotracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ateske.stotracker.ApplicationController.Days;

public class BusArrayAdaptor extends ArrayAdapter<String>
{
	//The day that this adapter is representing
	Days m_day;

	public BusArrayAdaptor(Context context, int resource, String[] objects, Days day) {
		super(context, resource, objects);
		m_day = day;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TextView view =(TextView) super.getView(position, convertView, parent);
		String text = view.getText().toString();
		
		String[] parts = text.split("\n");
		if (parts.length == 2)
		{
			return formatBusRoute(view, parts[0], parts[1]);
		}
		else if (CommonUtilities.isStringTime(text))
		{
			return formatBusTime(view, text);
		}
		else
		{
			return formatDirectionName(view, text);
		}
	}

	public View formatDirectionName(TextView view, String directionName)
	{
		String[] splitStr = directionName.split("//");
		String stopName = splitStr[splitStr.length-1];
		view.setText(stopName);
		return view;
	}
	
	public View formatBusRoute(TextView view, String busNumberStr, String busNameStr)
	{
		Spannable busNumber = new SpannableString(busNumberStr);
		Spannable busName = new SpannableString(busNameStr);
		
		//Set the bus number to bold
		busNumber.setSpan(new StyleSpan(Typeface.BOLD), 0, busNumber.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//Set the bus direction text to gray
		busName.setSpan(new ForegroundColorSpan(Color.GRAY), 0, busName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		view.setText(busNumber);
		view.append("\n");
		view.append(busName);
		
		return view;
	}
	
	public View formatBusTime(TextView view, String time)
	{
		if (CommonUtilities.isBusTimeInPast(time, m_day))
		{
			//If the bus has already passed, set the text to gray
			view.setTextColor(Color.GRAY);
		}
		else
		{
			//Otherwise, set it to black
			view.setTextColor(CommonUtilities.getEnabledTextColor());
		}
		
		return view;
	}
	
}
