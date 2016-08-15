/**
 *
 */
package com.bellman.bible.android.control.mynote;

import android.util.Log;
import android.widget.Toast;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.control.ControlFactory;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.service.common.CommonUtils;
import com.bellman.bible.service.db.mynote.MyNoteDBAdapter;
import com.bellman.bible.service.db.mynote.MyNoteDto;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User Note controller methods
 *
 * @author John D. Lewis [balinjdl at gmail dot com]
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public class MyNoteControl implements MyNote {

    private static final String MYNOTE_SORT_ORDER = "MyNoteSortOrder";

    private static final String TAG = "MyNoteControl";

    /**
     * Start chain of actions to switch to MyNote view
     *
     * @param verseRange
     */
    @Override
    public void showMyNote(VerseRange verseRange) {
        // if existing MyNote exists with same start verse then adjust range to match the note that will be edited
        final MyNoteDto existingMyNoteWithSameStartVerse = getMyNoteByStartVerse(verseRange);
        if (existingMyNoteWithSameStartVerse != null) {
            verseRange = existingMyNoteWithSameStartVerse.getVerseRange(verseRange.getVersification());
        }

        ControlFactory.getInstance().getCurrentPageControl().showMyNote(verseRange);
    }

    @Override
    public void showNoteView(MyNoteDto noteDto) {
        ControlFactory.getInstance().getCurrentPageControl().showMyNote(noteDto.getVerseRange());
    }

    @Override
    public String getMyNoteVerseKey(MyNoteDto myNote) {
        String keyText = "";
        try {
            Versification versification = ControlFactory.getInstance().getCurrentPageControl().getCurrentBible().getVersification();
            keyText = myNote.getVerseRange(versification).getName();
        } catch (Exception e) {
            Log.e(TAG, "Error getting verse text", e);
        }
        return keyText;
    }

    @Override
    public String getMyNoteTextByKey(Key verseRange) {
        // get a dto
        MyNoteDto myNote = getMyNoteByStartVerse(verseRange);

        // return an empty note dto
        String noteText = "";
        if (myNote != null) {
            noteText = myNote.getNoteText();
        }

        return noteText;
    }

    @Override
    public boolean saveMyNoteText(String myNote) {
        MyNoteDto dto = getCurrentMyNoteDto();
        dto.setNoteText(myNote);
        return saveMyNote(dto);
    }

    @Override
    public MyNoteDto getCurrentMyNoteDto() {
        //
        Key key = ControlFactory.getInstance().getCurrentPageControl().getCurrentMyNotePage().getKey();
        VerseRange verseRange;
        // The key should be a VerseRange
        if (key instanceof VerseRange) {
            verseRange = (VerseRange) key;
        } else {
            Verse verse = KeyUtil.getVerse(key);
            verseRange = new VerseRange(verse.getVersification(), verse);
        }

        // get a dto
        MyNoteDto myNote = getMyNoteByStartVerse(verseRange);

        // return an empty note dto
        if (myNote == null) {
            myNote = new MyNoteDto();
            myNote.setVerseRange(verseRange);
        }

        return myNote;
    }

    /**
     * save the note to the database if it is new or has been updated
     */
    @Override
    public boolean saveMyNote(MyNoteDto myNoteDto) {
        Log.d(TAG, "saveMyNote started...");
        boolean isSaved = false;

        if (myNoteDto.isNew()) {
            if (!myNoteDto.isEmpty()) {
                myNoteDto = addMyNote(myNoteDto);
                isSaved = true;
            }
        } else {
            MyNoteDto oldNote = getMyNoteByStartVerse(myNoteDto.getVerseRange());
            // delete empty notes
            if (myNoteDto.isEmpty()) {
                deleteMyNote(myNoteDto);
            } else if (!myNoteDto.equals(oldNote)) {
                // update changed notes
                updateMyNote(myNoteDto);
                isSaved = true;
            }
        }
        if (isSaved) {
            Toast.makeText(CurrentActivityHolder.getInstance().getContext(), R.string.mynote_saved, Toast.LENGTH_SHORT).show();
        }
        return isSaved;
    }

    @Override
    public String getMyNoteText(MyNoteDto myNote, boolean abbreviated) {
        String text = "";
        try {
            text = myNote.getNoteText();
            if (abbreviated) {
                //TODO allow longer lines if portrait or tablet
                boolean singleLine = true;
                text = CommonUtils.limitTextLength(text, 40, singleLine);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user note text", e);
        }
        return text;
    }

    // pure myNote methods

    /**
     * get all myNotes
     */
    public List<MyNoteDto> getAllMyNotes() {
        MyNoteDBAdapter db = new MyNoteDBAdapter();
        List<MyNoteDto> myNoteList = null;
        try {
            db.open();
            myNoteList = db.getAllMyNotes();
            myNoteList = getSortedMyNotes(myNoteList);
        } finally {
            db.close();
        }

        return myNoteList;
    }

    /**
     * get all user notes
     */
    public MyNoteDto getMyNoteById(Long id) {
        MyNoteDBAdapter db = new MyNoteDBAdapter();
        MyNoteDto myNote = null;
        try {
            db.open();
            myNote = db.getMyNoteDto(id);
        } finally {
            db.close();
        }

        return myNote;
    }

    /**
     * get user note with this key if it exists or return null
     */
    public MyNoteDto getMyNoteByStartVerse(Key key) {
        Verse startVerse = KeyUtil.getVerse(key);

        MyNoteDBAdapter db = new MyNoteDBAdapter();
        MyNoteDto myNote = null;
        try {
            db.open();
            myNote = db.getMyNoteByStartVerse(startVerse.getOsisRef());
        } finally {
            db.close();
        }

        return myNote;
    }

    /**
     * delete this user note (and any links to labels)
     */
    public boolean deleteMyNote(MyNoteDto myNote) {
        boolean bOk = false;
        if (myNote != null && myNote.getId() != null) {
            MyNoteDBAdapter db = new MyNoteDBAdapter();
            try {
                db.open();
                bOk = db.removeMyNote(myNote);
            } finally {
                db.close();
            }
        }
        return bOk;
    }

    /**
     * create a new myNote
     */
    private MyNoteDto addMyNote(MyNoteDto myNote) {
        MyNoteDBAdapter db = new MyNoteDBAdapter();
        MyNoteDto newMyNote = null;
        try {
            db.open();
            newMyNote = db.insertMyNote(myNote);
        } finally {
            db.close();
        }
        return newMyNote;
    }

    /**
     * create a new myNote
     */
    private MyNoteDto updateMyNote(MyNoteDto myNote) {
        MyNoteDBAdapter db = new MyNoteDBAdapter();
        MyNoteDto updatedMyNote = null;
        try {
            db.open();
            updatedMyNote = db.updateMyNote(myNote);
        } finally {
            db.close();
        }
        return updatedMyNote;
    }

    @Override
    public List<Verse> getVersesWithNotesInPassage(Key passage) {
        // assumes the passage only covers one book, which always happens to be the case here
        Verse firstVerse = KeyUtil.getVerse(passage);
        BibleBook book = firstVerse.getBook();

        MyNoteDBAdapter db = new MyNoteDBAdapter();
        List<MyNoteDto> myNoteList = null;
        try {
            db.open();
            myNoteList = db.getMyNotesInBook(book);
        } finally {
            db.close();
        }

        // convert to required versification and check verse is in passage
        List<Verse> versesInPassage = new ArrayList<Verse>();
        if (myNoteList != null) {
            boolean isVerseRange = passage instanceof VerseRange;
            Versification requiredVersification = firstVerse.getVersification();
            for (MyNoteDto myNoteDto : myNoteList) {
                VerseRange verseRange = myNoteDto.getVerseRange(requiredVersification);
                //TODO should not require VerseRange cast but bug in JSword
                if (isVerseRange) {
                    if (((VerseRange) passage).contains(verseRange.getStart())) {
                        versesInPassage.add(verseRange.getStart());
                    }
                } else {
                    if (passage.contains(verseRange)) {
                        versesInPassage.add(verseRange.getStart());
                    }
                }
            }
        }

        return versesInPassage;
    }

    private List<MyNoteDto> getSortedMyNotes(List<MyNoteDto> myNoteList) {
        Comparator<MyNoteDto> comparator = null;
        switch (getSortOrder()) {
            case DATE_CREATED:
                comparator = MyNoteDto.MYNOTE_CREATION_DATE_COMPARATOR;
                break;
            case BIBLE_BOOK:
            default:
                comparator = MyNoteDto.MYNOTE_BIBLE_ORDER_COMPARATOR;
                break;

        }
        Collections.sort(myNoteList, comparator);
        return myNoteList;
    }

    public void changeSortOrder() {
        if (getSortOrder().equals(MyNoteSortOrder.BIBLE_BOOK)) {
            setSortOrder(MyNoteSortOrder.DATE_CREATED);
        } else {
            setSortOrder(MyNoteSortOrder.BIBLE_BOOK);
        }
    }

    public MyNoteSortOrder getSortOrder() {
        String sortOrderStr = CommonUtils.getSharedPreference(MYNOTE_SORT_ORDER, MyNoteSortOrder.BIBLE_BOOK.toString());
        return MyNoteSortOrder.valueOf(sortOrderStr);
    }

    private void setSortOrder(MyNoteSortOrder sortOrder) {
        CommonUtils.saveSharedPreference(MYNOTE_SORT_ORDER, sortOrder.toString());
    }

    @Override
    public String getSortOrderDescription() {
        if (MyNoteSortOrder.BIBLE_BOOK.equals(getSortOrder())) {
            return CommonUtils.getResourceString(R.string.sort_by_bible_book);
        } else {
            return CommonUtils.getResourceString(R.string.sort_by_date);
        }
    }
}
