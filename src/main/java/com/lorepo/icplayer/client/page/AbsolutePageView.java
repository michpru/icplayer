package com.lorepo.icplayer.client.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lorepo.icplayer.client.model.Page;
import com.lorepo.icplayer.client.module.api.ILayoutDefinition;
import com.lorepo.icplayer.client.module.api.ILayoutDefinition.Property;
import com.lorepo.icplayer.client.module.api.IModuleModel;
import com.lorepo.icplayer.client.module.api.IModuleView;
import com.lorepo.icplayer.client.page.PageController.IPageDisplay;
import com.lorepo.icplayer.client.utils.DOMUtils;
import com.lorepo.icplayer.client.utils.MathJax;


/**
 * This is X,Y laytout
 * 
 * @author Krzysztof Langner
 *
 */
public class AbsolutePageView extends AbsolutePanel implements IPageDisplay{

	private Page currentPage;
	private HashMap<String, Widget> widgets = new HashMap<String, Widget>();
	private int focusedModule = 0;
	String currentModuleName = "";
	boolean moduleIsActivated = false;
	private HashMap<String, Widget> navigationWidgets = new HashMap<String, Widget>();
	List<String> modulesNames = new ArrayList<String>();
	public enum ExpectedModules {
		text, video;
		
		  public static boolean contains(String s) {
		      for(ExpectedModules choice:values()) {
		           if (choice.name().equals(s)) {
		        	   return true; 
		           }
		      }
		              
		      return false;
		  }
		  
		  public static List<String> getModulesTypes() {
			  ExpectedModules[] states = values();
			    List<String> names = new ArrayList<String>();

			    for (int i = 0; i < states.length; i++) {
			        names.add(states[i].name());
			    }

			    return names;
			}
	}
	
	public AbsolutePageView(){

		addStyleName("ic_page");
	}
	

	@Override
	public void setPage(Page newPage) {
	
		currentPage = newPage;
		String styles = "position:relative;overflow:hidden;";
		if(currentPage.getInlineStyle() != null){
			styles += currentPage.getInlineStyle(); 
			
		}
		DOMUtils.applyInlineStyle(getElement(), styles);
		if(!currentPage.getStyleClass().isEmpty()){
			addStyleName(currentPage.getStyleClass());
		}
		getElement().setId(currentPage.getId());
		removeAllModules();
	}


	@Override
	public void refreshMathJax() {
		MathJax.refreshMathJax(getElement());
	}
	
	@Override
	public void addModuleView(IModuleView view, IModuleModel module){

		int left, right, width, top, bottom, height;
		
		if(view instanceof Widget){
			Widget moduleView = (Widget) view;
			ILayoutDefinition layout = module.getLayout();
			
			if(layout.hasLeft()){
				left = calculatePosition(layout.getLeftRelativeTo(), 
						layout.getLeftRelativeToProperty(), module.getLeft());
				if(layout.hasRight()){
					right = calculatePosition(layout.getRightRelativeTo(), 
							layout.getRightRelativeToProperty(), -module.getRight());
					width = right-left;
				}
				else{
					width = module.getWidth();
				}
			}
			else{
				right = calculatePosition(layout.getRightRelativeTo(), 
						layout.getRightRelativeToProperty(), -module.getRight());
				width = module.getWidth();
				left = right-width;
			}
			
			if(layout.hasTop()){
				top = calculatePosition(layout.getTopRelativeTo(), 
						layout.getTopRelativeToProperty(), module.getTop());
				if(layout.hasBottom()){
					bottom = calculatePosition(layout.getBottomRelativeTo(), 
							layout.getBottomRelativeToProperty(), -module.getBottom());
					height = bottom-top;
				}
				else{
					height = module.getHeight();
				}
			}
			else{
				bottom = calculatePosition(layout.getBottomRelativeTo(), 
						layout.getBottomRelativeToProperty(), -module.getBottom());
				height = module.getHeight();
				top = bottom-height;
			}
			
			moduleView.setPixelSize(width, height);
		    add(moduleView, left, top);
		    widgets.put(module.getId(), moduleView);
		    addToNavigation(module, moduleView);
		}
	}

