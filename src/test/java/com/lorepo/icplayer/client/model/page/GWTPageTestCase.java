package com.lorepo.icplayer.client.model.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.xml.sax.SAXException;

import com.google.gwt.i18n.client.Dictionary;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;
import com.lorepo.icf.utils.i18n.DictionaryWrapper;
import com.lorepo.icplayer.client.mockup.xml.PageFactoryMockup;
import com.lorepo.icplayer.client.module.api.IModuleModel;
import com.lorepo.icplayer.client.module.shape.ShapeModule;
import com.lorepo.icplayer.client.utils.DomElementManipulator;

@GwtModule("com.lorepo.icplayer.Icplayer")
public class GWTPageTestCase extends GwtTest {
	
	private Page loadFromString(Page page, String xml) {
		return new PageFactoryMockup(page).loadFromString(xml);
	}
	
	private Page loadFromFile(Page page, String path) throws SAXException, IOException {
		InputStream xmlStream = getClass().getResourceAsStream(path);
		Scanner s = new Scanner(xmlStream).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
			
		return new PageFactoryMockup(page).loadFromString(result);
	}
	
	@Before
	public void setUp() {
		Dictionary dictMock = Mockito.mock(Dictionary.class);
		when(dictMock.get("width")).thenReturn("width");
		when(dictMock.get("height")).thenReturn("height");
		
		Set<String> dictValues = new HashSet<String>();
		dictValues.add("width");
		dictValues.add("height");
		when(dictMock.keySet()).thenReturn(dictValues);
		
		Whitebox.setInternalState(DictionaryWrapper.class, "dictionary", dictMock);
		Whitebox.setInternalState(DictionaryWrapper.class, "cachedKeySet", dictMock.keySet());
	}
	
	@Test
	public void saveScoringType() throws SAXException, IOException {
		
		Page page = this.loadFromFile(new Page("Page 2", ""), "testdata/page4.xml");
		
		String pageXML = page.toXML();
		Page resultPage = this.loadFromString(new Page("Page 2", ""), pageXML);
		
		assertTrue(resultPage.getScoringType() == Page.ScoringType.zeroOne);
	}
	
	/**
	 * Sprawdzenie, ze podczas zapisu jest poprawny XML
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test
	public void toXMLWithStyle() throws ParserConfigurationException, SAXException, IOException {
		
		Page page = new Page("ala", "kot");
		IModuleModel module = new ShapeModule();
		
		module.setLeft(1);
		module.setTop(2);
		module.setWidth(3);
		module.setHeight(4);
		module.setInlineStyle("background-image: url('/file/123');");
		page.getModules().add(module);
		String xml = page.toXML();
		
		new PageFactoryMockup(new Page("Ala", "")).loadFromString(xml);
	}
	
	@Test
	public void toXMLNotNull() {
		Page page = new Page("page1", "");
		String xml = page.toXML();
		
		assertNotNull(xml);
	}
	
	@Test
	public void toXMLSingleModule() {
		Page page = new Page("ala", "kot");
		IModuleModel module = new ShapeModule();
		
		module.setLeft(1);
		module.setTop(2);
		module.setWidth(3);
		module.setHeight(4);
		page.getModules().add(module);
		String xml = page.toXML();
		
		int index = xml.indexOf("<shapeModule ");
		
		assertTrue(index > -1);
	}
	
	/**
	 * Sprawdzenie, ze podczas zapisu jest poprawny XML
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test
	public void toXML() throws ParserConfigurationException, SAXException, IOException {
		Page page = new Page("ala", "kot");
		IModuleModel module = new ShapeModule();
		
		module.setLeft(1);
		module.setTop(2);
		module.setWidth(3);
		module.setHeight(4);
		page.getModules().add(module);
		String xml = page.toXML();
		
		this.loadFromString(new Page("ala", ""), xml);
	}
	
	@Test
	public void ChangeSizeAndSaveLoad() throws Exception {
		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);

		Page page = new Page("Sizes", "");
		
		page.setWidth(300);
		page.setHeight(400);
		
		String pageXML = page.toXML();

		Page resultPage = this.loadFromString(new Page("id", "path"), pageXML);
		
		assertEquals(300, resultPage.getWidth());
		assertEquals(400, resultPage.getHeight());
	}
	
	@Test
	public void saveLoadCssClass() throws Exception {
		Page page = new Page("Class test page", "");
		page.setStyleClass("DemoClass");
		
		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);

		Page resultPage = this.loadFromString(new Page("id", "path"), page.toXML());
		
		assertEquals("DemoClass", resultPage.getStyleClass());
	}

	@Test
	public void isReportableTrue() throws Exception {
		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);

		Page page = this.loadFromFile(new Page("Page 1", ""), "testdata/page.xml");
		
		Page resultPage = this.loadFromString(new Page("Page 2", ""), page.toXML());
		
		assertTrue(resultPage.isReportable());
	}
}
