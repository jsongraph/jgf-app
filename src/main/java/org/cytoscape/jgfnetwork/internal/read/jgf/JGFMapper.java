package org.cytoscape.jgfnetwork.internal.read.jgf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.jgfnetwork.internal.mapperclasses.*;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.SavePolicy;



public class JGFMapper {

	private final Graph graph;
	private final CyNetwork network;
	private final CyTableFactory cyTableFactory;
	private final CyTableManager cyTableManager;
	final Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
	
	// Default values
	private static final String MAP_COLOR = "#6999AE";
	private static final String TITLE_COLOR = "#32CCB6";
	
	private static final String JGF_ID = "ID";
	private static final String JGF_TYPE = "TYPE";
	private static final String JGF_LABEL = "LABEL";
	private static final String JGF_DIRECTED = "DIRECTED";
	
	private static final String JGF_GRAPH_DESCRIPTION = "DESCRIPTION";
	private static final String JGF_GRAPH_SPECIES = "SPECIES";
	private static final String JGF_GRAPH_VERSION = "VERSION";
	
	private static final String JGF_NODE_X = "XLOC";
	private static final String JGF_NODE_Y = "YLOC";
	private static final String JGF_NODE_Z = "ZLOC";
	private static final String JGF_BEL_FUNC = "BELFUNCTION";
	
	private static final String JGF_EDGE_SOURCE = "SOURCE_NODE";
	private static final String JGF_EDGE_TARGET= "TARGET_NODE";
	private static final String JGF_EDGE_CAUSAL = "CAUSAL";
	
public JGFMapper(final Graph graph, final CyNetwork network,CyTableFactory cyTableFactory, CyTableManager cyTableManager) {
		this.graph = graph;
		this.network = network;
		 this.cyTableFactory = cyTableFactory;
		 this.cyTableManager = cyTableManager;
		mapGraphMetadata(graph, network);
	}


	private final void mapGraphMetadata(final Graph graph,
		final CyNetwork network) {
	
	HashMap<String, Object> graphMetadata = graph.getMetadata();
	
	String version = "1.0";
	if( graphMetadata.containsKey("version"))
		version = graphMetadata.get("version").toString();
	
	String description = "";
	if( graphMetadata.containsKey("description"))
		description = graphMetadata.get("description").toString();
	
	String species = "";
	if( graphMetadata.containsKey("species_common_name"))
		species = graphMetadata.get("species_common_name").toString();
	
	final String graphName = graph.getLabel();
	final String graphTitle = graphName + " ver: " + version;
	
	final CyRow networkRow = network.getRow(network);
	networkRow.set(CyNetwork.NAME, graphTitle);

	network.getDefaultNetworkTable().createColumn(JGF_GRAPH_VERSION,
			String.class, true);
	network.getDefaultNetworkTable().createColumn(JGF_GRAPH_DESCRIPTION,
			String.class, true);
	network.getDefaultNetworkTable().createColumn(JGF_TYPE,
			String.class, true);
	network.getDefaultNetworkTable().createColumn(JGF_DIRECTED,
			Boolean.class, true);
	network.getDefaultNetworkTable().createColumn(JGF_GRAPH_SPECIES,
			String.class, true);
	
	
	networkRow.set(JGF_GRAPH_VERSION, version );
	networkRow.set(JGF_GRAPH_DESCRIPTION, description);
	networkRow.set(JGF_GRAPH_SPECIES, species);
	networkRow.set(JGF_TYPE, graph.getType());
	networkRow.set(JGF_DIRECTED, graph.getDirected());
}

	public void doMapping() throws IOException {
		createJGFNodeTable();
		createJGFEdgeTable();
		MapNodes();
		MapEdges();
	}	
	
