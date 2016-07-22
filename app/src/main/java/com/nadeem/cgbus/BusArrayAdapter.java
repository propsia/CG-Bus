package com.nadeem.cgbus;

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

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nadeem.cgbus.ApplicationController.Days;

import java.util.ArrayList;
import java.util.Arrays;

public class BusArrayAdapter extends ArrayAdapter<String>
{
	//The day that this adapter is representing
	Days m_day;
	ArrayList<String> m_fullList;
	boolean m_showCheckBox;
	String m_selectedBus;
	String m_selectedDirection;
	InterstitialAd  mIntersitialAd;

	public BusArrayAdapter(Context context, int resource, String[] objects, Days day, boolean showCheckBox, String selectedBus, String selectedDirection) {
		this(context, resource, new ArrayList<>(Arrays.asList(objects)), day, showCheckBox, selectedBus, selectedDirection);
	}


	private BusArrayAdapter(Context context, int resource, ArrayList<String> objects, Days day, boolean showCheckBox, String selectedBus, String selectedDirection) {
		super(context, resource, R.id.item_text, objects);
		m_fullList = new ArrayList(objects);
		m_day = day;
		m_showCheckBox = showCheckBox;
		m_selectedBus = selectedBus;
		m_selectedDirection = selectedDirection;
		this.pruneListForFavorites();
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout linearLayout = (LinearLayout)super.getView(position, convertView, parent);
		CheckBox checkBox = (CheckBox)linearLayout.getChildAt(1);
		TextView textView = (TextView)linearLayout.getChildAt(0);

		String text = textView.getText().toString();

		//Format the text
		textView.setContentDescription(text);
		String[] parts = text.split("\n");
		if (parts.length == 2)
		{
			formatBusRoute(textView, parts[1]);
		}

		else
		{
			formatDirectionName(textView, text);
		}

		//Format the checkbox
		if (m_showCheckBox)
		{
			String key = CommonUtilities.generateKey(m_selectedBus, m_selectedDirection, textView.getContentDescription().toString());
			boolean checked = CommonUtilities.isFavorite(key);
			checkBox.setChecked(checked);

			if (checked)
				checkBox.setButtonDrawable(R.drawable.ic_star_black_24dp);
			else
				checkBox.setButtonDrawable(R.drawable.ic_star_border_black_24dp);
		}
		else
		{
			checkBox.setClickable(false);
			checkBox.setVisibility(View.INVISIBLE);
		}

		//Set the font size
		if (CommonUtilities.isFontSizeOverride()) {
			textView.setTextSize(CommonUtilities.getFontSize());
		}

		if (CommonUtilities.isStringTime(text))
		{
			formatBusTime(textView, text);
		}
		else {
			textView.setTextColor(Color.BLACK);
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
	
	public View formatBusRoute(TextView view, String busNameStr) {
		Spannable busName = new SpannableString(busNameStr);

		//Set the bus direction text to black
		busName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, busName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		view.setText(busName);
		view.setTextColor(CommonUtilities.getEnabledTextColor());
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

		String favoriteKey = CommonUtilities.generateKey(m_selectedBus, m_selectedDirection,"");
		if (CommonUtilities.showFavorites(favoriteKey))
		{
			for (int i =0; i < m_fullList.size(); ++i)
			{
				String elem = m_fullList.get(i);
				String key = CommonUtilities.generateKey(m_selectedBus, m_selectedDirection, elem);

				if (CommonUtilities.isFavorite(key)) {
					this.add(elem);
				}

			}
		}
		else
		{
			this.addAll(m_fullList);
		}
	}
}
