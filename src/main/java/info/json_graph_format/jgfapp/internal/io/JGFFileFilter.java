package info.json_graph_format.jgfapp.internal.io;

import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Custom file filter to return JGF reader for all JSON JGF files.
 * <br><br>
 * FIXME
 * Requires a <em>.jgf</em> extension and cannot support the <em>.json</em>
 * extension due to <a href="http://code.cytoscape.org/redmine/issues/2391">
 * http://code.cytoscape.org/redmine/issues/2391</a>
 */
public class JGFFileFilter extends BasicCyFileFilter {

    private static final Logger logger = LoggerFactory.getLogger(JGFFileFilter.class);

    public JGFFileFilter(StreamUtil streamUtil) {
        super(new String[]{"jgf"},
                new String[]{"application/jgf+json"},
                "JSON Graph Format",
                DataCategory.NETWORK, streamUtil);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(final InputStream stream, final DataCategory category) {
        final String header = getHeader(stream, 5);
        logger.debug("File header: " + header);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(final URI uri, final DataCategory category) {
        try {
            return accepts(uri.toURL().openStream(), category);
        } catch (IOException e) {
            logger.error("Error while opening stream: " + uri, e);
            return false;
        }
    }
}
