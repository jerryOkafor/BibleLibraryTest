package com.bellman.bible.android.control;

import com.bellman.bible.service.history.HistoryManager;
import com.bellman.bible.service.sword.SwordDocumentFacade;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Support initialisation as i) do it now or ii) do this eventually
 */
public class Initialisation {

    private static final long INITIALISE_DELAY = 3000;
    private static Initialisation singleton = new Initialisation();
    private boolean isInitialised = false;

    private Initialisation() {
    }

    public static Initialisation getInstance() {
        return singleton;
    }

    /**
     * Allow Splash screen to be displayed if starting from scratch, otherwise, if returning to an Activity then ensure all initialisation occurs eventually.
     */
    public void initialiseEventually() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                initialiseNow();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, INITIALISE_DELAY);
    }

    /**
     * Call any init routines that must be called at least once near the start of running the app e.g. start HistoryManager
     */
    public synchronized void initialiseNow() {
        if (!isInitialised) {
            // force early initialisation of Control factory to prevent circular dependencies
            ControlFactory.getInstance();

            // force Sword to initialise itself
            SwordDocumentFacade.getInstance().getBibles();

            // Now initialise other system features

            // Initialise HistoryManager
            HistoryManager.getInstance().initialise();

            // needs to register a listener
            ControlFactory.getInstance().getDocumentBibleBooksFactory().initialise();

            isInitialised = true;
        }
    }
}
