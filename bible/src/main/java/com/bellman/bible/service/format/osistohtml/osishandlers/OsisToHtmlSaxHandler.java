package com.bellman.bible.service.format.osistohtml.osishandlers;

import com.bellman.bible.service.common.Logger;
import com.bellman.bible.service.device.ScreenSettings;
import com.bellman.bible.service.font.FontControl;
import com.bellman.bible.service.format.Note;
import com.bellman.bible.service.format.osistohtml.OsisToHtmlParameters;
import com.bellman.bible.service.format.osistohtml.preprocessor.HebrewCharacterPreprocessor;
import com.bellman.bible.service.format.osistohtml.preprocessor.TextPreprocessor;
import com.bellman.bible.service.format.osistohtml.strongs.StrongsHandler;
import com.bellman.bible.service.format.osistohtml.strongs.StrongsLinkCreator;
import com.bellman.bible.service.format.osistohtml.taghandler.BookmarkMarker;
import com.bellman.bible.service.format.osistohtml.taghandler.DivHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.DivineNameHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.FigureHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.HiHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.LHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.LbHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.LgHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.ListHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.ListItemHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.MilestoneHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.MyNoteMarker;
import com.bellman.bible.service.format.osistohtml.taghandler.NoteHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.OsisTagHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.PHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.QHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.ReferenceHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.TableCellHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.TableHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.TableRowHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.TitleHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.TransChangeHandler;
import com.bellman.bible.service.format.osistohtml.taghandler.VerseHandler;
import com.bellman.bible.service.format.osistohtml.tei.OrthHandler;
import com.bellman.bible.service.format.osistohtml.tei.PronHandler;
import com.bellman.bible.service.format.osistohtml.tei.RefHandler;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.jsword.book.OSISUtil;
import org.xml.sax.Attributes;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Convert OSIS tags into html tags
 * 
 * Example OSIS tags from KJV Ps 119 v1 showing title, w, note <title
 * canonical="true" subType="x-preverse" type="section"> <foreign
 * n="?">ALEPH.</foreign> </title> <w lemma="strong:H0835">Blessed</w>
 * <transChange type="added">are</transChange> <w lemma="strong:H08549">the
 * undefiled</w> ... <w lemma="strong:H01980" morph="strongMorph:TH8802">who
 * walk</w> ... <w lemma="strong:H03068">of the
 * <seg><divineName>Lord</divineName></seg></w>. <note type="study">undefiled:
 * or, perfect, or, sincere</note>
 * 
 * Example of notes cross references from ESV In the <note n="a"
 * osisID="Gen.1.1!crossReference.a" osisRef="Gen.1.1"
 * type="crossReference"><reference osisRef="Job.38.4-Job.38.7">Job
 * 38:4-7</reference>; <reference osisRef="Ps.33.6">Ps. 33:6</reference>;
 * <reference osisRef="Ps.136.5">136:5</reference>; <reference
 * osisRef="Isa.42.5">Isa. 42:5</reference>; <reference
 * osisRef="Isa.45.18">45:18</reference>; <reference
 * osisRef="John.1.1-John.1.3">John 1:1-3</reference>; <reference
 * osisRef="Acts.14.15">Acts 14:15</reference>; <reference
 * osisRef="Acts.17.24">17:24</reference>; <reference
 * osisRef="Col.1.16-Col.1.17">Col. 1:16, 17</reference>; <reference
 * osisRef="Heb.1.10">Heb. 1:10</reference>; <reference
 * osisRef="Heb.11.3">11:3</reference>; <reference osisRef="Rev.4.11">Rev.
 * 4:11</reference></note>beginning
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class OsisToHtmlSaxHandler extends OsisSaxHandler {

	private static final String HEBREW_LANGUAGE_CODE = "he";
	private static final Set<String> IGNORED_TAGS = new HashSet<>(Arrays.asList(OSISUtil.OSIS_ELEMENT_CHAPTER));
	private static final Logger log = new Logger("OsisToHtmlSaxHandler");
	// properties
	private OsisToHtmlParameters parameters;
	// tag handlers for the different OSIS tags
	private Map<String, OsisTagHandler> osisTagHandlers;
	private NoteHandler noteHandler;
	// processor for the tag content
	private TextPreprocessor textPreprocessor;
	// internal logic
	private VerseInfo verseInfo = new VerseInfo();
	private PassageInfo passageInfo = new PassageInfo();
	
	public OsisToHtmlSaxHandler(OsisToHtmlParameters parameters) {
		super();
		this.parameters = parameters;

		osisTagHandlers = new HashMap<>();

		BookmarkMarker bookmarkMarker = new BookmarkMarker(parameters, verseInfo, getWriter());
		MyNoteMarker myNoteMarker = new MyNoteMarker(parameters, verseInfo, getWriter());
		registerHandler( new VerseHandler(parameters, verseInfo, bookmarkMarker, myNoteMarker, getWriter()) );

		noteHandler = new NoteHandler(parameters, verseInfo, getWriter());
		registerHandler( noteHandler  );
		registerHandler( new ReferenceHandler(parameters, noteHandler, getWriter()) );
		registerHandler( new RefHandler(parameters, noteHandler, getWriter()) );

		registerHandler( new DivineNameHandler(getWriter()) );
		registerHandler( new TitleHandler(parameters, verseInfo, getWriter()) );
		registerHandler( new QHandler(parameters, getWriter()) );
		registerHandler( new MilestoneHandler(parameters, passageInfo, verseInfo, getWriter()) );
		registerHandler( new HiHandler(parameters, getWriter()) );
		registerHandler( new TransChangeHandler(parameters, getWriter()) );
		registerHandler( new OrthHandler(parameters, getWriter()) );
		registerHandler( new PronHandler(parameters, getWriter()) );
		registerHandler( new LbHandler(parameters, passageInfo, getWriter()) );
		registerHandler( new LgHandler(parameters, getWriter()) );
		registerHandler( new LHandler(parameters, getWriter()) );
		registerHandler( new PHandler(parameters, getWriter()) );
		registerHandler( new StrongsHandler(parameters, getWriter()) );
		registerHandler( new FigureHandler(parameters, getWriter()) );
		registerHandler( new DivHandler(parameters, verseInfo, passageInfo, getWriter()) );
		registerHandler( new TableHandler(getWriter()) );
		registerHandler( new TableRowHandler(getWriter()) );
		registerHandler( new TableCellHandler(getWriter()) );
		registerHandler( new ListHandler(getWriter()) );
		registerHandler( new ListItemHandler(getWriter()) );

		//TODO at the moment we can only have a single TextPreprocesor, need to chain them and maybe make the writer a TextPreprocessor and put it at the end of the chain
		if (HEBREW_LANGUAGE_CODE.equals(parameters.getLanguageCode())) {
			textPreprocessor = new HebrewCharacterPreprocessor();
		} else if (parameters.isConvertStrongsRefsToLinks()) {
			textPreprocessor = new StrongsLinkCreator();
		}
	}

	private void registerHandler(OsisTagHandler handler) {
		if (osisTagHandlers.put(handler.getTagName(), handler)!=null) {
			throw new InvalidParameterException("Duplicate handlers for tag "+handler.getTagName());
		}
	}

	@Override
	public void startDocument()  {
		String jQueryjs = "\n<script type='text/javascript' src='file:///android_asset/web/jquery-v3.1.0.min.js'></script>\n" +
				"<script type='text/javascript' src='file:///android_asset/web/jquery.longpress.js'></script>\n"+
				"<script type='text/javascript' src='file:///android_asset/web/jquery.nearest.min.1.4.0.js'></script>\n";
		String jsTag = "\n<script type='text/javascript' src='file:///android_asset/web/script.js'></script>\n";
		String styleSheetTags = parameters.getCssStylesheets();
		String customFontStyle = FontControl.getInstance().getHtmlFontStyle(parameters.getFont(), parameters.getCssClassForCustomFont());
		write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> "
				+ "<html xmlns='http://www.w3.org/1999/xhtml' dir='" + getDirection() + "'><head>"
				+ styleSheetTags+"\n"
				+ customFontStyle
				+ jQueryjs
				+ jsTag
				+ "<meta charset='utf-8'/>"
				+ "</head>"
				+ "<body onscroll='jsonscroll()' >");

		// force rtl for rtl languages - rtl support on Android is poor but
		// forcing it seems to help occasionally
		if (!parameters.isLeftToRight()) {
			write("<span dir='rtl'>");
		}
	}

	/*
	 * Called when the Parser Completes parsing the Current XML File.
	 */
	@Override
	public void endDocument() {

		// close last verse
		if (parameters.isVersePerline()) {
			//close last verse
			if (verseInfo.currentVerseNo>1) {
				write("</div>");
			}
		}

		// add optional footer e.g. Strongs show all occurrences link
		if (StringUtils.isNotEmpty(parameters.getExtraFooter())) {
			write(parameters.getExtraFooter());
		}

		if (!parameters.isLeftToRight()) {
			write("</span>");
		}
		// add padding at bottom to allow last verse to scroll to top of page
		// and become current verse
		write(getPaddingAtBottom() + "</body></html>");
	}

	/*
	 * Called when the starting of the Element is reached. For Example if we
	 * have Tag called <Title> ... </Title>, then this method is called when
	 * <Title> tag is Encountered while parsing the Current XML File. The
	 * AttributeList Parameter has the list of all Attributes declared for the
	 * Current Element in the XML File.
	 */
	@Override
    public void startElement(String namespaceURI,
            String sName, // simple name
            String qName, // qualified name
            Attributes attrs)
    {
		String name = getName(sName, qName); // element name

		debug(name, attrs, true);

		OsisTagHandler tagHandler = osisTagHandlers.get(name);
		if (tagHandler!=null) {
			tagHandler.start(attrs);
		} else {
			if (!IGNORED_TAGS.contains(name)) {
				log.info("Verse "+verseInfo.currentVerseNo+" unsupported OSIS tag:"+name);
			}
		}
	}

	/*
	 * Called when the Ending of the current Element is reached. For example in
	 * the above explanation, this method is called when </Title> tag is reached
	 */
	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) {
		String name = getName(sName, qName);

		debug(name, null, false);

		OsisTagHandler tagHandler = osisTagHandlers.get(name);
		if (tagHandler!=null) {
			tagHandler.end();
		}
	}

	/*
	 * While Parsing the XML file, if extra characters like space or enter
	 * Character are encountered then this method is called. If you don't want
	 * to do anything special with these characters, then you can normally leave
	 * this method blank.
	 */
	@Override
	public void characters(char buf[], int offset, int len) {
		String s = new String(buf, offset, len);

		// record that we are now beyond the verse, but do it quickly so as not to slow down parsing
		verseInfo.isTextSinceVerse = verseInfo.isTextSinceVerse ||
										len>2 ||
										StringUtils.isNotBlank(s);
		passageInfo.isAnyTextWritten = passageInfo.isAnyTextWritten || verseInfo.isTextSinceVerse;

		if (textPreprocessor!=null) {
			s = textPreprocessor.process(s);
		}

		write(s);
	}

	/*
	 * In the XML File if the parser encounters a Processing Instruction which
	 * is declared like this <?ProgramName:BooksLib
	 * QUERY="author, isbn, price"?> Then this method is called where Target
	 * parameter will have "ProgramName:BooksLib" and data parameter will have
	 * QUERY="author, isbn, price". You can invoke a External Program from this
	 * Method if required.
	 */
	public void processingInstruction(String target, String data) {
		// noop
	}

	public String getDirection() {
		return parameters.isLeftToRight() ? "ltr" : "rtl";
	}

	private String getPaddingAtBottom() {
		// the pure padding is the height of the WebView - one line height to keep one line on the screen
		// but some books already contain padding (br) at end so I fudge by multiplying line height by 2 to try to avoid all text scrolling off screen
		// this is not very accurate.  Some books have a <br />s at the end making the padding too large
		// also the user can toggle full screen after the last view height calculation
		// 1.5 is a fudge factor to try to keep a little of the text on the screen for books that end in a <br />
		int paddingHeightDips = ScreenSettings.getContentViewHeightDips() - (2 * ScreenSettings.getLineHeightDips());
		return "<img height='" + paddingHeightDips + "' width='1' border='0' vspace='0' style='display:block'/>";
	}

	public List<Note> getNotesList() {
		return noteHandler.getNotesList();
	}

	public static class VerseInfo {
		public int currentVerseNo;
		public int positionToInsertBeforeVerse;
		public boolean isTextSinceVerse = false;
	}

	public static class PassageInfo {
		public boolean isAnyTextWritten = false;
	}
}
