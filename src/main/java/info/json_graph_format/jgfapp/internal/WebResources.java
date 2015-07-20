package info.json_graph_format.jgfapp.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static java.lang.String.format;

public class WebResources {

    private static final String ROOT = "webresources";
    private static final String[] RESOURCES = new String[] {
            "bootstrap-3.3.5.css",
            "bootstrap-3.3.5.js",
            "font-awesome-3.2.1.css",
            "form.html",
            "jquery-1.11.0.js",
            "list.html",
            "list-row.html",
    };

    private Path tempDirectory = null;

    public WebResources() throws IOException {
        tempDirectory = Files.createTempDirectory("JGFAPP" + ROOT);
        tempDirectory.toFile().deleteOnExit();

        for (String name : RESOURCES) {
            Path nameAbsolute = tempDirectory.resolve(name);
            Files.copy(resourceAsStream(name), nameAbsolute, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public String getHTML(String name) {
        String html = resourceAsString(name);
        return html == null ?
                null :
                html.replace("<<ROOT>>", tempDirectory.toUri().toString());
    }

    private static InputStream resourceAsStream(String name) {
        return WebResources.class.getResourceAsStream(resourcePath(name));
    }

    private static String resourceAsString(String name) {
        StringBuilder bldr = new StringBuilder();
        InputStream stream = resourceAsStream(name);
        try (BufferedReader r = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = r.readLine()) != null)
                bldr.append(line).append("\n");
        } catch (IOException e) {
            return null;
        }

        return bldr.toString();
    }

    private static String resourcePath(String name) {
        return format("/%s/%s", ROOT, name);
    }
}
