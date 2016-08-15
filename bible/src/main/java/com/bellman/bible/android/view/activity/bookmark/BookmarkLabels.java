package com.bellman.bible.android.view.activity.bookmark;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.bookmark.Bookmark;
import com.bellman.bible.android.view.activity.base.ListActivityBase;
import com.bellman.bible.service.db.bookmark.BookmarkDto;
import com.bellman.bible.service.db.bookmark.LabelDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Choose a bible or commentary to use
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BookmarkLabels extends ListActivityBase {

    private static final String TAG = "BookmarkLabels";
    // this resource returns a CheckedTextView which has setChecked(..), isChecked(), and toggle() methods
    private static final int LIST_ITEM_TYPE = android.R.layout.simple_list_item_multiple_choice;
    private List<BookmarkDto> bookmarks;
    private Bookmark bookmarkControl;
    private List<LabelDto> labels = new ArrayList<>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, false);
        setContentView(R.layout.bookmark_labels);

        bookmarkControl = ControlFactory.getInstance().getBookmarkControl();

        long[] bookmarkIds = getIntent().getLongArrayExtra(Bookmarks.BOOKMARK_IDS_EXTRA);
        bookmarks = bookmarkControl.getBookmarksById(bookmarkIds);

        initialiseView();
    }

    private void initialiseView() {
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        loadLabelList();

        ArrayAdapter<LabelDto> listArrayAdapter = new ArrayAdapter<>(this,
                LIST_ITEM_TYPE,
                labels);
        setListAdapter(listArrayAdapter);

        initialiseCheckedLabels(bookmarks);

        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookmark_labels_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        LabelDto label = labels.get(menuInfo.position);
        if (label != null) {
            int id = item.getItemId();
            if (id == R.id.delete) {
                delete(label);
                return true;
            } else if (id == R.id.rename) {
                edit(R.string.rename, label);
                return true;
            }
        }
        return false;
    }

    /**
     * Finished selecting labels
     *
     * @param v
     */
    public void onOkay(View v) {
        Log.i(TAG, "Okay clicked");
        // get the labels that are currently checked
        List<LabelDto> selectedLabels = getCheckedLabels();

        //associate labels with bookmarks that were passed in
        for (BookmarkDto bookmark : bookmarks) {
            bookmarkControl.setBookmarkLabels(bookmark, selectedLabels);
        }
        finish();
    }

    private void delete(LabelDto label) {
        // remember which labels were checked
        List<LabelDto> checkedLabels = getCheckedLabels();
        checkedLabels.remove(label);

        // delete label from db
        bookmarkControl.deleteLabel(label);

        // now refetch the list of labels
        loadLabelList();

        // restore check status of remaining labels
        setCheckedLabels(checkedLabels);
    }

    /**
     * New Label requested
     */
    public void onNewLabel(View v) {
        Log.i(TAG, "New label clicked");

        LabelDto newLabel = new LabelDto();
        edit(R.string.new_label, newLabel);
    }

    private void edit(int titleId, final LabelDto label) {
        Log.i(TAG, "Rename label clicked");

        // Set an EditText view to get user input
        final EditText labelInput = new EditText(this);
        labelInput.setText(label.getName());

        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle(titleId)
                .setMessage(R.string.label_name_prompt)
                .setView(labelInput);

        alert.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = labelInput.getText().toString();
                label.setName(name);
                bookmarkControl.saveOrUpdateLabel(label);
                List<LabelDto> selectedLabels = getCheckedLabels();
                Log.d(TAG, "Num labels checked pre reload:" + selectedLabels.size());

                loadLabelList();

                setCheckedLabels(selectedLabels);
                Log.d(TAG, "Num labels checked finally:" + selectedLabels.size());
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    /**
     * load list of docs to display
     */
    private void loadLabelList() {

        // get long book names to show in the select list
        // must clear rather than create because the adapter is linked to this specific list
        labels.clear();
        labels.addAll(bookmarkControl.getAssignableLabels());

        // ensure ui is updated
        notifyDataSetChanged();
    }

    /**
     * check labels associated with the bookmark
     */
    private void initialiseCheckedLabels(List<BookmarkDto> bookmarks) {
        Set<LabelDto> allCheckedLabels = new HashSet<>();
        for (BookmarkDto bookmark : bookmarks) {
            // pre-tick any labels currently associated with the bookmark
            allCheckedLabels.addAll(bookmarkControl.getBookmarkLabels(bookmark));
        }
        setCheckedLabels(allCheckedLabels);
    }

    /**
     * get checked status of all labels
     */
    private List<LabelDto> getCheckedLabels() {
        // get selected labels
        ListView listView = getListView();
        List<LabelDto> checkedLabels = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            if (listView.isItemChecked(i)) {
                LabelDto label = labels.get(i);
                checkedLabels.add(label);
                Log.d(TAG, "Selected " + label.getName());
            }
        }
        return checkedLabels;
    }

    /**
     * set checked status of all labels
     *
     * @param labelsToCheck
     */
    private void setCheckedLabels(Collection<LabelDto> labelsToCheck) {
        for (int i = 0; i < labels.size(); i++) {
            if (labelsToCheck.contains(labels.get(i))) {
                getListView().setItemChecked(i, true);
            } else {
                getListView().setItemChecked(i, false);
            }
        }

        // ensure ui is updated
        notifyDataSetChanged();
    }
}