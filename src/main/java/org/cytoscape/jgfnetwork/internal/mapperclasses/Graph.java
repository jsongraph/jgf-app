package org.cytoscape.jgfnetwork.internal.mapperclasses;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Graph {
	
	private String type;
	private String label;
	private Boolean directed = true;
	@JsonProperty
	private HashMap <String, Object> metadata; 
	//metadata can contain ncbi_tax_id, species_common_name, description
	private List<Node> nodes;
	private List<Edge> edges;	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getDirected() {
		return directed;
	}

	public void setDirected(Boolean directed) {
		this.directed = directed;
	}

	public HashMap<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(HashMap<String, Object> metadata) {
		this.metadata = metadata;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	
}
