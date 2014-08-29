package org.openbel.belnetwork.api.util;

import org.cytoscape.io.read.VizmapReader;
import org.cytoscape.io.read.VizmapReaderManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.lang.String.format;

/**
 * {@link StyleUtility} provides convenience methods for working with
 * {@link VisualStyle visual styles} in Cytoscape.
 */
public class StyleUtility {

    /**
     * Contributes {@link VisualStyle visual styles} to Cytoscape from the
     * {@link InputStream styleResource}. The styles are removed first to prevent
     * duplicates and allow for this action to be idempotent.
     *
     * @param styleResource {@link InputStream} the stream which styles are loaded
     * from; cannot be {@code null}
     * @param styleResourceName {@link String} the resource name; cannot be
     * {@code null}
     * @param visMgr {@link VisualMappingManager} the Cytoscape service that is used
     * to view and edit currently loaded styles; cannot be {@code null}
     * @param vizmapReaderMgr {@link VizmapReaderManager} the Cytoscape service that
     * can read {@link VisualStyle visual styles} from a vizmap file; cannot be
     * {@code null}
     */
    public static void contributeStylesIdempotently(InputStream styleResource,
                                        String styleResourceName,
                                        VisualMappingManager visMgr,
                                        VizmapReaderManager vizmapReaderMgr) {
        if (styleResource == null)
            throw new NullPointerException("styleResource cannot be null");
        if (styleResourceName == null)
            throw new NullPointerException("styleResourceName cannot be null");
        if (visMgr == null)
            throw new NullPointerException("visMgr cannot be null");
        if (vizmapReaderMgr == null)
            throw new NullPointerException("vizmapReaderMgr cannot be null");

        // read vizmap
        VizmapReader r = vizmapReaderMgr.getReader(styleResource, styleResourceName);
        try {
            r.run(new NoOpTaskMonitor());
        } catch (Exception e) {
            throw new VizmapReadException(styleResourceName, e);
        }

        // collect visual style titles from resource
        Set<String> contributedStyleTitles = new HashSet<String>();
        for (VisualStyle vs : r.getVisualStyles()) {
            contributedStyleTitles.add(vs.getTitle());
        }

        // remove existing styles having those titles
        Iterator<VisualStyle> styleIt = visMgr.getAllVisualStyles().iterator();
        while(styleIt.hasNext()) {
            VisualStyle style = styleIt.next();
            if (contributedStyleTitles.contains(style.getTitle()))
                styleIt.remove();
        }

        // add visual styles from resource
        for (VisualStyle vs : r.getVisualStyles())
            visMgr.addVisualStyle(vs);
    }

    /**
     * Return the {@link VisualStyle}, identified by {@code title}, or {@code null}
     * if it does not exist in {@link VisualMappingManager}.
     *
     * @param title {@link String} title; cannot be {@code null}
     * @param mgr {@link VisualMappingManager} Cytoscape service; cannot be {@code null}
     * @return the {@link VisualStyle} or {@code null} if it does not exist
     */
    public static VisualStyle findVisualStyleByTitle(String title, VisualMappingManager mgr) {
        if (mgr == null) throw new NullPointerException("mgr cannot be null");
        if (title == null) throw new NullPointerException("title cannot be null");
        for (VisualStyle vs : mgr.getAllVisualStyles()) {
            if (vs.getTitle().equals(title))
                return vs;
        }
        return null;
    }

    static final class VizmapReadException extends RuntimeException {
        public VizmapReadException(String resourceName, Exception cause) {
            super(format("Unable to read vizmap from '%s'", resourceName), cause);
        }
    }

    private static final class NoOpTaskMonitor implements TaskMonitor {
        @Override
        public void setTitle(String s) {}

        @Override
        public void setProgress(double v) {}

        @Override
        public void setStatusMessage(String s) {}

        @Override
        public void showMessage(Level level, String s) {}
    }

    private StyleUtility() {
        // static accessors only
    }
}
