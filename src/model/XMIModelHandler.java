package model;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEList;

import com.google.common.collect.HashBiMap;

import controller.TextFieldPatternMatcher;
import javafx.scene.web.WebEngine;

/**
 * Creates and handles all models that are of the type "XMI-model"
 * 
 * @author maximiliansell
 *
 */
public class XMIModelHandler extends ModelHandler {

	private Collection<EObject> elist;
	private int nodeId;
	private int edgeId;

	private HashBiMap<EObject, Entry<Integer, String>> allNodes = HashBiMap.create();
	private HashBiMap<Entry<Integer, String>, Entry<String, String>> allEdges = HashBiMap.create();

	/**
	 * Creates an instance of the Class MetaModelHandler
	 * 
	 * @param elist - list of EObjects
	 */
	public XMIModelHandler(Collection<EObject> elist) {
		this.elist = elist;
		this.nodeId = 0;
		this.edgeId = 0;
	}

	/**
	 * Builds and fills the required HashBiMaps allNodes and allEdges with for
	 * Vis.js (visualisation) needed parameters
	 */
	public void buildVis() {
		extractNodes();
		extractEdges();
	}

	/**
	 * Extracts all important parameters for the nodes(vis.js) of the visualisation
	 * from the elist (selection) -> e.g. type of EClass, name of EClass ,
	 * attributes etc. and puts them into the HashBiMap allNodes. The contents of
	 * allNodes can be later used by the Class VisJsScriptTemplates to add nodes to
	 * the visualisation
	 */
	private void extractNodes() {
		elist.forEach(current -> {
			switch (current.eClass().eClass().getName()) {
			case "EClass":
				allNodes.put(current,
						new AbstractMap.SimpleEntry<Integer, String>(nodeId++, extractAttributes(current)));
				break;
			case "EEnum":
				String literalStr = "";
				EList<EEnumLiteral> literalList = ((EEnum) current).getELiterals();
				for (EEnumLiteral item : literalList)
					literalStr += "\\n" + item.getName();
				allNodes.put(current, new AbstractMap.SimpleEntry<Integer, String>(nodeId++, literalStr));
				break;
			}

		});
	}

	/**
	 * Extracts all important parameters for the edges(vis.js) of the visualisation
	 * from the elist (selection) -> e.g. type of edge, name of edge ,
	 * multiplicities etc. and puts them into the HashBiMap allEdges The contents of
	 * allNodes can be later used by the Class VisJsScriptTemplates to add edges to
	 * the visualisation
	 */
	public void extractEdges() {
		allNodes.forEach((key, value) -> {
			key.eClass().getEAllReferences().forEach(ref -> {
				if (!(key.eGet(ref) == null)) {
					if (key.eGet(ref).getClass().equals(EcoreEList.Dynamic.class)) {
						((EcoreEList.Dynamic<EObject>) (key.eGet(ref))).forEach(obj -> {
							if (allNodes.containsKey(obj)) {
								allEdges.putIfAbsent(
										new AbstractMap.SimpleEntry<Integer, String>(edgeId++, ref.getName()),
										new AbstractMap.SimpleEntry<String, String>(allNodes.get(key).getKey() + "",
												allNodes.get(obj).getKey() + ""));
							}
						});
					}
					if (key.eGet(ref).getClass().equals(DynamicEObjectImpl.class)) {
						if (allNodes.containsKey(key.eGet(ref))) {
							allEdges.putIfAbsent(new AbstractMap.SimpleEntry<Integer, String>(edgeId++, ref.getName()),
									new AbstractMap.SimpleEntry<String, String>(allNodes.get(key).getKey() + "",
											allNodes.get(key.eGet(ref)).getKey() + ""));
						}
					}
				}
			});
		});
	}

	/**
	 * Extracts and concats all attributes from the given EObject into a String for
	 * the map allNodes
	 * 
	 * @param eObj - EObject with potential attributes
	 * @return string of concated attributes
	 */
	private String extractAttributes(EObject current) {
		ArrayList<String> helpList = new ArrayList<String>();
		current.eClass().getEAllAttributes().forEach(attr -> {
			helpList.add("\\n" + attr.getName() + " = " + current.eGet(attr));
		});
		String attrStr = "";
		for (String str : helpList)
			attrStr = attrStr + str;
		return attrStr;
	}

