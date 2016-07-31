package com.bellman.bible.service.format.osistohtml.taghandler;


import com.bellman.bible.service.common.Logger;
import com.bellman.bible.service.format.osistohtml.HtmlTextWriter;
import com.bellman.bible.service.format.osistohtml.OsisToHtmlParameters;
import com.bellman.bible.service.format.osistohtml.osishandlers.OsisToHtmlSaxHandler;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.xml.sax.Attributes;

import java.util.HashSet;
import java.util.Set;

/** Display an img if the current verse has MyNote
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author. 
 */
public class MyNoteMarker implements OsisTagHandler {

	private Set<Integer> myNoteVerses = new HashSet<Integer>();
	
	private OsisToHtmlParameters parameters;
	
	private OsisToHtmlSaxHandler.VerseInfo verseInfo;
	
	private HtmlTextWriter writer;
	
	@SuppressWarnings("unused")
	private static final Logger log = new Logger("MyNoteMarker");

	public MyNoteMarker(OsisToHtmlParameters parameters, OsisToHtmlSaxHandler.VerseInfo verseInfo, HtmlTextWriter writer) {
		this.parameters = parameters;
		this.verseInfo = verseInfo;
		this.writer = writer;
		
		// create hashmap of verses to optimise verse note lookup
		myNoteVerses.clear();
		if (parameters.getVersesWithNotes()!=null) {
			for (Key key : parameters.getVersesWithNotes()) {
				Verse verse = KeyUtil.getVerse(key);
				myNoteVerses.add(verse.getVerse());
			}
		}
	}
	
	
	public String getTagName() {
        return "";
    }

	/** just after verse start tag
	 */
	public void start(Attributes attr) {
		if (myNoteVerses!=null && parameters.isShowMyNotes()) {
			if (myNoteVerses.contains(verseInfo.currentVerseNo)) {
				writer.write("<img src='file:///android_asset/images/pencil16x16.png' class='myNoteImg'/>");
			}
		}
	}

	public void end() {
	}
}
