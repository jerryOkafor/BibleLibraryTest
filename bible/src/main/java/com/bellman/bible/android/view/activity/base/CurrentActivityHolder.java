package com.bellman.bible.android.view.activity.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bellman.bible.android.Bible;
import com.bellman.bible.android.control.event.apptobackground.AppToBackgroundEvent;
import com.bellman.bible.android.control.event.apptobackground.AppToBackgroundEvent.Position;

import de.greenrobot.event.EventBus;

/** Allow operations form middle tier that require a reference to the current Activity
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class CurrentActivityHolder {


	// this was moved from the MainBibleActivity and has always been called this
	private static final String saveStateTag = "MainBibleActivity";
	private static final CurrentActivityHolder singleton = new CurrentActivityHolder();
	private static final String TAG = "CurrentActivityHolder";
	private Activity currentActivity;
	private boolean appIsInForeground = false;
	
	public static CurrentActivityHolder getInstance() {
		return singleton;
	}

	public Activity getCurrentActivity() {
		return currentActivity;
	}
	
	public void setCurrentActivity(Activity activity) {
		currentActivity = activity;

		// if activity changes then app must be in foreground so use this to trigger appToForeground event if it was in background
		appIsNowInForeground();
	}

	public void iAmNoLongerCurrent(Activity activity) {
		// if the next activity has not already overwritten my registration 
		if (currentActivity!=null && currentActivity.equals(activity)) {
			Log.w(TAG, "Temporarily null current ativity");
			currentActivity = null;
			if (appIsInForeground) {
				appIsInForeground = false;
				EventBus.getDefault().post(new AppToBackgroundEvent(Position.BACKGROUND));
			}
		}
	}
	
	/** really need to check for app being restored after an exit
	 */
	private void appIsNowInForeground() {
		if (!appIsInForeground) {
			Log.d(TAG, "AppIsInForeground firing event");
			appIsInForeground = true;
			EventBus.getDefault().post(new AppToBackgroundEvent(Position.FOREGROUND));
		}
	}
	
	/** convenience task with error checking
	 */
	public void runOnUiThread(Runnable runnable) {
		Activity activity = getCurrentActivity();
		if (activity!=null) {
			getCurrentActivity().runOnUiThread(runnable);
		}
	}


	public Context getContext() {
		return Bible.getInstance().getApplication();
	}

	public Application getApplication() {
		return Bible.getInstance().getApplication();
	}

	public SharedPreferences getAppStateSharedPreferences() {
		return currentActivity.getSharedPreferences(saveStateTag, 0);
	}
}
