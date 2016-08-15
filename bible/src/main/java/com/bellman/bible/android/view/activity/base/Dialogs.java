package com.bellman.bible.android.view.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.report.ErrorReportControl;
import com.bellman.bible.android.view.util.Hourglass;

/**
 * Class to manage the display of various dialogs
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class Dialogs {

    public static final int TOO_MANY_JOBS = 121;
    private static final String TAG = "Dialogs";
    private static final Dialogs singleton = new Dialogs();
    private ErrorReportControl errorReportControl = ControlFactory.getInstance().getErrorReportControl();
    private Hourglass hourglass = new Hourglass();
    private Callback doNothingCallback = new Callback() {
        @Override
        public void okay() {
            // by default do nothing when user clicks okay
            dismissHourglass();
        }
    };

    private Dialogs() {
        super();
    }

    public static Dialogs getInstance() {
        return singleton;
    }

    public void showMsg(int msgId, String param) {
        showErrorMsg(CurrentActivityHolder.getInstance().getApplication().getString(msgId, param));
    }

    public void showMsg(int msgId, boolean isCancelable, final Callback okayCallback) {
        showMsg(CurrentActivityHolder.getInstance().getApplication().getString(msgId), isCancelable, okayCallback, null);
    }

    public void showMsg(int msgId) {
        showErrorMsg(CurrentActivityHolder.getInstance().getApplication().getString(msgId));
    }

    public void showErrorMsg(int msgId) {
        showErrorMsg(CurrentActivityHolder.getInstance().getApplication().getString(msgId));
    }

    public void showErrorMsg(int msgId, String param) {
        showErrorMsg(CurrentActivityHolder.getInstance().getApplication().getString(msgId, param));
    }

    public void showErrorMsg(String msg) {
        showErrorMsg(msg, doNothingCallback);
    }

    public void showErrorMsg(int msgId, final Callback okayCallback) {
        showErrorMsg(CurrentActivityHolder.getInstance().getApplication().getString(msgId), okayCallback);
    }

    /**
     * Show error message and allow reporting of exception via e-mail to and-bible
     */
    public void showErrorMsg(int msgId, final Exception e) {
        showErrorMsg(CurrentActivityHolder.getInstance().getApplication().getString(msgId), e);
    }

    /**
     * Show error message and allow reporting of exception via e-mail to and-bible
     */
    public void showErrorMsg(String message, final Exception e) {
        Callback reportCallback = new Callback() {
            @Override
            public void okay() {
                errorReportControl.sendErrorReportEmail(e);
            }
        };

        showMsg(message, false, doNothingCallback, reportCallback);
    }

    public void showErrorMsg(final String msg, final Callback okayCallback) {
        showMsg(msg, false, okayCallback, null);
    }

    public void showMsg(final String msg, final boolean isCancelable, final Callback okayCallback, final Callback reportCallback) {
        Log.d(TAG, "showErrorMesage message:" + msg);
        try {
            final Activity activity = CurrentActivityHolder.getInstance().getCurrentActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(activity)
                                .setMessage(msg)
                                .setCancelable(isCancelable)
                                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int buttonId) {
                                        okayCallback.okay();
                                    }
                                });

                        // if cancelable then show a Cancel button
                        if (isCancelable) {
                            dlgBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int buttonId) {
                                    // do nothing
                                }
                            });
                        }

                        // enable report to andbible errors email list
                        if (reportCallback != null) {
                            dlgBuilder.setNeutralButton(R.string.report_error, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int buttonId) {
                                    reportCallback.okay();
                                }
                            });
                        }

                        dlgBuilder.show();
                    }
                });
            } else {
                Toast.makeText(CurrentActivityHolder.getInstance().getApplication().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing error message.  Original error msg:" + msg, e);
        }
    }

    public void showHourglass() {
        hourglass.show();
    }

    public void dismissHourglass() {
        hourglass.dismiss();
    }
}
