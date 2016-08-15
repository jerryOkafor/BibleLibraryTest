package com.bellman.bible.android.view.activity.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.navigation.NavigationControl;
import com.bellman.bible.android.control.page.CurrentPageManager;
import com.bellman.bible.android.view.activity.base.CustomTitlebarActivityBase;
import com.bellman.bible.android.view.util.buttongrid.ButtonGrid;
import com.bellman.bible.android.view.util.buttongrid.ButtonGrid.ButtonInfo;
import com.bellman.bible.android.view.util.buttongrid.OnButtonGridActionListener;
import com.bellman.bible.service.common.CommonUtils;

import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Choose a chapter to view
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class GridChoosePassageChapter extends CustomTitlebarActivityBase implements OnButtonGridActionListener {

    private static final String TAG = "GridChoosePassageChapter";

    private BibleBook mBibleBook = BibleBook.GEN;

    private NavigationControl navigationControl = ControlFactory.getInstance().getNavigationControl();

    static boolean navigateToVerse() {
        return CommonUtils.getSharedPreferences().getBoolean("navigate_to_verse_pref", false);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // background goes white in some circumstances if theme changes so prevent theme change
        setAllowThemeChange(false);
        super.onCreate(savedInstanceState);

        int bibleBookNo = getIntent().getIntExtra(GridChoosePassageBook.BOOK_NO, navigationControl.getDefaultBibleBookNo());
        //TODO av11n - this is done now
        mBibleBook = BibleBook.values()[bibleBookNo];

        // show chosen book in page title to confirm user choice
        try {
            //TODO av11n - probably should use same v11n as used in GridChoosePassageBook
            setTitle(navigationControl.getVersification().getLongName(mBibleBook));
        } catch (Exception nsve) {
            Log.e(TAG, "Error in selected book no", nsve);
        }

        ButtonGrid grid = new ButtonGrid(this);
        grid.setOnButtonGridActionListener(this);

        grid.addButtons(getBibleChaptersButtonInfo(mBibleBook));
        setContentView(grid);
    }

    private List<ButtonInfo> getBibleChaptersButtonInfo(BibleBook book) {
        int chapters = -1;
        try {
            chapters = navigationControl.getVersification().getLastChapter(book);
        } catch (Exception nsve) {
            chapters = -1;
        }

        List<ButtonInfo> keys = new ArrayList<ButtonInfo>();
        for (int i = 1; i <= chapters; i++) {
            ButtonInfo buttonInfo = new ButtonInfo();
            // this is used for preview
            buttonInfo.id = i;
            buttonInfo.name = Integer.toString(i);
            keys.add(buttonInfo);
        }
        return keys;
    }

    @Override
    public void buttonPressed(ButtonInfo buttonInfo) {
        int chapter = buttonInfo.id;
        Log.d(TAG, "Chapter selected:" + chapter);
        try {
            CurrentPageManager currentPageControl = ControlFactory.getInstance().getCurrentPageControl();
            if (!navigateToVerse() && !currentPageControl.getCurrentPage().isSingleKey()) {
                currentPageControl.getCurrentPage().setKey(new Verse(navigationControl.getVersification(), mBibleBook, chapter, 1));
                onSave(null);
            } else {
                // select verse
                Intent myIntent = new Intent(this, GridChoosePassageVerse.class);
                myIntent.putExtra(GridChoosePassageBook.BOOK_NO, mBibleBook.ordinal());
                myIntent.putExtra(GridChoosePassageBook.CHAPTER_NO, chapter);
                startActivityForResult(myIntent, chapter);
            }
        } catch (Exception e) {
            Log.e(TAG, "error on select of bible book", e);
        }
    }

    public void onSave(View v) {
        Log.i(TAG, "CLICKED");
        Intent resultIntent = new Intent(this, GridChoosePassageBook.class);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            returnToPreviousScreen();
        }
    }
}
