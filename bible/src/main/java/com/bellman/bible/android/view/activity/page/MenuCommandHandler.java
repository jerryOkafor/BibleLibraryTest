package com.bellman.bible.android.view.activity.page;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.download.DownloadControl;
import com.bellman.bible.android.view.activity.StartupActivity;
import com.bellman.bible.android.view.activity.base.ActivityBase;
import com.bellman.bible.android.view.activity.bookmark.Bookmarks;
import com.bellman.bible.android.view.activity.download.Download;
import com.bellman.bible.android.view.activity.help.Help;
import com.bellman.bible.android.view.activity.installzip.InstallZip;
import com.bellman.bible.android.view.activity.mynote.MyNotes;
import com.bellman.bible.android.view.activity.navigation.History;
import com.bellman.bible.android.view.activity.page.screen.WindowMenuCommandHandler;
import com.bellman.bible.android.view.activity.readingplan.DailyReading;
import com.bellman.bible.android.view.activity.readingplan.ReadingPlanSelectorList;
import com.bellman.bible.android.view.activity.settings.SettingsActivity;
import com.bellman.bible.android.view.activity.speak.Speak;
import com.bellman.bible.service.common.CommonUtils;

/**
 * Handle requests from the main menu
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class MenuCommandHandler {

    // request codes passed to and returned from sub-activities
    public static final int REFRESH_DISPLAY_ON_FINISH = 2;
    public static final int UPDATE_SUGGESTED_DOCUMENTS_ON_FINISH = 3;
    private static final String TAG = "MainMenuCommandHandler";
    private DownloadControl downloadControl;
    private MainBibleActivity callingActivity;
    private WindowMenuCommandHandler windowMenuCommandHandler;
    private String mPrevLocalePref = "";

    public MenuCommandHandler(MainBibleActivity activity) {
        super();
        this.callingActivity = activity;
        this.windowMenuCommandHandler = new WindowMenuCommandHandler();

        ControlFactory controlFactory = ControlFactory.getInstance();
        downloadControl = controlFactory.getDownloadControl();
    }

    /**
     * on Click handlers
     */
    public boolean handleMenuRequest(MenuItem menuItem) {
        boolean isHandled = false;

        // Activities
        {
            Intent handlerIntent = null;
            int requestCode = ActivityBase.STD_REQUEST_CODE;
            // Handle item selection
            int id = menuItem.getItemId();
            if (id == R.id.searchButton) {
                handlerIntent = ControlFactory
                        .getInstance()
                        .getSearchControl()
                        .getSearchIntent(ControlFactory.getInstance().getCurrentPageControl()
                                .getCurrentPage().getCurrentDocument());
            } else if (id == R.id.settingsButton) {
                handlerIntent = new Intent(callingActivity, SettingsActivity.class);
                // force the bible view to be refreshed after returning from settings screen because notes, verses, etc. may be switched on or off
                mPrevLocalePref = CommonUtils.getLocalePref();
                requestCode = REFRESH_DISPLAY_ON_FINISH;
            } else if (id == R.id.historyButton) {
                handlerIntent = new Intent(callingActivity, History.class);
            } else if (id == R.id.bookmarksButton) {
                handlerIntent = new Intent(callingActivity, Bookmarks.class);
            } else if (id == R.id.mynotesButton) {
                handlerIntent = new Intent(callingActivity, MyNotes.class);
            } else if (id == R.id.speakButton) {
                handlerIntent = new Intent(callingActivity, Speak.class);
            } else if (id == R.id.dailyReadingPlanButton) {
                // show todays plan or allow plan selection
                if (ControlFactory.getInstance().getReadingPlanControl().isReadingPlanSelected()) {
                    handlerIntent = new Intent(callingActivity, DailyReading.class);
                } else {
                    handlerIntent = new Intent(callingActivity, ReadingPlanSelectorList.class);
                }
            } else if (id == R.id.downloadButton) {
                if (downloadControl.checkDownloadOkay()) {
                    handlerIntent = new Intent(callingActivity, Download.class);
                    requestCode = UPDATE_SUGGESTED_DOCUMENTS_ON_FINISH;
                }
            } else if (id == R.id.installZipButton) {
                handlerIntent = new Intent(callingActivity, InstallZip.class);
            } else if (id == R.id.helpButton) {
                handlerIntent = new Intent(callingActivity, Help.class);
            } else if (id == R.id.backup) {
                ControlFactory.getInstance().getBackupControl().backupDatabase();
                isHandled = true;
            } else if (id == R.id.restore) {
                ControlFactory.getInstance().getBackupControl().restoreDatabase();
                isHandled = true;
            }

            if (!isHandled) {
                isHandled = windowMenuCommandHandler.handleMenuRequest(menuItem);
            }

            if (handlerIntent != null) {
                callingActivity.startActivityForResult(handlerIntent, requestCode);
                isHandled = true;
            }
        }

        return isHandled;
    }

    public boolean restartIfRequiredOnReturn(int requestCode) {
        if (requestCode == MenuCommandHandler.REFRESH_DISPLAY_ON_FINISH) {
            Log.i(TAG, "Refresh on finish");
            if (!CommonUtils.getLocalePref().equals(mPrevLocalePref)) {
                // must restart to change locale
                PendingIntent pendingIntent;
                if (CommonUtils.isIceCreamSandwichPlus()) {
                    // works on 4.x but not on 2.1
                    Intent startupIntent = new Intent("com.bellman.bible.android.activity.StartupActivity.class");
                    pendingIntent = PendingIntent.getActivity(callingActivity.getBaseContext(), 0, startupIntent, 0);
                } else {
                    //works on 2.1 but scroll errors on 4.x
                    Intent startupIntent = new Intent(callingActivity.getBaseContext(), StartupActivity.class);
                    //noinspection ResourceType
                    pendingIntent = PendingIntent.getActivity(callingActivity.getBaseContext(), 0, startupIntent, callingActivity.getIntent().getFlags());
                }
                AlarmManager mgr = (AlarmManager) callingActivity.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
                System.exit(2);
                return true;
            }
        }
        return false;
    }

    public boolean isDisplayRefreshRequired(int requestCode) {
        return requestCode == MenuCommandHandler.REFRESH_DISPLAY_ON_FINISH;
    }

    public boolean isDocumentChanged(int requestCode) {
        return requestCode == MenuCommandHandler.UPDATE_SUGGESTED_DOCUMENTS_ON_FINISH;
    }

}
