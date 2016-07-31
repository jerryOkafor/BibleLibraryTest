package app;

import android.app.Application;

/**
 * Created by Potencio on 7/31/2016.
 */


public class Bible extends Application {

    private static Bible singleton;
    @Override
    public void onCreate() {
        super.onCreate();
        // save to a singleton to allow easy access from anywhere
        singleton = this;
    }

    public static Bible getApplication() {
        return singleton;
    }
}
