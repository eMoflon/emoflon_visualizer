package model;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.HashBiMap;

import javafx.scene.control.ChoiceBox;
import javafx.scene.web.WebEngine;

public abstract class ModelHandler {

	public ModelHandler() {

	}

	public abstract void buildVis();

	public abstract void createNetwork(WebEngine engine);

	public abstract List<String> computeitems();

	public abstract List<Integer> getChoiceIds(String filterWord);
	
	public abstract List<Integer> getTextFieldIds(String filterWord);
	
	public abstract List<Integer> getNonHighlightIds(List<Integer> highlightIds);
	
	public abstract int getNodeId();
	
	

}
