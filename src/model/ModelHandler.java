package model;

import java.util.Map.Entry;

import com.google.common.collect.HashBiMap;

public abstract class ModelHandler {

	public ModelHandler() {
		
	}
	public void buildVis() {
		
	}
	public abstract void refreshWindow();
	
	public abstract HashBiMap<String, String> returnNodes();
	
	public abstract HashBiMap<String, String> returnAbstractNodes();
	
	public abstract HashBiMap<String, String> returnInterfaceNodes();
	
	public abstract HashBiMap<String, String> returnEnumNodes();
	
	public abstract HashBiMap<String, Entry<String, String>> returnEdges();
	
	public abstract HashBiMap<String, Entry<String, String>> returnBiDirEdges();

	public abstract HashBiMap<String, Entry<String, String>> returnHeridityEdges();

	public abstract HashBiMap<String, Entry<String, String>> returnImplementsEdges();

}
