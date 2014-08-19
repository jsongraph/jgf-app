package org.cytoscape.jgfnetwork.internal;

import java.util.Properties;

import static org.cytoscape.work.ServiceProperties.*;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.jgfnetwork.internal.read.jgf.JGFFileFilter;
import org.cytoscape.jgfnetwork.internal.read.jgf.JGFNetworkReaderFactory;
import org.cytoscape.jgfnetwork.internal.ui.EdgeSelectedListener;
import org.cytoscape.jgfnetwork.internal.ui.ShowEvidenceFactory;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory; 
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.osgi.framework.BundleContext;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.model.events.RowsSetListener;

/**
 * {@code CyActivator} is a class that is a starting point for OSGi bundles.
 * 
 * A quick overview of OSGi: The common currency of OSGi is the <i>service</i>.
 * A service is merely a Java interface, along with objects that implement the
 * interface. OSGi establishes a system of <i>bundles</i>. Most bundles import
 * services. Some bundles export services. Some do both. When a bundle exports a
 * service, it provides an implementation to the service's interface. Bundles
 * import a service by asking OSGi for an implementation. The implementation is
 * provided by some other bundle.
 * 
 * When OSGi starts your bundle, it will invoke {@CyActivator}'s
 * {@code start} method. So, the {@code start} method is where
 * you put in all your code that sets up your app. This is where you import and
 * export services.
 * 
 * Your bundle's {@code Bundle-Activator} manifest entry has a fully-qualified
 * path to this class. It's not necessary to inherit from
 * {@code AbstractCyActivator}. However, we provide this class as a convenience
 * to make it easier to work with OSGi.
 *
 * Note: AbstractCyActivator already provides its own {@code stop} method, which
 * {@code unget}s any services we fetch using getService().
 */
public class CyActivator extends AbstractCyActivator {
	/**
	 * This is the {@code start} method, which sets up your app. The
	 * {@code BundleContext} object allows you to communicate with the OSGi
	 * environment. You use {@code BundleContext} to import services or ask OSGi
	 * about the status of some service.
	 */
	public CyActivator() {
		super();
	}

	// comment
	@Override
	public void start(BundleContext bc) throws Exception {

		// importing services
		final StreamUtil streamUtil = getService(bc, StreamUtil.class);
		final CyNetworkViewFactory cyNetworkViewFactory = getService(bc, CyNetworkViewFactory.class);
		final CyNetworkFactory cyNetworkFactory = getService(bc, CyNetworkFactory.class);
		final CyNetworkManager cyNetworkManager = getService(bc, CyNetworkManager.class);
		final CyRootNetworkManager cyRootNetworkManager = getService(bc, CyRootNetworkManager.class);
		
		final CyTableManager cyTableManager = getService(bc, CyTableManager.class);
		final CyTableFactory cyTableFactory = getService(bc, CyTableFactory.class);
		
		VisualStyleFactory vsFactoryServiceRef = getService(bc, VisualStyleFactory.class); 
		VisualMappingFunctionFactory passthroughMappingFactoryRef = getService(bc, VisualMappingFunctionFactory.class,
				"(mapping.type=passthrough)");
		VisualMappingFunctionFactory discreteMappingFactoryRef = getService(bc, VisualMappingFunctionFactory.class,
				"(mapping.type=discrete)");
		
		// get a reference to Cytoscape service -- LoadVizmapFileTaskFactory 
		 final LoadVizmapFileTaskFactory loadVizmapFileTaskFactory =  getService(bc,LoadVizmapFileTaskFactory.class);

		
		JGFVisualStyleBuilder vsBuilder = new JGFVisualStyleBuilder(vsFactoryServiceRef, loadVizmapFileTaskFactory,
				discreteMappingFactoryRef, passthroughMappingFactoryRef);
		
		VisualMappingManager vmm = getService(bc, VisualMappingManager.class);
		
		// readers
		final CyFileFilter jgfReaderFilter = new JGFFileFilter(new String[] { "jgf"},
				new String[] { "application/jgf" }, "JSON JGF Files", DataCategory.NETWORK, streamUtil);
		final JGFNetworkReaderFactory jgfReaderFactory = new JGFNetworkReaderFactory(
				jgfReaderFilter, cyNetworkViewFactory, cyNetworkFactory, cyNetworkManager, cyRootNetworkManager,
				vsBuilder, vmm, cyTableFactory,  cyTableManager );
		final Properties jgfNetworkReaderFactoryProps = new Properties();
		jgfNetworkReaderFactoryProps.put(ID, "JGFNetworkReaderFactory");
		registerService(bc, jgfReaderFactory, InputStreamTaskFactory.class, jgfNetworkReaderFactoryProps);
		
		//Listens for change of the selected Edge - for updates to the custom panel in the results view
		final Properties edgeSelectedListenerProps = new Properties();
		edgeSelectedListenerProps.put(ID, "EdgeSelectedListener");
		final EdgeSelectedListener  edgeSelectedListener = new EdgeSelectedListener();
		registerService(bc, edgeSelectedListener, RowsSetListener.class, edgeSelectedListenerProps);
		
		final Properties evidenceFactoryProps = new Properties();
		evidenceFactoryProps.put(ID, "ShowEvidenceFactory");
		evidenceFactoryProps.put(PREFERRED_MENU, "Apps.JGF");
		evidenceFactoryProps.put(MENU_GRAVITY, "14.0");
		evidenceFactoryProps.put(TITLE, "View Evidence");
		registerService(bc, new ShowEvidenceFactory( ), EdgeViewTaskFactory.class, evidenceFactoryProps );	
	}
}

