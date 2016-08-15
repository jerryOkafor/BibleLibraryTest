package com.bellman.bible.android.view.activity.speak.actionbarbuttons;

import android.support.v4.view.MenuItemCompat;

import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.speak.SpeakControl;
import com.bellman.bible.android.view.activity.base.actionbar.QuickActionButton;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public abstract class SpeakActionBarButtonBase extends QuickActionButton {

    protected static final int SPEAK_START_PRIORITY = 10;
    @SuppressWarnings("unused")
    private static final String TAG = "Speak";
    private SpeakControl speakControl = ControlFactory.getInstance().getSpeakControl();

    public SpeakActionBarButtonBase() {
        // overridden by canShow
        super(MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    }

    /**
     * return true if Speak button can be shown
     */
    public boolean canSpeak() {
        boolean canspeakDoc = speakControl.isCurrentDocSpeakAvailable();
        return //isEnoughRoomInToolbar() &&
                canspeakDoc;
    }

    protected SpeakControl getSpeakControl() {
        return speakControl;
    }
}
