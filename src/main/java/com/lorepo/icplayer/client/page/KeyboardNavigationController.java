package com.lorepo.icplayer.client.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.lorepo.icf.utils.NavigationModuleIndentifier;
import com.lorepo.icplayer.client.PlayerEntryPoint;
import com.lorepo.icplayer.client.module.IButton;
import com.lorepo.icplayer.client.module.IWCAG;
import com.lorepo.icplayer.client.module.IWCAGPresenter;
import com.lorepo.icplayer.client.module.addon.AddonPresenter;
import com.lorepo.icplayer.client.module.api.IPresenter;
import com.lorepo.icplayer.client.module.choice.ChoiceView;
import com.lorepo.icplayer.client.module.text.TextView;

/*
	Usage:
		Module:
			- Presenter must implement IWCAGPresenter interface and must return IWCAG element
		Addon:
			- Addon must have keyboardController(keyCode, isShiftDown) function in presenter
*/
public final class KeyboardNavigationController {
	private boolean moduleIsActivated = false;
	private boolean isInitiated = false;
	private List<PresenterEntry> presentersOriginalOrder = new ArrayList<PresenterEntry>();
	private List<PresenterEntry> presenters = new ArrayList<PresenterEntry>();
	private boolean modeOn = false;
	private PlayerEntryPoint entryPoint;
	private PageController mainPageController;
	private PageController headerController;
	private PageController footerController;
	private JavaScriptObject invisibleInputForFocus = null;
	private int actualSelectedModuleIndex = 0;
	private boolean isWCAGSupportOn = false;
	private boolean isPresentersInit = false;
	
	//state
	private PresenterEntry savedEntry = null;
	
	class PresenterEntry {
		public IWCAGPresenter presenter = null;
		public boolean common = false;
		private String area = "main";  // header, main, footer TODO create ENUM

		PresenterEntry (IWCAGPresenter presenter, boolean isCommon) {
			this.presenter = presenter;
			this.common = isCommon;
		}

		public boolean isCommon() {
			return this.common;
		}
		
		public void setArea (String area) {
			this.area = area;
		}
		
		public String getArea () {
			return this.area;
		}
	}
	
	public KeyboardNavigationController() {
		this.waitOnMessages(this);
	}

	private void callEnterPressEvent () {
		DomEvent.fireNativeEvent(Document.get().createKeyDownEvent(false, false, true, false, KeyCodes.KEY_ENTER), RootPanel.get());
	}

	private native void waitOnMessages (KeyboardNavigationController x) /*-{
		$wnd.addEventListener("message", receiveMessage);
		function receiveMessage(event) {
			try {
				var eventData = JSON.parse(event.data);

				if (eventData.type !== "EXTERNAL_KEYDOWN_WATCHER") {
					return;
				}

				var keyCode = eventData.keyCode;
				var isShift = eventData.isShift;
				if (keyCode == 13 && isShift) {
					x.@com.lorepo.icplayer.client.page.KeyboardNavigationController::callEnterPressEvent()();
				}
			} catch (e) {
			}
		}
	}-*/;

	private void initialSelect() {
		if (this.getPresenters().size() == 0) {
			this.modeOn = false;
			return;
		}
		if (!this.getPresenters().get(this.actualSelectedModuleIndex).presenter.isSelectable(this.mainPageController.isTextToSpeechModuleEnable())) { //If first is not selectable
			this.setIndexToNextModule();
			if (this.actualSelectedModuleIndex == 0) { //And others modules too, then turn off navigation
				this.modeOn = false;
				return;
			}

		}
		selectCurrentModule();
		isInitiated = true;
	}
	
	// Sometimes modules can remove classes just activated or selected modules. We must restore them.
	private void restoreClasses() {
		if (!modeOn) {
			return;
		}
		this.selectCurrentModule();
	}
	
	private boolean isModuleButton() {
		if (this.getPresenters().get(this.actualSelectedModuleIndex).presenter instanceof IButton) {
			return true;
		}
		
		if (this.getPresenters().get(this.actualSelectedModuleIndex).presenter instanceof AddonPresenter) {	//Addon can be button or not, so AddonPresenter contains list of buttons
			AddonPresenter presenter = (AddonPresenter) this.getPresenters().get(this.actualSelectedModuleIndex).presenter;
			return presenter.isButton();
		}
		
		return false;
	}
	
