package com.ateske.stotracker;

import android.content.Context;
import android.graphics.Color;
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
		String time = view.getText().toString();
		if (CommonUtilities.isBusTimeInPast(time, m_day ))
		{
			view.setTextColor(Color.GRAY);
		}
		else
		{
			view.setTextColor(CommonUtilities.getEnabledTextColor());
		}
		return view;
	}
	
}
