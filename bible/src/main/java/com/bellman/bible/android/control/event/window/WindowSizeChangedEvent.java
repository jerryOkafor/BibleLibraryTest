package com.bellman.bible.android.control.event.window;


import com.bellman.bible.android.control.window.Window;

import java.util.Map;

/**
 * Window size changed - often due to separator being moved
 */
public class WindowSizeChangedEvent implements WindowEvent {

	private boolean isFinished;
	private Map<Window, Integer> screenVerseMap;
	
	public WindowSizeChangedEvent(boolean isFinished, Map<Window, Integer> screenVerseMap) {
		this.isFinished = isFinished;
		this.screenVerseMap = screenVerseMap;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public boolean isVerseNoSet(Window window) {
		return screenVerseMap.containsKey(window);
	}

	public Integer getVerseNo(Window window) {
		return screenVerseMap.get(window);
	}
}
