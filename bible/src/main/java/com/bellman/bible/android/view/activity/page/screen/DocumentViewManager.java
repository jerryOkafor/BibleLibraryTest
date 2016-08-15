package com.bellman.bible.android.view.activity.page.screen;

import android.app.Activity;
import android.widget.LinearLayout;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.event.window.NumberOfWindowsChangedEvent;
import com.bellman.bible.android.control.page.window.Window;
import com.bellman.bible.android.control.page.window.WindowControl;
import com.bellman.bible.android.view.activity.base.DocumentView;
import com.bellman.bible.android.view.activity.mynote.MyNoteViewBuilder;

import de.greenrobot.event.EventBus;

/**
 * Create Views for displaying documents
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public class DocumentViewManager {

    private DocumentWebViewBuilder documentWebViewBuilder;
    private MyNoteViewBuilder myNoteViewBuilder;
    private Activity mainActivity;
    private LinearLayout parent;

    private WindowControl windowControl;

    public DocumentViewManager(Activity mainActivity) {
        this.mainActivity = mainActivity;
        documentWebViewBuilder = new DocumentWebViewBuilder(this.mainActivity);
        myNoteViewBuilder = new MyNoteViewBuilder(this.mainActivity);
        this.parent = (LinearLayout) mainActivity.findViewById(R.id.mainBibleView);
        windowControl = ControlFactory.getInstance().getWindowControl();

        EventBus.getDefault().register(this);
    }

    public void onEvent(NumberOfWindowsChangedEvent event) {
        buildView();
    }

    public synchronized void buildView() {
        if (myNoteViewBuilder.isMyNoteViewType()) {
            documentWebViewBuilder.removeWebView(parent);
            myNoteViewBuilder.addMyNoteView(parent);
        } else {
            myNoteViewBuilder.removeMyNoteView(parent);
            documentWebViewBuilder.addWebView(parent);
        }
    }

    public DocumentView getDocumentView() {
        return getDocumentView(windowControl.getActiveWindow());
    }

    public DocumentView getDocumentView(Window window) {
        if (myNoteViewBuilder.isMyNoteViewType()) {
            return myNoteViewBuilder.getView();
        } else {
            // a specific screen is specified to prevent content going to wrong screen if active screen is changed fast
            return documentWebViewBuilder.getView(window);
        }
    }
}
