package org.cytoscape.jgfnetwork.internal.mapperclasses;

import java.util.HashMap;

public class Node {
	private String id;
	private String label;
	//can contain: x, y, z - type number
	private HashMap<String, Object> metadata;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public HashMap<String, Object> getMetadata() {
		return metadata;
	}
	public void setMetadata(HashMap<String, Object> metadata) {
		this.metadata = metadata;
	}
	
}