	private void MapNodes()
	{
		nodeMap.clear();
		for(Node n : graph.getNodes())
		{
			final CyNode cyNode = network.addNode();
			final CyRow row = network.getRow(cyNode);
			row.set(CyNetwork.NAME, n.getLabel());
			row.set(JGF_LABEL, n.getLabel());			
			if( n.getMetadata().containsKey("coordinate"))
			{
				@SuppressWarnings("unchecked")
				ArrayList<Double> loc = (ArrayList<Double>)n.getMetadata().get("coordinate");
				if( loc.size() >= 1)
					row.set(JGF_NODE_X, loc.get(0));
				if( loc.size() >= 2)
					row.set(JGF_NODE_Y, loc.get(1));
				if( loc.size() >= 3)
					row.set(JGF_NODE_Z, loc.get(2));
			}
			if( n.getMetadata().containsKey("bel_function_type"))
			{
				row.set(JGF_BEL_FUNC, n.getMetadata().get("bel_function_type"));	
			}
			//parse the label or bel_function for apply visual styles?
			nodeMap.put(n.getId(), cyNode); // for edges to use later for source and target mapping
		}
	}
	
	
	private void createJGFNodeTable() {
		network.getDefaultNodeTable().createColumn(JGF_NODE_X, Double.class,
				true);
		network.getDefaultNodeTable().createColumn(JGF_NODE_Y, Double.class,
				true);
		network.getDefaultNodeTable().createColumn(JGF_NODE_Z, Double.class,
				true);
		network.getDefaultNodeTable().createColumn(JGF_LABEL,
				String.class, true);
		network.getDefaultNodeTable().createColumn(JGF_BEL_FUNC, String.class,
				true);
	}
	
	private void MapEdges()
	{
		//edges do not have an ID from the JSon - but each added CyEdge has its own SUID
		for(Edge edge : graph.getEdges())
		{
		// need to find the source and target by NodeID in the nodemap
			CyNode sourceNode = nodeMap.get(edge.getSource());
			CyNode targetNode = nodeMap.get(edge.getTarget());
			final CyEdge newEdge = network.addEdge(sourceNode,
					targetNode, edge.getDirected());
			final CyRow row = network.getRow(newEdge);
			row.set(CyNetwork.NAME, edge.getLabel());			
			row.set(CyEdge.INTERACTION, edge.getRelation());
			
			final CyRow srow = network.getRow(sourceNode);
			final CyRow trow = network.getRow(targetNode);
			row.set(JGF_EDGE_SOURCE, srow.get(CyNetwork.NAME,String.class));
			row.set(JGF_EDGE_TARGET, trow.get(CyNetwork.NAME,String.class));				
			//if( n.getMetadata().containsKey("Evidences"))
			// Create an unassigned Table "JGF.Evidence" create columns for all properties and 2 List Columns for the Biological Context.
			if (edge.getMetadata()!= null)
			{			
				HashMap<String, Object> mdata = edge.getMetadata();		
				if(mdata.containsKey("evidences"))
				{
					String rawEvis = mdata.get("evidences").toString();	
					@SuppressWarnings("unchecked")
					List<LinkedHashMap<String, Object>> eviMap = (List<LinkedHashMap<String,Object>>)mdata.get("evidences");
			        List<Evidence> allEvis = new ArrayList<Evidence>();
		            for(HashMap<String,Object> item : eviMap)
		            {
		            	Evidence evi = new Evidence();
		            	evi.setBel_statement( item.get("bel_statement")==null ? "" :item.get("bel_statement").toString());
		            	evi.setSummary_text( item.get("summary_text")==null ? "" :item.get("summary_text").toString());
		            	Citation cit = new Citation();
		            	@SuppressWarnings("unchecked")
						LinkedHashMap<String, Object> citem = (LinkedHashMap<String, Object>)item.get("citation");
		            	cit.setId(citem.get("id")==null ? "" :citem.get("id").toString()); 
		            	cit.setType(citem.get("type")==null ? "" :citem.get("type").toString());
		            	cit.setName(citem.get("name")==null ? "" :citem.get("name").toString());		          
		            	evi.setCitation(cit);
		           
		            	BiologicalContext bc = new BiologicalContext();
		            	@SuppressWarnings("unchecked")
						LinkedHashMap<String, Object> bitem = (LinkedHashMap<String, Object>)item.get("biological_context");
		            	bc.setSpecies_common_name( bitem.get("species_common_name")==null ? "" :bitem.get("species_common_name").toString()); 
		            	bc.setNcbi_tax_id( bitem.get("ncbi_tax_id")==null ? 0 : (Integer)bitem.get("ncbi_tax_id"));          
		            	evi.setBiological_context(bc);;
		            	allEvis.add(evi);
		            }				      
		            processEvidences(newEdge, allEvis);			
				}
				if(mdata.containsKey("casual"))
				{			
					row.set(JGF_EDGE_CAUSAL, mdata.get("casual"));
				}				
			}
		}
	}
	
