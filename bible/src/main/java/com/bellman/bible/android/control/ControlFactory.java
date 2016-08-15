package com.bellman.bible.android.control;

import android.content.Context;

import com.bellman.bible.android.common.resource.AndroidResourceProvider;
import com.bellman.bible.android.common.resource.ResourceProvider;
import com.bellman.bible.android.control.backup.BackupControl;
import com.bellman.bible.android.control.bookmark.Bookmark;
import com.bellman.bible.android.control.bookmark.BookmarkControl;
import com.bellman.bible.android.control.comparetranslations.CompareTranslationsControl;
import com.bellman.bible.android.control.document.DocumentControl;
import com.bellman.bible.android.control.download.DownloadControl;
import com.bellman.bible.android.control.email.Emailer;
import com.bellman.bible.android.control.email.EmailerImpl;
import com.bellman.bible.android.control.event.ABEventBus;
import com.bellman.bible.android.control.event.EventManager;
import com.bellman.bible.android.control.footnoteandref.FootnoteAndRefControl;
import com.bellman.bible.android.control.link.LinkControl;
import com.bellman.bible.android.control.mynote.MyNote;
import com.bellman.bible.android.control.mynote.MyNoteControl;
import com.bellman.bible.android.control.navigation.DocumentBibleBooksFactory;
import com.bellman.bible.android.control.navigation.NavigationControl;
import com.bellman.bible.android.control.page.CurrentPageManager;
import com.bellman.bible.android.control.page.PageControl;
import com.bellman.bible.android.control.page.PageTiltScrollControl;
import com.bellman.bible.android.control.page.window.Window;
import com.bellman.bible.android.control.page.window.WindowControl;
import com.bellman.bible.android.control.page.window.WindowRepository;
import com.bellman.bible.android.control.readingplan.ReadingPlanControl;
import com.bellman.bible.android.control.report.ErrorReportControl;
import com.bellman.bible.android.control.search.SearchControl;
import com.bellman.bible.android.control.speak.SpeakControl;
import com.bellman.bible.android.control.versification.BibleTraverser;
import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;
import com.bellman.bible.android.view.activity.page.BibleJavascriptInterface;
import com.bellman.bible.android.view.activity.page.BibleView;
import com.bellman.bible.android.view.activity.page.MainBibleActivity;
import com.bellman.bible.android.view.activity.page.VerseActionModeMediator;
import com.bellman.bible.android.view.activity.page.VerseCalculator;
import com.bellman.bible.android.view.activity.page.VerseMenuCommandHandler;

import java.util.HashMap;
import java.util.Map;

//TODO replace with ioc (maybe)

