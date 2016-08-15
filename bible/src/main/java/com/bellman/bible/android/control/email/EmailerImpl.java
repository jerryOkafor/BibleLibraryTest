package com.bellman.bible.android.control.email;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;

import org.apache.commons.lang3.StringUtils;

/**
 * Jump straight to email program with subject, body, recipient, etc
 */
public class EmailerImpl implements Emailer {

    /* (non-Javadoc)
     * @see com.bellman.bible.android.control.report.Email#send(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void send(String emailDialogTitle, String subject, String text) {
        send(emailDialogTitle, null, subject, text);
    }

    /* (non-Javadoc)
     * @see com.bellman.bible.android.control.report.Email#send(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void send(String emailDialogTitle, String recipient, String subject, String body) {

        final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        if (StringUtils.isNotBlank(recipient)) {
            emailIntent.setData(Uri.fromParts("mailto", recipient, null));
        }

        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        Activity activity = CurrentActivityHolder.getInstance().getCurrentActivity();
        activity.startActivity(emailIntent);
    }
}
