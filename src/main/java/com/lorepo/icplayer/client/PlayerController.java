package com.lorepo.icplayer.client;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.lorepo.icf.utils.ILoadListener;
import com.lorepo.icf.utils.JavaScriptUtils;
import com.lorepo.icf.utils.URLUtils;
import com.lorepo.icf.utils.UUID;
import com.lorepo.icf.utils.XMLLoader;
import com.lorepo.icplayer.client.content.services.AssetsService;
import com.lorepo.icplayer.client.content.services.ReportableService;
import com.lorepo.icplayer.client.content.services.ScoreService;
import com.lorepo.icplayer.client.content.services.StateService;
import com.lorepo.icplayer.client.content.services.TimeService;
import com.lorepo.icplayer.client.model.Content;
import com.lorepo.icplayer.client.model.Page;
import com.lorepo.icplayer.client.model.PageList;
import com.lorepo.icplayer.client.module.api.IPresenter;
import com.lorepo.icplayer.client.module.api.player.IAssetsService;
import com.lorepo.icplayer.client.module.api.player.IPage;
import com.lorepo.icplayer.client.module.api.player.IPlayerServices;
import com.lorepo.icplayer.client.module.api.player.IReportableService;
import com.lorepo.icplayer.client.module.api.player.IScoreService;
import com.lorepo.icplayer.client.module.api.player.IStateService;
import com.lorepo.icplayer.client.module.api.player.ITimeService;
import com.lorepo.icplayer.client.page.KeyboardNavigationController;
import com.lorepo.icplayer.client.page.PageController;
import com.lorepo.icplayer.client.page.PagePopupPanel;
import com.lorepo.icplayer.client.ui.PlayerView;

public class PlayerController implements IPlayerController{

	private final	Content				contentModel;
	private PlayerConfig config = new PlayerConfig();
	private PageController		pageController1;
	private PageController		pageController2;
	private PageController		headerController;
	private PageController		footerController;
	private final	PlayerView			playerView;
	private long				timeStart = 0;
	private final TimeService			timeService;
	private final ScoreService		scoreService;
	private final AssetsService		assetsService;
	private final StateService		stateService;
	private final ReportableService reportableService;
	private ILoadListener		pageLoadListener;
	private PagePopupPanel		popupPanel;
	private final String sessionId;
	private String analyticsId;
	private boolean showCover = false;
	private boolean isPopupEnabled = false;
	private final KeyboardNavigationController keyboardController = new KeyboardNavigationController();
	private PlayerEntryPoint entryPoint;
	private int iframeScroll = 0;
	
	private int lastVisitedPageIndex = -1;
	private int currentMainPageIndex = -1;
	
	public PlayerController(Content content, PlayerView view, boolean bookMode, PlayerEntryPoint entryPoint){
		this.entryPoint = entryPoint;
		this.contentModel = content;
		this.playerView = view;
		this.playerView.setPlayerController(this);
		this.sessionId = UUID.uuid();
		this.scoreService = new ScoreService(this.contentModel.getScoreType());
		this.stateService = new StateService();
		this.assetsService = new AssetsService(this.contentModel);
		this.reportableService = new ReportableService();

		this.createPageControllers(bookMode);
		this.scoreService.setPlayerService(this.pageController1.getPlayerServices());
		this.timeService = new TimeService();
		this.keyboardController.run(entryPoint);
		this.getIFrameScroll(this);
	}

	private void createPageControllers(boolean bookMode) {
		this.pageController1 = new PageController(this);
		this.pageController1.setView(this.playerView.getPageView(0));
		if(bookMode){
			this.playerView.showTwoPages();
			this.pageController2 = new PageController(this);
			this.pageController2.setView(this.playerView.getPageView(1));
		}
	}

	public void initHeaders() {
		if (this.contentModel.getHeaders().size() > 0) {
			this.playerView.createHeader();
			this.headerController = new PageController(this.pageController1.getPlayerServices());
			this.headerController.setView(this.playerView.getHeaderView());
		}
		if (this.contentModel.getFooters().size() > 0) {
			this.playerView.createFooter();
			this.footerController = new PageController(this.pageController1.getPlayerServices());
			this.footerController.setView(this.playerView.getFooterView());
		}
	}


	public void addPageLoadListener(ILoadListener l){
		this.pageLoadListener = l;
	}

	/**
	 * get current loaded page index
	 * @return
	 */
	@Override
	public int getCurrentPageIndex(){
		return this.currentMainPageIndex;
	}


	@Override
	public Content	getModel(){
		return this.contentModel;
	}


	@Override
	public PlayerView getView(){
		return this.playerView;
	}

