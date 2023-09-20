package model;

import java.util.List;
import javafx.scene.web.WebEngine;

public abstract class ModelHandler {

	/**
	 * constructor of the abstract ModelHandler class
	 */
	public ModelHandler() {

	}

	/**
	 * Builds visualisation from given identified selection
	 */
	public abstract void buildVis();

	/**
	 * Creates a javascript based Vis.js-network with given JavaFX WebEngine using the
	 * xtend-file VisJsSciptTemplates methods
	 * 
	 * @param engine - used to execute a script from the xtend-file VisJsScriptTemplates
	 */
	public abstract void createNetwork(WebEngine engine);

	/**
	 * Computes a list of EClass names to be put into JavaFX-controls ChoiceBox
	 * 
	 * @return list of EClass-names of the current selection
	 */
	public abstract List<String> computeitems();

	/**
	 * Returns a list of node ids which contain the provided filterWord
	 * 
	 * @param filterWord - string that acts as the filter
	 * @return list of filtered node ids
	 */
	public abstract List<Integer> getChoiceIds(String filterWord);

	/**
	 * Returns a list of node ids which contain the provided filterWord
	 * 
	 * @param filterWord - string that acts as the filter
	 * @return list of filtered node ids
	 */
	public abstract List<Integer> getTextFieldIds(String filterWord);

	/**
	 * Returns a list of node ids which contain both of the given
	 * firstFilterWord-string and secondFilterWord-string
	 * 
	 * @param firstFilterWord  - string that acts as the filter
	 * @param secondFilterWord - string that acts as the filter
	 * @return list of filtered node ids
	 */
	public abstract List<Integer> getTextFieldAndIds(String firstFilterWord, String secondFilterWord);

	/**
	 * Returns a list of node ids which are not contained in the provided list
	 * highlightIds
	 * 
	 * @param highlightIds - list that acts as the filter
	 * @return list of filtered node ids
	 */
	public abstract List<Integer> getNonHighlightIds(List<Integer> highlightIds);

	/**
	 * Returns the highest of id of all nodes of the model
	 * 
	 * @return highest node id
	 */
	public abstract int getNodeId();

}
