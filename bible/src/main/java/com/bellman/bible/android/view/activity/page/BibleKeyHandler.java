package com.bellman.bible.android.view.activity.page;

import android.util.Log;
import android.view.KeyEvent;

import com.bellman.bible.android.control.ControlFactory;

/**
 * KeyEvent.KEYCODE_DPAD_LEFT was being swallowed by the BibleView after scrolling down (it gained focus)
 * so this class implements common key handling both for BibleView and MainBibleActivity
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BibleKeyHandler {

    private static final BibleKeyHandler singleton = new BibleKeyHandler();
    private static final String TAG = "BibleKeyHandler";
    // prevent too may scroll events causing multi-page changes
    private long lastHandledDpadEventTime = 0;

    private BibleKeyHandler() {
    }

    public static BibleKeyHandler getInstance() {
        return singleton;
    }

    /**
     * handle DPAD keys
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            Log.d(TAG, "D-Pad");
            // prevent too may scroll events causing multi-page changes
            if (event.getEventTime() - lastHandledDpadEventTime > 1000) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().next();
                } else {
                    ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().previous();
                }
                lastHandledDpadEventTime = event.getEventTime();
                return true;
            }
        }
        return false;
    }


}
