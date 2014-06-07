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
		String time = view.getText().toString();
		
		String[] parts = time.split("\n");
		if (parts.length == 2)
		{
			Spannable busNumber = new SpannableString(parts[0]);
			Spannable busName = new SpannableString(parts[1]);
			
			busNumber.setSpan(new StyleSpan(Typeface.BOLD), 0, busNumber.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			busName.setSpan(new ForegroundColorSpan(Color.GRAY), 0, busName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			view.setText(busNumber);
			view.append("\n");
			view.append(busName);
					
		}
		else
		{
			if (CommonUtilities.isBusTimeInPast(time, m_day ))
			{
				view.setTextColor(Color.GRAY);
			}
			else
			{
				view.setTextColor(CommonUtilities.getEnabledTextColor());
			}
		}

		return view;
	}
	
}
