package com.bellman.bible.android.control.window;


import org.crosswire.jsword.book.Book;

/**
 * Window used when user selects a link
 */
public class LinksWindow extends Window {

	// must be -ve so as not to interfere with incrementing window number sequence
	protected static final int DEDICATED_LINK_WINDOW_SCREEN_NO = -999;

	public LinksWindow(WindowLayout.WindowState windowState) {
		super(DEDICATED_LINK_WINDOW_SCREEN_NO, windowState);
		setSynchronised(false);
	}

	public LinksWindow() {
		super();
		setSynchronised(false);
	}

	@Override
	public boolean isLinksWindow() {
		return true;
	}

	/**
	 * If Links window is open then use its Bible else if closed then use Bible from active window
	 */
	protected Book getDefaultBible(Window activeWindow) {
		if (!isClosed()) {
    		return getPageManager().getCurrentBible().getCurrentDocument();
        } else {
    		return activeWindow.getPageManager().getCurrentBible().getCurrentDocument();
        }
	}

	/**
	 * Page state should reflect active window when links window is being used after being closed.  
	 * Not enough to select default bible because another module type may be selected in link.
	 */
	protected void initialisePageStateIfClosed(Window activeWindow) {
		// set links window state from active window if it was closed 
		if (getWindowLayout().getState().equals(WindowLayout.WindowState.CLOSED) && !activeWindow.isLinksWindow()) {
			// initialise links window documents from active window
			getPageManager().restoreState(activeWindow.getPageManager().getStateJson());
		}
	}
}