/** allow access to control layer
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class ControlFactory {
	private static ControlFactory singleton;
	private MainBibleActivity mainBibleActivity;
	private ResourceProvider resourceProvider;
	private EventManager eventManager;
	private Context context;
	private WindowRepository windowRepository;
	private DocumentBibleBooksFactory documentBibleBooksFactory = new DocumentBibleBooksFactory();
	private BibleTraverser bibleTraverser = new BibleTraverser();
	private DocumentControl documentControl = new DocumentControl();
	private PageControl pageControl = new PageControl();
	private WindowControl windowControl;
	private Map<Window, PageTiltScrollControl> screenPageTiltScrollControlMap = new HashMap<>();
	private LinkControl linkControl;
	private SearchControl searchControl = new SearchControl();
	private MyNote mynoteControl = new MyNoteControl();
	private DownloadControl downloadControl = new DownloadControl();
	private SpeakControl speakControl = new SpeakControl();
	private ReadingPlanControl readingPlanControl = new ReadingPlanControl();
	private CompareTranslationsControl compareTranslationsControl;
	private FootnoteAndRefControl footnoteAndRefControl;
	private BackupControl backupControl = new BackupControl();
	private Bookmark bookmarkControl;
	private Emailer emailer;
	private ErrorReportControl errorReportControl;
	private NavigationControl navigationControl = new NavigationControl();
	private boolean initialised = false;

	protected ControlFactory() {
	}

	public static ControlFactory getInstance() {
		if (singleton==null) {
			synchronized(ControlFactory.class) {
				if (singleton==null) {
					singleton = new ControlFactory();
					singleton.createAll();
				}
			}
		}
		return singleton;
	}

	public static void setInstance(ControlFactory controlFactory) {
		singleton = controlFactory;
	}
	
	protected void createAll() {
		resourceProvider = new AndroidResourceProvider();
		eventManager = ABEventBus.getDefault();

		emailer = new EmailerImpl();
		errorReportControl = new ErrorReportControl(emailer);

		bookmarkControl = new BookmarkControl(CurrentActivityHolder.getInstance().getCurrentActivity().getApplicationContext(), resourceProvider);

		// inject dependencies
		readingPlanControl.setSpeakControl(this.speakControl);

		navigationControl.setPageControl(this.pageControl);
		navigationControl.setDocumentBibleBooksFactory(documentBibleBooksFactory);
		searchControl.setDocumentBibleBooksFactory(documentBibleBooksFactory);
		
		bibleTraverser.setDocumentBibleBooksFactory(documentBibleBooksFactory);

		compareTranslationsControl = new CompareTranslationsControl(bibleTraverser);
		footnoteAndRefControl = new FootnoteAndRefControl(bibleTraverser);

		windowRepository = new WindowRepository();
		windowControl = new WindowControl(windowRepository, eventManager);

		linkControl = new LinkControl(windowControl);
	}

	protected void ensureAllInitialised() {
		if (!initialised) {
			synchronized (this) {
				if (!initialised) {
					windowRepository.initialise(eventManager);
					initialised = true;
				}
			}
		}
	}
	
	public DocumentControl getDocumentControl() {
		ensureAllInitialised();

		return documentControl;		
	}

	public DocumentBibleBooksFactory getDocumentBibleBooksFactory() {
		return documentBibleBooksFactory;
	}

	public PageControl getPageControl() {
		ensureAllInitialised();
		return pageControl;		
	}

	public WindowControl getWindowControl() {
		ensureAllInitialised();
		return windowControl;
	}

	public PageTiltScrollControl getPageTiltScrollControl(Window window) {
		PageTiltScrollControl pageTiltScrollControl = screenPageTiltScrollControlMap.get(window);
		if (pageTiltScrollControl == null) {
			synchronized (screenPageTiltScrollControlMap) {
				pageTiltScrollControl = screenPageTiltScrollControlMap.get(window);
				if (pageTiltScrollControl == null) {
					pageTiltScrollControl = new PageTiltScrollControl();
					screenPageTiltScrollControlMap.put(window, pageTiltScrollControl);
				}
			}
		}
		return pageTiltScrollControl;
	}

	public void provide(MainBibleActivity mainBibleActivity) {
		this.mainBibleActivity = mainBibleActivity;
	}

	public void inject(BibleView bibleView) {
		VerseActionModeMediator bibleViewVerseActionModeMediator = new VerseActionModeMediator(mainBibleActivity, bibleView, getPageControl(), new VerseMenuCommandHandler(mainBibleActivity, getPageControl()));

		BibleJavascriptInterface bibleJavascriptInterface = new BibleJavascriptInterface(bibleViewVerseActionModeMediator);

		bibleView.setBibleJavascriptInterface(bibleJavascriptInterface);
	}

	public void inject(BibleJavascriptInterface bibleJavascriptInterface) {
		bibleJavascriptInterface.setVerseCalculator(new VerseCalculator());
	}

	public SearchControl getSearchControl() {
		return searchControl;
	}

	public CurrentPageManager getCurrentPageControl() {
		ensureAllInitialised();
		Window activeWindow = windowControl.getActiveWindow();
		return activeWindow.getPageManager();		
	}

	public LinkControl getLinkControl() {
		return linkControl;
	}

	public Bookmark getBookmarkControl() {
		return bookmarkControl;
	}

	public MyNote getMyNoteControl() {
		return mynoteControl;
	}

	public DownloadControl getDownloadControl() {
		return downloadControl;
	}

	public SpeakControl getSpeakControl() {
		return speakControl;
	}

	public ReadingPlanControl getReadingPlanControl() {
		return readingPlanControl;
	}

	public CompareTranslationsControl getCompareTranslationsControl() {
		return compareTranslationsControl;
	}

	public FootnoteAndRefControl getFootnoteAndRefControl() {
		return footnoteAndRefControl;
	}

	public BackupControl getBackupControl() {
		return backupControl;
	}

	public NavigationControl getNavigationControl() {
		return navigationControl;
	}

	public BibleTraverser getBibleTraverser() {
		return bibleTraverser;
	}

	public ErrorReportControl getErrorReportControl() {
		return errorReportControl;
	}


}
