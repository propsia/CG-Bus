package com.ateske.stotracker;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.support.design.widget.TabLayout;

/**
 * A basic sample which shows how to use {@link com.example.android.common.view.SlidingTabLayout}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class RouteViewFragment extends Fragment
        implements OnItemClickListener, View.OnClickListener, TabLayout.OnTabSelectedListener, ISTOFragment {

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    private ApplicationController m_controller;
    private ViewContext m_viewContext;
    private View m_view;
    private TabLayout m_tabLayout;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        m_view = view;
        m_controller = new ApplicationController(getActivity());
        refreshViewContext();
    }

    private void refreshViewContext()
    {
        refreshViewContext(true);
    }
    private void refreshViewContext(boolean refreshContext) {

        if (refreshContext)
            m_viewContext = m_controller.getCurrentView();

        //Prepare the adapters for each  tab
        BusArrayAdaptor[] arrayAdaptors = new BusArrayAdaptor[m_viewContext.listViews.size()];
        for (int i = 0; i < arrayAdaptors.length; ++i)
        {
            String busNumber = null;
            String busDirection = null;
            String[] content = m_viewContext.listViews.get(i);
            ApplicationController.Days day = ApplicationController.Days.values()[i];

            if (i < m_viewContext.tabTitles.length)
            {
                String[] busNumberSplit = m_viewContext.viewTitle.split(": ");
                busNumber = busNumberSplit[busNumberSplit.length-1];
                busDirection = m_viewContext.tabTitles[i];
            }

            arrayAdaptors[i] = new BusArrayAdaptor(getActivity(), R.layout.list_item_star,content ,day, m_viewContext.favoritesEnabled, busNumber, busDirection, this);
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) m_view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter(this, arrayAdaptors, m_viewContext.tabTitles, m_viewContext.listScrollPosition));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        int tabToSelectIndex = m_viewContext.tabToSelect;
        m_tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        if (m_viewContext.tabTitles.length > 0) {
            m_tabLayout.setupWithViewPager(mViewPager);
            m_tabLayout.setVisibility(View.VISIBLE);
            m_tabLayout.addOnTabSelectedListener(this);

            TabLayout.Tab tabToSelect = m_tabLayout.getTabAt(tabToSelectIndex);
            tabToSelect.select();
            onTabSelected(tabToSelect);
        } else {
            m_tabLayout.setVisibility(View.GONE);
            m_tabLayout.setEnabled(false);
        }

        //Set up the action bar
        getActivity().invalidateOptionsMenu();
    }

    public boolean showFavoritesToggle()
    {
        return m_viewContext.favoritesEnabled;
    }

    public String getTitle()
    {
        return m_viewContext.viewTitle;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) ((LinearLayout)view).getChildAt(0);
        String text = textView.getText().toString();
        m_controller.addSelectionInfo(text,position);
        refreshViewContext();
    }

    @Override
    public void onClick(View v) {
        CheckBox checkBox = (CheckBox)v;
        LinearLayout linearLayout = (LinearLayout)checkBox.getParent();
        TextView textView = (TextView) linearLayout.getChildAt(0);
        String text = textView.getText().toString();
        String tab = null;
        int selectedTab = m_tabLayout.getSelectedTabPosition();
        if (selectedTab >= 0)
            tab = m_tabLayout.getTabAt(selectedTab).getText().toString();

        if(checkBox.isChecked()){
            m_controller.setFavorite(text, tab, true);
            checkBox.setButtonDrawable(CommonUtilities.getIcon(CommonUtilities.ICONS.FAVORITE));
        }
        else{
            m_controller.setFavorite(text, tab, false);
            checkBox.setButtonDrawable(CommonUtilities.getIcon(CommonUtilities.ICONS.NOT_FAVORITE));
        }
    }

    public boolean back(){
        boolean back = m_controller.back();
        refreshViewContext();
        return back;
    }

    public boolean isBackAllowed(){
        return m_controller.isBackAllowed();
    }

    public void onFavoritesToggle()
    {
        String currentTab = getCurrentTab();
        m_controller.toggleShowFavorites(currentTab);
        refreshViewContext(false);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        m_controller.setSelectedTab(tab.getText().toString(), tab.getPosition());
        m_viewContext.tabToSelect = tab.getPosition();
        getActivity().invalidateOptionsMenu();
    }

    @Override public void onTabUnselected(TabLayout.Tab tab) {}
    @Override public void onTabReselected(TabLayout.Tab tab) {}

    public int getToggleIcon() {
        String tab = getCurrentTab();
        if (m_controller.getOnlyShowFavorites(tab))
            return CommonUtilities.getIcon(CommonUtilities.ICONS.SHOW_FAVORITES);
        else
            return CommonUtilities.getIcon(CommonUtilities.ICONS.SHOW_ALL);
    }

    private String getCurrentTab(){
        int selectedTabPosition = m_tabLayout.getSelectedTabPosition();
        if (selectedTabPosition == -1)
            return null;
        TabLayout.Tab currentTab = m_tabLayout.getTabAt(selectedTabPosition);
        return currentTab.getText().toString();
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     */
    class SamplePagerAdapter extends PagerAdapter {
        private RouteViewFragment m_listener;
        private String[] m_tabTitles;
        private ArrayAdapter<String>[] m_arrayAdapters;
        private int[] m_defaultPositions;

        public SamplePagerAdapter(RouteViewFragment listener, ArrayAdapter<String>[] arrayAdapters, String[] tabTitles, int[] defaultPositions)
        {
            m_listener = listener;
            m_tabTitles = tabTitles;
            m_arrayAdapters = arrayAdapters;
            m_defaultPositions = defaultPositions;
        }

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return Math.max(m_tabTitles.length,1);
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {return m_tabTitles[position];}

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.list, container, false);
            ListView listView = (ListView) layout.getChildAt(0);
            listView.setOnItemClickListener(m_listener);

            listView.setAdapter(m_arrayAdapters[position]);

            ViewGroup viewGroup = (ViewGroup) listView.getParent();
            if (viewGroup != null)
                viewGroup.removeView(listView);

            container.addView(listView);

            listView.setSelection(m_defaultPositions[position]);

            return listView;
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}