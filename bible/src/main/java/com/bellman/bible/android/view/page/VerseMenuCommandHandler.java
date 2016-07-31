package com.bellman.bible.android.view.page;

import android.app.Activity;
import android.content.Intent;


import com.bellman.bible.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.page.PageControl;

import org.crosswire.jsword.passage.VerseRange;

/** Handle requests from the selected verse action menu
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class VerseMenuCommandHandler {

	private final Activity mainActivity;

	private final PageControl pageControl;

//	private final IntentHelper intentHelper = new IntentHelper();

	private static final String TAG = "VerseMenuCommandHandler";

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
//			int requestCode = ActivityBase.STD_REQUEST_CODE;

//			// Handle item selection
//			switch (menuItemId) {
//				case R.id.compareTranslations:
//					handlerIntent = new Intent(mainActivity, CompareTranslations.class);
//					isHandled = true;
//					break;
//				case R.id.notes:
//					handlerIntent = new Intent(mainActivity, FootnoteAndRefActivity.class);
//					isHandled = true;
//					break;
//				case R.id.add_bookmark:
//				case R.id.delete_bookmark:
//					ControlFactory.getInstance().getBookmarkControl().toggleBookmarkForVerseRange(verseRange);
//					// refresh view to show new bookmark icon
//					PassageChangeMediator.getInstance().forcePageUpdate();
//					isHandled = true;
//					break;
//				case R.id.myNoteAddEdit:
//					ControlFactory.getInstance().getMyNoteControl().showMyNote(verseRange);
//					isHandled = true;
//					break;
//				case R.id.copy:
//					pageControl.copyToClipboard(verseRange);
//					isHandled = true;
//					break;
//				case R.id.shareVerse:
//					pageControl.shareVerse(verseRange);
//					isHandled = true;
//					break;
//			}
//
//			if (handlerIntent!=null) {
//				intentHelper.updateIntentWithVerseRange(handlerIntent, verseRange);
//				mainActivity.startActivityForResult(handlerIntent, requestCode);
//			}
    	}

        return isHandled;
    }
 }
