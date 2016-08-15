package com.bellman.bible.android.view.activity.page.actionbar;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.view.Menu;

import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.android.view.activity.base.actionbar.ActionBarManager;
import com.bellman.bible.android.view.activity.base.actionbar.DefaultActionBarManager;
import com.bellman.bible.android.view.activity.speak.actionbarbuttons.SpeakActionBarButton;
import com.bellman.bible.android.view.activity.speak.actionbarbuttons.SpeakStopActionBarButton;
import com.bellman.bible.service.device.speak.event.SpeakEvent;
import com.bellman.bible.service.device.speak.event.SpeakEventListener;
import com.bellman.bible.service.device.speak.event.SpeakEventManager;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BibleActionBarManager extends DefaultActionBarManager implements ActionBarManager {

    private HomeTitle homeTitle = new HomeTitle();

    private BibleActionBarButton bibleActionBarButton = new BibleActionBarButton();
    private CommentaryActionBarButton commentaryActionBarButton = new CommentaryActionBarButton();
    private DictionaryActionBarButton dictionaryActionBarButton = new DictionaryActionBarButton();
    private StrongsActionBarButton strongsActionBarButton = new StrongsActionBarButton();

    private SpeakActionBarButton speakActionBarButton = new SpeakActionBarButton();
    private SpeakStopActionBarButton stopActionBarButton = new SpeakStopActionBarButton();

    public BibleActionBarManager() {
        // the manager will also instantly fire a catch-up event to ensure state is current
        SpeakEventManager.getInstance().addSpeakEventListener(new SpeakEventListener() {
            @Override
            public void speakStateChange(SpeakEvent e) {
                updateButtons();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.bellman.bible.android.view.activity.page.actionbar.ActionBarManager#prepareOptionsMenu(android.app.Activity, android.view.Menu, android.support.v7.app.ActionBar, com.bellman.bible.android.view.activity.page.MenuCommandHandler)
     */
    @Override
    public void prepareOptionsMenu(Activity activity, Menu menu, ActionBar actionBar) {
        super.prepareOptionsMenu(activity, menu, actionBar);

        homeTitle.addToBar(actionBar, activity);

        // order is important to keep bible, cmtry, ... in same place on right
        stopActionBarButton.addToMenu(menu);
        speakActionBarButton.addToMenu(menu);

        strongsActionBarButton.addToMenu(menu);
        dictionaryActionBarButton.addToMenu(menu);
        commentaryActionBarButton.addToMenu(menu);
        bibleActionBarButton.addToMenu(menu);
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
                homeTitle.update();

                bibleActionBarButton.update();
                commentaryActionBarButton.update();
                dictionaryActionBarButton.update();
                strongsActionBarButton.update();

                speakActionBarButton.update();
                stopActionBarButton.update();
            }
        });
    }
}
