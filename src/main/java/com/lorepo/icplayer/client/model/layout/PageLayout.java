package com.lorepo.icplayer.client.model.layout;

import com.lorepo.icf.utils.UUID;

public class PageLayout implements PageLayoutBuilder{

	public enum PageLayoutTypes {
		small,
		medium,
		large
	};
	
	
	public static int MAX_TRESHOLD = 100000;
	private String name;
	private String id;
	private int treshold;
	private PageLayoutTypes type;
	private String styleID;
	
	public PageLayout(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static PageLayout createDefaultPageLayout() {
		PageLayout defaultPageLayout = new PageLayout("default", "default");
		defaultPageLayout.setTreshold(PageLayout.MAX_TRESHOLD);
		defaultPageLayout.setType(PageLayoutTypes.small);
		defaultPageLayout.setCssID("default");
		
		return defaultPageLayout;
	}
	
	public static PageLayout createPageLayout(String name, int treshold, PageLayoutTypes type) {
		PageLayout newPageLayout = new PageLayout(UUID.uuid(), name);
		newPageLayout.setType(type);
		newPageLayout.setCssID("default");
		newPageLayout.setTreshold(treshold);
		
		return newPageLayout;
	}
	
	public String getStyleID () {
		return this.styleID;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setTreshold(int value) {
		this.treshold = value;
	}	

	@Override
	public void setType(PageLayoutTypes type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}
	
	public String getID() {
		return this.id;
	}

	@Override
	public void setCssID(String styleID) {
		this.styleID = styleID;
	}
	
	public boolean isThisCssStyle(String styleID) {
		return this.styleID.compareTo(styleID) == 0;
	}
}
