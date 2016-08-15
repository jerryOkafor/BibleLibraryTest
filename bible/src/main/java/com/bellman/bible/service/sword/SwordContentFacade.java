package com.bellman.bible.service.sword;

import android.content.SharedPreferences;
import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.common.Constants;
import com.bellman.bible.service.common.Logger;
import com.bellman.bible.service.common.ParseException;
import com.bellman.bible.service.css.CssControl;
import com.bellman.bible.service.font.FontControl;
import com.bellman.bible.service.format.HtmlMessageFormatter;
import com.bellman.bible.service.format.Note;
import com.bellman.bible.service.format.OSISInputStream;
import com.bellman.bible.service.format.osistohtml.OsisToHtmlParameters;
import com.bellman.bible.service.format.osistohtml.osishandlers.OsisToCanonicalTextSaxHandler;
import com.bellman.bible.service.format.osistohtml.osishandlers.OsisToHtmlSaxHandler;
import com.bellman.bible.service.format.osistohtml.osishandlers.OsisToSpeakTextSaxHandler;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.xml.sax.ContentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/** JSword facade
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class SwordContentFacade {
	
	private static final String TAG = "SwordContentFacade";
	private static final Logger log = new Logger(SwordContentFacade.class.getName());
	// set to false for testing
	public static boolean isAndroid = true; //CommonUtils.isAndroid();
	private static SwordContentFacade singleton;
	private DocumentParseMethod documentParseMethod = new DocumentParseMethod();
	private CssControl cssControl = new CssControl();
	private SAXParser saxParser;

	private SwordContentFacade() {
	}
	
	public static SwordContentFacade getInstance() {
		if (singleton==null) {
			synchronized(SwordContentFacade.class)  {
				if (singleton==null) {
					SwordContentFacade instance = new SwordContentFacade();
					singleton = instance;
				}
			}
		}
		return singleton;
	}

	public static void setAndroid(boolean isAndroid) {
		SwordContentFacade.isAndroid = isAndroid;
	}
	
	/** top level method to fetch html from the raw document data
	 *
	 * @param book
	 * @param key
	 * @return
	 * @throws NoSuchKeyException
	 * @throws BookException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ParserConfigurationException
	 */
	public String readHtmlText(Book book, Key key) throws ParseException
	{
		String retVal = "";
		if (book==null || key==null) {
			retVal = "";
		} else if (Books.installed().getBook(book.getInitials())==null) {
			Log.w(TAG, "Book may have been uninstalled:"+book);
			String errorMsg = CurrentActivityHolder.getInstance().getApplication().getString(R.string.document_not_installed, book.getInitials());
			String htmlMsg = HtmlMessageFormatter.format(errorMsg);
			retVal = htmlMsg;
		} else if (!bookContainsAnyOf(book, key)) {
			Log.w(TAG, "KEY:"+key.getOsisID()+" not found in doc:"+book);
			String htmlMsg = HtmlMessageFormatter.format(R.string.error_key_not_in_document);
			retVal = htmlMsg;
		} else {

			// we have a fast way of handling OSIS zText docs but some docs need the superior JSword error recovery for mismatching tags
			// try to parse using optimised method first if a suitable document and it has not failed previously
			boolean isParsedOk = false;
			if ("OSIS".equals(book.getBookMetaData().getProperty("SourceType")) &&
				"zText".equals(book.getBookMetaData().getProperty("ModDrv")) &&
				documentParseMethod.isFastParseOkay(book, key)) {
				try {
					retVal = readHtmlTextOptimizedZTextOsis(book, key);
					isParsedOk = true;
				} catch (ParseException pe) {
					documentParseMethod.failedToParse(book, key);
				}
			}

			// fall back to slightly slower JSword method with JSword's fallback approach of removing all tags
			if (!isParsedOk) {
				retVal = readHtmlTextStandardJSwordMethod(book, key);
			}
		}
		return retVal;
	}

	/** Get Footnotes and references from specified document page
	 */
	public List<Note> readFootnotesAndReferences(Book book, Key key) throws ParseException {
		List<Note> retVal = new ArrayList<Note>();
		try {
			// based on standard JSword SAX handling method because few performance gains would be gained for the extra complexity of Streaming
			BookData data = new BookData(book, key);
			SAXEventProvider osissep = data.getSAXEventProvider();
			if (osissep != null) {
				OsisToHtmlSaxHandler osisToHtml = getSaxHandler(book, key);

				osissep.provideSAXEvents(osisToHtml);

				retVal = osisToHtml.getNotesList();
			} else {
				Log.e(TAG, "No osis SEP returned");
			}
	        return retVal;
		} catch (Exception e) {
			log.error("Parsing error", e);
			throw new ParseException("Parsing error", e);
		}

	}

	/**
	 * Use OSISInputStream which loads a single verse at a time as required.
	 * This reduces memory requirements compared to standard JDom SaxEventProvider
	 */
	private String readHtmlTextOptimizedZTextOsis(Book book, Key key) throws ParseException
	{
		log.debug("Using fast method to fetch document data");
		/**
		 * When you supply an InputStream, the SAX implementation wraps the stream in an InputStreamReader;
		 * then SAX automatically detects the correct character encoding from the stream. You can then omit the setEncoding() step,
		 * reducing the method invocations once again. The result is an application that is faster, and always has the correct character encoding.
		 */
		InputStream is = new OSISInputStream(book, key);

		OsisToHtmlSaxHandler osisToHtml = getSaxHandler(book, key);

		SAXParser parser = getSAXParser();
		try {
			parser.parse(is, osisToHtml);
		} catch (Exception e) {
			log.error("Parsing error", e);
			throw new ParseException("Parsing error", e);
		}

		return osisToHtml.toString();
	}

	private String readHtmlTextStandardJSwordMethod(Book book, Key key) throws ParseException
	{
		log.debug("Using standard JSword to fetch document data");
		String retVal;

		try {
			BookData data = new BookData(book, key);
			SAXEventProvider osissep = data.getSAXEventProvider();
			if (osissep == null) {
				Log.e(TAG, "No osis SEP returned");
				retVal = "Error fetching osis SEP";
			} else {
				OsisToHtmlSaxHandler osisToHtml = getSaxHandler(book, key);

				osissep.provideSAXEvents(osisToHtml);

				retVal = osisToHtml.toString();
			}
			return retVal;
		} catch (Exception e) {
			log.error("Parsing error", e);
			throw new ParseException("Parsing error", e);
		}
	}

	/**
	 * Obtain a SAX event provider for the OSIS document representation of one
	 * or more book entries.
	 *
	 * @param book
	 *            the book to use
	 * @param reference
	 *            a reference, appropriate for the book, of one or more entries
	 */
	public SAXEventProvider getOSIS(Book book, String reference, int maxKeyCount)
			throws BookException, NoSuchKeyException {
		Key key;
		if (BookCategory.BIBLE.equals(book.getBookCategory())) {
			key = book.getKey(reference);
			((Passage) key).trimVerses(maxKeyCount);
		} else {
			key = book.createEmptyKeyList();

			Iterator<Key> iter = book.getKey(reference).iterator();
			int count = 0;
			while (iter.hasNext()) {
				if (++count >= maxKeyCount) {
					break;
				}
				key.addAll(iter.next());
			}
		}

		BookData data = new BookData(book, key);
		return data.getSAXEventProvider();
	}

    /**
     * Get just the canonical text of one or more book entries without any
     * markup.
	 *
	 * @param book
	 *            the book to use
	 * @param key
     *            a reference, appropriate for the book, of one or more entries
     */
    public String getCanonicalText(Book book, Key key) throws NoSuchKeyException, BookException, ParseException {
    	try {
			BookData data = new BookData(book, key);
			SAXEventProvider osissep = data.getSAXEventProvider();

			ContentHandler osisHandler = new OsisToCanonicalTextSaxHandler();

			osissep.provideSAXEvents(osisHandler);

			return osisHandler.toString();
    	} catch (Exception e) {
    		Log.e(TAG, "Error getting text from book" , e);
			return CurrentActivityHolder.getInstance().getApplication().getString(R.string.error_occurred);
		}
	}

    /**
     * Get text to be spoken without any markup.
	 *
	 * @param book
	 *            the book to use
	 * @param key
     *            a reference, appropriate for the book, of one or more entries
     */
    public String getTextToSpeak(Book book, Key key) throws NoSuchKeyException, BookException, ParseException {
    	try {
			BookData data = new BookData(book, key);
			SAXEventProvider osissep = data.getSAXEventProvider();

			boolean sayReferences = BookCategory.GENERAL_BOOK.equals(book.getBookCategory());
			ContentHandler osisHandler = new OsisToSpeakTextSaxHandler(sayReferences);


			osissep.provideSAXEvents(osisHandler);

			return osisHandler.toString();
    	} catch (Exception e) {
    		Log.e(TAG, "Error getting text from book" , e);
			return CurrentActivityHolder.getInstance().getApplication().getString(R.string.error_occurred);
		}
	}

	private SAXParser getSAXParser() throws ParseException {
		try {
			if (saxParser==null) {
	    		SAXParserFactory spf = SAXParserFactory.newInstance();
	    		spf.setValidating(false);
	   			saxParser = spf.newSAXParser();
	    	}
		} catch (Exception e) {
			log.error("SAX parser error", e);
			throw new ParseException("SAX parser error", e);
		}
		return saxParser;
    }

	/**
	 * Get just the canonical text of one or more book entries without any
	 * markup.
	 *
	 * @param book
	 *            the book to use
	 * @param reference
     *            a reference, appropriate for the book, of one or more entries
     */
    public String getPlainText(Book book, String reference, int maxKeyCount) throws BookException, NoSuchKeyException {
    	String plainText = "";
    	try {
    		if (book != null) {
		        Key key = book.getKey(reference);
		        plainText = getPlainText(book, key, maxKeyCount);
    		}
    	} catch (Exception e) {
    		Log.e(TAG, "Error getting plain text", e);
    	}
    	return plainText;
    }

    /**
     * Get just the canonical text of one or more book entries without any
     * markup.
	 *
	 * @param book
	 *            the book to use
	 * @param key
     *            a reference, appropriate for the book, of one or more entries
     */
    public String getPlainText(Book book, Key key, int maxKeyCount) throws BookException, NoSuchKeyException {
    	String plainText = "";
    	try {
    		if (book != null) {
		        plainText = getCanonicalText(book, key);

				// trim any preceeding spaces that make the final output look uneven
				plainText = plainText.trim();
			}
    	} catch (Exception e) {
    		Log.e(TAG, "Error getting plain text", e);
    	}
    	return plainText;
    }

    public Key search(Book bible, String searchText) throws BookException {
// 		  example of fetching Strongs ref - only works with downloaded indexes!
//        Book book = getDocumentByInitials("KJV");
//        Key key1 = book.find("strong:h3068");
//        System.out.println("h3068 result count:"+key1.getCardinality());

		Log.d(TAG,	"Searching:"+bible+" Search term:" + searchText);

		// This does a standard operator search. See the search
		// documentation for more examples of how to search
		Key key = bible.find(searchText); //$NON-NLS-1$

		Log.d(TAG,	"There are "+key.getCardinality()+" verses containing " + searchText);

		return key;

	}

	private OsisToHtmlSaxHandler getSaxHandler(Book book, Key key) {
		OsisToHtmlParameters osisToHtmlParameters = new OsisToHtmlParameters();
		BookCategory bookCategory = book.getBookCategory();
		BookMetaData bmd = book.getBookMetaData();
		osisToHtmlParameters.setLeftToRight(bmd.isLeftToRight());
		osisToHtmlParameters.setLanguageCode(book.getLanguage().getCode());
		osisToHtmlParameters.setModuleBasePath(book.getBookMetaData().getLocation());

		// If Bible or Commentary then set Basis for partial references to current Key/Verse
		if (BookCategory.BIBLE.equals(bookCategory) || BookCategory.COMMENTARY.equals(bookCategory)) {
			osisToHtmlParameters.setBasisRef(key);
			osisToHtmlParameters.setDocumentVersification(((AbstractPassageBook)book).getVersification());
		}

		if (isAndroid) {
	    	// HunUj has an error in that refs are not wrapped so automatically add notes around refs
	    	osisToHtmlParameters.setAutoWrapUnwrappedRefsInNote("HunUj".equals(book.getInitials()));

			SharedPreferences preferences = CommonUtils.getSharedPreferences();
			if (preferences!=null) {
				// prefs applying to any doc type
				osisToHtmlParameters.setShowNotes(preferences.getBoolean("show_notes_pref", true));
				osisToHtmlParameters.setRedLetter(preferences.getBoolean("red_letter_pref", false));
				osisToHtmlParameters.setCssStylesheetList( cssControl.getAllStylesheetLinks() );

				// show verse numbers if user has selected to show verse numbers AND the book is a bible (so don't even try to show verses in a Dictionary)
				if (BookCategory.BIBLE.equals(bookCategory)) {
					osisToHtmlParameters.setShowVerseNumbers(preferences.getBoolean("show_verseno_pref", true) && BookCategory.BIBLE.equals(bookCategory));
					osisToHtmlParameters.setVersePerline(preferences.getBoolean("verse_per_line_pref", false));
					osisToHtmlParameters.setShowMyNotes(preferences.getBoolean("show_mynotes_pref", true));
					osisToHtmlParameters.setShowBookmarks(preferences.getBoolean("show_bookmarks_pref", true));
					osisToHtmlParameters.setShowTitles(preferences.getBoolean("section_title_pref", true));
					osisToHtmlParameters.setVersesWithNotes(ControlFactory.getInstance().getMyNoteControl().getVersesWithNotesInPassage(key));
					osisToHtmlParameters.setVersesWithBookmarks(ControlFactory.getInstance().getBookmarkControl().getVersesWithBookmarksInPassage(key));

					// showMorphology depends on showStrongs to allow the toolbar toggle button to affect both strongs and morphology
					boolean showStrongs = preferences.getBoolean("show_strongs_pref", true);
					osisToHtmlParameters.setShowStrongs(showStrongs);
					osisToHtmlParameters.setShowMorphology(showStrongs && preferences.getBoolean("show_morphology_pref", false));
				}

				if (BookCategory.DICTIONARY.equals(bookCategory)) {
					if (book.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
						//add allHebrew refs link
						String prompt = CurrentActivityHolder.getInstance().getApplication().getString(R.string.all_hebrew_occurrences);
						osisToHtmlParameters.setExtraFooter("<br /><a href='" + Constants.ALL_HEBREW_OCCURRENCES_PROTOCOL + ":" + key.getName() + "' class='allStrongsRefsLink'>" + prompt + "</a>");

						//convert text refs to links
						osisToHtmlParameters.setConvertStrongsRefsToLinks(true);
					} else if (book.hasFeature(FeatureType.GREEK_DEFINITIONS)) {
						//add allGreek refs link
						String prompt = CurrentActivityHolder.getInstance().getApplication().getString(R.string.all_greek_occurrences);
						osisToHtmlParameters.setExtraFooter("<br /><a href='"+Constants.ALL_GREEK_OCCURRENCES_PROTOCOL+":"+key.getName()+"' class='allStrongsRefsLink'>"+prompt+"</a>");

						//convert text refs to links
						osisToHtmlParameters.setConvertStrongsRefsToLinks(true);
					}
				}

				// which font, if any
				osisToHtmlParameters.setFont(FontControl.getInstance().getFontForBook(book));
				osisToHtmlParameters.setCssClassForCustomFont(FontControl.getInstance().getCssClassForCustomFont(book));

				// indent depth - larger screens have a greater indent
				osisToHtmlParameters.setIndentDepth(CommonUtils.getResourceInteger(R.integer.poetry_indent_chars));
			}
		}
		return new OsisToHtmlSaxHandler(osisToHtmlParameters);
	}

	/**
     * When checking a book contains a chapter SwordBook returns false if verse 0 is not in the chapter so this method compensates for that
     * 
     * This can be removed if SwordBook.contains is converted to be containsAnyOf as discussed in JS-273
     */
    private boolean bookContainsAnyOf(Book book, Key key) {
        if (book.contains(key)) {
            return true;
        }
        
        Iterator<Key> iter = key.iterator();
        while (iter.hasNext()) {
            if (book.contains(iter.next())) {
                return true;
            }
        }
        
        return false;
    }
}
