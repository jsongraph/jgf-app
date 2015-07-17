package info.json_graph_format.jgfapp.internal;

import info.json_graph_format.jgfapp.api.*;
import info.json_graph_format.jgfapp.api.util.StyleUtility;
import info.json_graph_format.jgfapp.internal.io.JGFFileFilter;
import info.json_graph_format.jgfapp.internal.io.JGFNetworkReaderFactory;
import info.json_graph_format.jgfapp.internal.io.JGFNetworkWriterFactory;
import info.json_graph_format.jgfapp.internal.listeners.SessionListener;
import info.json_graph_format.jgfapp.internal.ui.About;
import info.json_graph_format.jgfapp.internal.ui.EvidencePanelComponent;
import info.json_graph_format.jgfapp.internal.ui.ShowEvidenceFactory;
import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.read.VizmapReaderManager;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.osgi.framework.BundleContext;

import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Properties;

import static info.json_graph_format.jgfapp.api.util.HelpUtility.createBrowseHelpAction;
import static info.json_graph_format.jgfapp.internal.Constants.STYLE_RESOURCE_PATH;
import static org.cytoscape.work.ServiceProperties.*;

/**
 * {@code CyActivator} is a class that is a starting point for OSGi bundles.
 * <p>
 * A quick overview of OSGi: The common currency of OSGi is the <i>service</i>.
 * A service is merely a Java interface, along with objects that implement the
 * interface. OSGi establishes a system of <i>bundles</i>. Most bundles reference
 * services. Some bundles register services. Some do both. When a bundle registers a
 * service, it provides an implementation to the service's interface. Bundles
 * reference a service by asking OSGi for an implementation. The implementation is
 * provided by some other bundle.
 * <p>
 * When OSGi starts your bundle, it will invoke
 * {@link CyActivator#start(org.osgi.framework.BundleContext)} method. So, the
 * {@code start} method is where you put in all your code that sets up your app.
 * This is where you reference and register services.
 * <p>
 * Your bundle's {@code Bundle-Activator} manifest entry has a fully-qualified
 * path to this class. It's not necessary to inherit from
 * {@code AbstractCyActivator}. However, we provide this class as a convenience
 * to make it easier to work with OSGi.
 * <p>
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
        final CyNetworkViewManager cyNetworkViewManager = getService(bc, CyNetworkViewManager.class);
        final CyRootNetworkManager cyRootNetworkManager = getService(bc, CyRootNetworkManager.class);
        final CyTableManager cyTableManager = getService(bc, CyTableManager.class);
        final CyTableFactory cyTableFactory = getService(bc, CyTableFactory.class);
        final VisualMappingManager visMgr = getService(bc, VisualMappingManager.class);
        final VizmapReaderManager vizmapReaderMgr = getService(bc, VizmapReaderManager.class);
        final CyEventHelper eventHelper = getService(bc, CyEventHelper.class);
        final CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
        final CyApplicationConfiguration cyAppConfig = getService(bc, CyApplicationConfiguration.class);

        // contribute visual styles
        CyActivator.contributeStyles(visMgr, vizmapReaderMgr);

        // API implementations
        final GraphReader graphReader = new GraphReaderImpl();
        final GraphWriter graphWriter = new GraphWriterImpl();
        final BELEvidenceMapper belEvidenceMapper = new BELEvidenceMapperImpl();
        final GraphConverter graphConverter = new BELGraphConverterImpl(cyNetworkFactory, belEvidenceMapper, cyTableManager);

        // register reader
        final CyFileFilter jgfFileFilter = new JGFFileFilter(streamUtil);
        final JGFNetworkReaderFactory jgfReaderFactory = new JGFNetworkReaderFactory(
                jgfFileFilter, appMgr, cyNetworkViewFactory, cyNetworkFactory,
                cyNetworkManager, cyNetworkViewManager, cyRootNetworkManager, cyTableFactory,
                cyTableManager, visMgr, eventHelper, graphReader, graphConverter,
                belEvidenceMapper);
        final Properties jgfNetworkReaderFactoryProps = new Properties();
        jgfNetworkReaderFactoryProps.put(ID, "JGFNetworkReaderFactory");
        registerService(bc, jgfReaderFactory, InputStreamTaskFactory.class, jgfNetworkReaderFactoryProps);

        // register writer
        final JGFNetworkWriterFactory jgfWriterFactory = new JGFNetworkWriterFactory(
                jgfFileFilter, graphConverter, graphWriter);
        final Properties jgfNetworkWriterFactoryProps = new Properties();
        jgfNetworkWriterFactoryProps.put(ID, "JGFNetworkWriterFactory");
        registerAllServices(bc, jgfWriterFactory, jgfNetworkWriterFactoryProps);

        // register listeners
        final Properties evidenceProps = new Properties();
        evidenceProps.put("name", "evidence");
        EvidencePanelComponent evidencePanelComponent = new EvidencePanelComponent(belEvidenceMapper, cyTableManager, cyNetworkManager, appMgr);
        registerAllServices(bc, evidencePanelComponent, evidenceProps);
        registerAllServices(bc, new SessionListener(visMgr, vizmapReaderMgr), new Properties());

        final Properties evidenceFactoryProps = new Properties();
        evidenceFactoryProps.put(ID, "ShowEvidenceFactory");
        evidenceFactoryProps.put(PREFERRED_MENU, "Apps.JGF App");
        evidenceFactoryProps.put(MENU_GRAVITY, "14.0");
        evidenceFactoryProps.put(TITLE, "View Evidence");
        registerService(bc, new ShowEvidenceFactory(cyTableManager, cySwingApplication, evidencePanelComponent), EdgeViewTaskFactory.class, evidenceFactoryProps);

        // register about action
        AbstractCyAction aboutAction = new AbstractCyAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                About.showDialog(JGFApp.NAME, JGFApp.DESC, JGFApp.VERSION, JGFApp.ICON, JGFApp.LINKS);
            }
        };
        aboutAction.setMenuGravity(0.0f);
        aboutAction.setPreferredMenu("Apps.JGF App");
        final Properties aboutActionProps = new Properties();
        aboutActionProps.put("id", "jgfApp.about.action");
        registerService(bc, aboutAction, CyAction.class, aboutActionProps);

        // register help action
        AbstractCyAction helpAction = createBrowseHelpAction("/docs.zip", "docs/index.html", cyAppConfig, this);
        helpAction.setMenuGravity(1.0f);
        helpAction.setPreferredMenu("Apps.JGF App");
        final Properties helpActionProps = new Properties();
        helpActionProps.put("id", "jgfApp.help.action");
        registerService(bc, helpAction, CyAction.class, helpActionProps);
    }

    public static void contributeStyles(VisualMappingManager visMgr, VizmapReaderManager vizmapReaderMgr) {
        if (visMgr == null) throw new NullPointerException("visMgr cannot be null");
        if (vizmapReaderMgr == null) throw new NullPointerException("vizmapReaderMgr cannot be null");

        InputStream styleResource = CyActivator.class.getResourceAsStream(STYLE_RESOURCE_PATH);
        StyleUtility.contributeStylesIdempotently(styleResource, STYLE_RESOURCE_PATH, visMgr, vizmapReaderMgr);
    }
}