	private int calculatePosition(String widgetName, Property property, int modulePos) {
		int pageWidth = DOM.getElementPropertyInt(getElement(), "clientWidth");
		int pageHeight = DOM.getElementPropertyInt(getElement(), "clientHeight");
		int pos = 0;
		Widget widget = widgets.get(widgetName);
		
		if(property == Property.left){
			if(widget != null){
				pos = widget.getAbsoluteLeft()-getAbsoluteLeft()+modulePos;
			}
			else{
				pos = modulePos;
			}
		}
		else if(property == Property.right){
			if(widget != null){
				pos = widget.getAbsoluteLeft()+widget.getOffsetWidth()-getAbsoluteLeft()+modulePos;
			}
			else{
				pos = pageWidth+modulePos;
			}
		}
		else if(property == Property.top){
			if(widget != null){
				pos = widget.getAbsoluteTop()-getAbsoluteTop()+modulePos;
			}
			else{
				pos = modulePos;
			}
		}
		else if(property == Property.bottom){
			if(widget != null){
				pos = widget.getAbsoluteTop()+widget.getOffsetHeight()-getAbsoluteTop()+modulePos;
			}
			else{
				pos = pageHeight+modulePos;
			}
		}
		
		return pos;
	}


	@Override
	public void setWidth(int width) {
		setWidth(width+"px");
	}


	@Override
	public void setHeight(int height) {
		setHeight(height+"px");
	}

	@Override
	public void removeAllModules() {
		widgets.clear();
		clear();
	}
	
	private void selectModule(String moduleName) {
		Widget widget = navigationWidgets.get(moduleName);
		
		widget.getElement().addClassName("ic_selected_module");
	}
	
	private void activateModule(String moduleName) {
		if (moduleName.isEmpty()) return;
		
		Widget widget = navigationWidgets.get(moduleName);
		widget.getElement().addClassName("ic_active_module");
		
		this.moduleIsActivated = true;
		
		setModuleStatus(moduleName, true, true);
	}
	
	private void deselectModule(String currentModuleName) {
		if (currentModuleName.isEmpty()) return;
		
		Widget currentWidget = navigationWidgets.get(currentModuleName);
		currentWidget.getElement().removeClassName("ic_selected_module");
		currentWidget.getElement().removeClassName("ic_active_module");
	}
	
	private void deactivateModule(String currentModuleName) {
		if (currentModuleName.isEmpty()) return;
		
		Widget currentWidget = navigationWidgets.get(currentModuleName);
		currentWidget.getElement().removeClassName("ic_active_module");
		
		this.moduleIsActivated = false;
		
		setModuleStatus(currentModuleName, true, false);
	}
	

	public void selectNextModule() {
		int size = focusedModule % navigationWidgets.size();
		String moduleName = modulesNames.get(size);

		selectModule(moduleName);
		deselectModule(currentModuleName);
		
		currentModuleName = moduleName;
		focusedModule++;
		
		setModuleStatus(moduleName, true, false);
	}
	
	private static native void setModuleStatus(String name, boolean selected, boolean activated) /*-{
		$wnd.moduleStatus = {
			name: name,
			selected: selected,
			activated: activated
		}
	}-*/;
	
	
	private void addToNavigation(IModuleModel module, Widget moduleView) {
		if (ExpectedModules.contains(module.getModuleTypeName().toLowerCase())) {
			navigationWidgets.put(module.getId(), moduleView);
			modulesNames.add(module.getId());
		}
	}
	
	@Override
	public void runKeyboardNavigation(final EventBus eventBus) {
		setModuleStatus("", false, false); //initialize moduleStatus on Page loaded
		
		RootPanel.get().addDomHandler(new KeyDownHandler() {

	        @Override
	        public void onKeyDown(KeyDownEvent event) {
	            if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
	            	if (!moduleIsActivated) {
		            	event.preventDefault();
	            		selectNextModule();
	            	}
	            }
	            
	            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
	            	event.preventDefault();
	            	activateModule(currentModuleName);
	            }
	            
	            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
	            	event.preventDefault();
	            	deactivateModule(currentModuleName);
	            }
	        }
	    }, KeyDownEvent.getType());
	}
}
