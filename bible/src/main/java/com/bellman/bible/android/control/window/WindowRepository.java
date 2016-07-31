package com.bellman.bible.android.control.window;

import android.content.SharedPreferences;

import com.bellman.bible.android.control.event.EventManager;
import com.bellman.bible.android.control.event.apptobackground.AppToBackgroundEvent;
import com.bellman.bible.android.control.event.window.CurrentWindowChangedEvent;
import com.bellman.bible.service.common.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class WindowRepository {

	// 1 based screen no
	private Window activeWindow;
	
	private List<Window> windowList;
	
	private LinksWindow dedicatedLinksWindow;
	
	private int maxWindowNoUsed = 0;
	
	private final Logger logger = new Logger(this.getClass().getName());
	
	public void initialise(EventManager eventManager) {
		dedicatedLinksWindow = new LinksWindow(WindowLayout.WindowState.CLOSED);

		windowList = new ArrayList<Window>();

		// restore state from previous invocation
    	restoreState();
    	setDefaultActiveWindow();
    	
		// listen for AppToBackgroundEvent to save state when moved to background
    	eventManager.register(this);
	}
	
	//TODO if user presses a link then should also show links window
	public List<Window> getWindows() {
		List<Window> windows = new ArrayList<>(windowList);
		addLinksWindowIfVisible(windows);
		return windows;
	}

	private void addLinksWindowIfVisible(List<Window> windows) {
		if (dedicatedLinksWindow.isVisible()) {
			windows.add(dedicatedLinksWindow);
		}
	}

	public List<Window> getVisibleWindows() {
		List<Window> maximisedWindows = getWindows(WindowLayout.WindowState.MAXIMISED);
		if (!maximisedWindows.isEmpty()) {
			// links window is still displayable in maximised mode but does not have the requested MAXIMIZED state
			if (!maximisedWindows.contains(dedicatedLinksWindow)) {
				addLinksWindowIfVisible(maximisedWindows);
			}
			// should only ever be one maximised window
			return maximisedWindows;
		} else {
			return getWindows(WindowLayout.WindowState.SPLIT);
		}
	}

	public List<Window> getMaximisedScreens() {
		return getWindows(WindowLayout.WindowState.MAXIMISED);
	}
	

	public List<Window> getMinimisedScreens() {
		if (isMaximisedState()) {
			// if a window is maximised then show no minimised windows
			return new ArrayList<>();
		} else {
			return getWindows(WindowLayout.WindowState.MINIMISED);
		}
	}

	protected boolean isMaximisedState() {
		for (Window window : getWindows()) {
			if (window.getWindowLayout().getState() == WindowLayout.WindowState.MAXIMISED) {
				return true;
			}
		}
		return false;
	}
	
	private List<Window> getWindows(WindowLayout.WindowState state) {
		List<Window> windows = new ArrayList<>();
		for (Window window : getWindows()) {
			if (window.getWindowLayout().getState() == state) {
				windows.add(window);
			}
		}
		return windows;
	}

	public Window getWindow(int screenNo) {
		for (Window window : getWindows()) {
			if (window.getScreenNo()==screenNo) {
				return window;
			}
		}
		return null;
	}
	
	public LinksWindow getDedicatedLinksWindow() {
		return dedicatedLinksWindow;
	}

	public Window addNewWindow() {
		// ensure main screen is not maximized
		getActiveWindow().getWindowLayout().setState(WindowLayout.WindowState.SPLIT);

		return addNewWindow(getNextWindowNo());
	}

	public boolean isMultiWindow() {
		List<Window> windows = getVisibleWindows();
		return windows.size()>1;
	}
	
	public void setDefaultActiveWindow() {
		for (Window window : getWindows()) {
			if (window.isVisible()) {
				setActiveWindow(window);
				return;
			}
		}
		
		// no suitable window found so add one and make it default
		setActiveWindow(addNewWindow(getNextWindowNo()));
	}
	
	private WindowLayout.WindowState getDefaultState() {
		return WindowLayout.WindowState.SPLIT;
	}

	public Window getActiveWindow() {
		return activeWindow;
	}

	public void setActiveWindow(Window newActiveWindow) {
		if (!newActiveWindow.equals(this.activeWindow)) {
			this.activeWindow = newActiveWindow;
			EventBus.getDefault().post(new CurrentWindowChangedEvent(activeWindow));
		}
	}
	
	public List<Window> getWindowsToSynchronise() {
		List<Window> windows = getVisibleWindows();
		windows.remove(getActiveWindow());
		
		return windows;
	}
	
	public void minimise(Window window) {
		window.getWindowLayout().setState(WindowLayout.WindowState.MINIMISED);

		// has the active screen been minimised?
		if (getActiveWindow().equals(window)) {
			setDefaultActiveWindow();
		}
	}

	public void close(Window window) {
		window.getWindowLayout().setState(WindowLayout.WindowState.CLOSED);
		
		// links window is just closed not deleted
		if (!window.isLinksWindow()) {
			
			if (!windowList.remove(window)) {
				logger.error("Failed to close window "+window.getScreenNo());
			}
		}

		// has the active screen been minimised?
		if (getActiveWindow().equals(window)) {
			setDefaultActiveWindow();
		}

	}
	
	public void moveWindowToPosition(Window window, int position) {
		int originalWindowIndex = windowList.indexOf(window);
		
		if (originalWindowIndex==-1) {
			logger.warn("Attempt to move missing window");
			return;
		}
		if (position>windowList.size()) {
			logger.warn("Attempt to move window beyond end of window list");
			return;
		}

		windowList.remove(originalWindowIndex);

		windowList.add(position, window);
	}

	/**
	 * Return window no larger than any windows created during this session and larger than 0
	 */
	private int getNextWindowNo() {
		return maxWindowNoUsed+1;
	}
	
	private Window addNewWindow(int screenNo) {
		Window newScreen = new Window(screenNo, getDefaultState());
		maxWindowNoUsed = Math.max(maxWindowNoUsed, screenNo);
		windowList.add(newScreen);
		return newScreen;
	}
	
	/** 
	 * If app moves to background then save current state to allow continuation after return
	 * 
	 * @param appToBackgroundEvent Event info
	 */
	public void onEvent(AppToBackgroundEvent appToBackgroundEvent) {
		if (appToBackgroundEvent.isMovedToBackground()) {
			saveState();
		}
	}
    /** save current page and document state */
	protected void saveState() {
    	logger.info("Save instance state for screens");
//    	SharedPreferences settings = Bible.getApplication().getAppStateSharedPreferences();
//		saveState(settings);
	}

	/** restore current page and document state */
    private void restoreState() {
    	try {
        	logger.info("Restore instance state for screens");
//			SharedPreferences settings = Bible.getApplication().getAppStateSharedPreferences();
//    		restoreState(settings);
    	} catch (Exception e) {
    		logger.error("Restore error", e);
    	}
    }
	/** called during app close down to save state
	 * 
	 * @param outState
	 */
	private void saveState(SharedPreferences outState) {
		logger.info("save state");
		try {
	
			JSONObject windowRepositoryStateObj = new JSONObject();
			JSONArray windowStateArray = new JSONArray();
			for (Window window : windowList) {
				try {
					if (window.getWindowLayout().getState() != WindowLayout.WindowState.CLOSED) {
						windowStateArray.put(window.getStateJson());
					}
				} catch (JSONException je) {
					logger.error("Error saving screen state", je);
				}
			}
			windowRepositoryStateObj.put("windowState", windowStateArray);
			
			SharedPreferences.Editor editor = outState.edit();
			editor.putString("windowRepositoryState", windowRepositoryStateObj.toString());
			editor.commit();
		} catch (JSONException je) {
			logger.error("Saving window state", je);
		}
	}
	
	/** called during app start-up to restore previous state
	 * 
	 * @param inState
	 */
	private void restoreState(SharedPreferences inState) {
		logger.info("restore state");
		String windowRepositoryStateString = inState.getString("windowRepositoryState", null);
		if (StringUtils.isNotEmpty(windowRepositoryStateString)) {
			try {
				JSONObject windowRepositoryState = new JSONObject(windowRepositoryStateString);
				JSONArray windowState = windowRepositoryState.getJSONArray("windowState");
				if (windowState.length()>0) {

					// remove current (default) state before restoring
					windowList.clear();
					
					for (int i=0; i<windowState.length(); i++) {
						try {
							JSONObject screenState = windowState.getJSONObject(i);
							Window window = new Window();
							window.restoreState(screenState);

							maxWindowNoUsed = Math.max(maxWindowNoUsed, window.getScreenNo());
							
							windowList.add(window);
						} catch (JSONException je) {
							logger.error("Error restoring screen state", je);
						}
					}
				}
			} catch (JSONException je) {
				logger.error("Error restoring screen state", je);
			}
		}
	}
}
