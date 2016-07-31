package com.bellman.bible.android.view;

import android.app.Activity;
import android.widget.LinearLayout;

import com.bellman.bible.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.window.Window;
import com.bellman.bible.android.control.window.WindowControl;


/**
 * Create Views for displaying documents
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Martin Denham [mjdenham at gmail dot com]
 */
public class DocumentViewManager {

	private final WindowControl windowControl;
	private DocumentWebViewBuilder documentWebViewBuilder;
	private Activity mainActivity;
	private LinearLayout parent;

	
	public DocumentViewManager(Activity mainActivity) {
		this.mainActivity = mainActivity;
		documentWebViewBuilder = new DocumentWebViewBuilder(this.mainActivity);
		this.parent = (LinearLayout)mainActivity.findViewById(R.id.mainBibleView);
		windowControl = ControlFactory.getInstance().getWindowControl();
	}


	public synchronized void buildView() {
    		documentWebViewBuilder.addWebView(parent);

	}

	public DocumentView getDocumentView() {
		return getDocumentView(windowControl.getActiveWindow());
	}
	public DocumentView getDocumentView(Window window) {

			// a specific screen is specified to prevent content going to wrong screen if active screen is changed fast
			return documentWebViewBuilder.getView(window);

	}
}
