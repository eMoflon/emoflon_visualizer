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

import com.google.common.collect.HashBiMap;

import javafx.scene.web.WebEngine;

public class XMIModelHandler extends ModelHandler {

	private Collection<EObject> elist;
	private Collection<EClassifier> eclassifiers;

	private int nodeIdCounter;
	private int edgeIdCounter;
	private int heridityEdgesCounter;
	private int implementEdgesCounter;

	protected HashBiMap<String, String> nodesWithIds = HashBiMap.create();
	protected HashBiMap<String, String> nodes = HashBiMap.create();
	protected HashBiMap<String, String> enumNodes = HashBiMap.create();
	protected HashBiMap<String, String> abstractNodes = HashBiMap.create();
	protected HashBiMap<String, String> interfaceNodes = HashBiMap.create();
	protected HashBiMap<String, ArrayList<String>> nodesAttr = HashBiMap.create();

	protected HashBiMap<String, Entry<String, String>> edges = HashBiMap.create();
	protected HashBiMap<String, Entry<String, String>> biDirEdges = HashBiMap.create();
	protected HashBiMap<String, Entry<String, String>> heridityEdges = HashBiMap.create();
	protected HashBiMap<String, Entry<String, String>> implementsEdges = HashBiMap.create();

	public XMIModelHandler(Collection<EObject> elist) {
		this.elist = elist;
		eclassifiers = elist.iterator().next().eClass().getEPackage().getEClassifiers();
	}

