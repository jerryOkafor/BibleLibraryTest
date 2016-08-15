package com.bellman.bible.service.download;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.sword.SwordBook;

import java.util.List;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public abstract class RepoBase {

	public abstract List<Book> getRepoBooks(boolean refresh) throws InstallException;
	public abstract String getRepoName();

	/** get a list of books that are available in Xiphos repo and seem to work in Embedded Bible
	 */
	public List<Book> getBookList(BookFilter bookFilter, boolean refresh) throws InstallException {
		
		DownloadManager crossWireDownloadManager = new DownloadManager();
        List<Book> bookList = crossWireDownloadManager.getDownloadableBooks(bookFilter, getRepoName(), refresh);

		return bookList;		
	}

	public void storeRepoNameInMetaData(List<Book> bookList) {
		for (Book book : bookList) {
			// SwordBookMetaData must not persist these properties because many downloadable books may have the same name, 
			// and we set the props every time so they do not need to be persisted
			if (book instanceof SwordBook) {
				book.getBookMetaData().setProperty(DownloadManager.REPOSITORY_KEY, getRepoName());
			} else {
				book.getBookMetaData().putProperty(DownloadManager.REPOSITORY_KEY, getRepoName());
			}
        }
	}

	public void downloadDocument(Book document) throws InstallException, BookException {
		DownloadManager downloadManager = new DownloadManager();
		downloadManager.installBook(getRepoName(), document);
	}

}
