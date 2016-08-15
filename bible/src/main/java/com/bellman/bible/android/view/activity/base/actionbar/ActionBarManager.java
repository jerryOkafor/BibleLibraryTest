package com.bellman.bible.android.view.activity.base.actionbar;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.view.Menu;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public interface ActionBarManager {

    void prepareOptionsMenu(Activity activity, Menu menu, ActionBar actionBar);

    void updateButtons();

}