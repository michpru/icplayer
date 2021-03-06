package com.lorepo.icplayer.client.model.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.xml.client.Element;
import com.lorepo.icf.properties.IBooleanProperty;
import com.lorepo.icf.properties.IEnumSetProperty;
import com.lorepo.icf.properties.IImageProperty;
import com.lorepo.icf.properties.IProperty;
import com.lorepo.icf.properties.IPropertyListener;
import com.lorepo.icf.utils.i18n.DictionaryWrapper;
import com.lorepo.icplayer.client.framework.module.IStyleListener;
import com.lorepo.icplayer.client.mockup.xml.XMLParserMockup;
import com.lorepo.icplayer.client.model.page.Page;
import com.lorepo.icplayer.client.model.page.Page.LayoutType;
import com.lorepo.icplayer.client.module.api.IModuleModel;
import com.lorepo.icplayer.client.module.shape.ShapeModule;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v1;
import com.lorepo.icplayer.client.module.text.TextParser;
import com.lorepo.icplayer.client.utils.DomElementManipulator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DictionaryWrapper.class, Page.class, TextParser.class})
public class PageTestCase {

	private boolean eventReceived;
	
	private void loadPage(String xmlFile, Page page) {
		InputStream inputStream = getClass().getResourceAsStream(xmlFile);
		try {
			XMLParserMockup xmlParser = new XMLParserMockup();
			Element element = xmlParser.parser(inputStream);
			PageParser_v1 parser = new PageParser_v1();
			parser.setPage(page);
			page = (Page) parser.parse(element);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void loadFromXMLAddon() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/addon.page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("AddonPage", "");
		loadPage("testdata/addon.page.xml", page);
		
		IModuleModel moduleModel = page.getModules().get(0); 
		assertEquals("HelloWorldAddon", moduleModel.getModuleTypeName());
	}

	@Test
	public void getModuleById() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/addon.page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("AddonPage", "");
		loadPage("testdata/addon.page.xml", page);
		
		IModuleModel moduleModel = page.getModules().getModuleById("button1"); 
		assertNotNull(moduleModel);
	}

	@Test
	public void setNameSendEvent() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/addon.page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("AddonPage", "");
		loadPage("testdata/addon.page.xml",page);
		
		eventReceived = false;
		page.addPropertyListener(new IPropertyListener() {
			
			@Override
			public void onPropertyChanged(IProperty source) {
				eventReceived = true;
			}
		});

		page.setName("new name");
		