	private void setWCAGModulesStatus (boolean isOn) {
		for (PresenterEntry p:  this.presenters) {
			IPresenter ip = (IPresenter) p.presenter;
			
			// TODO Add to interface
			if (ip.getModel().getModuleName() == "Text") {
				TextView tv = (TextView) p.presenter.getWCAGController();
				tv.setIsWCAGSOn(isOn);
			}
		}
	}

	private void changeKeyboardMode (KeyDownEvent event, boolean isWCAGSupportOn) {
		if (isWCAGSupportOn && !this.mainPageController.isTextToSpeechModuleEnable()) {
			return;
		}
		
		this.modeOn = !this.modeOn;
		this.isWCAGSupportOn = isWCAGSupportOn;
		
		if (this.mainPageController != null) {
			this.mainPageController.setTextReading(this.modeOn && this.isWCAGSupportOn);
		}
		
		if (this.modeOn) {
			this.setFocusOnInvisibleElement();
			if (this.isInitiated) {
				this.selectCurrentModule();
			} else {
				this.initialSelect();
			}
			
			this.mainPageController.readStartText();
		} else {
			this.manageKey(event);
			this.deselectCurrentModule();
			this.deselectAllModules();
			
			this.mainPageController.readExitText();
		}
		this.setWCAGModulesStatus(this.modeOn && this.isWCAGSupportOn);
		
		this.actualSelectedModuleIndex = 0;
	}

	private void changeCurrentModule(KeyDownEvent event) {
		if (!this.modeOn) {
			return;
		}
		
		this.deselectCurrentModule();
		if (event.isShiftKeyDown()) {
			this.setIndexToPreviousModule();
		} else {
			this.setIndexToNextModule();
		}
		this.selectCurrentModule();
		this.readTitle();
	}
	
	private void readTitle () {
		final PresenterEntry presenterEntry = this.getPresenters().get(this.actualSelectedModuleIndex);
		final String area = presenterEntry.getArea();
		final IPresenter iPresenter = (IPresenter) presenterEntry.presenter;
		final String id = iPresenter.getModel().getId();
		
		this.mainPageController.playTitle(area, id);
	}

	private int getNextElementIndex (int step) {
		int index = this.actualSelectedModuleIndex;
		do {
			final int presentersSize = this.getPresenters().size();
			index += step;

			index = index % presentersSize;
			if (index < 0) {
				index = presentersSize - 1;
			}

			if (index == this.actualSelectedModuleIndex) break; // if all modules are hidden then break loop
		} while (!this.getPresenters().get(index).presenter.isSelectable(this.mainPageController.isTextToSpeechModuleEnable()));

		return index;
	}

	private void setIndexToNextModule() {
		this.actualSelectedModuleIndex = this.getNextElementIndex(1);
	}

	private void setIndexToPreviousModule () {
		this.actualSelectedModuleIndex = this.getNextElementIndex(-1);
	}

