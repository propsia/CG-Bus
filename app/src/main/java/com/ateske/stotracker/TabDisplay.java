package com.ateske.stotracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ateske.stotracker.ApplicationController.Days;

import org.xmlpull.v1.XmlPullParserException;

import java.util.List;

public class TabDisplay extends ActionBarActivity implements
		ActionBar.TabListener, OnItemClickListener {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private static ApplicationController m_controller;
	private enum AnimationTypes {NONE, FORWARD, BACK};
    private ViewContext m_viewContext;
    MenuItem m_filterButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get user configuration
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        CommonUtilities.setPreferenceManager(sharedPref);
        
        //Set up the theme
        setTheme(CommonUtilities.getSelectedTheme());
        
        //Initialize the controller
        m_controller = new ApplicationController(this);
        
        //Create a dummy page to hold empty content while
        //we load the first page
        mViewPager = new ViewPager(this);
        setContentView(mViewPager);
		       
        //Load the initial view of the app
		renderView(AnimationTypes.NONE);
		
		if (android.os.Build.VERSION.SDK_INT  < 14)
			getActionBar().hide();
		
	}

	protected void onResume(){
		super.onResume();
		renderView(AnimationTypes.NONE);
	}
    public void renderView(AnimationTypes animation)
    {
        renderView(animation, true);
    }
	public void renderView(AnimationTypes animation, boolean updateContext)
	{		
		//Get the next view to render
        if (updateContext) {
            //try {
                m_viewContext = m_controller.getCurrentView();
            //} catch (XmlPullParserException e) {
                //System.err.println("An exception occurred while getting the view");
                //System.exit(-1);
            //}
        }
		
		// Create the adapter that will return a fragment for each of the tabs
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(),this, m_viewContext.listViews, m_viewContext.tabTitles, m_viewContext.listScrollPosition, m_viewContext.favoritesEnabled);
		
		// Set up the ViewPager with the sections adapter.
		ViewPager newView = new ViewPager(this);
		newView.setId(R.id.random_id);
		newView.setAdapter(mSectionsPagerAdapter);
		
		//Start the animation
		if (animation == AnimationTypes.FORWARD)
		{		
			mViewPager.startAnimation(AnimationUtils.loadAnimation(this, R.anim.forward_out));
        	newView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.forward_in));
		}
		if (animation == AnimationTypes.BACK)
		{
			mViewPager.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_out));
			newView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_in));
		}
		
		mViewPager = newView;
		setContentView(newView);
		
		//Set up the action bar with appropriate tabs
		final ActionBar actionBar = getSupportActionBar();
		
		actionBar.removeAllTabs();
		if (m_viewContext.tabTitles.length > 0)
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		else
		{
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		
		//Are we allowed to hit back via the menu bar?
        actionBar.setDisplayHomeAsUpEnabled(m_viewContext.backPossible);
        if(Build.VERSION.SDK_INT >= 14 ){
        	actionBar.setHomeButtonEnabled(m_viewContext.backPossible);
        }
		
		// When swiping between different sections, select the corresponding
		newView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

        int tabToSelect = m_viewContext.tabToSelect;
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getTabCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			Tab newTab = actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this);
			
			actionBar.addTab(newTab);
			if (tabToSelect == i)
				actionBar.selectTab(actionBar.getTabAt(i));
		}
		
		//Set title
		setTitle(m_viewContext.viewTitle);
		actionBar.setTitle(m_viewContext.viewTitle);

        //Set action icon status
        if (m_filterButton != null)
        {
            updateActionIcons(m_viewContext.favoritesEnabled);
        }
		
	}

    private void updateActionIcons(boolean favoritesEnabled){
        if (favoritesEnabled)
        {
            m_filterButton.setVisible(true);

			String tab = getCurrentTab();
            if (m_controller.getOnlyShowFavorites(tab))
                m_filterButton.setIcon(CommonUtilities.getIcon(CommonUtilities.ICONS.SHOW_FAVORITES));
            else
                m_filterButton.setIcon(CommonUtilities.getIcon(CommonUtilities.ICONS.SHOW_ALL));
        }
        else
        {
            m_filterButton.setVisible(false);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        m_filterButton = menu.findItem(R.id.favorites_toggle);

        //try{
			updateActionIcons(m_controller.getCurrentView().favoritesEnabled);
		//}
        //catch(XmlPullParserException e){}

        //MenuItem settingsButton = menu.findItem(R.id.action_settings);
        //settingsButton.setIcon(CommonUtilities.getIcon(CommonUtilities.ICONS.SETTINGS));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == android.R.id.home){
//        	onBackPressed();
//        	return true;
//        }
//        else if (id == R.id.action_settings)
//        {
//			finish();
//			Intent intent = new Intent(this, SettingsActivity.class);
//			startActivity(intent);
//        }
//        else if (id == R.id.favorites_toggle)
//        {
//			String tab = getCurrentTab();
//            m_controller.toggleShowFavorites(tab);
//            renderView(AnimationTypes.NONE, false);
//        }
//        return super.onOptionsItemSelected(item);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		m_controller.setSelectedTab(tab.getText().toString(), tab.getPosition());
        m_viewContext.tabToSelect = tab.getPosition();
        updateActionIcons(m_viewContext.favoritesEnabled);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
		
		List<String[]> m_pageContents;
		String[] m_tabTitles;
		int[] m_listPosition;
		OnItemClickListener m_clickListener;
        boolean m_allowFavorites;

		public SectionsPagerAdapter(FragmentManager fm, OnItemClickListener listener, 
				List<String[]> pageContents, String[] tabTitles, int[] listPosition, boolean allowFavorites) {
			super(fm);
			
			if (pageContents.size() != 1 && pageContents.size() != tabTitles.length)
				throw new IllegalArgumentException("In tab mode, the number of tab titles and pages must be the same");
			
			m_pageContents = pageContents;
			m_tabTitles = tabTitles;
			m_clickListener = listener;
			m_listPosition = listPosition;
            m_allowFavorites = allowFavorites;
			
		}

		@Override
		public Fragment getItem(int position) {
			Days day = null;
			if (m_pageContents.size() == 3)
			{
				switch (position)
				{
				case 0:
					day = Days.WEEKDAY;
					break;
				case 1:
					day = Days.SATURDAY;
					break;
				case 2:
					day = Days.SUNDAY;
				}
			}

            String[] busNumberSplit = m_viewContext.viewTitle.split(": ");
            String busNumber = null;
            String busDirection = null;

            if (position < m_viewContext.tabTitles.length)
            {
                busNumber = busNumberSplit[busNumberSplit.length-1];
                busDirection = m_viewContext.tabTitles[position];
            }

			PlaceholderFragment fragment = PlaceholderFragment.newInstance(m_pageContents.get(position), m_listPosition[position], day, m_allowFavorites,busNumber, busDirection );
			fragment.setOnItemClickListener(m_clickListener);
			return fragment;
		}

		@Override
		public int getCount() {
			return m_pageContents.size();
		}
		
		public int getTabCount() {
			return m_tabTitles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return m_tabTitles[position];
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends ListFragment {
		
		private static final String LIST_CONTENTS = "list_contents";
		private static final String LIST_POSITION = "list_position";
		private static final String SELECTED_DAY = "selected_day";
        private static final String ALLOW_FAVORITES = "allow_favorites";
        private static final String SELECTED_BUS = "selected_bus";
        private static final String SELECTED_DIRECTION = "selected_direction";
		
		private OnItemClickListener m_listener;
		private int m_defaultPosition;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(String[] listItems, int listPosition, Days day, boolean allowFavorites, String selectedBus, String selectedDirection) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putStringArray(LIST_CONTENTS, listItems);
			args.putInt(LIST_POSITION, listPosition);
			args.putSerializable(SELECTED_DAY, day);
            args.putBoolean(ALLOW_FAVORITES, allowFavorites);
            args.putString(SELECTED_BUS, selectedBus);
            args.putString(SELECTED_DIRECTION, selectedDirection);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle args = this.getArguments();
			String[] values = (String[]) args.get(LIST_CONTENTS);
			Days day = (Days) args.get(SELECTED_DAY);
            boolean allowFavorites = (boolean)args.get(ALLOW_FAVORITES);
            String selectedBus = (String) args.get(SELECTED_BUS);
            String selectedDirection = (String) args.get(SELECTED_DIRECTION);
			//BusArrayAdaptor adapter = new BusArrayAdaptor(getActivity().getBaseContext(), R.layout.list_item_star, values, day, allowFavorites,selectedBus, selectedDirection);
			//setListAdapter(adapter);
			m_defaultPosition = (int)args.get(LIST_POSITION);
			
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState)
		{
			getListView().setSelection(m_defaultPosition);
			getListView().setFastScrollEnabled(true);
		}
		
		public void setOnItemClickListener(OnItemClickListener listener)
		{
			m_listener = listener;
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			m_listener.onItemClick(l, v, position, id);
		}

	}

	public void checkboxClicked(View v) {
		CheckBox checkBox = (CheckBox)v;
		LinearLayout linearLayout = (LinearLayout)checkBox.getParent();
		TextView textView = (TextView) linearLayout.getChildAt(0);
		String text = textView.getContentDescription().toString();
		String tab = getCurrentTab();

		if(checkBox.isChecked()){
			m_controller.setFavorite(text, tab, true);
            checkBox.setButtonDrawable(CommonUtilities.getIcon(CommonUtilities.ICONS.FAVORITE));
		}
		else{
            m_controller.setFavorite(text, tab, false);
            checkBox.setButtonDrawable(CommonUtilities.getIcon(CommonUtilities.ICONS.NOT_FAVORITE));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		String selection = (String) listView.getItemAtPosition(position);
		if (m_controller.addSelectionInfo(selection, position))
			renderView(AnimationTypes.FORWARD);		
	}
	
    @Override
    public void onBackPressed() {
    	if (m_controller.back()){
    		renderView(AnimationTypes.BACK);
    	}
    	else
    		finish();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
    }

	private String getCurrentTab(){
		String tab = null;
		int tabIndex = mViewPager.getCurrentItem();
		if (tabIndex < m_viewContext.tabTitles.length)
			tab = m_viewContext.tabTitles[tabIndex];
		return tab;
	}
}