package com.bellman.bible.android.control.page;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.versification.ConvertibleVerseRange;
import com.bellman.bible.service.common.ParseException;
import com.bellman.bible.service.download.FakeSwordBookFactory;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.Versification;

import java.io.IOException;

/**
 * Provide information for My Note page
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class CurrentMyNotePage extends CurrentCommentaryPage implements CurrentPage {

    private static final String MY_NOTE_DUMMY_CONF = "[MyNote]\nDescription=My Note\nCategory=OTHER\nModDrv=zCom\nBlockType=CHAPTER\nLang=en\nEncoding=UTF-8\nLCSH=Bible--Commentaries.\nDataPath=./modules/comments/zcom/mynote/\nAbout=\nVersification=";
    private static final String TAG = "CurrentMyNotePage";
    private ConvertibleVerseRange currentNoteVerseRange;
    // just one fake book for every note
    private Book fakeMyNoteBook;
    private Versification fakeMyNoteBookVersification;

    /* default */ CurrentMyNotePage(CurrentBibleVerse currentVerse) {
        super(currentVerse);
    }

    @Override
    public String getCurrentPageContent() throws ParseException {
        return ControlFactory.getInstance().getMyNoteControl().getMyNoteTextByKey(getKey());
    }

    @Override
    public Book getCurrentDocument() {
        try {
            if (fakeMyNoteBook == null || fakeMyNoteBookVersification == null || !fakeMyNoteBookVersification.equals(getCurrentVersification())) {
                Versification v11n = getCurrentVersification();
                fakeMyNoteBook = FakeSwordBookFactory.createFakeRepoBook("My Note", MY_NOTE_DUMMY_CONF + v11n.getName(), "");
                fakeMyNoteBookVersification = v11n;
            }
        } catch (IOException | BookException e) {
            Log.e(TAG, "Error creating fake MyNote book", e);
        }
        return fakeMyNoteBook;
    }

    /**
     * can we enable the main menu search button
     */
    @Override
    public boolean isSearchable() {
        return false;
    }

    /**
     * can we enable the main menu Speak button
     */
    @Override
    public boolean isSpeakable() {
        //TODO doesn't work currently - enable later
        return false;
    }

    public BookCategory getBookCategory() {
        return BookCategory.OTHER;
    }

    private Versification getCurrentVersification() {
        return getCurrentBibleVerse().getVersificationOfLastSelectedVerse();
    }

    /**
     * set key without notification
     *
     * @param key
     */
    public void doSetKey(Key key) {
        if (key != null) {
            Verse verse = KeyUtil.getVerse(key);

            VerseRange verseRange;
            if (key instanceof VerseRange) {
                verseRange = (VerseRange) key;
            } else {
                verseRange = new VerseRange(verse.getVersification(), verse);
            }
            currentNoteVerseRange = new ConvertibleVerseRange(verseRange);

            getCurrentBibleVerse().setVerseSelected(getVersification(), verse);
        }
    }

    /* (non-Javadoc)
     * @see com.bellman.bible.android.control.CurrentPage#getKey()
     */
    @Override
    public Key getKey() {
        if (currentNoteVerseRange != null) {
            return currentNoteVerseRange.getVerseRange(getVersification());
        } else {
            return getCurrentBibleVerse().getVerseSelected(getVersification());
        }
    }

    public int getNumberOfVersesDisplayed() {
        return currentNoteVerseRange != null ? currentNoteVerseRange.getVerseRange().getCardinality() : 1;
    }

    @Override
    public boolean isSingleKey() {
        return currentNoteVerseRange == null || currentNoteVerseRange.getVerseRange().getCardinality() == 1;
    }

    @Override
    public void updateOptionsMenu(Menu menu) {
        super.updateOptionsMenu(menu);

        MenuItem menuItem = menu.findItem(R.id.windowSubMenu);
        if (menuItem != null) {
            menuItem.setEnabled(false);
        }
    }
}