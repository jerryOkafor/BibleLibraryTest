package com.bellman.bible.android.control.comparetranslations;

import android.util.Log;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.control.page.CurrentPageManager;
import com.bellman.bible.android.control.versification.BibleTraverser;
import com.bellman.bible.android.control.versification.ConvertibleVerseRange;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.font.FontControl;
import com.bellman.bible.service.sword.SwordContentFacade;
import com.bellman.bible.service.sword.SwordDocumentFacade;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BookName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Support the Compare Translations screen
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class CompareTranslationsControl {

    private static final String TAG = "CompareTranslationsCtrl";
    private SwordDocumentFacade swordDocumentFacade = SwordDocumentFacade.getInstance();
    private SwordContentFacade swordContentFacade = SwordContentFacade.getInstance();
    private BibleTraverser bibleTraverser;

    public CompareTranslationsControl(BibleTraverser bibleTraverser) {
        this.bibleTraverser = bibleTraverser;
    }

    public String getTitle(VerseRange verseRange) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean wasFullBookname = BookName.isFullBookName();
        BookName.setFullBookName(false);

        stringBuilder.append(CurrentActivityHolder.getInstance().getApplication().getString(R.string.compare_translations))
                .append(": ")
                .append(CommonUtils.getKeyDescription(verseRange));

        BookName.setFullBookName(wasFullBookname);
        return stringBuilder.toString();
    }

    public void setVerse(Verse verse) {
        getCurrentPageManager().getCurrentBible().doSetKey(verse);
    }

    /**
     * Calculate next verse
     */
    public VerseRange getNextVerseRange(VerseRange verseRange) {
        return bibleTraverser.getNextVerseRange(getCurrentPageManager().getCurrentPassageDocument(), verseRange);
    }

    /**
     * Calculate next verse
     */
    public VerseRange getPreviousVerseRange(VerseRange verseRange) {
        return bibleTraverser.getPreviousVerseRange(getCurrentPageManager().getCurrentPassageDocument(), verseRange);
    }

    /**
     * return the list of verses to be displayed
     */
    public List<TranslationDto> getAllTranslations(VerseRange verseRange) {
        List<TranslationDto> retval = new ArrayList<>();
        List<Book> books = swordDocumentFacade.getBibles();
        FontControl fontControl = FontControl.getInstance();

        ConvertibleVerseRange convertibleVerseRange = new ConvertibleVerseRange(verseRange);

        for (Book book : books) {
            try {
                String text = swordContentFacade.getPlainText(book, convertibleVerseRange.getVerseRange(((AbstractPassageBook) book).getVersification()), 1);
                if (text.length() > 0) {

                    // does this book require a custom font to display it
                    File fontFile = null;
                    String fontForBook = fontControl.getFontForBook(book);
                    if (StringUtils.isNotEmpty(fontForBook)) {
                        fontFile = fontControl.getFontFile(fontForBook);
                    }

                    // create DTO with all required info to display this Translation text
                    retval.add(new TranslationDto(book, text, fontFile));
                }
            } catch (Exception nske) {
                Log.d(TAG, verseRange + " not in " + book);
            }
        }

        return retval;
    }

    public void showTranslationForVerseRange(TranslationDto translationDto, VerseRange verseRange) {
        getCurrentPageManager().setCurrentDocumentAndKey(translationDto.getBook(), verseRange.getStart());
    }

    public CurrentPageManager getCurrentPageManager() {
        return ControlFactory.getInstance().getCurrentPageControl();
    }
}
