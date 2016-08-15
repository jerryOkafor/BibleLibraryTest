package com.bellman.bible.android.view.activity.base;

import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;

import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.view.activity.base.actionbar.ActionBarManager;
import com.bellman.bible.android.view.activity.base.actionbar.DefaultActionBarManager;

/**
 * Base class for activities with a custom title bar
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public abstract class CustomTitlebarActivityBase extends ActivityBase {

    protected static final int NO_OPTIONS_MENU = 0;
    private static final String TAG = "CustomTBarBase";
    private ActionBarManager actionBarManager;

    //TODO hourglass	private ProgressBar mProgressBarIndeterminate;
    private int optionsMenuId;

    public CustomTitlebarActivityBase() {
        this(new DefaultActionBarManager(), NO_OPTIONS_MENU);
    }

    public CustomTitlebarActivityBase(int optionsMenuId) {
        this(new DefaultActionBarManager(), optionsMenuId);
    }

    public CustomTitlebarActivityBase(ActionBarManager actionBarManager, int optionsMenuId) {
        this.actionBarManager = actionBarManager;
        this.optionsMenuId = optionsMenuId;
    }

    /**
     * custom title bar code to add the FEATURE_CUSTOM_TITLE just before setContentView
     * and set the new titlebar layout just after
     */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

//TODO hourglass        mProgressBarIndeterminate = (ProgressBar)findViewById(R.id.progressCircular);
    }

    /**
     * load the default menu items from xml config
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionsMenuId != NO_OPTIONS_MENU) {
            // Inflate the menu
            getMenuInflater().inflate(optionsMenuId, menu);
        }

        boolean showMenu = super.onCreateOptionsMenu(menu);

        return showMenu;
    }

    /**
     * Allow some menu items to be hidden or otherwise altered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        actionBarManager.prepareOptionsMenu(this, menu, getSupportActionBar());

        // must return true for menu to be displayed
        return true;
    }

    /**
     * Hide/show the actionbar and call base class to hide/show everything else
     */
    public void toggleFullScreen() {
        super.toggleFullScreen();

        if (!isFullScreen()) {
            Log.d(TAG, "Fullscreen off");
            getSupportActionBar().show();
        } else {
            Log.d(TAG, "Fullscreen on");
            getSupportActionBar().hide();
        }

        getContentView().requestLayout();
    }

    /**
     * Called whenever something like strong preferences have been changed by the user.  Should refresh the screen
     */
    protected void preferenceSettingsChanged() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // the title bar has different widths depending on the orientation
        updateActionBarButtons();
    }

    /**
     * update the quick links in the title bar
     */
    public void updateActionBarButtons() {
        actionBarManager.updateButtons();
    }

    //TODO move this somewhere appropriate or call PageControl directly

    /**
     * return true if Strongs numbers are shown
     */
    public boolean isStrongsShown() {
        return ControlFactory.getInstance().getPageControl().isStrongsShown();
    }

    public void setProgressBar(boolean on) {
//TODO hourglass		mProgressBarIndeterminate.setVisibility(on ? View.VISIBLE : View.GONE);
    }
}
