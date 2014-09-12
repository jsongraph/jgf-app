package info.json_graph_format.jgfapp.api.util;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.service.util.AbstractCyActivator;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import static info.json_graph_format.jgfapp.api.util.IOUtility.extract;
import static info.json_graph_format.jgfapp.api.util.URIUtility.openInBrowser;
import static java.lang.String.format;

/**
 * {@link HelpUtility} provides convenient access and hooks for help documentation.
 */
public class HelpUtility {

    /**
     * Create a {@link AbstractCyAction cytoscape action} for opening a link to the
     * {@code indexPath} of HTML documentation in {@code helpZipPath}.
     * <br><br>
     * The {@code helpZipPath} is extracted into the cytoscape configuration path of
     * the {@link AbstractCyActivator appClass}. The {@code helpZipPath} is searched
     * in the classpath.
     *
     * @param helpZipPath {@link String} resource path to zip help file; cannot be
     * {@code null}
     * @param indexPath {@link String} zip relative path that identifies the index page
     * to browse to; cannot be {@code null}
     * @param appConfig {@link CyApplicationConfiguration}; cannot be {@code null}
     * @param appClass {@link AbstractCyActivator}; cannot be {@code null}
     * @return the {@link AbstractCyAction} to launch {@code indexPath} in browser
     * @throws NullPointerException when {@code helpZipPath}, {@code indexPath},
     * {@code appConfig}, or {@code appClass} is {@code null}
     * @throws FileNotFoundException when {@code helpZipPath} does not exist or the
     * application configuration path does not exist
     * @throws IOException when directories cannot be created as needed
     */
    public static AbstractCyAction createBrowseHelpAction(final String helpZipPath,
                                                          final String indexPath,
                                                          final CyApplicationConfiguration appConfig,
                                                          final AbstractCyActivator appClass)
            throws IOException {

        // check help path in classpath resources
        if (helpZipPath == null)
            throw new NullPointerException("helpZipPath cannot be null");
        if (appClass == null)
            throw new NullPointerException("appClass cannot be null");
        InputStream is = appClass.getClass().getResourceAsStream(helpZipPath);
        if (is == null) {
            String fmt = "The help zip resource \"%s\" does not exist.";
            throw new FileNotFoundException(format(fmt, helpZipPath));
        }

        // check application configuration
        if (appConfig == null)
            throw new NullPointerException("appConfig cannot be null");
        File appDir = appConfig.getAppConfigurationDirectoryLocation(appClass.getClass());
        if (appDir == null)
            throw new FileNotFoundException("The application configuration directory does not exist.");
        if (!appDir.isDirectory()) {
            if (!appDir.mkdirs()) {
                String fmt = "The application configuration path \"%s\" cannot be created.";
                throw new IOException(format(fmt, appDir.getAbsolutePath()));
            }
        }

        final File appDocsDir = new File(appDir, "help");
        ZipInputStream zip = null;
        try {
            zip = new ZipInputStream(is);
            extract(zip, appDocsDir);
        } finally {
            if (zip != null) zip.close();
        }

        return new AbstractCyAction("Help Documentation") {

            @Override
            public void actionPerformed(ActionEvent event) {
                File helpIndex = new File(appDocsDir, indexPath);
                if (!helpIndex.exists() || !helpIndex.canRead()) {
                    String fmt = "The help index \"%s\" cannot be read.";
                    throw new RuntimeException(format(fmt, helpIndex.getAbsolutePath()));
                }
                try {
                    openInBrowser(helpIndex.toURI());
                } catch (IOException e) {
                    String fmt = "The help index \"%s\" cannot be opened in a browser.";
                    throw new RuntimeException(format(fmt, helpIndex.getAbsolutePath()), e);
                }
            }
        };
    }

    private HelpUtility() {
        // static accessors only
    }
}
