package model;

import java.util.Collection;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

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
	public void createNetworkToggle(WebEngine engine) {
		// TODO Auto-generated method stub

	}

}
