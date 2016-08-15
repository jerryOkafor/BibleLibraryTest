package com.bellman.bible.android.view.activity.page;

import android.view.GestureDetector.SimpleOnGestureListener;

/**
 * Listen for side swipes to change chapter.  This listener class seems to work better that subclassing WebView.
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BibleViewGestureListener extends SimpleOnGestureListener {

    private static final String TAG = "BibleGestureListener";
    private BibleView bibleView;

    public BibleViewGestureListener(BibleView bibleView) {
        super();
        this.bibleView = bibleView;
    }

//	/** WebView does not handle long presses automatically via onCreateContextMenu so do it here
//	 */
//	@Override
//	public void onLongPress(MotionEvent e) {
//		Log.d(TAG, "onLongPress");
//
//		bibleView.onLongPress(e.getX(), e.getY());
//	}
}
