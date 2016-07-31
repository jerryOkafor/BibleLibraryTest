package com.bellman.bible.android.control.event.window;


import com.bellman.bible.android.control.window.Window;

/**
 * Correct bible page is shown but need to scroll to a different verse
 */
public class ScrollSecondaryWindowEvent implements WindowEvent {

	private final Window window;
	private final int verseNo;
	
	public ScrollSecondaryWindowEvent(Window window, int verseNo) {
		this.window = window;
		this.verseNo = verseNo;
	}

	public Window getWindow() {
		return window;
	}

	public int getVerseNo() {
		return verseNo;
	}
}
