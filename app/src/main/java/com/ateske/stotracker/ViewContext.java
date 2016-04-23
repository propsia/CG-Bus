package com.ateske.stotracker;

import java.util.List;

public class ViewContext {
	public List<String[]> listViews;
	public String[] tabTitles;
	public String viewTitle;
	public boolean backPossible = true;
	public int[] listScrollPosition = new int[]{0,0,0};
	public int tabToSelect = 0;
	public boolean favoritesEnabled = false;
}
