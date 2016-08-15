package com.bellman.bible.android.view.activity.page.screen;

import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.page.window.Window;
import com.bellman.bible.android.control.page.window.WindowControl;

public class WindowMenuCommandHandler {

    private WindowControl windowControl;

    public WindowMenuCommandHandler() {
        windowControl = ControlFactory.getInstance().getWindowControl();
    }

    /**
     * on Click handlers
     */
    public boolean handleMenuRequest(MenuItem menuItem) {
        boolean isHandled = false;

        // Handle item selection
        Window activeWindow = windowControl.getActiveWindow();
        int id = menuItem.getItemId();
        if (id == R.id.windowNew) {
            windowControl.addNewWindow();
            isHandled = true;
        } else if (id == R.id.windowMaximise) {
            if (activeWindow.isMaximised()) {
                windowControl.unmaximiseWindow(activeWindow);
                menuItem.setChecked(false);
            } else {
                windowControl.maximiseWindow(activeWindow);
                menuItem.setChecked(true);
            }
            isHandled = true;
        } else if (id == R.id.windowMinimise) {
            windowControl.minimiseCurrentWindow();
            isHandled = true;
        } else if (id == R.id.windowClose) {
            windowControl.closeCurrentWindow();
            isHandled = true;
        } else if (id == R.id.windowMoveFirst) {
            windowControl.moveCurrentWindowToFirst();
            isHandled = true;
        } else if (id == R.id.windowSynchronise) {
            if (activeWindow.isSynchronised()) {
                windowControl.unsynchroniseCurrentWindow();
                menuItem.setChecked(false);
            } else {
                windowControl.synchroniseCurrentWindow();
                menuItem.setChecked(true);
            }
            isHandled = true;

        }

        return isHandled;
    }
}
