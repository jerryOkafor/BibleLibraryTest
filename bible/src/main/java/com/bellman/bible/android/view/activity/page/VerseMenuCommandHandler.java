package com.bellman.bible.android.view.activity.page;

import android.app.Activity;
import android.content.Intent;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.PassageChangeMediator;
import com.bellman.bible.android.control.page.PageControl;
import com.bellman.bible.android.view.activity.base.ActivityBase;
import com.bellman.bible.android.view.activity.base.IntentHelper;
import com.bellman.bible.android.view.activity.comparetranslations.CompareTranslations;
import com.bellman.bible.android.view.activity.footnoteandref.FootnoteAndRefActivity;

import org.crosswire.jsword.passage.VerseRange;

/**
 * Handle requests from the selected verse action menu
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class VerseMenuCommandHandler {

    private static final String TAG = "VerseMenuCommandHandler";
    private final Activity mainActivity;
    private final PageControl pageControl;
    private final IntentHelper intentHelper = new IntentHelper();

    public VerseMenuCommandHandler(Activity mainActivity, PageControl pageControl) {
        super();
        this.mainActivity = mainActivity;
        this.pageControl = pageControl;
    }

    /**
     * on Click handler for Selected verse menu
     */
    public boolean handleMenuRequest(int menuItemId, VerseRange verseRange) {
        boolean isHandled = false;

        {
            Intent handlerIntent = null;
            int requestCode = ActivityBase.STD_REQUEST_CODE;

            // Handle item selection
            if (menuItemId == R.id.compareTranslations) {
                handlerIntent = new Intent(mainActivity, CompareTranslations.class);
                isHandled = true;
            } else if (menuItemId == R.id.notes) {
                handlerIntent = new Intent(mainActivity, FootnoteAndRefActivity.class);
                isHandled = true;
            } else if (menuItemId == R.id.add_bookmark || menuItemId == R.id.delete_bookmark) {

                ControlFactory.getInstance().getBookmarkControl().toggleBookmarkForVerseRange(verseRange);
                // refresh view to show new bookmark icon
                PassageChangeMediator.getInstance().forcePageUpdate();
                isHandled = true;
            } else if (menuItemId == R.id.myNoteAddEdit) {
                ControlFactory.getInstance().getMyNoteControl().showMyNote(verseRange);
                isHandled = true;
            } else if (menuItemId == R.id.copy) {
                pageControl.copyToClipboard(verseRange);
                isHandled = true;
            } else if (menuItemId == R.id.shareVerse) {
                pageControl.shareVerse(verseRange);
                isHandled = true;

            }

            if (handlerIntent != null) {
                intentHelper.updateIntentWithVerseRange(handlerIntent, verseRange);
                mainActivity.startActivityForResult(handlerIntent, requestCode);
            }
        }

        return isHandled;
    }
}