	/**
	 * Creates a javascript based Vis.js-network with given JavaFX WebEngine using
	 * the xtend-file VisJsSciptTemplates methods
	 * 
	 * @param engine - used to execute a script from the xtend-file
	 *               VisJsScriptTemplates
	 */
	@Override
	public void createNetwork(WebEngine engine) {
		allNodes.forEach((key, value) -> {
			switch (key.eClass().eClass().getName()) {
			case "EClass":
				if (key.eClass().isAbstract()) {
					if (key.eClass().isInterface()) {
						engine.executeScript(VisJsScriptTemplates.addInterfaceNode(value.getKey(),
								"name = " + key.eClass().getName(), value.getValue()));
					} else {
						engine.executeScript(VisJsScriptTemplates.addAbstractNode(value.getKey(),
								"name = " + key.eClass().getName(), value.getValue()));
					}
				} else {
					engine.executeScript(VisJsScriptTemplates.addNode(value.getKey(),
							"name = " + key.eClass().getName(), value.getValue()));
				}
				break;
			case "EEnum":
				engine.executeScript(VisJsScriptTemplates.addEnumNode(value.getKey(),
						"name = " + ((EEnum) key).getName(), value.getValue()));
			}
		});
		allEdges.forEach((key, value) -> {
			engine.executeScript(VisJsScriptTemplates.addEdge(value.getKey(), value.getValue(), key.getValue()));
		});
	}

	/**
	 * Computes a list of all EClass names contained by the current xmi-model ->
	 * resulting list can be later put into the JavaFX-controls ChoiceBox
	 * 
	 * @return list of EClass-names of the current selection
	 */
	@Override
	public List<String> computeitems() {
		List<String> availableEClasses = new ArrayList<String>();
		allNodes.forEach((key, value) -> {
			if (!availableEClasses.contains(key.eClass().getName()))
				availableEClasses.add(key.eClass().getName());
		});
		return availableEClasses;
	}

	/**
	 * Searches in the map allNodes for EClass names which match the provided
	 * filterWord and returns a list containing their ids
	 * 
	 * @param filterWord - string that acts as the filter
	 * @return list of filtered node ids
	 */
	@Override
	public List<Integer> getChoiceIds(String filterWord) {
		List<Integer> choiceIds = new ArrayList<Integer>();
		allNodes.forEach((key, value) -> {
			if (key.eClass().getName().equals(filterWord)) {
				choiceIds.add(value.getKey());
			}
		});
		return choiceIds;
	}

	/**
	 * Searches in the map allNodes for nodes which contain the provided filterWord
	 * and returns a list containing their ids
	 * 
	 * @param filterWord - string that acts as the filter
	 * @return list of filtered node ids
	 */
	@Override
	public List<Integer> getTextFieldIds(String filterWord) {
		List<Integer> textFieldIds = new ArrayList<Integer>();
		allNodes.forEach((key, value) -> {
			if (TextFieldPatternMatcher.matchTextFieldInput(filterWord, value.getValue())
					|| TextFieldPatternMatcher.matchTextFieldInput(filterWord, key.eClass().getName())) {
				textFieldIds.add(value.getKey());
			}
		});
		return textFieldIds;
	}

	/**
	 * Searches in the map allNodes for nodes which contain both of the given
	 * firstFilterWord-string and secondFilterWord-string and returns a list
	 * containing their ids
	 * 
	 * @param firstFilterWord  - string that acts as the filter
	 * @param secondFilterWord - string that acts as the filter
	 * @return list of filtered node ids
	 */
	@Override
	public List<Integer> getNonHighlightIds(List<Integer> highlightIds) {
		List<Integer> nonhighlightIds = new ArrayList<Integer>();
		allNodes.forEach((key, value) -> {
			if (!highlightIds.contains(value.getKey()))
				nonhighlightIds.add(value.getKey());
		});
		return nonhighlightIds;
	}

	/**
	 * Searches in the map allNodes for nodes which are not contained in the
	 * provided list highlightIds and returns a list containing their ids
	 * 
	 * @param highlightIds - list that acts as the filter
	 * @return list of filtered node ids
	 */
	@Override
	public List<Integer> getTextFieldAndIds(String firstFilterWord, String secondFilterWord) {
		List<Integer> textFieldIds = new ArrayList<Integer>();
		allNodes.forEach((key, value) -> {
			if ((TextFieldPatternMatcher.matchTextFieldInput(firstFilterWord, value.getValue())
					|| TextFieldPatternMatcher.matchTextFieldInput(firstFilterWord, key.eClass().getName()))
					&& (TextFieldPatternMatcher.matchTextFieldInput(secondFilterWord, value.getValue())
							|| TextFieldPatternMatcher.matchTextFieldInput(secondFilterWord, key.eClass().getName()))) {
				textFieldIds.add(value.getKey());
			}
		});
		return textFieldIds;
	}

	/**
	 * Returns the highest of id of all nodes contained by the xmi-model
	 * 
	 * @return highest node id
	 */
	@Override
	public int getNodeId() {
		return nodeId - 1;
	}

}
