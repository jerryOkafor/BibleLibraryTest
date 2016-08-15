package com.bellman.bible.android.view.activity.download;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.view.activity.base.Dialogs;
import com.bellman.bible.android.view.activity.base.ProgressActivityBase;
import com.bellman.bible.android.view.activity.page.MainBibleActivity;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.sword.SwordDocumentFacade;

import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;

/**
 * Prompt user to wait until first bible has downloaded before going to main screen
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class EnsureBibleDownloaded extends ProgressActivityBase {

    private static final String TAG = "EnsureBibleDownloaded";

    private int clickCount = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Displaying ensure-bible-downloaded");
        setContentView(R.layout.ensure_bible_downloaded);

        if (SwordDocumentFacade.getInstance().getBibles().size() > 0) {
            gotoMainScreen();
        }
    }

    @Override
    protected void jobFinished(Progress prog) {
        Log.d(TAG, "Finished download, going to main screen");
        if (SwordDocumentFacade.getInstance().getBibles().size() > 0) {
            gotoMainScreen();
        } else {

            Log.w(TAG, "Could not immediately find downloaded bible");
            // can't find downloaded bible, wait a sec and try again
            CommonUtils.pause(2);
            if (SwordDocumentFacade.getInstance().getBibles().size() > 0) {
                Log.d(TAG, "Downloaded bible found now");
                gotoMainScreen();
            } else {
                Log.e(TAG, "Downloaded bible not found");
                if (JobManager.getJobCount() == 0) {
                    // something went wrong with the download
                    Dialogs.getInstance().showErrorMsg(R.string.download_complete_no_bibles);
                }
            }
        }
    }

    /**
     * user pressed contimue
     */
    public void onContinue(View v) {
        Log.i(TAG, "CLICKED");
        if (SwordDocumentFacade.getInstance().getBibles().size() > 0) {
            gotoMainScreen();
        } else {
            TextView warn = (TextView) findViewById(R.id.waitForBibleNotYet);
            // if already warned then alternate the wait text
            if (clickCount++ % 2 == 0) {
                warn.setText(R.string.wait_for_bible_not_yet);
            } else {
                warn.setText(R.string.please_wait);
            }

            warn.setVisibility(View.VISIBLE);
        }
    }

    private void gotoMainScreen() {
        // set an appropriate default verse for the bible just downloaded
        ControlFactory.getInstance().getPageControl().setFirstUseDefaultVerse();

        Intent intent = new Intent(this, MainBibleActivity.class);
        startActivity(intent);
        finish();
    }

}
