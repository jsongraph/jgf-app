# bel-network-app

A [Cytoscape](http://www.cytoscape.org/) app to import and export [BEL](http://www.openbel.org/bel-expression-language) networks encoded as [JSON Graph Format](http://json-graph-format.info/).

About JSON Graph Format (JGF)
=============================

- [JSON Graph Format specification](http://json-graph-format.info/schema.json)
- [BEL network child schema](https://github.com/jsongraph/json-graph-specification/blob/master/child-schemas/bel-json-graph.schema.json)
- [Example exports of BEL networks](#) (***need link!***)

Goals
=====
Goal of this project is the following:

- Import JSON Graph Format into Cytoscape.
- Export JSON Graph Format out of Cytoscape.
- Provide visual style for BEL networks.
- View evidence details associated with an Edge in BEL Network.

Design
======
- Though the schema currently support multiple as well as single graph, the current implementation of JGFNetwork only supports a single graph. 

Status
======
- Under development - first version expected complete early Sept 2014.

Requirements
============
- [Cytoscape 3.x](http://www.cytoscape.org/)

Installation
============
- See [INSTALL.md](https://github.com/OpenBEL/bel-network-app/blob/master/INSTALL.md).
