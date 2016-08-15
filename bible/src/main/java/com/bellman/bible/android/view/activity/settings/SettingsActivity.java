package com.bellman.bible.android.view.activity.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.page.PageTiltScrollControl;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.android.view.activity.base.Dialogs;
import com.bellman.bible.android.view.util.UiUtils;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.device.ScreenSettings;
import com.bellman.bible.service.device.ScreenTimeoutSettings;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * show settings
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String LOCALE_PREF = "locale_pref";

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // change theme according to light sensor
        UiUtils.applyTheme(this);

        super.onCreate(savedInstanceState);

        // allow partial integration with Embedded Bible framework - without this TTS stops
        // putting this before the below ensures any error dialog will be displayed in front of the settings screen and not the previous screen
        // see onStop for paired iAmNoLongerCurrent method call
        CurrentActivityHolder.getInstance().setCurrentActivity(this);

        try {
            addPreferencesFromResource(R.xml.settings);

            //If no light sensor exists switch to old boolean check box
            // see here for method: http://stackoverflow.com/questions/4081533/how-to-remove-android-preferences-from-the-screen
            Preference unusedNightModePreference = getPreferenceScreen().findPreference(ScreenSettings.getUnusedNightModePreferenceKey());
            getPreferenceScreen().removePreference(unusedNightModePreference);

            // if no tilt sensor then remove tilt-to-scroll setting
            if (!PageTiltScrollControl.isTiltSensingPossible()) {
                Preference tiltToScrollPreference = getPreferenceScreen().findPreference(PageTiltScrollControl.TILT_TO_SCROLL_PREFERENCE_KEY);
                getPreferenceScreen().removePreference(tiltToScrollPreference);
            }

            // only JellyBean supports Malayalam so remove ml for older versions of Android
            if (!CommonUtils.isJellyBeanPlus()) {
                ListPreference localePref = (ListPreference) getPreferenceScreen().findPreference(LOCALE_PREF);
                CharSequence[] entries = localePref.getEntries();
                CharSequence[] entryValues = localePref.getEntryValues();
                int mlIndex = ArrayUtils.indexOf(entryValues, "ml");
                if (mlIndex != -1) {
                    Log.d(TAG, "removing Malayalam from preference list");
                    localePref.setEntries(ArrayUtils.remove(entries, mlIndex));
                    localePref.setEntryValues(ArrayUtils.remove(entryValues, mlIndex));
                }
            }

            addScreenTimeoutSettings();

        } catch (Exception e) {
            Log.e(TAG, "Error preparing preference screen", e);
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
        }
    }

    private void addScreenTimeoutSettings() {
        ListPreference timeoutPref = (ListPreference) getPreferenceScreen().findPreference(ScreenTimeoutSettings.SCREEN_TIMEOUT_PREF);

        timeoutPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // update screen timeout
                try {
                    if (StringUtils.isNotEmpty((String) newValue)) {
                        ScreenTimeoutSettings.setScreenTimeout(Integer.parseInt((String) newValue));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating timeout set in preference screen", e);
                }

                return true;
            }
        });

        ScreenTimeoutSettings screenTimeoutSettings = new ScreenTimeoutSettings();
        timeoutPref.setEntries(screenTimeoutSettings.getPreferenceEntries());
        timeoutPref.setEntryValues(screenTimeoutSettings.getPreferenceEntryValues());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getLocalClassName(), "onStop");
        // call this onStop, although it is not guaranteed to be called, to ensure an overlap between dereg and reg of current activity, otherwise AppToBackground is fired mistakenly
        CurrentActivityHolder.getInstance().iAmNoLongerCurrent(this);
    }

}
