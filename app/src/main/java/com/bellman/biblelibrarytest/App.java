package com.bellman.biblelibrarytest;

import android.app.Application;

import com.bellman.bible.android.Bible;

/**
 * Created by Potencio on 9/18/2016.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Bible.getInstance().initAll(this);
    }
}