	/**
	 * 
	 */
	public void buildVis() {
		System.out.println("BasicModel");
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
				// do smt.
				if (current.eClass().isAbstract()) {
					if (current.eClass().isInterface()) {
						// EObject is an Interface
						interfaceNodes.put(nodeIdCounter + "", "<i>Interface</i>" + "\\n<code>"
								+ current.eClass().getName() + "</code>\\n" + extractEAttributes(current));
						nodesWithIds.put(nodeIdCounter + "", current.eClass().getName());
						nodeIdCounter++;
					} else {
						// EClass is abstract
						abstractNodes.put(nodeIdCounter + "", "<i>Abstract</i>" + "\\n<code>"
								+ (current.eClass().getName() + "</code>\\n" + "--" + extractEAttributes(current)));
						nodesWithIds.put(nodeIdCounter + "", current.eClass().getName());
						nodeIdCounter++;
					}

				} else {
					// EClass is just an EClass
					nodes.put(nodeIdCounter + "",
							"<code>" + current.eClass().getName() + "</code>\\n" + "--" + extractEAttributes(current));
					nodesWithIds.put(nodeIdCounter + "", current.eClass().getName());
					nodeIdCounter++;
				}
				break;

			case "EEnum":
				String literalStr = "";
				EList<EEnumLiteral> literalList = ((EEnum) current).getELiterals();
				for (EEnumLiteral item : literalList)
					literalStr += "\\n" + item.getName();
				enumNodes.put(((EClass) current).getName() + "", "<i>" + ((EClass) current).getName() + "\\n<code>"
						+ ((EClass) current).getName() + "</code>" + "\\n" + literalStr);
				nodesWithIds.put(nodeIdCounter + "", ((EClass) current).getName());
				nodeIdCounter++;
				break;
			}

		});
		int n = 0;
	}

	/**
	 * 
	 * @param elist
	 * @return
	 */
	public String extractEAttributes(EObject current) {

		ArrayList<String> helpList = new ArrayList<String>();
		System.out.println(current.eClass().getEAllAttributes());
		current.eClass().getEAllAttributes().forEach(attr -> {
			helpList.add("\\n" + "• " + attr.getEType().getName() + " " + attr.getName());
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
		ArrayList<String> biDirMergeNames = new ArrayList<>();
		ArrayList<EObject> listForEdges = new ArrayList<EObject>();
		elist.forEach(current -> {
			listForEdges.add(current);
			current.eClass().getEAllReferences().forEach(ref ->{
				if(nodesWithIds.containsValue(ref.getEType().getName())) {
					if(ref.getEOpposite() == null) {
						edges.put(ref.getName() + "   " + ref.getLowerBound() + "..." + ref.getUpperBound(),
								new AbstractMap.SimpleEntry<String, String>(
										nodesWithIds.inverse().get(ref.getEContainingClass().getName()),
										nodesWithIds.inverse().get(ref.getEType().getName())));
					}else {
						if(!biDirEdges.containsValue(new AbstractMap.SimpleEntry<String, String>(
										nodesWithIds.inverse().get(ref.getEContainingClass().getName()),
										nodesWithIds.inverse().get(ref.getEType().getName())))) {
						biDirEdges.put(ref.getName() + "   " + ref.getLowerBound() + "..." + ref.getUpperBound(),
								new AbstractMap.SimpleEntry<String, String>(
										nodesWithIds.inverse().get(ref.getEType().getName()),
										nodesWithIds.inverse().get(ref.getEContainingClass().getName())));
					}else {
						String oldValue = biDirEdges.inverse().get(new AbstractMap.SimpleEntry<String, String>(
								nodesWithIds.inverse().get(ref.getEContainingClass().getName()),
								nodesWithIds.inverse().get(ref.getEType().getName())));
						biDirEdges.inverse().replace(new AbstractMap.SimpleEntry<String, String>(
								nodesWithIds.inverse().get(ref.getEContainingClass().getName()),
								nodesWithIds.inverse().get(ref.getEType().getName())), oldValue + "\\n" + ref.getName() + "   " + ref.getLowerBound() + "..." + ref.getUpperBound());
					}
						int n = 0;
					}
				}
			});
		});
		for (int i = 0; i < listForEdges.size(); i++) {
			for (int k = 0; k < listForEdges.size(); k++) {
				if (!listForEdges.get(i).eClass().equals(listForEdges.get(k).eClass())
						&& listForEdges.get(i).eClass().isSuperTypeOf(listForEdges.get(k).eClass())) {
					if (listForEdges.get(i).eClass().isInterface()) {
						implementsEdges.put(implementEdgesCounter + "",
								new AbstractMap.SimpleEntry<String, String>(
										nodesWithIds.inverse().get(listForEdges.get(i).eClass().getName()),
										nodesWithIds.inverse().get(listForEdges.get(k).eClass().getName())));
						implementEdgesCounter++;
					} else {
						heridityEdges.put(heridityEdgesCounter + "",
								new AbstractMap.SimpleEntry<String, String>(
										nodesWithIds.inverse().get(listForEdges.get(i).eClass().getName()),
										nodesWithIds.inverse().get(listForEdges.get(k).eClass().getName())));
						heridityEdgesCounter++;
					}
				}
			}
		}

	}

	/**
	 * 
	 * @param mergeList
	 */
	private void biDirNamesMerge(ArrayList<String> mergeList) {
		int i = 0;
		for (Entry<Entry<String, String>, String> entry : biDirEdges.inverse().entrySet()) {
			String value = entry.getValue();
			entry.setValue(value + "\\n" + mergeList.get(i));
			i++;
		}
	}

	@Override
	public void createNetwork(WebEngine engine) {
		engine.executeScript(VisJsScriptTemplates.addNodes(nodes));
		engine.executeScript(VisJsScriptTemplates.addAbstractNodes(abstractNodes));
		engine.executeScript(VisJsScriptTemplates.addInterfaceNodes(interfaceNodes));
		engine.executeScript(VisJsScriptTemplates.addEnumNodes(enumNodes));
		engine.executeScript(VisJsScriptTemplates.addEdges(edges));
		engine.executeScript(VisJsScriptTemplates.addBidirectionalEdges(biDirEdges));
		engine.executeScript(VisJsScriptTemplates.addHeridityEdges(heridityEdges));
		engine.executeScript(VisJsScriptTemplates.addInterfaceNodes(interfaceNodes));
		engine.executeScript("var network = new vis.Network(container,data, options);");

	}

	@Override
	public void refreshWindow() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	private boolean checkForDupe(String str1, String str2) {
		if (biDirEdges.containsValue(new AbstractMap.SimpleEntry<String, String>(str1, str2))
				|| biDirEdges.containsValue(new AbstractMap.SimpleEntry<String, String>(str2, str1))) {
			return false;
		} else
			return true;
	}

}