	@Override
	public void switchToCommonPage(String pageName) {
		int index = this.getModel().getCommonPages().findPageIndexByName(pageName);

		if (index > -1) {
			this.switchToCommonPage(index);
		} else {
			Window.alert("Missing page:\n<" + pageName + ">");
		}
	}

	/**
	 * Przełączenie się na stronę o podanej nazwie
	 * @param pageName
	 * @return true if page found
	 */
	@Override
	public void switchToPage(String pageName) {
		int index = this.getModel().getPages().findPageIndexByName(pageName);

		if (index > -1){
			this.switchToPage(index);
		} else {
			Window.alert("Missing page:\n<" + pageName + ">");
		}
	}
	
	@Override
	public void switchToCommonPageById(String id) {
		int index = this.getModel().getCommonPages().findPageIndexById(id);

		if (index > -1) {
			this.switchToCommonPage(index);
		} else {
			Window.alert("Missing common page:\n<" + id + ">");
		}
	}
	
	@Override
	public void switchToPageById(String pageId) {
		int index = this.getModel().getPages().findPageIndexById(pageId);
		if(index > -1){
			this.switchToPage(index);
		}
		else{
			Window.alert("Missing page with id:\n<" + pageId + ">");
		}
	}

	@Override
	public void switchToPrevPage() {
		int index = this.currentMainPageIndex-1;
		if(this.pageController2 != null && index > 0) {
			index -= 1;
		}
		if(index >= 0) {
			this.switchToPage(index);
		}
	}
	
	@Override
	public void switchToLastVisitedPage() {
		if(this.isCurrentPageInCommons()) {
			this.switchToPage(this.currentMainPageIndex);
		} else {
			this.switchToPage(this.lastVisitedPageIndex);
		}
	}


	@Override
	public void switchToNextPage() {

		PageList pages = this.contentModel.getPages();
	
		int index = this.currentMainPageIndex + 1;
		if(this.pageController2 != null && index + 1 < pages.getTotalPageCount()) {
			index += 1;
		}
		if(index < pages.getTotalPageCount()) {
			this.switchToPage(index);
		}
	}


	/**
	 * Switch to page at given index
	 * @param index
	 */
	@Override
	public void switchToPage(int index) {
		if (this.lastVisitedPageIndex == -1) { //if player was started for the first time
			this.lastVisitedPageIndex = index;
			this.currentMainPageIndex = index;
		}
		else if (this.currentMainPageIndex != index) {
			this.lastVisitedPageIndex = this.currentMainPageIndex;
			this.currentMainPageIndex = index;
		}
		
		this.closeCurrentPages();
		IPage page;
		if(this.pageController2 != null){
			if( (!this.showCover && index%2 > 0) ||
				(this.showCover && index%2 == 0 && index > 0))
			{
				index -= 1;
			}
		}
		if(index < this.contentModel.getPages().getTotalPageCount()){
			page = this.contentModel.getPage(index);
		}
		else{
			this.currentMainPageIndex = 0;
			page = this.contentModel.getPage(0);
		}

		if(this.showCover && index == 0){
			this.playerView.showSinglePage();
			this.switchToPage(page, this.pageController1);
		}
		else{
			this.switchToPage(page, this.pageController1);
			if(this.pageController2 != null && index+1 < this.contentModel.getPages().getTotalPageCount()){
				this.playerView.showTwoPages();
				page = this.contentModel.getPage(index+1);
				this.switchToPage(page, this.pageController2);
			}
		}
	}

	public void switchToCommonPage(int index) {
		JavaScriptUtils.log("switchToCommonPage START");
		this.closeCurrentPages();
		IPage page;
		if (this.pageController2 != null) {
			if ((!this.showCover && index % 2 > 0) || (this.showCover && index % 2 == 0 && index > 0)) {
				index -= 1;
			}
		}

		if (index < this.contentModel.getCommonPages().getTotalPageCount()) {
			page = this.contentModel.getCommonPage(index);
		} else {
			page = this.contentModel.getCommonPage(0);
		}

		if (this.showCover && index == 0) {
			this.playerView.showSinglePage();
			this.switchToPage(page, this.pageController1);
		} else {
			this.switchToPage(page, this.pageController1);
			if (this.pageController2 != null && index+1 < this.contentModel.getCommonPages().getTotalPageCount()) {
				this.playerView.showTwoPages();
				page = this.contentModel.getCommonPage(index+1);
				this.switchToPage(page, this.pageController2);
			}
		}
		JavaScriptUtils.log("switchToCommonPage END");
	}


