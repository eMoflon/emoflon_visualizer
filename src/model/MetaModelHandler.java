package model;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;

import com.google.common.collect.HashBiMap;

import controller.TextFieldPatternMatcher;
import javafx.scene.web.WebEngine;

/**
 * Creates and handles all models that are of the type "meta-model"
 * 
 * @author maximiliansell
 *
 */
public class MetaModelHandler extends ModelHandler {

	private int nodeId;
	private int edgeId;
	private Collection<EObject> elist;
	private HashBiMap<EObject, Entry<Integer, String>> allNodes = HashBiMap.create();
	private HashMap<Entry<String, String>, Entry<String, String>> allEdges = new HashMap<Entry<String, String>, Entry<String, String>>();

	/**
	 * Creates an instance of the Class MetaModelHandler
	 * 
	 * @param elist - list of EObjects
	 */
	public MetaModelHandler(Collection<EObject> elist) {
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
			switch (current.eClass().getName()) {
			case "EClass":
				allNodes.put(current,
						new AbstractMap.SimpleEntry<Integer, String>(nodeId++, extractEAttributes(current)));

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
	private void extractEdges() {
		elist.forEach(current -> {
			switch (current.eClass().getName()) {
			case "EClass":
				allNodes.forEach((k, v) -> {
					if (!current.equals(k) && k.eClass().getName() != "EEnum") {
						if (((EClass) current).isSuperTypeOf((EClass) k)) {
							if (((EClass) current).isInterface() && allNodes.containsKey(current)) {
								allEdges.put(new AbstractMap.SimpleEntry<String, String>("implements", edgeId++ + ""),
										new AbstractMap.SimpleEntry<String, String>(allNodes.get(current).getKey() + "",
												allNodes.get(k).getKey() + ""));
							} else {
								if (allNodes.containsKey(current)) {
									allEdges.put(new AbstractMap.SimpleEntry<String, String>("heridity", edgeId++ + ""),
											new AbstractMap.SimpleEntry<String, String>(
													allNodes.get(current).getKey() + "",
													allNodes.get(k).getKey() + ""));
								}
							}
						}
					}
				});
				((EClass) current).getEAllReferences().forEach(ref -> {
					if (ref.getEOpposite() != null) {
						if (allNodes.containsKey(ref.getEReferenceType())
								&& allNodes.containsKey(ref.getEOpposite().getEReferenceType())) {
							if (!allEdges.containsValue(new AbstractMap.SimpleEntry<String, String>(
									allNodes.get(ref.getEOpposite().getEReferenceType()).getKey() + "",
									allNodes.get(ref.getEReferenceType()).getKey() + ""))) {
								allEdges.put(
										new AbstractMap.SimpleEntry<String, String>("biDir",
												ref.getName() + "   " + ref.getLowerBound() + "..."
														+ ref.getUpperBound() + ", " + ref.getEOpposite().getName()
														+ "   " + ref.getEOpposite().getLowerBound() + "..."
														+ ref.getEOpposite().getUpperBound()),
										new AbstractMap.SimpleEntry<String, String>(
												allNodes.get(ref.getEReferenceType()).getKey() + "",
												allNodes.get(ref.getEOpposite().getEReferenceType()).getKey() + ""));
							}
						}
					} else {
						if (allNodes.containsKey(ref.getEReferenceType())
								&& allNodes.containsKey(ref.getEContainingClass())) {
							allEdges.put(
									new AbstractMap.SimpleEntry<String, String>("edge",
											ref.getName() + "   " + ref.getLowerBound() + "..." + ref.getUpperBound()),
									new AbstractMap.SimpleEntry<String, String>(
											allNodes.get(ref.getEContainingClass()).getKey() + "",
											allNodes.get(ref.getEReferenceType()).getKey() + ""));
						}
					}
				});
				break;
			}
		});

	}

	/**
	 * Extracts and concats all attributes from the given EObject into a String for
	 * the map allNodes
	 * 
	 * @param eObj - EObject with potential attributes
	 * @return string of concated attributes
	 */
	private String extractEAttributes(EObject eObj) {

		ArrayList<String> helpList = new ArrayList<String>();

		eObj.eClass().getEAllContainments().forEach(ref -> {
			if (ref.getEReferenceType().getName() == "EStructuralFeature") {
				EList<EObject> refList = (EObjectContainmentWithInverseEList) eObj.eGet(ref);
				for (int i = 0; i < refList.size(); i++) {
					helpList.add("\\n" + ((EStructuralFeature) refList.get(i)).getName() + " : "
							+ ((EStructuralFeature) refList.get(i)).getEType().getName());
				}
				;
			}
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
	public void createNetwork(WebEngine engine) {
		allNodes.forEach((key, value) -> {
			switch (key.eClass().getName()) {
			case "EClass":
				if (((EClass) key).isAbstract()) {
					if (((EClass) key).isInterface()) {
						engine.executeScript(VisJsScriptTemplates.addInterfaceNode(value.getKey(),
								((EClass) key).getName(), value.getValue()));
					} else {
						engine.executeScript(VisJsScriptTemplates.addAbstractNode(value.getKey(),
								((EClass) key).getName(), value.getValue()));
					}
				} else {
					engine.executeScript(
							VisJsScriptTemplates.addNode(value.getKey(), ((EClass) key).getName(), value.getValue()));
				}
				break;
			case "EEnum":
				engine.executeScript(
						VisJsScriptTemplates.addEnumNode(value.getKey(), ((EEnum) key).getName(), value.getValue()));
			}
		});
		allEdges.forEach((key, value) -> {
			switch (key.getKey()) {
			case "implements":
				engine.executeScript(VisJsScriptTemplates.addImplementsEdge(value.getKey(), value.getValue()));
				break;
			case "heridity":
				engine.executeScript(VisJsScriptTemplates.addHeridityEdge(value.getKey(), value.getValue()));
				break;
			case "biDir":
				engine.executeScript(
						VisJsScriptTemplates.addBiDirEdge(value.getKey(), value.getValue(), key.getValue()));
				break;
			case "edge":
				engine.executeScript(VisJsScriptTemplates.addEdge(value.getKey(), value.getValue(), key.getValue()));
				break;
			}
		});
	}

	/**
	 * Computes a list of all EClass names contained by the current metamodel ->
	 * resulting list can be later put into the JavaFX-controls ChoiceBox
	 * 
	 * @return list of EClass-names of the current selection
	 */
	@Override
	public List<String> computeitems() {
		List<String> availableEClasses = new ArrayList<String>();
		allNodes.forEach((key, value) -> {
			switch (key.eClass().getName()) {
			case "EClass":
				if (!availableEClasses.contains(((EClass) key).getName()))
					availableEClasses.add(((EClass) key).getName());
				break;
			case "EEnum":
				if (!availableEClasses.contains(((EEnum) key).getName()))
					availableEClasses.add(((EEnum) key).getName());
				break;
			}
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
			switch (key.eClass().getName()) {
			case "EClass":
				if (((EClass) key).getName().equals(filterWord)) {
					choiceIds.add(value.getKey());
				}
				break;
			case "EEnum":
				if (((EEnum) key).getName().equals(filterWord)) {
					choiceIds.add(value.getKey());
				}
				break;
			}
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
					| TextFieldPatternMatcher.matchTextFieldInput(filterWord, ((EClass) key).getName())) {
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
					|| TextFieldPatternMatcher.matchTextFieldInput(firstFilterWord, ((EClass) key).getName()))
					&& (TextFieldPatternMatcher.matchTextFieldInput(secondFilterWord, value.getValue())
							|| TextFieldPatternMatcher.matchTextFieldInput(secondFilterWord,
									((EClass) key).getName()))) {
				textFieldIds.add(value.getKey());
			}
		});
		return textFieldIds;
	}

	/**
	 * Returns the highest of id of all nodes contained by the metamodel
	 * 
	 * @return highest node id
	 */
	@Override
	public int getNodeId() {
		return nodeId - 1;
	}

}
