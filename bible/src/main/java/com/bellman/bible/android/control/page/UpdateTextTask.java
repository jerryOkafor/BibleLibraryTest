package com.bellman.bible.android.control.page;

import android.os.AsyncTask;
import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.page.window.Window;
import com.bellman.bible.service.format.HtmlMessageFormatter;
import com.bellman.bible.util.SharedConstants;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
abstract public class UpdateTextTask extends AsyncTask<Window, Integer, String> {

	private static final String TAG = "UpdateTextTask";
	private Window window;
	private int verseNo = SharedConstants.NO_VALUE;
	private float yScreenOffsetRatio = SharedConstants.NO_VALUE;
	
    /** callbacks from base class when result is ready */
	abstract protected void showText(String text, Window screenToUpdate, int verseNo, float yOffsetRatio);
	
	@Override
	protected void onPreExecute() {
		//NOOP
	}
	
	@Override
    protected String doInBackground(Window... windows) {
        Log.d(TAG, "Loading html in background");
    	String text = "Error";
    	try {
    		window = windows[0];
    		CurrentPage currentPage = window.getPageManager().getCurrentPage(); 
    		Book document = currentPage.getCurrentDocument();
    		// if bible show whole chapter
    		Key key = currentPage.getKey();
    		// but allow for jump to specific verse e.g. after search result
    		if (currentPage instanceof CurrentBiblePage) {
    			verseNo = ((CurrentBiblePage)currentPage).getCurrentVerseNo();
    		} else {
    			yScreenOffsetRatio = currentPage.getCurrentYOffsetRatio();
    		}

            Log.d(TAG, "Loading document:"+document.getInitials()+" key:"+key.getOsisRef());
            
            text = currentPage.getCurrentPageContent();
            
    	} catch (Exception e) {
    		Log.e(TAG, "Error getting bible text", e);
    		//TODO use resource
    		text = HtmlMessageFormatter.format("Error getting bible text: "+e.getMessage());
    	} catch (OutOfMemoryError oom) {
    		Log.e(TAG, "Out of memory error", oom);
    		System.gc();
    		text = HtmlMessageFormatter.format(R.string.error_page_too_large);
    	}
    	return text;
    }

    protected void onPostExecute(String htmlFromDoInBackground) {
        Log.d(TAG, "Got html length "+htmlFromDoInBackground.length());
        showText(htmlFromDoInBackground, window, verseNo, yScreenOffsetRatio);
    }
}
