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
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;

import com.google.common.collect.HashBiMap;

import controller.TextFieldPatternMatcher;
import javafx.scene.control.ChoiceBox;
import javafx.scene.web.WebEngine;

public class MetaModelHandler extends ModelHandler {

	private int nodeId;
	private int edgeId;
	private Collection<EObject> elist;
	private HashBiMap<EObject, Entry<Integer, String>> allNodes = HashBiMap.create();
	private HashMap<Entry<String, String>, Entry<String, String>> allEdges = new HashMap<Entry<String, String>, Entry<String, String>>();

	/**
	 * 
	 * @param elist
	 */
	public MetaModelHandler(Collection<EObject> elist) {
		this.elist = elist;
		this.nodeId = 0;
		this.edgeId = 0;
	}

	/**
	 * 
	 */
	public void buildVis() {
		extractNodes();
		extractEdges();

	}

	/**
	 * 
	 */
	public void extractNodes() {
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
	 * 
	 */
	public void extractEdges() {
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
														+ ref.getUpperBound()),
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
	 * 
	 * @param elist
	 * @return
	 */
	public String extractEAttributes(EObject eobj) {

		ArrayList<String> helpList = new ArrayList<String>();

		eobj.eClass().getEAllContainments().forEach(ref -> {
			if (ref.getEReferenceType().getName() == "EStructuralFeature") {
				EList<EObject> refList = (EObjectContainmentWithInverseEList) eobj.eGet(ref);
				for (int i = 0; i < refList.size(); i++) {
					helpList.add("\\n" + "• " + ((EStructuralFeature) refList.get(i)).getEType().getName() + " "
							+ ((EStructuralFeature) refList.get(i)).getName());
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
	 * 
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
	 * 
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
	 * 
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
	 * 
	 */
	@Override
	public int getNodeId() {
		return nodeId - 1;
	}

	/**
	 * 
	 */
	@Override
	public List<Integer> getTextFieldIds(String filterWord) {
		List<Integer> textFieldIds = new ArrayList<Integer>();
		allNodes.forEach((key, value) -> {
			if (TextFieldPatternMatcher.matchTextFieldInput(filterWord, value.getValue())) {
				textFieldIds.add(value.getKey());
			}
		});
		return textFieldIds;
	}

}
