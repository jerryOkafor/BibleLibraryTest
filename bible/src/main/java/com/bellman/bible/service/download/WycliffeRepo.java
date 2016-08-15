package com.bellman.bible.service.download;

import com.bellman.bible.service.sword.AcceptableBookTypeFilter;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallException;

import java.util.List;

/** some books need renaming after download due to problems with Xiphos module case
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class WycliffeRepo extends RepoBase {

	// see here for info ftp://ftp.xiphos.org/mods.d/
	private static final String REPOSITORY = "Wycliffe";
	
	private static BookFilter SUPPORTED_DOCUMENTS = new AcceptableBookTypeFilter();

	/** get a list of books that are available in default repo and seem to work in Embedded Bible
	 */
	public List<Book> getRepoBooks(boolean refresh) throws InstallException {

		List<Book> books = getBookList(SUPPORTED_DOCUMENTS, refresh);
		storeRepoNameInMetaData(books);
		
		return books;
	}
	
	@Override
	public String getRepoName() {
		return REPOSITORY;
	}
}
