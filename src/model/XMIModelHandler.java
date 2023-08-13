package model;

import java.util.Collection;
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

import javafx.scene.web.WebEngine;

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
		elist.forEach(current -> {
			current.eClass().getEAllReferences().forEach(ref -> {
				if (!(current.eGet(ref) == null)) {
					if (current.eGet(ref).getClass().equals(EcoreEList.Dynamic.class)) {
						((EcoreEList.Dynamic<EObject>) (current.eGet(ref))).forEach(obj -> {
							if (allNodes.containsKey(obj)) {
								allEdges.putIfAbsent(
										new AbstractMap.SimpleEntry<Integer, String>(edgeId++, ref.getName()),
										new AbstractMap.SimpleEntry<String, String>(allNodes.get(current).getKey() + "",
												allNodes.get(obj).getKey() + ""));
							}
						});
					}
					if (current.eGet(ref).getClass().equals(DynamicEObjectImpl.class)) {
						if (allNodes.containsKey(current.eGet(ref))) {
							allEdges.putIfAbsent(new AbstractMap.SimpleEntry<Integer, String>(edgeId++, ref.getName()),
									new AbstractMap.SimpleEntry<String, String>(allNodes.get(current).getKey() + "",
											allNodes.get(current.eGet(ref)).getKey() + ""));
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
								key.eClass().getName(), value.getValue()));
						engine.executeScript(VisJsScriptTemplates.addAttrInterfaceNode(value.getKey(),
								key.eClass().getName(), ""));
					} else {
						engine.executeScript(VisJsScriptTemplates.addAbstractNode(value.getKey(),
								key.eClass().getName(), value.getValue()));
						engine.executeScript(VisJsScriptTemplates.addAttrAbstractNode(value.getKey(),
								key.eClass().getName(), ""));
					}
				} else {
					engine.executeScript(
							VisJsScriptTemplates.addNode(value.getKey(), key.eClass().getName(), value.getValue()));
					engine.executeScript(
							VisJsScriptTemplates.addAttrNode(value.getKey(), key.eClass().getName(), ""));
				}
				break;
			case "EEnum":
				engine.executeScript(
						VisJsScriptTemplates.addEnumNode(value.getKey(), ((EEnum) key).getName(), value.getValue()));
				engine.executeScript(
						VisJsScriptTemplates.addAttrEnumNode(value.getKey(), ((EEnum) key).getName(), ""));
			}
		});
		allEdges.forEach((key, value) -> {
//			engine.executeScript(VisJsScriptTemplates.addEdge(value.getValue().getKey(), value.getValue().getValue(), key.getValue()));
			engine.executeScript(VisJsScriptTemplates.addEdge(value.getKey(), value.getValue(), key.getValue()));
		});
//		engine.executeScript("var network = new vis.Network(container,data, options);");
	}

	@Override
	public void createNetworkToggle(WebEngine engine) {
		allNodes.forEach((key, value) -> {
			switch (key.eClass().eClass().getName()) {
			case "EClass":
				if (key.eClass().isAbstract()) {
					if (key.eClass().isInterface()) {
						engine.executeScript(VisJsScriptTemplates.addInterfaceNode(value.getKey(),
								key.eClass().getName(), ""));
					} else {
						engine.executeScript(VisJsScriptTemplates.addAbstractNode(value.getKey(),
								key.eClass().getName(), ""));
					}
				} else {
					engine.executeScript(
							VisJsScriptTemplates.addNode(value.getKey(), key.eClass().getName(), ""));
				}
				break;
			case "EEnum":
				engine.executeScript(
						VisJsScriptTemplates.addNode(value.getKey(), ((EEnum) key).getName(), ""));
			}
		});
		allEdges.forEach((key, value) -> {
//			engine.executeScript(VisJsScriptTemplates.addEdge(value.getValue().getKey(), value.getValue().getValue(), key.getValue()));
			engine.executeScript(VisJsScriptTemplates.addEdge(value.getKey(), value.getValue(), key.getValue()));
		});
//		engine.executeScript("var network = new vis.Network(container,data, options);");

	}

	@Override
	public void createNetworkWithoutEdges(WebEngine engine) {
		// TODO Auto-generated method stub
		
	}

}
