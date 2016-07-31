package com.bellman.bible.android.control.page;

import android.app.Activity;
import android.view.Menu;


import com.bellman.bible.service.common.ParseException;
import com.bellman.bible.service.format.Note;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.passage.Key;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public interface CurrentPage {

	public abstract String toString();

	public abstract BookCategory getBookCategory();

	public abstract Class<? extends Activity> getKeyChooserActivity();

	public abstract void next();

	public abstract void previous();
	
	/** get incremented key according to the type of page displayed - verse, chapter, ...
	 */
	public abstract Key getKeyPlus(int num);

	/** add or subtract a number of pages from the current position and return Page
	 */
	public Key getPagePlus(int num);

	/** set key without updating screens */
	public abstract void doSetKey(Key key);

	/** set key and update screens */
	public abstract void setKey(Key key);

	public abstract boolean isSingleKey();
	
	// bible and commentary share a key (verse)
	public boolean isShareKeyBetweenDocs();

	/** get current key
	 */
	public abstract Key getKey();
	
	/** get key for 1 verse instead of whole chapter if bible
	 */
	public abstract Key getSingleKey();
	
	public abstract Book getCurrentDocument();

	public abstract void setCurrentDocument(Book currentBible);

	public void setCurrentDocumentAndKey(Book doc, Key key);
	
	public boolean checkCurrentDocumentStillInstalled();

	/** get a page to display */
	public String getCurrentPageContent() throws ParseException;

	/** get footnotes */
	public List<Note> getCurrentPageFootnotesAndReferences() throws ParseException;

	public abstract void updateOptionsMenu(Menu menu);

	public abstract void restoreState(JSONObject state) throws JSONException;

	public abstract JSONObject getStateJson() throws JSONException;

	public abstract void setInhibitChangeNotifications(boolean inhibitChangeNotifications);

	public abstract boolean isInhibitChangeNotifications();

	public abstract boolean isSearchable();
	public abstract boolean isSpeakable();
	
	//screen offset as a percentage of total height of screen
	public float getCurrentYOffsetRatio();
	public void setCurrentYOffsetRatio(float currentYOffsetRatio);

}