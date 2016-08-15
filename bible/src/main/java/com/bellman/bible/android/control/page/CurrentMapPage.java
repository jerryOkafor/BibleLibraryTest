package com.bellman.bible.android.control.page;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.view.activity.navigation.genbookmap.ChooseMapKey;

import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.passage.Key;

/**
 * Reference to current Map shown by viewer
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class CurrentMapPage extends CachedKeyPage implements CurrentPage {

    @SuppressWarnings("unused")
    private static final String TAG = "CurrentMapPage";
    private Key key;

    /* default */ CurrentMapPage() {
        super(false);
    }

    public BookCategory getBookCategory() {
        return BookCategory.MAPS;
    }

    @Override
    public Class<? extends Activity> getKeyChooserActivity() {
        return ChooseMapKey.class;
    }

    /**
     * set key without notification
     *
     * @param key
     */
    public void doSetKey(Key key) {
        this.key = key;
    }

    /* (non-Javadoc)
     * @see com.bellman.bible.android.control.CurrentPage#getKey()
     */
    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void next() {
        Key next = getKeyPlus(1);
        if (next != null) {
            setKey(next);
        }
    }

    @Override
    public void previous() {
        Key prev = getKeyPlus(-1);
        if (prev != null) {
            setKey(prev);
        }
    }

    @Override
    public void updateOptionsMenu(Menu menu) {
        super.updateOptionsMenu(menu);

        MenuItem menuItem = menu.findItem(R.id.bookmarksButton);
        if (menuItem != null) {
            menuItem.setEnabled(false);
        }

    }

    @Override
    public boolean isSingleKey() {
        return true;
    }

    /**
     * can we enable the main menu search button
     */
    @Override
    public boolean isSearchable() {
        return false;
    }
}