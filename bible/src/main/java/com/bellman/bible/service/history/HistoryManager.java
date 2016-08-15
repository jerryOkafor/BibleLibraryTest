package com.bellman.bible.service.history;

import android.app.Activity;
import android.util.Log;

import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.event.ABEventBus;
import com.bellman.bible.android.control.event.passage.BeforeCurrentPageChangeEvent;
import com.bellman.bible.android.control.page.CurrentPage;
import com.bellman.bible.android.control.page.window.Window;
import com.bellman.bible.android.control.page.window.WindowControl;
import com.bellman.bible.android.view.activity.base.AndBibleActivity;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.android.view.activity.page.MainBibleActivity;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Application managed History List
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public class HistoryManager {

    private static final String TAG = "HistoryManager";
    private static int MAX_HISTORY = 80;
    private static HistoryManager singleton = new HistoryManager();
    private static WindowControl windowControl = ControlFactory.getInstance().getWindowControl();
    private Map<Window, Stack<HistoryItem>> screenHistoryStackMap = new HashMap<>();
    private boolean isGoingBack = false;

    private HistoryManager() {
    }

    public static HistoryManager getInstance() {
        return singleton;
    }

    public void initialise() {
        Log.i(TAG, "Registering HistoryManager with EventBus");
        // register for BeforePageCangeEvent
        ABEventBus.getDefault().safelyRegister(this);
    }

    /**
     * allow current page to save any settings or data before being changed
     */
    public void onEvent(BeforeCurrentPageChangeEvent event) {
        beforePageChange();
    }

    public boolean canGoBack() {
        return getHistoryStack().size() > 0;
    }

    /**
     * called when a verse is changed to allow current Activity to be saved in History list
     */
    public void beforePageChange() {
        // if we cause the change by requesting Back then ignore it
        if (!isGoingBack) {
            HistoryItem item = createHistoryItem();
            add(getHistoryStack(), item);
        }
    }

    private HistoryItem createHistoryItem() {
        HistoryItem historyItem = null;

        Activity currentActivity = CurrentActivityHolder.getInstance().getCurrentActivity();
        if (currentActivity instanceof MainBibleActivity) {
            CurrentPage currentPage = ControlFactory.getInstance().getCurrentPageControl().getCurrentPage();
            Book doc = currentPage.getCurrentDocument();
            if (currentPage.getKey() == null) {
                return null;
            }

            Key key = currentPage.getSingleKey();
            float yOffsetRatio = currentPage.getCurrentYOffsetRatio();
            historyItem = new KeyHistoryItem(doc, key, yOffsetRatio);
        } else if (currentActivity instanceof AndBibleActivity) {
            AndBibleActivity andBibleActivity = (AndBibleActivity) currentActivity;
            if (andBibleActivity.isIntegrateWithHistoryManager()) {
                historyItem = new IntentHistoryItem(currentActivity.getTitle(), ((AndBibleActivity) currentActivity).getIntentForHistoryList());
            }
        }
        return historyItem;
    }

    public void goBack() {
        if (getHistoryStack().size() > 0) {
            try {
                Log.d(TAG, "History size:" + getHistoryStack().size());
                isGoingBack = true;

                // pop the previous item
                HistoryItem previousItem = getHistoryStack().pop();

                if (previousItem != null) {
                    Log.d(TAG, "Going back to:" + previousItem);
                    previousItem.revertTo();

                    // finish current activity if not the Main screen
                    Activity currentActivity = CurrentActivityHolder.getInstance().getCurrentActivity();
                    if (!(currentActivity instanceof MainBibleActivity)) {
                        currentActivity.finish();
                    }
                }
            } finally {
                isGoingBack = false;
            }
        }
    }

    public List<HistoryItem> getHistory() {
        List<HistoryItem> allHistory = new ArrayList<HistoryItem>(getHistoryStack());
        // reverse so most recent items are at top rather than end
        Collections.reverse(allHistory);
        return allHistory;
    }

    /**
     * add item and check size of stack
     *
     * @param stack
     * @param item
     */
    private synchronized void add(Stack<HistoryItem> stack, HistoryItem item) {
        if (item != null) {
            if (stack.isEmpty() || !item.equals(stack.peek())) {
                Log.d(TAG, "Adding " + item + " to history");
                Log.d(TAG, "Stack size:" + stack.size());

                stack.push(item);

                while (stack.size() > MAX_HISTORY) {
                    Log.d(TAG, "Shrinking large stack");
                    stack.remove(0);
                }
            }
        }
    }

    private Stack<HistoryItem> getHistoryStack() {
        Window window = windowControl.getActiveWindow();
        Stack<HistoryItem> historyStack = screenHistoryStackMap.get(window);
        if (historyStack == null) {
            synchronized (screenHistoryStackMap) {
                historyStack = screenHistoryStackMap.get(window);
                if (historyStack == null) {
                    historyStack = new Stack<HistoryItem>();
                    screenHistoryStackMap.put(window, historyStack);
                }
            }
        }
        return historyStack;
    }
}
