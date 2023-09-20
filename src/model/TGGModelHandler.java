package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import com.google.common.collect.HashBiMap;
import javafx.scene.web.WebEngine;

/**
 * Creates and handles all models that are of the type "TGG"
 * 
 * @author maximiliansell
 *
 */
public class TGGModelHandler extends ModelHandler {
	
	private int nodeId;
	private int edgeId;
	private Collection<EObject> elist;
	private HashBiMap<EObject, Entry<Integer, String>> allNodes = HashBiMap.create();
	private HashMap<Entry<String, String>, Entry<String, String>> allEdges = new HashMap<Entry<String, String>, Entry<String, String>>();
	
	/**
	 * Creates an instance of the Class TGGModelHandler
	 * 
	 * @param elist - list of EObjects
	 */
	public TGGModelHandler(Collection<EObject> elist) {
		this.elist = elist;
	}

	/**
	 * 
	 */
	public void buildVis() {

	}

	/**
	 * 
	 */
	@Override
	public void createNetwork(WebEngine engine) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	@Override
	public List<String> computeitems() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 
	 */
	@Override
	public List<Integer> getChoiceIds(String filterWord) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	@Override
	public int getNodeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 */
	@Override
	public List<Integer> getTextFieldIds(String filterWord) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	@Override
	public List<Integer> getNonHighlightIds(List<Integer> highlightIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	@Override
	public List<Integer> getTextFieldAndIds(String firstFilterWord, String secondFilterWord) {
		// TODO Auto-generated method stub
		return null;
	}
}


