package com.bellman.bible.android.view.activity.mynote;

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
import com.bellman.bible.android.control.mynote.MyNote;
import com.bellman.bible.android.view.activity.base.ListActionModeHelper;
import com.bellman.bible.android.view.util.widget.TwoLineListItem;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.db.mynote.MyNoteDto;

import java.util.List;

/**
 * Display a single Note in a list row
 *
 * @author John D. Lewis [balinjdl at gmail dot com]
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public class MyNoteItemAdapter extends ArrayAdapter<MyNoteDto> {
    private static final String TAG = "UserNoteItemAdapter";
    private static int ACTIVATED_COLOUR = CommonUtils.getResourceColor(R.color.list_item_activated);
    private final ListActionModeHelper.ActionModeActivity actionModeActivity;
    private int resource;
    private MyNote usernoteControl;

    public MyNoteItemAdapter(Context _context, int _resource, List<MyNoteDto> _items, ListActionModeHelper.ActionModeActivity actionModeActivity) {
        super(_context, _resource, _items);
        resource = _resource;
        usernoteControl = ControlFactory.getInstance().getMyNoteControl();
        this.actionModeActivity = actionModeActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyNoteDto item = getItem(position);

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
            String key = usernoteControl.getMyNoteVerseKey(item);
            view.getText1().setText(key);
        }

        // set value for the second text field
        if (view.getText2() != null) {
            try {
                String noteText = usernoteControl.getMyNoteText(item, true);
                view.getText2().setText(noteText);
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
