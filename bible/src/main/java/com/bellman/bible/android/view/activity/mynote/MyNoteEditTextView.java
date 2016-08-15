package com.bellman.bible.android.view.activity.mynote;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.event.passage.BeforeCurrentPageChangeEvent;
import com.bellman.bible.android.control.mynote.MyNote;
import com.bellman.bible.android.view.activity.base.DocumentView;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.device.ScreenSettings;

import de.greenrobot.event.EventBus;

/**
 * Show a User Note and allow view/edit
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public class MyNoteEditTextView extends EditText implements DocumentView {

    @SuppressWarnings("unused")
    private static final String TAG = "MyNoteEditTextView";
    private MyNote myNoteControl = ControlFactory.getInstance().getMyNoteControl();

    public MyNoteEditTextView(Context context) {
        super(context);
        setSingleLine(false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
        setGravity(Gravity.TOP);

        applyPreferenceSettings();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // register for passage change events
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // register for passage change events
        EventBus.getDefault().unregister(this);
    }

    /**
     * allow current page to save any settings or data before being changed
     */
    public void onEvent(BeforeCurrentPageChangeEvent event) {
        // force MyNote.save if in MyNote and suddenly change to another view
        save();
    }

    private void save() {
        myNoteControl.saveMyNoteText(getText().toString());
    }

    @Override
    public void show(String html, int jumpToVerse, float jumpToYOffsetRatio) {
        applyPreferenceSettings();
        setText(html);
    }

    @Override
    public void applyPreferenceSettings() {
        changeBackgroundColour();

        SharedPreferences preferences = CommonUtils.getSharedPreferences();
        int fontSize = preferences.getInt("text_size_pref", 16);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
    }

    @Override
    public boolean changeBackgroundColour() {
        if (ScreenSettings.isNightMode()) {
            setBackgroundColor(Color.BLACK);
            setTextColor(Color.WHITE);
        } else {
            setBackgroundColor(Color.WHITE);
            setTextColor(Color.BLACK);
        }
        // should not return false but it is used to see if text needs refreshing, which it doesn't
        return false;
    }

    public boolean isPageNextOkay() {
        return false;
    }

    public boolean isPagePreviousOkay() {
        return false;
    }

    @Override
    public boolean pageDown(boolean toBottom) {
        return false;
    }

    @Override
    public float getCurrentPosition() {
        return 0;
    }

    @Override
    public View asView() {
        return this;
    }

    @Override
    public void onScreenTurnedOn() {
        // NOOP
    }

    @Override
    public void onScreenTurnedOff() {
        // NOOP
    }
}
