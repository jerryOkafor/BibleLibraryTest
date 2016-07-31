package com.bellman.bible.service.format.osistohtml.taghandler;
import com.bellman.bible.service.common.Constants;
import com.bellman.bible.service.common.Logger;
import com.bellman.bible.service.format.osistohtml.HtmlTextWriter;
import com.bellman.bible.service.format.osistohtml.OsisToHtmlParameters;
import com.bellman.bible.service.format.osistohtml.osishandlers.OsisToHtmlSaxHandler;

import org.crosswire.jsword.book.OSISUtil;
import org.xml.sax.Attributes;

/** Line break
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author. 
 */
public class LbHandler implements OsisTagHandler {

	private OsisToHtmlSaxHandler.PassageInfo passageInfo;
	
	private HtmlTextWriter writer;
	
	@SuppressWarnings("unused")
	private OsisToHtmlParameters parameters;
	
	@SuppressWarnings("unused")
	private static final Logger log = new Logger("LHandler");

	public LbHandler(OsisToHtmlParameters parameters, OsisToHtmlSaxHandler.PassageInfo passageInfo, HtmlTextWriter writer) {
		this.parameters = parameters;
		this.passageInfo = passageInfo;
		this.writer = writer;
	}
	
	@Override
	public String getTagName() {
        return OSISUtil.OSIS_ELEMENT_LB;
    }

	@Override
	public void start(Attributes attrs) {
		if (passageInfo.isAnyTextWritten) {
			writer.write(Constants.HTML.BR);
		}
	}

	@Override
	public void end() {
	}
}
