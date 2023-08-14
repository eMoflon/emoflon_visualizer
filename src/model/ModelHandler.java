package model;

import java.util.Map.Entry;

import com.google.common.collect.HashBiMap;

import javafx.scene.web.WebEngine;

public abstract class ModelHandler {

	public ModelHandler() {
		
	}
	public abstract void buildVis();
	
	public abstract void createNetworkToggle(WebEngine engine);
	
	public abstract void createNetwork(WebEngine engine);

}
