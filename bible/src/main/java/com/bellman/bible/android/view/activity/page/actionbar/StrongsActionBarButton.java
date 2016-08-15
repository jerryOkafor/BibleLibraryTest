package com.bellman.bible.android.view.activity.page.actionbar;

import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.PassageChangeMediator;
import com.bellman.bible.android.control.document.DocumentControl;
import com.bellman.bible.android.view.activity.base.actionbar.QuickActionButton;
import com.bellman.bible.service.common.CommonUtils;

/**
 * Toggle Strongs numbers on/off
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class StrongsActionBarButton extends QuickActionButton {

    private DocumentControl documentControl = ControlFactory.getInstance().getDocumentControl();

    public StrongsActionBarButton() {
        // SHOW_AS_ACTION_ALWAYS is overriden by setVisible which depends on canShow() below
        // because when visible this button is ALWAYS on the Actionbar
        super(MenuItemCompat.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onMenuItemClick(MenuItem arg0) {
        // update the show-strongs pref setting according to the ToggleButton
        CommonUtils.getSharedPreferences().edit().putBoolean("show_strongs_pref", !isStrongsVisible()).commit();
        // redisplay the current page; this will also trigger update of all menu items
        PassageChangeMediator.getInstance().forcePageUpdate();

        return true;
    }

    private boolean isStrongsVisible() {
        return CommonUtils.getSharedPreferences().getBoolean("show_strongs_pref", true);
    }

    @Override
    protected String getTitle() {
        return CommonUtils.getResourceString(isStrongsVisible() ? R.string.strongs_toggle_button_on : R.string.strongs_toggle_button_off);
    }

    /**
     * return true if Strongs are relevant to this doc & screen
     * Don't show with speak button on narrow screen to prevent over-crowding
     */
    @Override
    protected boolean canShow() {
        return documentControl.isStrongsInBook() &&
                (isWide() || !isSpeakMode());
    }
}
