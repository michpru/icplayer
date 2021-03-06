package com.lorepo.icplayer.client.module.text;

import com.google.gwt.dom.client.Element;
import com.lorepo.icplayer.client.module.text.LinkInfo.LinkType;

public interface ITextViewListener {

	public void onLinkClicked(LinkType type, String link, String target);
	public void onValueEdited(String id, String newValue);
	public void onValueChanged(String id, String newValue);
	public void onGapClicked(String controlId);
	public void onGapFocused(String controlId, Element element);
	public void onGapBlured(String gapId, Element element);
	public void onDropdownClicked(String id);
	public void onGapDragged(String gapId);
	public void onGapStopped(String gapId);
	public void onGapDropped(String id);
	public void onKeyAction(String gapId, Element element);
	public void onUserAction(String id, String newValue);
}
