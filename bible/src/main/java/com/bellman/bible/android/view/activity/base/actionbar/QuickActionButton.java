package com.bellman.bible.android.view.activity.base.actionbar;

import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.speak.SpeakControl;
import com.bellman.bible.service.common.CommonUtils;

import java.lang.ref.WeakReference;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
abstract public class QuickActionButton implements OnMenuItemClickListener {

    private static final int NO_ICON = 0;
    private static int nextItemId = 100;
    private MenuItem menuItem;
    private int showAsActionFlags;
    // weak to prevent ref from this (normally static) menu preventing gc of book selector
    private WeakReference<OnClickListener> weakOnClickListener;
    private int thisItemId = nextItemId++;
    private SpeakControl speakControl = ControlFactory.getInstance().getSpeakControl();

    public QuickActionButton(int showAsActionFlags) {
        this.showAsActionFlags = showAsActionFlags;
    }

    abstract protected String getTitle();

    abstract protected boolean canShow();

    public void addToMenu(Menu menu) {
        if (menuItem == null || (menu.findItem(thisItemId) == null)) {
            menuItem = menu.add(Menu.NONE, thisItemId, Menu.NONE, "");
            MenuItemCompat.setShowAsAction(menuItem, showAsActionFlags);
            menuItem.setOnMenuItemClickListener(this);
            update(menuItem);
        }
    }

    public void update() {
        if (menuItem != null) {
            update(menuItem);
        }
    }

    protected void update(MenuItem menuItem) {
        // canShow means must show because we rely on AB logic
        menuItem.setVisible(canShow());

        menuItem.setTitle(getTitle());

        int iconResId = getIcon();
        if (iconResId != NO_ICON) {
            menuItem.setIcon(iconResId);
        }
    }

    /**
     * Provide the possibility of handling clicks outside of the button e.g. in Activity
     */
    public void registerClickListener(OnClickListener onClickListener) {
        this.weakOnClickListener = new WeakReference<OnClickListener>(onClickListener);
    }

    /**
     * This is sometimes overridden but can be used to handle clicks in the Activity
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        OnClickListener onClickListener = weakOnClickListener.get();
        if (onClickListener != null) {
            onClickListener.onClick(null);
        }
        update();
        return true;
    }


    protected int getIcon() {
        return NO_ICON;
    }

    protected boolean isWide() {
        return 4 < CommonUtils.getResourceInteger(R.integer.number_of_quick_buttons);
    }

    protected boolean isSpeakMode() {
        return speakControl.isSpeaking() || speakControl.isPaused();
    }
}
