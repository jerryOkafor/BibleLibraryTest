package com.bellman.bible.android.control.page.window;

import com.bellman.bible.android.control.page.CurrentPageManager;
import com.bellman.bible.android.control.page.window.WindowLayout.WindowState;
import com.bellman.bible.service.common.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class Window {
	


	private boolean isSynchronised = true;
	
	private WindowLayout windowLayout;
	
	private CurrentPageManager currentPageManager;
	
	// 1 based screen no
	private int screenNo;
	
	private final Logger logger = new Logger(this.getClass().getName());
	
	public Window(int screenNo, WindowState windowState) {
		this.screenNo = screenNo;
		this.windowLayout = new WindowLayout( windowState );
	}

	/**
     * Used when restoring state
     */
	public Window() {
		this.windowLayout = new WindowLayout(WindowState.SPLIT);
	}

	public CurrentPageManager getPageManager() {
		// for now lazily create to prevent NPE on start up due to circular dependency
		if (currentPageManager==null) {
			this.currentPageManager = new CurrentPageManager();
		}
		return currentPageManager;
	}

	public int getScreenNo() {
		return screenNo;
	}

	public boolean isClosed() {
		return getWindowLayout().getState().equals(WindowState.CLOSED);
	}
	
	public boolean isMaximised() {
		return getWindowLayout().getState().equals(WindowState.MAXIMISED);
	}
	
	public void setMaximised(boolean maximise) {
		if (maximise) {
			getWindowLayout().setState(WindowState.MAXIMISED);
		} else {
			getWindowLayout().setState(WindowState.SPLIT);
		}
	}
	
	public boolean isSynchronised() {
		return isSynchronised;
	}
	
	public void setSynchronised(boolean isSynchronised) {
		this.isSynchronised = isSynchronised;
	}

	public boolean isVisible() {
		return 	getWindowLayout().getState()!=WindowState.MINIMISED &&
				getWindowLayout().getState()!=WindowState.CLOSED;
	}

	
	public WindowOperation getDefaultOperation() {
		// if window is maximised then default operation is always to unmaximise
		if (isMaximised()) {
			return WindowOperation.MAXIMISE;
		} else if (isLinksWindow()) {
			return WindowOperation.CLOSE;
		} else {
			return WindowOperation.MINIMISE;
		}
	}

	public JSONObject getStateJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("screenNo", screenNo)
			.put("isSynchronised", isSynchronised)
			.put("windowLayout", windowLayout.getStateJson())
			.put("pageManager", getPageManager().getStateJson());
		return object;
	}

	public void restoreState(JSONObject jsonObject) throws JSONException {
		try {
			this.screenNo = jsonObject.getInt("screenNo");
			this.isSynchronised = jsonObject.getBoolean("isSynchronised");
			this.windowLayout.restoreState(jsonObject.getJSONObject("windowLayout"));
			this.getPageManager().restoreState(jsonObject.getJSONObject("pageManager"));
		} catch (Exception e) {
			logger.warn("Window state restore error:"+e.getMessage(), e);
		}
	}

	public WindowLayout getWindowLayout() {
		return windowLayout;
	}
	
	public boolean isLinksWindow() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Window [screenNo=" + screenNo + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Window other = (Window) obj;
		return screenNo == other.screenNo;
	}	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + screenNo;
		return result;
	}

	public enum WindowOperation {
		MAXIMISE, MINIMISE, RESTORE, CLOSE
	}
}
