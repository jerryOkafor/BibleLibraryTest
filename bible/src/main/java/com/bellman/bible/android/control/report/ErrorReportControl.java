package com.bellman.bible.android.control.report;

import android.os.Build;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.email.Emailer;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.service.common.CommonUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorReportControl {

    private Emailer emailer;

    public ErrorReportControl(Emailer emailer) {
        this.emailer = emailer;
    }

    public void sendErrorReportEmail(Exception e) {
        String text = createErrorText(e);

        String title = CurrentActivityHolder.getInstance().getApplication().getString(R.string.report_error);
        String subject = getSubject(e, title);

        emailer.send(title, "errors.andbible@gmail.com", subject, text);
    }

    private String createErrorText(Exception exception) {
        try {
            StringBuilder text = new StringBuilder();
            text.append("Embedded Bible version: ").append(CommonUtils.getApplicationVersionName()).append("\n");
            text.append("Android version: ").append(Build.VERSION.RELEASE).append("\n");
            text.append("Android SDK version: ").append(Build.VERSION.SDK_INT).append("\n");
            text.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            text.append("Model: ").append(Build.MODEL).append("\n\n");
            text.append("SD card Mb free: ").append(CommonUtils.getSDCardMegsFree()).append("\n\n");

            final Runtime runtime = Runtime.getRuntime();
            final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
            final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
            text.append("Used heap memory in Mb: ").append(usedMemInMB).append("\n");
            text.append("Max heap memory in Mb: ").append(maxHeapSizeInMB).append("\n\n");

            if (exception != null) {
                StringWriter errors = new StringWriter();
                exception.printStackTrace(new PrintWriter(errors));
                text.append("Exception:\n").append(errors.toString());
            }

            return text.toString();
        } catch (Exception e) {
            return "Exception occurred preparing error text:" + e.getMessage();
        }
    }

    private String getSubject(Exception e, String title) {
        if (e == null || e.getStackTrace().length == 0) {
            return title;
        }

        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement elt : stack) {
            if (elt.getClassName().contains("com.bellman.bible")) {
                return e.getMessage() + ":" + elt.getClassName() + "." + elt.getMethodName() + ":" + elt.getLineNumber();
            }
        }

        return e.getMessage();
    }
}

