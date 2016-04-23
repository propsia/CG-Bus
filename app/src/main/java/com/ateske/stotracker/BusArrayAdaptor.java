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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ateske.stotracker.ApplicationController.Days;

import java.util.ArrayList;
import java.util.Arrays;

public class BusArrayAdaptor extends ArrayAdapter<String>
{
	//The day that this adapter is representing
	Days m_day;
	ArrayList<String> m_fullList;
	boolean m_showCheckBox;

	public BusArrayAdaptor(Context context, int resource, String[] objects, Days day, boolean showCheckBox) {
		this(context, resource, new ArrayList<>(Arrays.asList(objects)), day, showCheckBox);
	}
	public BusArrayAdaptor(Context context, int resource, String[] objects, Days day) {
		this(context, resource, new ArrayList<>(Arrays.asList(objects)), day, true);
	}

	private BusArrayAdaptor(Context context, int resource, ArrayList<String> objects, Days day, boolean showCheckBox) {
		super(context, resource, R.id.item_text, objects);
		m_fullList = new ArrayList(objects);
		m_day = day;
		m_showCheckBox = showCheckBox;
		this.pruneListForFavorites();
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout linearLayout = (LinearLayout)super.getView(position, convertView, parent);
		CheckBox checkBox = (CheckBox)linearLayout.getChildAt(1);
		TextView textView = (TextView)linearLayout.getChildAt(0);
		String text = textView.getText().toString();

		//Format the text
		String[] parts = text.split("\n");
		if (parts.length == 2)
		{
			formatBusRoute(textView, parts[0], parts[1]);
		}
		else if (CommonUtilities.isStringTime(text))
		{
			formatBusTime(textView, text);
		}
		else
		{
			formatDirectionName(textView, text);
		}

		//Format the checkbox
		if (m_showCheckBox)
		{
			boolean checked = CommonUtilities.isFavorite(textView.getText().toString());
			checkBox.setChecked(checked);

			if (checked)
				checkBox.setButtonDrawable(CommonUtilities.getIcon(CommonUtilities.ICONS.FAVORITE));
			else
				checkBox.setButtonDrawable(CommonUtilities.getIcon(CommonUtilities.ICONS.NOT_FAVORITE));
		}
		else
		{
			checkBox.setClickable(false);
			checkBox.setVisibility(View.INVISIBLE);
		}


		return linearLayout;
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

	public void pruneListForFavorites()
	{
		if (!m_showCheckBox)
			return;

		this.clear();

		if (CommonUtilities.showFavorites())
		{
			for (int i =0; i < m_fullList.size(); ++i)
			{
				String elem = m_fullList.get(i);
				if (CommonUtilities.isFavorite(elem))
					this.add(elem);

			}
		}
		else
		{
			this.addAll(m_fullList);
		}
	}
}
