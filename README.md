# JGFNetwork
### JGF(JSON Graph Format) JSON file support App for Cytoscape 3.x.
(https://github.com/?)


Introduction
====

[Cytoscape](http://www.cytoscape.org/) is a widely used bioinformatics software platform for integrating, analyzing, and visualizing biological network data sets.  JGFNetwork is a [Cytoscape App](http://apps.cytoscape.org/) that provides support for importing JGF File and displaying the edges associated evidence.

About JGF
====

The Schema information for the JGF Json format can be found at: (https://github.com/jsongraph/json-graph-specification)
The import for this app supports the child BEL schema found at: (https://github.com/jsongraph/json-graph-specification/tree/master/child-schemas)
Netorks that support the export of the JGF format can be found in the CBN application (link here).


Goals
====
Goal of this project is the following:

* Full support for importing JGF networks into Cytoscape
* Ability for user to view the evidence information details associated with an Edge in Cytoscape.

Design
====
* Though the schema currently support multiple as well as single graph, the current implementation of JGFNetwork only
  supports a single graph. 

Status
====
* Under development - first version expected complete early Sept 2014.
