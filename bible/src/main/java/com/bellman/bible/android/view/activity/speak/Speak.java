package com.bellman.bible.android.view.activity.speak;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.speak.NumPagesToSpeakDefinition;
import com.bellman.bible.android.control.speak.SpeakControl;
import com.bellman.bible.android.view.activity.base.CustomTitlebarActivityBase;
import com.bellman.bible.android.view.activity.base.Dialogs;

/**
 * Allow user to enter search criteria
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class Speak extends CustomTitlebarActivityBase {

    private static final String TAG = "Speak";
    private NumPagesToSpeakDefinition[] numPagesToSpeakDefinitions;
    private CheckBox mQueueCheckBox;
    private CheckBox mRepeatCheckBox;
    private SpeakControl speakControl;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Displaying Search view");
        setContentView(R.layout.speak);

        speakControl = ControlFactory.getInstance().getSpeakControl();

        // set title of chapter/verse/page selection
        numPagesToSpeakDefinitions = speakControl.getNumPagesToSpeakDefinitions();

        // set a suitable prompt for the different numbers of chapters
        for (NumPagesToSpeakDefinition defn : numPagesToSpeakDefinitions) {
            RadioButton numChaptersCheckBox = (RadioButton) findViewById(defn.getRadioButtonId());

            numChaptersCheckBox.setText(defn.getPrompt());
        }

        // set defaults for Queue and Repeat
        mQueueCheckBox = (CheckBox) findViewById(R.id.queue);
        mRepeatCheckBox = (CheckBox) findViewById(R.id.repeat);
        mQueueCheckBox.setChecked(true);
        mRepeatCheckBox.setChecked(false);

        Log.d(TAG, "Finished displaying Speak view");
    }

    public void onRewind(View v) {
        try {
            speakControl.rewind();
        } catch (Exception e) {
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
        }
    }

    public void onStop(View v) {
        try {
            speakControl.stop();
        } catch (Exception e) {
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
        }
    }

    public void onPause(View v) {
        try {
            speakControl.pause();
        } catch (Exception e) {
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
        }
    }

    public void onSpeak(View v) {
        try {
            if (speakControl.isPaused()) {
                speakControl.continueAfterPause();
            } else {
                speakControl.speak(getSelectedNumPagesToSpeak(), isQueue(), isRepeat());
            }
        } catch (Exception e) {
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
        }
    }

    public void onForward(View v) {
        try {
            speakControl.forward();
        } catch (Exception e) {
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
        }
    }

    private NumPagesToSpeakDefinition getSelectedNumPagesToSpeak() {
        RadioGroup chaptersRadioGroup = (RadioGroup) findViewById(R.id.numChapters);
        int selectedId = chaptersRadioGroup.getCheckedRadioButtonId();

        for (NumPagesToSpeakDefinition defn : numPagesToSpeakDefinitions) {
            if (selectedId == defn.getRadioButtonId()) {
                return defn;
            }
        }
        // error - should not get here
        return numPagesToSpeakDefinitions[0];
    }

    private boolean isQueue() {
        return mQueueCheckBox.isChecked();
    }

    private boolean isRepeat() {
        return mRepeatCheckBox.isChecked();
    }
}
