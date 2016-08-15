package com.bellman.bible.android.view.activity.speak.actionbarbuttons;

import android.util.Log;
import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.document.DocumentControl;
import com.bellman.bible.android.view.activity.base.Dialogs;
import com.bellman.bible.service.common.CommonUtils;

/**
 * Toggle Strongs numbers on/off
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class SpeakActionBarButton extends SpeakActionBarButtonBase {

    private static final String TAG = "SpeakActionBarButtonBas";
    private DocumentControl documentControl = ControlFactory.getInstance().getDocumentControl();

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        try {
            getSpeakControl().speakToggleCurrentPage();

            update(menuItem);
        } catch (Exception e) {
            Log.e(TAG, "Error toggling speech", e);
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
        }
        return true;
    }

    @Override
    protected String getTitle() {
        return CommonUtils.getResourceString(R.string.speak);
    }

    @Override
    protected int getIcon() {
        if (getSpeakControl().isSpeaking()) {
            return android.R.drawable.ic_media_pause;
        } else if (getSpeakControl().isPaused()) {
            return android.R.drawable.ic_media_play;
        } else {
            return R.drawable.ic_hearing_24dp;
        }
    }

    @Override
    protected boolean canShow() {
        // show if speakable or already speaking (to pause), and only if plenty of room
        return (super.canSpeak() || isSpeakMode()) &&
                (isWide() || !documentControl.isStrongsInBook() || isSpeakMode());
    }
}
