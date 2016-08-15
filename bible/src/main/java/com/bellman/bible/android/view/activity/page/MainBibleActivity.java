package com.bellman.bible.android.view.activity.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.BibleContentManager;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.PassageChangeMediator;
import com.bellman.bible.android.control.event.apptobackground.AppToBackgroundEvent;
import com.bellman.bible.android.control.event.passage.PassageChangeStartedEvent;
import com.bellman.bible.android.control.event.passage.PassageChangedEvent;
import com.bellman.bible.android.control.event.passage.PreBeforeCurrentPageChangeEvent;
import com.bellman.bible.android.control.event.window.CurrentWindowChangedEvent;
import com.bellman.bible.android.control.page.CurrentPage;
import com.bellman.bible.android.control.page.window.WindowControl;
import com.bellman.bible.android.view.activity.StartupActivity;
import com.bellman.bible.android.view.activity.base.CustomTitlebarActivityBase;
import com.bellman.bible.android.view.activity.page.actionbar.BibleActionBarManager;
import com.bellman.bible.android.view.activity.page.screen.DocumentViewManager;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.device.ScreenSettings;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

import de.greenrobot.event.EventBus;

/**
 * The main activity screen showing Bible text
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class MainBibleActivity extends CustomTitlebarActivityBase implements VerseActionModeMediator.ActionModeMenuDisplay {

    public static final String EXTRA_URI = "extra_uri";
    private static final String TAG = "MainBibleActivity";
    private static BibleActionBarManager bibleActionBarManager = new BibleActionBarManager();
    private DocumentViewManager documentViewManager;
    private BibleContentManager bibleContentManager;
    private WindowControl windowControl;
    // handle requests from main menu
    private MenuCommandHandler mainMenuCommandHandler;
    // detect swipe left/right
    private GestureDetectorCompat gestureDetector;

    private boolean mWholeAppWasInBackground = false;

    // swipe fails on older versions of Android (2.2, 2.3, but not 3.0+) if event not passed to parent - don't know why
    // scroll occurs on later versions after double-tap maximize
    private boolean alwaysDispatchTouchEventToSuper = !CommonUtils.isHoneycombPlus();
    private Intent mIntent;

    public MainBibleActivity() {
        super(bibleActionBarManager, R.menu.main);
    }

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating MainBibleActivity");
        super.onCreate(savedInstanceState, true);
        mIntent = getIntent();
        setContentView(R.layout.main_bible_view);

        ControlFactory.getInstance().provide(this);

        // create related objects
        BibleGestureListener gestureListener = new BibleGestureListener(MainBibleActivity.this);
        gestureDetector = new GestureDetectorCompat(this, gestureListener);

        windowControl = ControlFactory.getInstance().getWindowControl();

        documentViewManager = new DocumentViewManager(this);
        documentViewManager.buildView();

        bibleContentManager = new BibleContentManager(documentViewManager);

        mainMenuCommandHandler = new MenuCommandHandler(this);

        // register for passage change and appToBackground events
        EventBus.getDefault().register(this);
        // force the screen to be populated
        //th Passage Change Mediator class inits the whole bible layout
        PassageChangeMediator.getInstance().forcePageUpdate();
        if (mIntent.hasExtra(StartupActivity.EXTRA_BIBLE_URI)) {
            String uri = mIntent.getStringExtra(StartupActivity.EXTRA_BIBLE_URI).toString();
            BibleRef ref = getAllRefFromUri(uri);

            try {

                BibleBook book = BibleBook.values()[ref.getBook()];
                Versification v11n = ControlFactory.getInstance().getNavigationControl().getVersification();
                ControlFactory.getInstance().getCurrentPageControl().getCurrentBible().setKey(new Verse(v11n, book,
                        ref.getChapter(), ref.getVerse()));
            } catch (Exception e) {

                Log.e(TAG, "error on select of bible book", e);
            }
        }


    }

    private BibleRef getAllRefFromUri(String uri) {
        String usefulPath = StringUtils.substring(uri.toString(), 17);
        String[] spStr = StringUtils.split(usefulPath, "/");
        int bookNo = getBibleBookNo(spStr[0]);

        BibleRef ref = new BibleRef(bookNo, Integer.parseInt(spStr[1]), Integer.parseInt(spStr[2]));
        Log.d(TAG, ref.toString());
        return ref;

    }

    private int getBibleBookNo(String s) {
        switch (s) {
            case "Intro.Bible":
                return 0;
            case "Intro_OT":
                return 1;

            case "Gen":
                return 2;

            case "Exod":
                return 3;

            case "Lev":
                return 4;

            case "Num":
                return 5;

            case "Deut":
                return 6;

            case "Josh":
                return 7;

            case "Judge":
                return 8;

            case "Ruth":
                return 9;

            case "1Sam":
                return 10;

            case "2Sam":

                return 11;
            case "1Kgs":
                return 12;

            case "2Kgs":
                return 13;

            case "1Chr":
                return 14;

            case "2Chr":
                return 15;

            case "Ezra":
                return 16;

            case "Neh":
                return 17;

            case "Esth":
                return 18;

            case "Job":
                return 19;

            case "Ps":
                return 20;

            case "Prov":
                return 21;

            case "Eccl":
                return 22;

            case "Song":
                return 23;

            case "Isa":
                return 24;

            case "Jer":
                return 25;

            case "Lam":
                return 26;

            case "Ezek":
                return 27;

            case "Dan":
                return 28;

            case "Hos":
                return 29;

            case "Joel":
                return 30;

            case "Amos":
                return 31;

            case "Obad":
                return 32;

            case "Jonah":
                return 33;

            case "Mic":
                return 34;

            case "Nah":
                return 35;

            case "Hab":
                return 36;

            case "Zeph":
                return 37;

            case "Hag":
                return 38;

            case "Zech":
                return 39;

            case "Mal":
                return 40;

            case "Intro.NT":
                return 41;

            case "Matt":
                return 42;

            case "Mark":
                return 43;

            case "Luke":
                return 44;

            case "John":
                return 45;

            case "Acts":
                return 46;

            case "Rom":
                return 47;

            case "1Cor":
                return 48;

            case "2Cor":
                return 49;

            case "Gal":
                return 50;

            case "Eph":
                return 51;

            case "Phil":
                return 52;

            case "Col":
                return 53;

            case "1Thess":
                return 54;

            case "2Thess":
                return 55;

            case "1Tim":
                return 56;

            case "2Tim":
                return 57;

            case "Titus":
                return 58;

            case "Phlm":
                return 59;

            case "Heb":
                return 60;

            case "Jas":
                return 61;

            case "1Pet":
                return 62;

            case "2Pet":
                return 63;

            case "1John":
                return 64;

            case "2John":
                return 65;

            case "3John":
                return 65;

            case "Jude":
                return 67;

            case "Rev":
                return 68;

            default:
                return 2;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * called if the app is re-entered after returning from another app.
     * Trigger redisplay in case mobile has gone from light to dark or vice-versa
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        if (mWholeAppWasInBackground) {
            mWholeAppWasInBackground = false;
            refreshIfNightModeChange();
        }
    }

    /**
     * Need to know when app is returned to foreground to check the screen colours
     */
    public void onEvent(AppToBackgroundEvent event) {
        if (event.isMovedToBackground()) {
            mWholeAppWasInBackground = true;
        }
    }

    @Override
    protected void onScreenTurnedOff() {
        super.onScreenTurnedOff();
        documentViewManager.getDocumentView().onScreenTurnedOff();
    }

    @Override
    protected void onScreenTurnedOn() {
        super.onScreenTurnedOn();
        refreshIfNightModeChange();
        documentViewManager.getDocumentView().onScreenTurnedOn();
    }

    /**
     * if using auto night mode then may need to refresh
     */
    private void refreshIfNightModeChange() {
        // colour may need to change which affects View colour and html
        // first refresh the night mode setting using light meter if appropriate
        if (ScreenSettings.isNightModeChanged()) {
            // then update text if colour changed
            documentViewManager.getDocumentView().changeBackgroundColour();
            PassageChangeMediator.getInstance().forcePageUpdate();
        }

    }

    /**
     * adding android:configChanges to manifest causes this method to be called on flip, etc instead of a new instance and onCreate, which would cause a new observer -> duplicated threads
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // essentially if the current page is Bible then we need to recalculate verse offsets
        // if not then don't redisplay because it would force the page to the top which would be annoying if you are half way down a gen book page
        if (!ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().isSingleKey()) {
            // force a recalculation of verse offsets
            PassageChangeMediator.getInstance().forcePageUpdate();
        } else if (windowControl.isMultiWindow()) {
            // need to layout multiple windows differently
            windowControl.orientationChange();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "Keycode:" + keyCode);
        // common key handling i.e. KEYCODE_DPAD_RIGHT & KEYCODE_DPAD_LEFT
        if (BibleKeyHandler.getInstance().onKeyUp(keyCode, event)) {
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_SEARCH && ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().isSearchable())) {
            Intent intent = ControlFactory.getInstance().getSearchControl().getSearchIntent(ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().getCurrentDocument());
            if (intent != null) {
                startActivityForResult(intent, STD_REQUEST_CODE);
            }
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * on Click handlers.  Go through each handler until one returns true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mainMenuCommandHandler.handleMenuRequest(item) ||
                super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Activity result:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (mainMenuCommandHandler.restartIfRequiredOnReturn(requestCode)) {
            // restart done in above
        } else if (mainMenuCommandHandler.isDisplayRefreshRequired(requestCode)) {
            preferenceSettingsChanged();
        } else if (mainMenuCommandHandler.isDocumentChanged(requestCode)) {
            updateActionBarButtons();
        }
    }

    @Override
    protected void preferenceSettingsChanged() {
        documentViewManager.getDocumentView().applyPreferenceSettings();
        PassageChangeMediator.getInstance().forcePageUpdate();
    }

    /**
     * allow current page to save any settings or data before being changed
     */
    public void onEvent(PreBeforeCurrentPageChangeEvent event) {
        CurrentPage currentPage = ControlFactory.getInstance().getCurrentPageControl().getCurrentPage();
        if (currentPage != null) {
            // save current scroll position so history can return to correct place in document
            float screenPosn = getCurrentPosition();
            currentPage.setCurrentYOffsetRatio(screenPosn);
        }
    }

    public void onEvent(CurrentWindowChangedEvent event) {
        MainBibleActivity.this.updateActionBarButtons();

        // onPrepareOptionsMenu only called once on Android 2.2, 2.3, 3.0: http://stackoverflow.com/questions/29925104/onprepareoptionsmenu-only-called-once-on-android-2-3
        // so forcefully invalidate it on old versions
        if (!CommonUtils.isIceCreamSandwichPlus()) {
            supportInvalidateOptionsMenu();
        }
    }

    /**
     * called just before starting work to change the current passage
     */
    public void onEventMainThread(PassageChangeStartedEvent event) {
        documentViewManager.buildView();
        setProgressBar(true);
    }

    /**
     * called by PassageChangeMediator after a new passage has been changed and displayed
     */
    public void onEventMainThread(PassageChangedEvent event) {
        setProgressBar(false);
        updateActionBarButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // allow webView to start monitoring tilt by setting focus which causes tilt-scroll to resume
        documentViewManager.getDocumentView().asView().requestFocus();
    }

    /**
     * Some menu items must be hidden for certain document types
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // construct the options menu
        super.onPrepareOptionsMenu(menu);

        // disable some options depending on document type
        ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().updateOptionsMenu(menu);

        // if there is no backup file then disable the restore menu item
        ControlFactory.getInstance().getBackupControl().updateOptionsMenu(menu);

        // set Synchronised checkbox correctly
        ControlFactory.getInstance().getWindowControl().updateOptionsMenu(menu);

        // must return true for menu to be displayed
        return true;
    }

    /**
     * Event raised by javascript as a result of longtap
     */
    @Override
    public void showVerseActionModeMenu(final ActionMode.Callback actionModeCallbackHandler) {
        Log.d(TAG, "showVerseActionModeMenu");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ActionMode actionMode = startSupportActionMode(actionModeCallbackHandler);

                // Fix for onPrepareActionMode not being called: https://code.google.com/p/android/issues/detail?id=159527
                if (actionMode != null) {
                    actionMode.invalidate();
                }
            }
        });
    }

    public void clearVerseActionMode(final ActionMode actionMode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                actionMode.finish();
            }
        });
    }

    /**
     * return percentage scrolled down page
     */
    public float getCurrentPosition() {
        return documentViewManager.getDocumentView().getCurrentPosition();
    }

    /**
     * user swiped right
     */
    public void next() {
        if (getDocumentViewManager().getDocumentView().isPageNextOkay()) {
            ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().next();
        }
    }

    /**
     * user swiped left
     */
    public void previous() {
        if (getDocumentViewManager().getDocumentView().isPagePreviousOkay()) {
            ControlFactory.getInstance().getCurrentPageControl().getCurrentPage().previous();
        }
    }

    // handle swipe left and right
    // http://android-journey.blogspot.com/2010_01_01_archive.html
    //http://android-journey.blogspot.com/2010/01/android-gestures.html
    // above dropped in favour of simpler method below
    //http://developer.motorola.com/docstools/library/The_Widget_Pack_Part_3_Swipe/
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        // should only call super if below returns false
        if (this.gestureDetector.onTouchEvent(motionEvent) && !alwaysDispatchTouchEventToSuper) {
            return true;
        } else {
            return super.dispatchTouchEvent(motionEvent);
        }
    }

    protected DocumentViewManager getDocumentViewManager() {
        return documentViewManager;
    }

    protected BibleContentManager getBibleContentManager() {
        return bibleContentManager;
    }
}