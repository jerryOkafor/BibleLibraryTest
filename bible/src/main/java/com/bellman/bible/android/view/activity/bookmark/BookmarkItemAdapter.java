package com.bellman.bible.android.view.activity.bookmark;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.bookmark.Bookmark;
import com.bellman.bible.android.view.activity.base.ListActionModeHelper;
import com.bellman.bible.android.view.util.widget.TwoLineListItem;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.db.bookmark.BookmarkDto;

import java.util.List;

/**
 * nice example here: http://shri.blog.kraya.co.uk/2010/04/19/android-multi-line-select-list/
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BookmarkItemAdapter extends ArrayAdapter<BookmarkDto> {

    private static final String TAG = "BookmarkItemAdapter";
    private static int ACTIVATED_COLOUR = CommonUtils.getResourceColor(R.color.list_item_activated);
    private final ListActionModeHelper.ActionModeActivity actionModeActivity;
    private int resource;
    private Bookmark bookmarkControl;

    public BookmarkItemAdapter(Context _context, int _resource, List<BookmarkDto> _items, ListActionModeHelper.ActionModeActivity actionModeActivity) {
        super(_context, _resource, _items);
        resource = _resource;
        bookmarkControl = ControlFactory.getInstance().getBookmarkControl();
        this.actionModeActivity = actionModeActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BookmarkDto item = getItem(position);

        // Pick up the TwoLineListItem defined in the xml file
        TwoLineListItem view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (TwoLineListItem) inflater.inflate(resource, parent, false);
        } else {
            view = (TwoLineListItem) convertView;
        }

        // Set value for the first text field
        if (view.getText1() != null) {
            String key = bookmarkControl.getBookmarkVerseKey(item);
            view.getText1().setText(key);
        }

        // set value for the second text field
        if (view.getText2() != null) {
            try {
                String verseText = bookmarkControl.getBookmarkVerseText(item);
                view.getText2().setText(verseText);
            } catch (Exception e) {
                Log.e(TAG, "Error loading label verse text", e);
                view.getText2().setText("");
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            if (actionModeActivity.isItemChecked(position)) {
                view.setBackgroundColor(ACTIVATED_COLOUR);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        return view;
    }
}