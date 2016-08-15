package com.bellman.bible.service.format.osistohtml.tei;

import com.bellman.bible.service.format.osistohtml.HtmlTextWriter;
import com.bellman.bible.service.format.osistohtml.OsisToHtmlParameters;
import com.bellman.bible.service.format.osistohtml.taghandler.NoteHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.ReferenceHandler;

import org.xml.sax.Attributes;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class RefHandler extends ReferenceHandler {

    public RefHandler(OsisToHtmlParameters osisToHtmlParameters, NoteHandler noteHandler, HtmlTextWriter theWriter) {
        super(osisToHtmlParameters, noteHandler, theWriter);
    }
    
    @Override
	public String getTagName() {
		return TEIUtil.TEI_ELEMENT_REF;
	}

    @Override
	public void start(Attributes attrs) {
		String target = attrs.getValue(TEIUtil.TEI_ATTR_TARGET);
		start(target);
	}
}
