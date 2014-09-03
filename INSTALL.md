Installation
============

### From [Cytoscape App Store](http://apps.cytoscape.org/):

> - *todo*

### Manual Install within Cytoscape:

> 1. Open the App Manager (*Apps* → *App Manager* → *Install from File...*)
> 2. Select the BEL Network App jar obtained from the [releases page](https://github.com/OpenBEL/bel-network-app/releases/).
> 3. Click the *Open* button to install the plugin.

### From the sourcecode in this repository:

> Requirements:
> 
> - [Java](http://java.sun.com)
> - [Apache Maven](http://maven.apache.org/)
> - [Cytoscape 3.x](http://www.cytoscape.org/)
>
> Build process:
>
> 1. ``mvn package``
> 2. *Install from File* in Cytoscape
>   - *Apps* → *App Manager* → *Install from File...*
>   - Choose ``target/bel-network-app.jar`` to install.
