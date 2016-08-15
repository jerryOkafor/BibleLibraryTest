package com.bellman.bible.service.format;

import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.service.device.ScreenSettings;
import com.bellman.bible.util.SharedConstants;

/** prepare an error message for display in a WebView
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class HtmlMessageFormatter {

	private static final String NIGHT_STYLESHEET = "<link href='file:///android_asset/web/"
			+ SharedConstants.NIGHT_MODE_STYLESHEET
			+ "' rel='stylesheet' type='text/css'/>";
	
	private static final String NIGHT_HEADER = "<html><head>"+NIGHT_STYLESHEET+"</head><body>";
	private static final String NIGHT_FOOTER = "</body></html>";
	
	@SuppressWarnings("unused")
	private static final String TAG = "HtmlmessageFormatter";
	
	/** wrap text with nightmode css if required
	 */
	public static String format(int msgId) {
		String errorMsg = CurrentActivityHolder.getInstance().getApplication().getResources().getString(msgId);
		return format(errorMsg);
	}
	
	/** wrap text with nightmode css if required
	 */
	public static String format(String text) {
		boolean isNightMode = ScreenSettings.isNightMode();
		
		String formattedText = "";
		
		// only require special formatting for nightmode
		if (!isNightMode) {
			formattedText = text;
		} else {
			formattedText = NIGHT_HEADER + text + NIGHT_FOOTER;
		}
		return formattedText;
	}
}
