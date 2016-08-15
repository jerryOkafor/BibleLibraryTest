package com.bellman.bible.service.history;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.service.common.CommonUtils;

/**
 * Any item in the History list that is not related to the main bible activity view e.g. search results etc
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public class IntentHistoryItem extends HistoryItemBase {

    private static final String TAG = "IntentHistoryItem";
    private CharSequence description;
    private Intent intent;

    public IntentHistoryItem(CharSequence description, Intent intent) {
        super();
        this.description = description;
        this.intent = intent;

        // prevent re-add of intent to history if reverted to
//		intent.putExtra(HISTORY_INTENT, true);
    }

    public IntentHistoryItem(int descriptionId, Intent intent) {
        this(CommonUtils.getResourceString(descriptionId), intent);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof IntentHistoryItem)) {
            return false;
        }
        if (o == this) {
            return true;
        }

        IntentHistoryItem oihs = (IntentHistoryItem) o;
        // assumes intent exists
        return intent.equals(oihs.intent);
    }

    @Override
    public CharSequence getDescription() {
        return description;
    }

    @Override
    public void revertTo() {
        Log.d(TAG, "Revert to history item:" + description);
        // need to get current activity and call startActivity on that
        Activity currentActivity = CurrentActivityHolder.getInstance().getCurrentActivity();

        // start activity chosen from activity
        currentActivity.startActivity(intent);
    }
}
