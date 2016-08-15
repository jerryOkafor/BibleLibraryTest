package com.bellman.bible.android.view.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.search.SearchControl;
import com.bellman.bible.android.view.activity.base.Dialogs;
import com.bellman.bible.android.view.activity.base.ProgressActivityBase;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.sword.SwordDocumentFacade;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.common.progress.Progress;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.IndexStatus;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class SearchIndexProgressStatus extends ProgressActivityBase {

    private static final String TAG = "SearchIndexProgressStatus";
    private Book documentBeingIndexed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_index_status);

        hideButtons();
        setMainText(getString(R.string.indexing_wait_msg));

        String docInitials = getIntent().getStringExtra(SearchControl.SEARCH_DOCUMENT);
        documentBeingIndexed = SwordDocumentFacade.getInstance().getDocumentByInitials(docInitials);
    }

    /**
     * check index exists and go to search screen if index exists
     * if no more jobs in progress and no index then error
     */
    @Override
    protected void jobFinished(Progress jobJustFinished) {
        // give the document up to 12 secs to reload - the Progress declares itself finished before the index status has been changed
        int attempts = 0;
        while (!IndexStatus.DONE.equals(documentBeingIndexed.getIndexStatus()) && attempts++ < 6) {
            CommonUtils.pause(2);
        }

        // if index is fine then goto search
        if (IndexStatus.DONE.equals(documentBeingIndexed.getIndexStatus())) {
            Log.i(TAG, "Index created");
            Intent intent = null;
            if (StringUtils.isNotEmpty(getIntent().getStringExtra(SearchControl.SEARCH_TEXT))) {
                // the search string was passed in so execute it directly
                intent = new Intent(this, SearchResults.class);
                intent.putExtras(getIntent().getExtras());
            } else {
                // just go to the normal Search screen
                intent = new Intent(this, Search.class);
            }
            startActivity(intent);
            finish();
        } else {
            // if jobs still running then just wait else error

            if (isAllJobsFinished()) {
                Log.e(TAG, "Index finished but document's index is invalid");
                Dialogs.getInstance().showErrorMsg(R.string.error_occurred);
            }
        }
    }
}
