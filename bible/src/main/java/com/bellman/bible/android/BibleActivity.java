package com.bellman.bible.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;

import com.bellman.bible.R;
import com.bellman.bible.android.control.document.VerseActionModeMediator;

public class BibleActivity extends AppCompatActivity implements VerseActionModeMediator.ActionModeMenuDisplay {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public static Intent createIntent(Context context, String mUri) {
        return new Intent(context, BibleActivity.class);
    }

    @Override
    public void showVerseActionModeMenu(ActionMode.Callback actionModeCallbackHandler) {

    }

    @Override
    public void clearVerseActionMode(ActionMode actionMode) {

    }


    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }


    /**
     * A static method fro static get application calls
     *
     */

    public static Context getAppContext()
    {
        return getInstance().getApplicationContext();

    }
    /**
     * Retrieves the {@link BibleActivity} instance associated  the the specified app.
     */
    public static BibleActivity getInstance() {
        return new BibleActivity();
    }

    public BibleIntentBuilder createBibleIntentBuilder() {
        return new BibleIntentBuilder();
    }


    /**
     * Builder for the Bible to open a quote
     */

    public class BibleIntentBuilder {
        private String mUri;

        public BibleIntentBuilder() {

        }

        /**
         * Set the string uri
         */
        public BibleIntentBuilder setUri(String uri) {
            mUri = uri;
            return this;
        }

        public Intent build() {
            Context context = getApplicationContext();
            return BibleActivity.createIntent(
                    context, mUri);
        }

    }

}
