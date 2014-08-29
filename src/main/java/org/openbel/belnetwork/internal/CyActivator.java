package org.openbel.belnetwork.internal;

import java.io.InputStream;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.VizmapReaderManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.openbel.belnetwork.api.BELEvidenceMapper;
import org.openbel.belnetwork.api.BELGraphConverter;
import org.openbel.belnetwork.api.BELGraphReader;
import org.openbel.belnetwork.internal.listeners.SessionListener;
import org.openbel.belnetwork.internal.io.JGFFileFilter;
import org.openbel.belnetwork.internal.io.JGFNetworkReaderFactory;
import org.openbel.belnetwork.internal.ui.EdgeSelectedListener;
import org.openbel.belnetwork.internal.ui.ShowEvidenceFactory;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.openbel.belnetwork.api.util.StyleUtility;
import org.osgi.framework.BundleContext;
import org.cytoscape.model.events.RowsSetListener;

import static org.cytoscape.work.ServiceProperties.*;
import static org.openbel.belnetwork.internal.Constants.*;

/**
 * {@code CyActivator} is a class that is a starting point for OSGi bundles.
 *
 * A quick overview of OSGi: The common currency of OSGi is the <i>service</i>.
 * A service is merely a Java interface, along with objects that implement the
 * interface. OSGi establishes a system of <i>bundles</i>. Most bundles reference
 * services. Some bundles register services. Some do both. When a bundle registers a
 * service, it provides an implementation to the service's interface. Bundles
 * reference a service by asking OSGi for an implementation. The implementation is
 * provided by some other bundle.
 *
 * When OSGi starts your bundle, it will invoke
 * {@link CyActivator#start(org.osgi.framework.BundleContext)} method. So, the
 * {@code start} method is where you put in all your code that sets up your app.
 * This is where you reference and register services.
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
     * environment. You use {@code BundleContext} to reference services or ask OSGi
     * about the status of some service.
     */
    public CyActivator() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext bc) throws Exception {

        // reference services
        final StreamUtil streamUtil = getService(bc, StreamUtil.class);
        final CyApplicationManager appMgr = getService(bc, CyApplicationManager.class);
        final CyNetworkViewFactory cyNetworkViewFactory = getService(bc, CyNetworkViewFactory.class);
        final CyNetworkFactory cyNetworkFactory = getService(bc, CyNetworkFactory.class);
        final CyNetworkManager cyNetworkManager = getService(bc, CyNetworkManager.class);
        final CyRootNetworkManager cyRootNetworkManager = getService(bc, CyRootNetworkManager.class);
        final CyTableManager cyTableManager = getService(bc, CyTableManager.class);
        final CyTableFactory cyTableFactory = getService(bc, CyTableFactory.class);
        final VisualMappingManager visMgr = getService(bc, VisualMappingManager.class);
        final VizmapReaderManager vizmapReaderMgr = getService(bc, VizmapReaderManager.class);
        final CyEventHelper eventHelper = getService(bc, CyEventHelper.class);

        // contribute visual styles
        CyActivator.contributeStyles(visMgr, vizmapReaderMgr);

        // API implementations
        final BELGraphReader belGraphReader = new BELGraphReaderImpl();
        final BELGraphConverter belGraphConverter = new BELGraphConverterImpl(cyNetworkFactory);
        final BELEvidenceMapper belEvidenceMapper = new BELEvidenceMapperImpl();

        // readers
        final CyFileFilter jgfReaderFilter = new JGFFileFilter(streamUtil);
        final JGFNetworkReaderFactory jgfReaderFactory = new JGFNetworkReaderFactory(
                jgfReaderFilter, appMgr, cyNetworkViewFactory, cyNetworkFactory,
                cyNetworkManager, cyRootNetworkManager, cyTableFactory,
                cyTableManager, visMgr, eventHelper, belGraphReader, belGraphConverter,
                belEvidenceMapper);
        final Properties jgfNetworkReaderFactoryProps = new Properties();
        jgfNetworkReaderFactoryProps.put(ID, "JGFNetworkReaderFactory");
        registerService(bc, jgfReaderFactory, InputStreamTaskFactory.class, jgfNetworkReaderFactoryProps);

        // listeners
        final Properties edgeSelectedListenerProps = new Properties();
        edgeSelectedListenerProps.put(ID, "EdgeSelectedListener");
        final EdgeSelectedListener  edgeSelectedListener = new EdgeSelectedListener();
        registerService(bc, edgeSelectedListener, RowsSetListener.class, edgeSelectedListenerProps);
        registerAllServices(bc, new SessionListener(visMgr, vizmapReaderMgr), new Properties());

        final Properties evidenceFactoryProps = new Properties();
        evidenceFactoryProps.put(ID, "ShowEvidenceFactory");
        evidenceFactoryProps.put(PREFERRED_MENU, "Apps.JGF");
        evidenceFactoryProps.put(MENU_GRAVITY, "14.0");
        evidenceFactoryProps.put(TITLE, "View Evidence");
        registerService(bc, new ShowEvidenceFactory(), EdgeViewTaskFactory.class, evidenceFactoryProps);
    }

    public static void contributeStyles(VisualMappingManager visMgr, VizmapReaderManager vizmapReaderMgr) {
        if (visMgr == null) throw new NullPointerException("visMgr cannot be null");
        if (vizmapReaderMgr == null) throw new NullPointerException("vizmapReaderMgr cannot be null");

        InputStream styleResource = CyActivator.class.getResourceAsStream(STYLE_RESOURCE_PATH);
        StyleUtility.contributeStylesIdempotently(styleResource, STYLE_RESOURCE_PATH, visMgr, vizmapReaderMgr);
    }
}
