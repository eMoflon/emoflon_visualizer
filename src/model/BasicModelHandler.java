package model;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;

import com.google.common.collect.HashBiMap;

public class BasicModelHandler extends ModelHandler {

	private Collection<EObject> elist;

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

	public BasicModelHandler(Collection<EObject> elist) {
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
		//Will be used to form one String containing all EAttributes of the current EObject
		
		elist.forEach(current -> {
			switch (current.eClass().getName()) {
			case "EClass":
				// do smt.
				if (((EClass) current).isAbstract()) {
					if (((EClass) current).isInterface()) {
						// EObject is an Interface
						interfaceNodes.put(nodeIdCounter + "",
								"<i>Interface</i>" + "\\n<code>" + (((EClass) current).getName()) + "</code>\\n" + extractEAttributes(current));
						nodesWithIds.put(nodeIdCounter + "", ((EClass)current).getName());
						nodeIdCounter++;
					} else {
						// EClass is abstract
						abstractNodes.put(nodeIdCounter + "", "<i>Abstract</i>" + "\\n<code>"  + ((EClass)current).getName()
								+ "</code>\\n" +  "--" + extractEAttributes(current));
						nodesWithIds.put(nodeIdCounter + "", ((EClass)current).getName());
						nodeIdCounter++;
					}

				}else {
					//EClass is just an EClass
					nodes.put(nodeIdCounter + "", "<code>" + ((EClass)current).getName() + "</code>\\n" + "--" + extractEAttributes(current));
					nodesWithIds.put(nodeIdCounter + "", ((EClass)current).getName());
					nodeIdCounter++;
				}
				break;
				
			case "EEnum":	
				String literalStr = "";
				EList<EEnumLiteral> literalList = ((EEnum) current).getELiterals();
				for (EEnumLiteral item : literalList)
					literalStr += "\\n" + item.getName();
				enumNodes.put(((EClass)current).getName() + "", "<i>" + ((EClass)current).getName() + "\\n<code>"
						+ ((EClass)current).getName() + "</code>" + "\\n" + literalStr);
				nodesWithIds.put(nodeIdCounter + "", ((EClass)current).getName());
				nodeIdCounter++;
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
					EList<EObject> refList = (EObjectContainmentWithInverseEList)eobj.eGet(ref);
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
	public void extractEdges() {
		ArrayList<String> biDirMergeNames = new ArrayList<>();
//		
//		for (int i = 0; i < elist.size(); i++) {
//			for (int k = 0; k < elist.size(); k++) {
//				if (elist.get(i).eClass().getName() == "EClass" && elist.get(k).eClass().getName() == "EClass"
//						&& i != k)
//					if (((EClass) elist.get(i)).isSuperTypeOf((EClass) elist.get(k))) {
//						if(!((EClass) elist.get(i)).isInterface()) {
//						heridityEdges.put(heridityEdgesCounter + "",
//								new AbstractMap.SimpleEntry<String, String>(
//										nodesWithIds.inverse().get(elist.get(i).getName()),
//										nodesWithIds.inverse().get(elist.get(k).getName())));
//						heridityEdgesCounter++;
//						}else {
//							implementsEdges.put(implementEdgesCounter + "",
//									new AbstractMap.SimpleEntry<String, String>(
//											nodesWithIds.inverse().get(elist.get(i).getName()),
//											nodesWithIds.inverse().get(elist.get(k).getName())));
//							implementEdgesCounter++;
//						}
//					}
//			}
//
//			EObject current = elist.get(i);
//			current.eClass().getEAllContainments().forEach(ref -> {
//				if (ref.getEReferenceType().getName() == "EStructuralFeature") {
//					EList<EObject> refList = (EObjectContainmentWithInverseEList) current.eGet(ref);
//					for (int j = 0; j < refList.size(); j++) {
//						helpList.add(((EStructuralFeature) refList.get(j)).getName());
//						if (refList.get(j).eClass().getName() == "EReference") {
//							EReference er = (EReference) refList.get(j);
//							if (checkForDupe(nodesWithIds.inverse().get(er.getEContainingClass().getName()),
//									nodesWithIds.inverse().get(er.getEType().getName()))) {
//								if (er.getEOpposite() != null) {
//									biDirEdges.put(
//											er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound(),
//											new AbstractMap.SimpleEntry<String, String>(
//													nodesWithIds.inverse().get(er.getEContainingClass().getName()),
//													nodesWithIds.inverse().get(er.getEType().getName())));
//									continue;
//								}
//
//								if (!edges.containsKey(
//										er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound())) {
//									edges.put(er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound(),
//											new AbstractMap.SimpleEntry<String, String>(
//													nodesWithIds.inverse().get(er.getEContainingClass().getName()),
//													nodesWithIds.inverse().get(er.getEType().getName())));
//								} else {
//									edges.put(
//											er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound()
//													+ " ",
//											new AbstractMap.SimpleEntry<String, String>(
//													nodesWithIds.inverse().get(er.getEContainingClass().getName()),
//													nodesWithIds.inverse().get(er.getEType().getName())));
//
//								}
//							} else {
//								biDirMergeNames.add(er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound());
//							}
//						}
//					}
//				}
//				;
//			}
//
//			);
//		}
//		biDirNamesMerge(biDirMergeNames);
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
	public HashBiMap<String, String> returnNodes() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
