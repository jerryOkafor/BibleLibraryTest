package com.bellman.bible.android.view.activity.help;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.android.view.activity.base.CustomTitlebarActivityBase;
import com.bellman.bible.service.common.CommonUtils;

/**
 * show a history list and allow to go to history item
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class Help extends CustomTitlebarActivityBase {
    private static final String TAG = "Help";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Displaying Help view");
        setContentView(R.layout.help);

        TextView versionTextView = (TextView) findViewById(R.id.versionText);
        String versionMsg = CurrentActivityHolder.getInstance().getApplication().getString(R.string.version_text, CommonUtils.getApplicationVersionName());
        versionTextView.setText(versionMsg);
    }
}
