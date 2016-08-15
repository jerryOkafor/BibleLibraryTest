package com.bellman.bible.service.history;

import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.page.window.Window;
import com.bellman.bible.android.control.page.window.WindowControl;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public abstract class HistoryItemBase implements HistoryItem {

    private static WindowControl windowControl = ControlFactory.getInstance().getWindowControl();
    private Window window;

    public HistoryItemBase() {
        super();
        this.window = windowControl.getActiveWindow();
    }

    @Override
    public Window getScreen() {
        return window;
    }
}
