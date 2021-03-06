package com.lorepo.icplayer.client.xml.module;

import java.util.HashMap;

import com.google.gwt.xml.client.Element;
import com.lorepo.icplayer.client.xml.module.parsers.IModuleModelBuilder;
import com.lorepo.icplayer.client.xml.module.parsers.IModuleModelParser;
import com.lorepo.icplayer.client.xml.module.parsers.ModuleParser_v0;
import com.lorepo.icplayer.client.xml.module.parsers.ModuleParser_v1;
import com.lorepo.icplayer.client.xml.module.parsers.ModuleParser_v2;
import com.lorepo.icplayer.client.xml.module.parsers.ModuleParser_v3;

public class ModuleXMLParsersFactory {

	private IModuleModelBuilder module;
	private HashMap<String, IModuleModelParser> parsersMap = new HashMap<String, IModuleModelParser>();
	
	public ModuleXMLParsersFactory(IModuleModelBuilder module) {
		this.module = module;
		this.addParser(new ModuleParser_v0());
		this.addParser(new ModuleParser_v1());
		this.addParser(new ModuleParser_v2());
		this.addParser(new ModuleParser_v3());
	}
	
	public void addParser(IModuleModelParser parser) {
		parser.setModule(this.module);
		this.parsersMap.put(parser.getVersion(), parser);
	}
	
	public void produce(Element node, String version) {
		this.parsersMap.get(version).parse(node);
	}
}
