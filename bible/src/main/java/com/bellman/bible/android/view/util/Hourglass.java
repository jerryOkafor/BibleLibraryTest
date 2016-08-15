package com.bellman.bible.android.view.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;

/**
 * Helper class to show HourGlass
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class Hourglass {

    private static final String TAG = "HourGlass";
    private ProgressDialog hourglass;

    public Hourglass() {
        super();
    }

    public void show() {
        final Activity activity = CurrentActivityHolder.getInstance().getCurrentActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hourglass = new ProgressDialog(activity);
                    hourglass.setMessage(CurrentActivityHolder.getInstance().getApplication().getText(R.string.please_wait));
                    hourglass.setIndeterminate(true);
                    hourglass.setCancelable(false);
                    hourglass.show();
                }
            });
        }
    }

    public void dismiss() {
        try {
            if (hourglass != null) {
                final Activity activity = CurrentActivityHolder.getInstance().getCurrentActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hourglass.dismiss();
                            hourglass = null;
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error dismissing hourglass", e);
        }
    }

    public ProgressDialog getHourglass() {
        return hourglass;
    }
}
