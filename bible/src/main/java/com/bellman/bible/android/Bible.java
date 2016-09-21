package com.bellman.bible.android;

import android.app.Application;
import android.util.Log;

import com.bellman.bible.android.control.Initialisation;
import com.bellman.bible.android.view.activity.base.Dialogs;
import com.bellman.bible.service.device.ProgressNotificationManager;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ReporterEvent;
import org.crosswire.common.util.ReporterListener;

import java.util.Locale;

/**
 * Main Embedded Bible application singleton object
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class Bible {

    private static final String TEXT_SIZE_PREF = "text_size_pref";
    // this was moved from the MainBibleActivity and has always been called this
    private static final String saveStateTag = "MainBibleActivity";
    private static final String TAG = "BibleApplication";
    private static Bible singleton = new Bible();
    private Locale overrideLocale;
    private int errorDuringStartup = 0;
    private Application application;

    public static Bible getInstance() {
        return singleton;
    }

    /*
    * gets the Application
    * */

    public Application getApplication() {
        return application;
    }

    public void initAll(Application application) {
        this.application = application;
        Log.i(TAG, "OS:" + System.getProperty("os.name") + " ver " + System.getProperty("os.version"));
        Log.i(TAG, "Java:" + System.getProperty("java.vendor") + " ver " + System.getProperty("java.version"));
        Log.i(TAG, "Java home:" + System.getProperty("java.home"));
        Log.i(TAG, "User dir:" + System.getProperty("user.dir") + " Timezone:" + System.getProperty("user.timezone"));

        // fix for null context class loader (http://code.google.com/p/android/issues/detail?id=5697)
        // this affected jsword dynamic classloading
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        installJSwordErrorReportListener();
        // initialise link to Android progress control display in Notification bar
        ProgressNotificationManager.getInstance().initialise();


        Locale locale = Locale.getDefault();
        Log.i(TAG, "Locale language:" + locale.getLanguage() + " Variant:" + locale.getDisplayName());

        // various initialisations required every time at app startup
        Initialisation.getInstance().initialiseEventually();

    }

    /**
     * Allow user interface locale override by changing Settings
     */
//    private void allowLocaleOverride() {
//        // Has the user selected a custom locale?
//        Configuration config = getBaseContext().getResources().getConfiguration();
//        String lang = CommonUtils.getLocalePref();
//        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
//            overrideLocale = new Locale(lang);
//            Locale.setDefault(overrideLocale);
//            config.locale = overrideLocale;
//            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//        }
//    }

    /**
     * If locale is overridden then need to set the locale again on any configuration change; see following link
     * http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
     */
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (overrideLocale != null) {
//            if (!overrideLocale.getLanguage().equals(newConfig.locale.getLanguage())) {
//                Log.d(TAG, "re-applying changed Locale");
//                Locale.setDefault(overrideLocale);
//                Configuration config = new Configuration();
//                config.locale = overrideLocale;
//                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//            }
//        }
//    }


    /** return false if old android and hebrew locale
     *
     * @param locale
     * @return
     */
//	private boolean isLocaleSupported(Locale locale) {
//		String langCode = locale.getLanguage();
//		boolean isHebrew = langCode.equalsIgnoreCase("he") || langCode.equalsIgnoreCase("iw");
//		if (isHebrew && !CommonUtils.isHebrewFonts()) {
//			// Locale is Hebrew but OS is old and has no Hebrew fonts
//			return false;
//		} else {
//			return true;
//		}
//	}

    /**
     * JSword calls back to this listener in the event of some types of error
     */
    private void installJSwordErrorReportListener() {
        Reporter.addReporterListener(new ReporterListener() {
            @Override
            public void reportException(final ReporterEvent ev) {
                showMsg(ev);
            }

            @Override
            public void reportMessage(final ReporterEvent ev) {
                showMsg(ev);
            }

            private void showMsg(ReporterEvent ev) {
                String msg = null;
                if (ev == null) {
                    msg = "An Error occurred";
                } else if (!StringUtils.isEmpty(ev.getMessage())) {
                    msg = ev.getMessage();
                } else if (ev.getException() != null && StringUtils.isEmpty(ev.getException().getMessage())) {
                    msg = ev.getException().getMessage();
                } else {
                    msg = "An Error occurred";
                }

                // convert Throwable to Exception for Dialogs
                Exception e;
                if (ev != null) {
                    Throwable th = ev.getException();
                    e = th instanceof Exception ? (Exception) th : new Exception("Jsword Exception", th);
                } else {
                    e = new Exception("JSword Exception");
                }

                Dialogs.getInstance().showErrorMsg(msg, e);
            }
        });
    }
//
//    @Override
//    public void onTerminate() {
//        Log.i(TAG, "onTerminate");
//        super.onTerminate();
//    }
//
//    // difficult to show dialogs during Activity onCreate so save it until later
//    public int getErrorDuringStartup() {
//        return errorDuringStartup;
//    }
//
//    public SharedPreferences getAppStateSharedPreferences() {
//        return getSharedPreferences(saveStateTag, 0);
//    }
}
