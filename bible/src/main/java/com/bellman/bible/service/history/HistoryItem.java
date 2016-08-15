package com.bellman.bible.service.history;

import com.bellman.bible.android.control.page.window.Window;

/**
 * An item in the History List
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public interface HistoryItem {

    CharSequence getDescription();

    Window getScreen();

    // do back to the state at this point
    void revertTo();

}