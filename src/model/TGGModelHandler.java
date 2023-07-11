package model;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

public class TGGModelHandler extends ModelHandler {

	private Collection<EObject> elist;
	
	public TGGModelHandler(Collection<EObject> elist) {
		this.elist = elist;
	}
	
	public void buildVis() {
		
	}

	@Override
	public HashBiMap<String, String> returnNodes() {
		// TODO Auto-generated method stub
		return null;
	}
}
