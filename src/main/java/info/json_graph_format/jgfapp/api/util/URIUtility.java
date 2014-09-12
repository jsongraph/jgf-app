package info.json_graph_format.jgfapp.api.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * {@link URIUtility} provides convenient methods for working with {@link URI}
 * objects.
 */
public class URIUtility {

    /**
     * Open the {@link URI} in the system's browser.
     *
     * @param uri {@link URI}; cannot be {@code null}
     * @throws NullPointerException when {@code uri} is {@code null}
     * @throws IllegalStateException when {@link Desktop} is not supported or
     * does not support the {@link Desktop.Action#BROWSE} action.
     * @throws IOException when an IO error occurs browsing to {@code uri}
     */
    public static void openInBrowser(URI uri) throws IOException {
        if (uri == null) throw new NullPointerException("uri cannot be null");
        if (!Desktop.isDesktopSupported() ||
                !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
            throw new IllegalStateException("Desktop browsing is not supported.");

        Desktop.getDesktop().browse(uri);
    }

    /**
     * Return {@code true} if {@link URI} is valid, {@code false} otherwise.
     *
     * @param uri {@link String}; {@code null} yields {@code false}
     * @return {@code true} if valid {@link URI}; {@code false} otherwise
     */
    public static boolean isValid(String uri) {
        if (uri == null) return false;
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