		assertTrue(eventReceived);
	}

	@Test
	public void styleEvent() throws SAXException, IOException {
		
		Page page = new Page("AddonPage", "");
		
		eventReceived = false;
		page.addStyleListener(new IStyleListener() {
			
			@Override
			public void onStyleChanged() {
				eventReceived = true;
			}
		});

		page.setInlineStyle("test");
		
		assertTrue(eventReceived);
	}


	/**
	 * Try to parse XML
	 * @param xml
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static void parseXML(String xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
		docBuilder = docBuilderFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
        docBuilder.parse(is);
	}
	
	@Test
	public void isLoaded() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("testdata/addon.page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("AddonPage", "");
		loadPage("testdata/addon.page.xml", page);
		
		assertTrue(page.isLoaded());
	}

	@Test
	public void getPageSize() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);

		Page page = new Page("Sizes", "");
		loadPage("testdata/page.xml", page);
		
		assertEquals(100, page.getWidth());
		assertEquals(200, page.getHeight());
	}

	@Test
	public void pageSizeProperties() throws Exception {
		PowerMockito.spy(DictionaryWrapper.class);
		when(DictionaryWrapper.get("width")).thenReturn("width");
		when(DictionaryWrapper.get("height")).thenReturn("height");
		
		InputStream inputStream = getClass().getResourceAsStream("testdata/page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("Sizes", "");
		loadPage("testdata/page.xml", page);

		int width = 0;
		int height = 0;
		
		for(int i = 0; i < page.getPropertyCount(); i++){
		
			IProperty property = page.getProperty(i);
			if(property.getName().compareToIgnoreCase("width") == 0){
				width = Integer.parseInt(property.getValue());
			}
			else if(property.getName().compareToIgnoreCase("height") == 0){
				height = Integer.parseInt(property.getValue());
			}
		}
		
		assertEquals(100, width);
		assertEquals(200, height);
	}

	@Test
	public void reportableProperty(){
		PowerMockito.spy(DictionaryWrapper.class);
		when(DictionaryWrapper.get("is_reportable")).thenReturn("Is&nbsp;reportable");

		Page page = new Page("Page 1", "");

		assertTrue(page.isReportable());

		for(int i = 0; i < page.getPropertyCount(); i++){
			IProperty property = page.getProperty(i);
			if(property instanceof IBooleanProperty && 
					property.getName().compareToIgnoreCase("Is&nbsp;reportable") == 0)
			{
				property.setValue("false");
			}
		}

		assertFalse(page.isReportable());
	}


	@Test
	public void previewProperty(){
		PowerMockito.spy(DictionaryWrapper.class);
		when(DictionaryWrapper.get("Preview")).thenReturn("Preview");

		Page page = new Page("Page 1", "");
		page.setPreview("/file/1");
		String foundValue = null;

		for(int i = 0; i < page.getPropertyCount(); i++){
			IProperty property = page.getProperty(i);
			if(property instanceof IImageProperty && 
					property.getName().compareToIgnoreCase("Preview") == 0)
			{
				foundValue = property.getValue();
				break;
			}
		}

		assertEquals("/file/1", foundValue);
	}
	

	@Test
	public void outstreachHeight1() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("Sizes", "");
		loadPage("testdata/page.xml", page);
		
		page.outstreachHeight(20, 8);
		IModuleModel module1 = page.getModules().get(0);
		IModuleModel module2 = page.getModules().get(1);
		
		assertEquals(10, module1.getTop());
		assertEquals(98, module2.getTop());
	}

	@Test
	public void outstreachHeight2() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("Sizes", "");
		loadPage("testdata/page.xml", page);
		
		page.outstreachHeight(20, -8);
		IModuleModel module1 = page.getModules().get(0);
		IModuleModel module2 = page.getModules().get(1);
		
		assertEquals(10, module1.getTop());
		assertEquals(82, module2.getTop());
	}

	@Test
	public void uniqueName() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("Sizes", "");
		loadPage("testdata/page.xml", page);
		
		String name = page.createUniquemoduleId("Test");
		assertEquals("Test1", name);
	}

	@Test
	public void getPixelLayout() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("Sizes", "");
		loadPage("testdata/page.xml", page);
		
		assertTrue(page.getLayout() == LayoutType.pixels);
	}

	@Test
	public void getResponsiveLayout() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/page2.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("Sizes", "");
		loadPage("testdata/page2.xml", page);
		
		assertTrue(page.getLayout() == LayoutType.responsive);
	}
	

	@Test
	public void defaultScoringType() throws Exception {

		InputStream inputStream = getClass().getResourceAsStream("testdata/page.xml");
		XMLParserMockup xmlParser = new XMLParserMockup();
		Element element = xmlParser.parser(inputStream);

		DomElementManipulator manipulator = Mockito.mock(DomElementManipulator.class);
		PowerMockito.whenNew(DomElementManipulator.class).withArguments(Mockito.any(String.class)).thenReturn(manipulator);
		
		Page page = new Page("Page 1", "");
		loadPage("testdata/page.xml", page);
		
		assertTrue(page.getScoringType() == Page.ScoringType.percentage);
	}
	

	@Test
	public void scoreTypeProperty(){
		PowerMockito.spy(DictionaryWrapper.class);
		when(DictionaryWrapper.get("score_type")).thenReturn("Score Type");

		Page page = new Page("Page 1", "");
		page.setPreview("/file/1");
		String foundValue = null;

		for(int i = 0; i < page.getPropertyCount(); i++){
			IProperty property = page.getProperty(i);
			if( property instanceof IEnumSetProperty && 
				property.getName().equalsIgnoreCase("Score Type"))
			{
				foundValue = property.getValue();
				break;
			}
		}

		assertEquals("percentage", foundValue);
	}
}
