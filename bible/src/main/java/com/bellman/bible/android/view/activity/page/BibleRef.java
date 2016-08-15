package com.bellman.bible.android.view.activity.page;

import org.crosswire.jsword.versification.BibleBook;

/**
 * Created by Potencio on 8/7/2016.
 */
public class BibleRef {
    private int book;
    private int chapter;
    private int verse;

    public BibleRef(int book, int chapter, int verse) {
        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
    }

    public int getBook() {
        return book;
    }

    public void setBook(int book) {
        this.book = book;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }


    @Override
    public String toString() {
        return  "Bible Ref: "+ book+"."+getChapter()+"."+getVerse();
    }
}