	public void run (PlayerEntryPoint entry) {
		entryPoint = entry;
				
		RootPanel.get().addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.isShiftKeyDown()) {
					event.preventDefault();
					changeKeyboardMode(event, false);
					return;
				}
				
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.isControlKeyDown()) {
					event.preventDefault();
					changeKeyboardMode(event, true);
					return;
				}

				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB && modeOn) { // Disable tab default action if eKeyboard is working
					event.preventDefault();
				}

				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB && (!moduleIsActivated || isModuleButton())) {
					if (moduleIsActivated) { // If we was in button, and he was clicked then we want to disactivate that button
						deactivateModule();
						moduleIsActivated = false;
					}
					changeCurrentModule(event);
					return;
				}

				if (modeOn && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					event.preventDefault();
					activateModule();
				}

				if (modeOn && moduleIsActivated) {
					manageKey(event);
				}
				
				if (modeOn && event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					event.preventDefault();
					deactivateModule();
				}
				
				restoreClasses();
			}
		}, KeyDownEvent.getType());
	}
	
	private void setFocusOnInvisibleElement () {
		this.focusElement(this.getInputElement());
	} 
		
	private native JavaScriptObject	getInputElement() /*-{
		var input = $wnd.$("#input_element_for_focus_to_change_focused_element_by_browser").get(0);
		if (!input) {
			input = $wnd.$("<input/>");
			input.attr("id", "input_element_for_focus_to_change_focused_element_by_browser");
			input.css({
				"opacity": 0.0001,
				"pointer-events": "none",
				"position": "absolute",
				"top": "0px"
			});
			var body = $wnd.$("body");
			body.append(input);
		}
		
		return input;
	}-*/;
	
	private native void focusElement (JavaScriptObject element) /*-{
		element.focus();
	}-*/;

	private void manageKey (KeyDownEvent event) {
		IWCAG wcagWidget = this.getPresenters().get(this.actualSelectedModuleIndex).presenter.getWCAGController();
		if (wcagWidget == null) {
			return;
		}

		switch (event.getNativeEvent().getKeyCode()) {
			case KeyCodes.KEY_UP:
				wcagWidget.up();
				break;
			case KeyCodes.KEY_DOWN:
				wcagWidget.down();
				break;
			case KeyCodes.KEY_LEFT:
				wcagWidget.left();
				break;
			case KeyCodes.KEY_RIGHT:
				wcagWidget.right();
				break;
			case KeyCodes.KEY_ESCAPE:
				wcagWidget.escape();
				break;
			case KeyCodes.KEY_ENTER:
				wcagWidget.enter(event.isShiftKeyDown() || event.isControlKeyDown());
				break;
			case KeyCodes.KEY_TAB:
				if (event.isShiftKeyDown()) {
					wcagWidget.shiftTab();
				} else {
					wcagWidget.tab();
				}
				break;
			case 32:
				wcagWidget.space();
				break;
			default:
				wcagWidget.customKeyCode(event);
				break;
		};
	}

	private void activateModule () {
		if (!this.modeOn) {
			return;
		}
		
		this.getPresenters().get(this.actualSelectedModuleIndex).presenter.selectAsActive("ic_active_module");
		
		IWCAGPresenter p = this.getPresenters().get(this.actualSelectedModuleIndex).presenter;
		playTextToSpeechContent(p);
		
		this.moduleIsActivated = true;
	}
	
	private void deactivateModule () {
		this.getPresenters().get(this.actualSelectedModuleIndex).presenter.deselectAsActive("ic_active_module");
		this.moduleIsActivated = false;
	}
	
	private void selectCurrentModule() {
		if (this.getPresenters().size() == 0) {
			return;
		}
		
		this.getPresenters().get(this.actualSelectedModuleIndex).presenter.selectAsActive("ic_selected_module");
	}

	private void deselectCurrentModule () {
		if (this.getPresenters().size() == 0) {
			return;
		}

		this.getPresenters().get(this.actualSelectedModuleIndex).presenter.deselectAsActive("ic_selected_module");
	}
	
	private void deselectAllModules () {
		if (this.getPresenters().size() == 0) {
			return;
		}
		this.moduleIsActivated = false;
		
		for (PresenterEntry ip: this.getPresenters()) {
			IWCAGPresenter presenter = ip.presenter;
			presenter.deselectAsActive("ic_selected_module");
			presenter.deselectAsActive("ic_active_module");
		}
	}
	
	public boolean isModuleActivated () {
		return moduleIsActivated;
	}
	
	// TODO poprawic dzialanie dla 2 nawigacji
	public void reset () {
		if (this.presenters.size() == 0 && this.presentersOriginalOrder.size() == 0) {
			return;
		}
		
		final boolean isCommonModuleActivated1 = this.presenters.size() > 0 ? this.presenters.get(this.actualSelectedModuleIndex).isCommon() && isModuleActivated() : false;
		final boolean isCommonModuleActivated2 = this.presentersOriginalOrder.size() > 0 ? this.presentersOriginalOrder.get(this.actualSelectedModuleIndex).isCommon() && isModuleActivated() : false;

		if (!isCommonModuleActivated1 || !isCommonModuleActivated2) {
			this.moduleIsActivated = false;
			this.isInitiated = false;
			this.actualSelectedModuleIndex = 0;
		}
		
		this.isPresentersInit = false;
		this.presenters.clear();
		this.presentersOriginalOrder.clear();
	}
	
	public void save () {
		if (this.getPresenters().size() == 0) {
			return;
		}

		if(!this.modeOn) {
			return;
		}

		this.savedEntry = this.getPresenters().get(this.actualSelectedModuleIndex);
	}

	public void restore () {
		if (this.savedEntry == null) {
			return;
		}

		for (int i = 0; i < this.getPresenters().size(); i++) {
			IPresenter presenter = (IPresenter) this.getPresenters().get(i).presenter;
			IPresenter savedPresenter = (IPresenter) this.savedEntry.presenter;

			if (presenter.getModel() == savedPresenter.getModel()) {
				this.actualSelectedModuleIndex = i;
				this.initialSelect();
				this.activateModule();
				return;
			}
		}
		
		this.actualSelectedModuleIndex = 0;
		this.initialSelect();
	}
	
	private PresenterEntry getPresenterById (List<PresenterEntry> mainPagePresenters, String id) {
		for (PresenterEntry presenter: mainPagePresenters) {
			IPresenter iPresenter = (IPresenter) presenter.presenter;
			if (iPresenter.getModel().getId().equals(id)) {
				return presenter;
			}
		}
		
		return null;
	}
	
	private List<PresenterEntry> generatePresenters (PageController controller, boolean isCommonPage) {
		List<PresenterEntry> result = new ArrayList<PresenterEntry>();
		
		if (controller != null) {
			for (IPresenter presenter : controller.getPresenters()) {
				if (presenter instanceof IWCAGPresenter) {
					result.add(new PresenterEntry((IWCAGPresenter) presenter, isCommonPage));
				}
			}
		}
		
		return result;
	}
	
	private List<PresenterEntry> sortTextToSpeechModules (PageController main, PageController header, PageController footer) {
		List<PresenterEntry> mainPresenters = this.generatePresenters(main, false);
		List<PresenterEntry> headerPresenters = this.generatePresenters(header, true);
		List<PresenterEntry> footerPresenters = this.generatePresenters(footer, true);
		
		List<PresenterEntry> result = new ArrayList<PresenterEntry>();
		List<PresenterEntry> currentPresenter = new ArrayList<PresenterEntry>();
		
		List<NavigationModuleIndentifier> TTSModules = new ArrayList<NavigationModuleIndentifier>();
		if (main != null) {
			TTSModules = main.getModulesOrder();
		}
		
		for (NavigationModuleIndentifier module: TTSModules) {
			if (module.getArea().equals("main")) {
				currentPresenter = mainPresenters;
			}
			
			if (module.getArea().equals("header")) {
				currentPresenter = headerPresenters;
			}
			
			if (module.getArea().equals("footer")) {
				currentPresenter = footerPresenters;
			}
			
			PresenterEntry localPresenter = getPresenterById(currentPresenter, module.getId());
			if (localPresenter != null) {
				localPresenter.setArea(module.getArea());
				result.add(localPresenter);
			}
		}
		
		return result;
	}

	private void addToNavigation (PageController controller, boolean isCommon) {
		if (controller == null || controller.getWidgets() == null) {
			return;
		}
		
		this.presentersOriginalOrder.addAll(this.generatePresenters(controller, isCommon));
	}
	
	private void playTextToSpeechContent (IWCAGPresenter iWCAGPresenter) {
		IPresenter ip = (IPresenter) iWCAGPresenter;
		
		if (ip.getModel().getModuleName() == "Choice") {
			ChoiceView cv = (ChoiceView) iWCAGPresenter.getWCAGController();
			cv.setTextToSpeechVoices(mainPageController.getMultiPartDescription(ip.getModel().getId()));
			cv.setPageController(mainPageController);
		} else if (ip.getModel().getModuleName() == "Text") {
			TextView tv = (TextView) iWCAGPresenter.getWCAGController();
			tv.setIsWCAGSOn(true);
			tv.setPageController(mainPageController);
		} else {
			mainPageController.playDescription(ip.getModel().getId());
		}

	}
	
	public void addHeaderToNavigation (PageController controller) {
		this.headerController = controller;
		addToNavigation(controller, true);
	}
	
	public void addFooterToNavigation (PageController controller) {
		this.footerController = controller;
		addToNavigation(controller, true);
	}
	
	public void addMainToNavigation (PageController controller) {
		this.mainPageController = controller;
		addToNavigation(controller, false);
	}
	
	public void addSecondToNavigation (PageController controller) {
		addToNavigation(controller, false);
	}
	
	private List<PresenterEntry> getPresenters () {
		if (!this.isPresentersInit && this.isWCAGSupportOn) {
			this.presenters = this.sortTextToSpeechModules(this.mainPageController, this.headerController, this.footerController);
			
			this.isPresentersInit = this.presenters.size() > 0;
		}
		
		return this.isWCAGSupportOn ? this.presenters : this.presentersOriginalOrder;
	}
	
}