	private void switchToPage(IPage page, final PageController pageController){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("page", page.getId());
		this.sendAnalytics("switch to page", params );
		// Load new page
		String baseUrl = this.contentModel.getBaseUrl();
		XMLLoader reader = new XMLLoader(page);
		String url = URLUtils.resolveURL(baseUrl, page.getHref());
		this.playerView.showWaitDialog();
		reader.load(url, new ILoadListener() {

			@Override
			public void onFinishedLoading(Object obj) {
				Page page = (Page) obj;
				String isReportable = getReportableService().getStates().get(page.getId());
				if (isReportable != null) {
					if (isReportable.toLowerCase() == "true") {
						page.setAsReportable();
					} else {
						page.setAsNonReportable();
					}
				}
				pageLoaded(page, pageController);
				if(pageLoadListener != null){
					pageLoadListener.onFinishedLoading(obj);
				}
				playerView.hideWaitDialog();
				if(timeStart == 0){
					timeStart = System.currentTimeMillis();
				}

				if (!keyboardController.isModuleActivated()) {
					scrollViewToBeggining();
				}
			}

			@Override
			public void onError(String error) {
				playerView.hideWaitDialog();
				JavaScriptUtils.log("Can't load page: " + error);
			}
		});

	}

	private void pageLoaded(Page page, PageController pageController) {
		JavaScriptUtils.log("pageLoaded START");
		this.keyboardController.save();
		JavaScriptUtils.log("pageLoaded 1");
		this.keyboardController.reset();
		JavaScriptUtils.log("pageLoaded 2");

		pageController.setPage(page);
		JavaScriptUtils.log("pageLoaded 3");
		
		if (this.headerController != null && pageController != this.pageController2) {
			JavaScriptUtils.log("pageLoaded 4");
		    this.setHeader(page);
		    JavaScriptUtils.log("pageLoaded 5");
		}
		JavaScriptUtils.log("pageLoaded 6");
		this.keyboardController.addHeaderToNavigation(this.headerController);
		JavaScriptUtils.log("pageLoaded 7");
		
		this.keyboardController.addMainToNavigation(this.pageController1);
		JavaScriptUtils.log("pageLoaded 8");
		this.keyboardController.addSecondToNavigation(this.pageController2);
		JavaScriptUtils.log("pageLoaded 9");

		if (this.footerController != null && pageController != this.pageController2) {
			JavaScriptUtils.log("pageLoaded 10");
			this.setFooter(page);
			JavaScriptUtils.log("pageLoaded 11");
		}
		JavaScriptUtils.log("pageLoaded 12");
		this.keyboardController.addFooterToNavigation(this.footerController);
		JavaScriptUtils.log("pageLoaded 13");
		this.keyboardController.restore();
		JavaScriptUtils.log("pageLoaded END");
	}

