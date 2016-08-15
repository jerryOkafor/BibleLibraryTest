package com.bellman.bible.android.view.activity.navigation.biblebookactionbar;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View.OnClickListener;

import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.android.view.activity.base.actionbar.ActionBarManager;
import com.bellman.bible.android.view.activity.base.actionbar.DefaultActionBarManager;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BibleBookActionBarManager extends DefaultActionBarManager implements ActionBarManager {

    private ScriptureToggleActionBarButton scriptureToggleActionBarButton;
    private SortActionBarButton sortActionBarButton;

    public BibleBookActionBarManager() {
        scriptureToggleActionBarButton = new ScriptureToggleActionBarButton();
        sortActionBarButton = new SortActionBarButton();
    }

    public void registerScriptureToggleClickListener(OnClickListener scriptureToggleClickListener) {
        scriptureToggleActionBarButton.registerClickListener(scriptureToggleClickListener);
    }

    public void setScriptureShown(boolean isScripture) {
        scriptureToggleActionBarButton.setOn(isScripture);
    }

    public SortActionBarButton getSortButton() {
        return sortActionBarButton;
    }

    /* (non-Javadoc)
     * @see com.bellman.bible.android.view.activity.page.actionbar.ActionBarManager#prepareOptionsMenu(android.app.Activity, android.view.Menu, android.support.v7.app.ActionBar, com.bellman.bible.android.view.activity.page.MenuCommandHandler)
     */
    @Override
    public void prepareOptionsMenu(Activity activity, Menu menu, ActionBar actionBar) {
        super.prepareOptionsMenu(activity, menu, actionBar);

        scriptureToggleActionBarButton.addToMenu(menu);
        sortActionBarButton.addToMenu(menu);
    }

    /* (non-Javadoc)
     * @see com.bellman.bible.android.view.activity.page.actionbar.ActionBarManager#updateButtons()
     */
    @Override
    public void updateButtons() {
        super.updateButtons();

        // this can be called on end of speech in non-ui thread
        CurrentActivityHolder.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scriptureToggleActionBarButton.update();
                sortActionBarButton.update();
            }
        });
    }
}
