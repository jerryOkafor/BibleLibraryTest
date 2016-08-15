package com.bellman.bible.android.view.activity.base;

/**
 * Base class for List activities.  Copied from Android source.
 * A copy of ListActivity from Android source which also extends ActionBarActivity and the Embedded Bible Activity base class.
 * <p>
 * ListActivity does not extend ActionBarActivity so when implementing ActionBar functionality I created this, which does.
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */

public class SharedActivityState {
    private static SharedActivityState singleton = new SharedActivityState();
    // show title bar state is shared by all Activity windows
    private boolean mFullScreen = false;

    public static SharedActivityState getInstance() {
        return singleton;
    }

    public void toggleFullScreen() {
        mFullScreen = !mFullScreen;
    }

    public boolean isFullScreen() {
        return mFullScreen;
    }
}
