package com.bellman.bible.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.Initialisation;
import com.bellman.bible.android.view.activity.base.Dialogs;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.device.ProgressNotificationManager;
import com.bellman.bible.service.device.ScreenSettings;
import com.bellman.bible.service.device.ScreenTimeoutSettings;
import com.bellman.bible.service.sword.SwordDocumentFacade;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.common.util.Language;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ReporterEvent;
import org.crosswire.common.util.ReporterListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.bridge.BookIndexer;

import java.util.List;
import java.util.Locale;

/**
 * Main Embedded Bible application singleton object
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BibleApplication extends Application {

    private static final String TEXT_SIZE_PREF = "text_size_pref";
    // this was moved from the MainBibleActivity and has always been called this
    private static final String saveStateTag = "MainBibleActivity";
    private static final String TAG = "BibleApplication";
    private static BibleApplication singleton;
    private Locale overrideLocale;
    private int errorDuringStartup = 0;
    private ScreenTimeoutSettings screenTimeoutSettings = new ScreenTimeoutSettings();

    public static BibleApplication getApplication() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // save to a singleton to allow easy access from anywhere
        singleton = this;

        Log.i(TAG, "OS:" + System.getProperty("os.name") + " ver " + System.getProperty("os.version"));
        Log.i(TAG, "Java:" + System.getProperty("java.vendor") + " ver " + System.getProperty("java.version"));
        Log.i(TAG, "Java home:" + System.getProperty("java.home"));
        Log.i(TAG, "User dir:" + System.getProperty("user.dir") + " Timezone:" + System.getProperty("user.timezone"));

        // fix for null context class loader (http://code.google.com/p/android/issues/detail?id=5697)
        // this affected jsword dynamic classloading
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        installJSwordErrorReportListener();

        // some changes may be required for different versions
        upgradePersistentData();

        // initialise link to Android progress control display in Notification bar
        ProgressNotificationManager.getInstance().initialise();

        allowLocaleOverride();
        Locale locale = Locale.getDefault();
        Log.i(TAG, "Locale language:" + locale.getLanguage() + " Variant:" + locale.getDisplayName());

        screenTimeoutSettings.overrideScreenTimeout();

        // various initialisations required every time at app startup
        Initialisation.getInstance().initialiseEventually();
    }

    /**
     * Allow user interface locale override by changing Settings
     */
    private void allowLocaleOverride() {
        // Has the user selected a custom locale?
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = CommonUtils.getLocalePref();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            overrideLocale = new Locale(lang);
            Locale.setDefault(overrideLocale);
            config.locale = overrideLocale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    /**
     * If locale is overridden then need to set the locale again on any configuration change; see following link
     * http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (overrideLocale != null) {
            if (!overrideLocale.getLanguage().equals(newConfig.locale.getLanguage())) {
                Log.d(TAG, "re-applying changed Locale");
                Locale.setDefault(overrideLocale);
                Configuration config = new Configuration();
                config.locale = overrideLocale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }
        }
    }

    private void upgradePersistentData() {
        SharedPreferences prefs = CommonUtils.getSharedPreferences();
        int prevInstalledVersion = prefs.getInt("version", -1);
        if (prevInstalledVersion < CommonUtils.getApplicationVersionNumber() && prevInstalledVersion > -1) {
            Editor editor = prefs.edit();

            // ver 16 and 17 needed text size pref to be changed to int from string
            if (prevInstalledVersion < 16) {
                Log.d(TAG, "Upgrading preference");
                String textSize = "16";
                if (prefs.contains(TEXT_SIZE_PREF)) {
                    Log.d(TAG, "text size pref exists");
                    try {
                        textSize = prefs.getString(TEXT_SIZE_PREF, "16");
                    } catch (Exception e) {
                        // maybe the conversion has already taken place e.g. in debug environment
                        textSize = Integer.toString(prefs.getInt(TEXT_SIZE_PREF, 16));
                    }
                    Log.d(TAG, "existing value:" + textSize);
                    editor.remove(TEXT_SIZE_PREF);
                }

                int textSizeInt = Integer.parseInt(textSize);
                editor.putInt(TEXT_SIZE_PREF, textSizeInt);

                Log.d(TAG, "Finished Upgrading preference");
            }

            // there was a problematic Chinese index architecture before ver 24 so delete any old indexes
            if (prevInstalledVersion < 24) {
                Log.d(TAG, "Deleting old Chinese indexes");
                Language CHINESE = new Language("zh");

                List<Book> books = SwordDocumentFacade.getInstance().getDocuments();
                for (Book book : books) {
                    if (CHINESE.equals(book.getLanguage())) {
                        try {
                            BookIndexer bookIndexer = new BookIndexer(book);
                            // Delete the book, if present
                            if (bookIndexer.isIndexed()) {
                                Log.d(TAG, "Deleting index for " + book.getInitials());
                                bookIndexer.deleteIndex();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting index", e);
                        }
                    }
                }
            }
            // add new
            if (prevInstalledVersion < 61) {
                if (prefs.contains(ScreenSettings.NIGHT_MODE_PREF_NO_SENSOR)) {
                    String pref2Val = prefs.getBoolean(ScreenSettings.NIGHT_MODE_PREF_NO_SENSOR, false) ? "true" : "false";
                    Log.d(TAG, "Setting new night mode pref list value:" + pref2Val);
                    editor.putString(ScreenSettings.NIGHT_MODE_PREF_WITH_SENSOR, pref2Val);
                }
            }

            // add new
            if (prevInstalledVersion < 89) {
                if (!prefs.contains(ScreenTimeoutSettings.SCREEN_TIMEOUT_PREF)) {
                    Log.d(TAG, "Adding default screen timeout setting");
                    editor.putString(ScreenTimeoutSettings.SCREEN_TIMEOUT_PREF, Integer.toString(ScreenTimeoutSettings.DEFAULT_VALUE));
                }
            }

            // clear old split screen config because it has changed a lot
            if (prevInstalledVersion < 154) {
                editor.remove("screen1_weight");
                editor.remove("screen2_minimized");
                editor.remove("split_screen_pref");
            }

            // clear setting temporarily used for window state
            if (prevInstalledVersion < 157) {
                SharedPreferences appPrefs = getAppStateSharedPreferences();
                if (appPrefs.contains("screenStateArray")) {
                    Log.d(TAG, "Removing screenStateArray");
                    appPrefs.edit()
                            .remove("screenStateArray")
                            .commit();
                }
            }


            editor.putInt("version", CommonUtils.getApplicationVersionNumber());
            editor.commit();
            Log.d(TAG, "Finished all Upgrading");
        }
    }

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
                    msg = getString(R.string.error_occurred);
                } else if (!StringUtils.isEmpty(ev.getMessage())) {
                    msg = ev.getMessage();
                } else if (ev.getException() != null && StringUtils.isEmpty(ev.getException().getMessage())) {
                    msg = ev.getException().getMessage();
                } else {
                    msg = getString(R.string.error_occurred);
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

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate");
        super.onTerminate();
    }

    // difficult to show dialogs during Activity onCreate so save it until later
    public int getErrorDuringStartup() {
        return errorDuringStartup;
    }

    public SharedPreferences getAppStateSharedPreferences() {
        return getSharedPreferences(saveStateTag, 0);
    }
}
