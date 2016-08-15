package com.bellman.bible.android.control.page;

import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.view.activity.base.Dialogs;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
abstract public class CachedKeyPage extends CurrentPageBase {

    private static String TAG = "CachedKeyPage";
    private List<Key> mCachedGlobalKeyList;

    CachedKeyPage(boolean shareKeyBetweenDocs) {
        super(shareKeyBetweenDocs);
    }

    @Override
    public void setCurrentDocument(Book doc) {
        // if doc changes then clear any caches from the previous doc
        if (doc != null && !doc.equals(getCurrentDocument())) {
            mCachedGlobalKeyList = null;
        }
        super.setCurrentDocument(doc);
    }

    //TODO remove this and do binary search of globalkeylist

    /**
     * make dictionary key lookup much faster
     *
     * @return
     */
    public List<Key> getCachedGlobalKeyList() {
        if (getCurrentDocument() != null && mCachedGlobalKeyList == null) {
            try {
                Log.d(TAG, "Start to create cached key list for " + getCurrentDocument());
                // this cache is cleared in setCurrentDoc
                mCachedGlobalKeyList = new ArrayList<Key>();

                for (Key key : getCurrentDocument().getGlobalKeyList()) {
                    // root key has no name and can be ignored but also check for any other keys with no name
                    if (!StringUtils.isEmpty(key.getName())) {
                        mCachedGlobalKeyList.add(key);
                    }
                }

            } catch (OutOfMemoryError oom) {
                mCachedGlobalKeyList = null;
                System.gc();
                Log.e(TAG, "out of memory", oom);
                throw oom;
            } catch (Exception e) {
                mCachedGlobalKeyList = null;
                System.gc();
                Log.e(TAG, "Error getting keys for " + getCurrentDocument(), e);
                Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
            }
            Log.d(TAG, "Finished creating cached key list len:" + mCachedGlobalKeyList.size());
        }
        return mCachedGlobalKeyList;
    }

    /**
     * add or subtract a number of pages from the current position and return Verse
     */
    public Key getKeyPlus(int num) {
        Key currentKey = getKey();
        int keyPos = findIndexOf(currentKey);
        // move forward or backward to new posn
        int newKeyPos = keyPos + num;
        // check bounds
        newKeyPos = Math.min(newKeyPos, getCachedGlobalKeyList().size() - 1);
        newKeyPos = Math.max(newKeyPos, 0);
        // get the actual key at that posn
        return getCachedGlobalKeyList().get(newKeyPos);
    }

    /**
     * find index of key in cached key list but cater for TreeKeys too
     */
    protected int findIndexOf(Key key) {
        return getCachedGlobalKeyList().indexOf(key);
    }
}