	private static void scrollViewToBeggining() {

		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			@Override
			public void execute() {
				Window.scrollTo(0, 0);
			}
		});
	}

	private void closeCurrentPages() {
		this.closePopup();
		this.pageController1.updateScore(false);

		if (this.isBookMode()) {
			this.pageController2.updateScore(false);
		}

		this.updateState();

		JavaScriptUtils.log("closeCurrentPages 1");
		this.pageController1.closePage();
		JavaScriptUtils.log("closeCurrentPages 2");
		if(this.isBookMode()){
			JavaScriptUtils.log("closeCurrentPages 3");
			this.pageController2.closePage();
			JavaScriptUtils.log("closeCurrentPages 4");
		}
	}

	public void updateScore() {
		this.pageController1.updateScore(false);

		if (this.isBookMode()) {
			this.pageController2.updateScore(false);
		}
	}

	private void updateTimeForCurrentPages() {
		IPage page1 = this.pageController1.getPage();
		IPage page2 = null;
		if (this.pageController2 != null) {
			page2 = this.pageController2.getPage();
		}
		this.timeService.updateTimeForPages(page1, page2);
	}

	public void updateState() {
		this.updateTimeForCurrentPages();
		HashMap<String, String> state = this.pageController1.getState();
		this.stateService.addState(state);
		if(this.pageController2 != null){
			state = this.pageController2.getState();
			this.stateService.addState(state);
		}
		if(this.headerController != null) {
			state = this.headerController.getState();
			this.stateService.addState(state);
		}
		if(this.footerController != null) {
			state = this.footerController.getState();
			this.stateService.addState(state);
		}
	}


	public IPlayerServices getPlayerServices() {
		return this.pageController1.getPlayerServices();
	}


	@Override
	public long getTimeElapsed() {
		return (System.currentTimeMillis()-this.timeStart)/1000;
	}


	@Override
	public IScoreService getScoreService() {
		return this.scoreService;
	}

	@Override
	public IAssetsService getAssetsService() {
		return this.assetsService;
	}


	@Override
	public IStateService getStateService() {
		return this.stateService;
	}


	@Override
	public void showPopup(String pageName, String top, String left, String additionalClasses) {
		if (this.isPopupEnabled()) {
			return;
		}
		this.setPopupEnabled(true);
		Page page  = this.contentModel.findPageByName(pageName);
		PageController popupPageControler = new PageController(this);
		this.popupPanel = new PagePopupPanel(this.getView(), popupPageControler, top, left, additionalClasses);
		this.popupPanel.showPage(page, this.contentModel.getBaseUrl());
		this.popupPanel.setPagePlayerController(this.pageController1);
	}


	@Override
	public void closePopup() {
		if(this.popupPanel != null){
			this.popupPanel.close();
			this.setPopupEnabled(false);
			this.popupPanel = null;
		}
	}


	@Override
	public void sendAnalytics(String event, HashMap<String, String> params) {
		if(this.analyticsId == null){
			return;
		}

		String url = "http://www.bluenotepad.com/api/log?" +
				"notepad=" + this.analyticsId + "&session=" + this.sessionId + "&event=" + event;
		if( params != null){
			for(String key : params.keySet()){
				url += "&" + key + "=" + params.get(key).replace("&nbsp;", " ");
			}
		}
		String encodedUrl = URL.encode(url);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, encodedUrl);
		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(Request request, Throwable exception) {
				}
				@Override
				public void onResponseReceived(Request request, Response response){
				}
			});
		} catch (RequestException e) {
		}
	}


	public void setAnalytics(String id) {
		this.analyticsId = id;
	}


	public void setFirstPageAsCover(boolean showCover) {
		this.showCover = showCover;
	}


	@Override
	public boolean isBookMode() {
		return this.pageController2 != null;
	}


	@Override
	public boolean hasCover() {
		return this.showCover;
	}


	@Override
	public boolean isPopupEnabled() {
		return this.isPopupEnabled;
	}


	@Override
	public void setPopupEnabled(boolean enabled) {
		this.isPopupEnabled = enabled;
	}


	@Override
	public IPresenter findHeaderModule(String id) {
		if (this.headerController == null) {
			return null;
		}

		return this.headerController.findModule(id);
	}

	@Override
	public IPresenter findFooterModule(String id) {
		if (this.footerController == null) {
			return null;
		}

		return this.footerController.findModule(id);
	}

	@Override
	public PlayerConfig getPlayerConfig() {
		return this.config;
	}

	@Override
	public ITimeService getTimeService() {
		this.updateTimeForCurrentPages();
		return this.timeService;
	}
	
	public boolean hasHeader() {
		return this.headerController != null;
	}
	
	public boolean hasFooter() {
		return this.footerController != null;
	}


	public void setPlayerConfig(PlayerConfig config) {
		this.config = config;
	}

	public void fireOutstretchHeightEvent() {
		this.entryPoint.fireOutstretchHeightEvent();
	}

	@Override
	public int getIframeScroll() {
		return this.iframeScroll;
	}
	
	public void setIframeScroll (int scroll) {
		this.iframeScroll = scroll;
	}
	
	public native int getIFrameScroll (PlayerController x) /*-{
		var iframeScroll = 0;
		$wnd.addEventListener('message', function (event) {
			var data = event.data;
	
			if ((typeof data == 'string' || data instanceof String) && data.indexOf('I_FRAME_SCROLL:') === 0) {
				iframeScroll = JSON.parse(data.replace('I_FRAME_SCROLL:', ''));
				x.@com.lorepo.icplayer.client.PlayerController::setIframeScroll(I)(iframeScroll);
			}
		}, false);
	}-*/;

	@Override
	public IReportableService getReportableService() {
		return this.reportableService;
	}

	private boolean isCurrentPageInCommons() {
		int commonsPageSize = this.contentModel.getCommonPages().getPageCount();
		for(int i = 0; i < commonsPageSize; i++) {
			if(this.contentModel.getCommonPage(i) == this.pageController1.getPage()) {
				return true;
			}
		}
		return false;
	}
	
	private void setHeader(Page page) {
		Page header = null;
		
		if (page.hasHeader()) {
			header = this.getModel().getHeader(page);
		}
		
	    if (header != null) {
			//default or selected header exists
	    	if (this.playerView.getHeaderView() == null) {
	    		this.playerView.createHeader();
	    		this.headerController.setView(this.playerView.getHeaderView());
	    	}
	    	
			this.headerController.setPage(header);
	    } else {
			this.playerView.removeHeaderView();
		}
	}
	
	private void setFooter(Page page) {
		Page footer = null;
		
		if (page.hasFooter()) {
			footer = this.getModel().getFooter(page);
		}

		if (footer != null) {
			//default or selected footer exists
			if (this.playerView.getFooterView() == null) {
	    		this.playerView.createFooter();
	    		this.footerController.setView(this.playerView.getFooterView());
	    	}
			
			this.footerController.setPage(footer);
		} else {
			this.playerView.removeFooterView();
		}
	}
	
}
