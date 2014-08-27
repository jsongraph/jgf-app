package org.openbel.belnetwork.internal.read.jgf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openbel.belnetwork.internal.Constants.JGF_DESC;
import static org.openbel.belnetwork.internal.Constants.JGF_EXT;
import static org.openbel.belnetwork.internal.Constants.JGF_MIME;

/**
 * Custom file filter to return JGF reader for all JSON JGF files.
 */
public class JGFFileFilter extends BasicCyFileFilter {

    private static final Logger logger = LoggerFactory.getLogger(JGFFileFilter.class);

    public JGFFileFilter(StreamUtil streamUtil) {
        super(JGF_EXT, JGF_MIME, JGF_DESC, DataCategory.NETWORK, streamUtil);
    }

    @Override
    public boolean accepts(final InputStream stream, final DataCategory category) {
        final String header = getHeader(stream, 5);
        logger.debug("File header: " + header);

        return true;
    }

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