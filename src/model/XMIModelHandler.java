package model;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreEList;

import com.google.common.collect.HashBiMap;

import javafx.scene.control.ChoiceBox;
import javafx.scene.web.WebEngine;

/**
 * 
 * @author ms21xino
 *
 */
public class XMIModelHandler extends ModelHandler {

	private Collection<EObject> elist;
	private int nodeId;
	private int edgeId;

	private HashBiMap<EObject, Entry<Integer, String>> allNodes = HashBiMap.create();
	private HashBiMap<Entry<Integer, String>, Entry<String, String>> allEdges = HashBiMap.create();

	public XMIModelHandler(Collection<EObject> elist) {
		this.elist = elist;
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
		// Will be used to form one String containing all EAttributes of the current
		// EObject
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
	 * 
	 * @param elist
	 * @return
	 */
	public String extractAttributes(EObject current) {
		ArrayList<String> helpList = new ArrayList<String>();
		current.eClass().getEAllAttributes().forEach(attr -> {
			helpList.add("\\n" + "• " + attr.getName() + " = " + current.eGet(attr));
		});
		String attrStr = "";
		for (String str : helpList)
			attrStr = attrStr + str;
		return attrStr;
	}

	/**
	 * 
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
	 * 
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
	 * 
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
	 * 
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
		return null;
	}

}
