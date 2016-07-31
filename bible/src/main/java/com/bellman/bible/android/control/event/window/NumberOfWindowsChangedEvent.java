package com.bellman.bible.android.control.event.window;


import com.bellman.bible.android.control.window.Window;

import java.util.Map;

/**
 * 	Window has been minimized/restored/closed/added
 */
public class NumberOfWindowsChangedEvent implements WindowEvent {
	
	private Map<Window, Integer> screenVerseMap;

	public NumberOfWindowsChangedEvent(Map<Window, Integer> screenVerseMap) {
		this.screenVerseMap = screenVerseMap;
	}

	public boolean isVerseNoSet(Window window) {
		return screenVerseMap.containsKey(window);
	}

	public Integer getVerseNo(Window window) {
		return screenVerseMap.get(window);
	}
}
