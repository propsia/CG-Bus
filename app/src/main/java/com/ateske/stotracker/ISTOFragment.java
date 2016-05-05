package com.ateske.stotracker;

public interface ISTOFragment {
    boolean showFavoritesToggle();
    String getTitle();
    boolean back();
    boolean isBackAllowed();
}
