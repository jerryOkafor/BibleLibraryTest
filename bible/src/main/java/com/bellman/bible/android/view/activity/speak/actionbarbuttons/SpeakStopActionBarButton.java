package com.bellman.bible.android.view.activity.speak.actionbarbuttons;

import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.service.common.CommonUtils;

/**
 * Stop Speaking
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class SpeakStopActionBarButton extends SpeakActionBarButtonBase {

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        getSpeakControl().stop();

        return true;
    }

    @Override
    protected String getTitle() {
        return CommonUtils.getResourceString(R.string.stop);
    }

    @Override
    protected int getIcon() {
        return R.drawable.ic_media_stop;
    }

    @Override
    protected boolean canShow() {
        return isSpeakMode();
    }
}