	private void createJGFEdgeTable() {
		network.getDefaultEdgeTable().createColumn(JGF_EDGE_SOURCE,
				String.class, true);
		network.getDefaultEdgeTable().createColumn(JGF_EDGE_TARGET,
				String.class, true);
		network.getDefaultEdgeTable().createColumn(JGF_EDGE_CAUSAL,
				Boolean.class, true);
	}

	
	private void processEvidences(CyEdge edge, List<Evidence> evis)
	{
		CyTable eviTable = GetOrCreateEvidenceTable(cyTableManager, cyTableFactory);
	    CyRow networkRow = network.getRow(network);
		String graphTitle = networkRow.get(CyNetwork.NAME, String.class);
		Long netSUID = network.getSUID();
		Long edgeSUID = edge.getSUID();
		//put the evidence into the table.
		for( Evidence evi: evis)
		{
			CyRow row = eviTable.getRow(SUIDFactory.getNextSUID());
			row.set("NETWORK_SUID", netSUID);
			row.set("NETWORK_NAME", graphTitle);
			row.set("EDGE_SUID", edgeSUID);
			row.set("BEL_STATEMENT", evi.getBel_statement());
			row.set("CITATION_TYPE", evi.getCitation().getType());
			row.set("CITATION_ID", evi.getCitation().getId());
			row.set("CITATION_NAME", evi.getCitation().getName());
			row.set("SUMMARY_TEXT", evi.getSummary_text());
			row.set("SPECIES", evi.getBiological_context().getSpecies_common_name());
		}		
	}
	
	
	private CyTable GetOrCreateEvidenceTable(CyTableManager cyTableManager, CyTableFactory cyTableFactory)
	{
		CyTable evTbl = null;		
		Set<CyTable> allTables =cyTableManager.getAllTables(true);
	    for (Iterator<CyTable> it = allTables.iterator(); it.hasNext(); ) 
	    {
	    	CyTable f = it.next();
	        if (f.getTitle() == "JGF.Evidence")
	        	evTbl = f;
	    }
		if( evTbl== null)
		{
			evTbl = cyTableFactory.createTable("JGF.Evidence", "SUID", Long.class, true, false);
			evTbl.setSavePolicy(SavePolicy.DO_NOT_SAVE);
			//Add all the columns
			evTbl.createColumn("NETWORK_SUID", Long.class, true, null);
		    evTbl.createColumn("NETWORK_NAME", String.class, true);
		    evTbl.createColumn("EDGE_SUID", Long.class, true, null);
			evTbl.createColumn("BEL_STATEMENT", String.class, false);
			evTbl.createColumn("CITATION_TYPE", String.class, false);
			evTbl.createColumn("CITATION_ID", String.class, false);
			evTbl.createColumn("CITATION_NAME", String.class, false);
			evTbl.createColumn("SUMMARY_TEXT", String.class, false);
			evTbl.createColumn("SPECIES", String.class, false);	
			cyTableManager.addTable(evTbl);	
		}
		return evTbl;
	}
	
	
	

}