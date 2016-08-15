package com.bellman.bible.android.view.activity.navigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.navigation.NavigationControl;
import com.bellman.bible.android.view.activity.base.CustomTitlebarActivityBase;
import com.bellman.bible.android.view.activity.navigation.biblebookactionbar.BibleBookActionBarManager;
import com.bellman.bible.android.view.util.buttongrid.ButtonGrid;
import com.bellman.bible.android.view.util.buttongrid.ButtonGrid.ButtonInfo;
import com.bellman.bible.android.view.util.buttongrid.OnButtonGridActionListener;

import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

import java.util.ArrayList;
import java.util.List;

/**
 * Choose a bible book e.g. Psalms
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class GridChoosePassageBook extends CustomTitlebarActivityBase implements OnButtonGridActionListener {

    static final String BOOK_NO = "BOOK_NO";
    static final String CHAPTER_NO = "CHAPTER_NO";
    // colour and grouping taken from http://en.wikipedia.org/wiki/Books_of_the_Bible
    private static final int PENTATEUCH_COLOR = Color.rgb(0xCC, 0xCC, 0xFE);
    private static final int HISTORY_COLOR = Color.rgb(0xFE, 0xCC, 0x9B);
    private static final int WISDOM_COLOR = Color.rgb(0x99, 0xFF, 0x99);
    private static final int MAJOR_PROPHETS_COLOR = Color.rgb(0xFF, 0x99, 0xFF);
    private static final int MINOR_PROPHETS_COLOR = Color.rgb(0xFF, 0xFE, 0xCD);
    private static final int GOSPEL_COLOR = Color.rgb(0xFF, 0x97, 0x03);
    private static final int ACTS_COLOR = Color.rgb(0x00, 0x99, 0xFF);
    private static final int PAULINE_COLOR = Color.rgb(0xFF, 0xFF, 0x31);
    private static final int GENERAL_EPISTLES_COLOR = Color.rgb(0x67, 0xCC, 0x66); // changed 99 to CC to make a little clearer on dark background
    private static final int REVELATION_COLOR = Color.rgb(0xFE, 0x33, 0xFF);
    private static final int OTHER_COLOR = ACTS_COLOR;
    private static final String TAG = "GridChoosePassageBook";
    private static BibleBookActionBarManager bibleBookActionBarManager = new BibleBookActionBarManager();
    private ButtonGrid buttonGrid;
    private boolean isCurrentlyShowingScripture = true;
    private NavigationControl navigationControl = ControlFactory.getInstance().getNavigationControl();
    /**
     * Handle scripture/Appendix toggle
     */
    private OnClickListener scriptureToggleClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            isCurrentlyShowingScripture = !isCurrentlyShowingScripture;

            buttonGrid.clear();
            buttonGrid.addButtons(getBibleBookButtonInfo());

            bibleBookActionBarManager.setScriptureShown(isCurrentlyShowingScripture);
        }
    };
    /**
     * Handle scripture/Appendix toggle
     */
    private OnClickListener sortOrderClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            navigationControl.changeBibleBookSortOrder();

            buttonGrid.clear();
            buttonGrid.addButtons(getBibleBookButtonInfo());
        }
    };

    public GridChoosePassageBook() {
        super(bibleBookActionBarManager, R.menu.choose_passage_book_menu);

        bibleBookActionBarManager.registerScriptureToggleClickListener(scriptureToggleClickListener);
        bibleBookActionBarManager.getSortButton().registerClickListener(sortOrderClickListener);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // background goes white in some circumstances if theme changes so prevent theme change
        setAllowThemeChange(false);
        super.onCreate(savedInstanceState);

        isCurrentlyShowingScripture = navigationControl.isCurrentDefaultScripture();
        bibleBookActionBarManager.setScriptureShown(isCurrentlyShowingScripture);

        buttonGrid = new ButtonGrid(this);

        buttonGrid.setOnButtonGridActionListener(this);

        buttonGrid.addButtons(getBibleBookButtonInfo());

        setContentView(buttonGrid);
    }

    @Override
    public void buttonPressed(ButtonInfo buttonInfo) {
        Log.d(TAG, "Book:" + buttonInfo.id + " " + buttonInfo.name);
        bookSelected(buttonInfo.id);
    }

    private void bookSelected(int bibleBookNo) {
        Log.d(TAG, "Book selected:" + bibleBookNo);
        try {
            //TODO av11n - this is done now
            BibleBook book = BibleBook.values()[bibleBookNo];
            Versification v11n = getVersification();
            // if there is only 1 chapter then no need to select chapter, but may need to select verse still
            if (!navigationControl.hasChapters(book)) {
                if (!GridChoosePassageChapter.navigateToVerse()) {
                    ControlFactory.getInstance().getCurrentPageControl().getCurrentBible().setKey(new Verse(v11n, book, 1, 1));
                    returnToPreviousScreen();
                } else {
                    // select verse (only 1 chapter)
                    //this means that the book has only one chapter
                    Intent myIntent = new Intent(this, GridChoosePassageVerse.class);
                    myIntent.putExtra(GridChoosePassageBook.BOOK_NO, bibleBookNo);
                    myIntent.putExtra(GridChoosePassageBook.CHAPTER_NO, 1);
                    startActivityForResult(myIntent, 1);
                }
            } else {
                // select chapter
                Intent myIntent = new Intent(this, GridChoosePassageChapter.class);
                myIntent.putExtra(GridChoosePassageBook.BOOK_NO, bibleBookNo);
                startActivityForResult(myIntent, bibleBookNo);
            }
        } catch (Exception e) {
            Log.e(TAG, "error on select of bible book", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            returnToPreviousScreen();
        }
    }

    private List<ButtonInfo> getBibleBookButtonInfo() {
        boolean isShortBookNamesAvailable = isShortBookNames();
        BibleBook currentBibleBook = KeyUtil.getVerse(ControlFactory.getInstance().getCurrentPageControl().getCurrentBible().getKey()).getBook();

        List<BibleBook> bibleBookList = navigationControl.getBibleBooks(isCurrentlyShowingScripture);
        List<ButtonInfo> keys = new ArrayList<ButtonInfo>(bibleBookList.size());
        for (BibleBook book : bibleBookList) {
            ButtonInfo buttonInfo = new ButtonInfo();
            try {
                // this is used for preview
                buttonInfo.id = book.ordinal();
                buttonInfo.name = getShortBookName(book, isShortBookNamesAvailable);
                buttonInfo.textColor = getBookTextColor(book.ordinal());
                buttonInfo.highlight = book.equals(currentBibleBook);
            } catch (NoSuchVerseException nsve) {
                buttonInfo.name = "ERR";
            }
            keys.add(buttonInfo);
        }
        return keys;
    }

    /**
     * @return
     * @throws NoSuchVerseException
     */
    private boolean isShortBookNames() {
        try {
            return !getVersification().getShortName(BibleBook.GEN).equals(getVersification().getLongName(BibleBook.GEN));
        } catch (Exception nsve) {
            // should never get here
            Log.e(TAG, "No such bible book no: 1", nsve);
            return false;
        }
    }

    private String getShortBookName(BibleBook book, boolean isShortBookNamesAvailable) throws NoSuchVerseException {
        // shortened names exist so use them
        if (isShortBookNamesAvailable) {
            return getVersification().getShortName(book);
        }

        // getShortName will return the long name in place of the short name
        String bookName = getVersification().getLongName(book);

        // so now we shorten the name programatically
        StringBuilder shortenedName = new StringBuilder(4);
        int i = 0;
        while (shortenedName.length() < 4 && i < bookName.length()) {
            char ch = bookName.charAt(i);
            if (ch != ' ' && ch != '.') {
                shortenedName.append(ch);
            }
            i++;
        }

        return shortenedName.toString();
    }

    private int getBookTextColor(int bookNo) {
        // colour and grouping taken from http://en.wikipedia.org/wiki/Books_of_the_Bible
        if (bookNo <= BibleBook.DEUT.ordinal()) {
            // Pentateuch - books of Moses
            return PENTATEUCH_COLOR;
        } else if (bookNo <= BibleBook.ESTH.ordinal()) {
            // History
            return HISTORY_COLOR;
        } else if (bookNo <= BibleBook.SONG.ordinal()) {
            // Wisdom
            return WISDOM_COLOR;
        } else if (bookNo <= BibleBook.DAN.ordinal()) {
            // Major prophets
            return MAJOR_PROPHETS_COLOR;
        } else if (bookNo <= BibleBook.MAL.ordinal()) {
            // Minor prophets
            return MINOR_PROPHETS_COLOR;
        } else if (bookNo <= BibleBook.JOHN.ordinal()) {
            // Gospels
            return GOSPEL_COLOR;
        } else if (bookNo <= BibleBook.ACTS.ordinal()) {
            // Acts
            return ACTS_COLOR;
        } else if (bookNo <= BibleBook.PHLM.ordinal()) {
            // Pauline epistles
            return PAULINE_COLOR;
        } else if (bookNo <= BibleBook.JUDE.ordinal()) {
            // General epistles
            return GENERAL_EPISTLES_COLOR;
        } else if (bookNo <= BibleBook.JUDE.ordinal()) {
            // Revelation
            return REVELATION_COLOR;
        } else {
            return OTHER_COLOR;
        }
    }

    private Versification getVersification() {
        return navigationControl.getVersification();
    }
}
