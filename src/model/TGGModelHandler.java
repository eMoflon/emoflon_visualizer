package model;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

import javafx.scene.control.ChoiceBox;
import javafx.scene.web.WebEngine;

public class TGGModelHandler extends ModelHandler {

	private Collection<EObject> elist;

	public TGGModelHandler(Collection<EObject> elist) {
		this.elist = elist;
	}

	public void buildVis() {

	}

	@Override
	public void createNetwork(WebEngine engine) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> computeitems() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Integer> getChoiceIds(String filterWord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNodeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Integer> getTextFieldIds(String filterWord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getNonHighlightIds(List<Integer> highlightIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getTextFieldAndIds(String firstFilterWord, String secondFilterWord) {
		// TODO Auto-generated method stub
		return null;
	}
}


