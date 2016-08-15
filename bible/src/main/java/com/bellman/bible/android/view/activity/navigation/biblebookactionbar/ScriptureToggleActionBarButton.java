package com.bellman.bible.android.view.activity.navigation.biblebookactionbar;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.navigation.NavigationControl;
import com.bellman.bible.android.view.activity.base.actionbar.ToggleActionBarButton;
import com.bellman.bible.service.common.CommonUtils;

/**
 * Toggle between 66 Bible books and deuterocanonical books
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class ScriptureToggleActionBarButton extends ToggleActionBarButton {

    private NavigationControl navigationControl = ControlFactory.getInstance().getNavigationControl();

    public ScriptureToggleActionBarButton() {
        super(R.drawable.ic_action_new, R.drawable.ic_action_undo);
    }

    @Override
    protected String getTitle() {
        if (isOn()) {
            return CommonUtils.getResourceString(R.string.deuterocanonical);
        } else {
            return CommonUtils.getResourceString(R.string.bible);
        }
    }

    @Override
    protected boolean canShow() {
        return navigationControl.getBibleBooks(false).size() > 0;
    }
}
