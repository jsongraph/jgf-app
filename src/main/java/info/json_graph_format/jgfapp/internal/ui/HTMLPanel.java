package info.json_graph_format.jgfapp.internal.ui;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public abstract class HTMLPanel extends JFXPanel {

    private String htmlContent;
    protected WebEngine webEngine;

    protected HTMLPanel(String location) {
        htmlContent = read(HTMLPanel.class.getResourceAsStream(location));
        Platform.runLater(this::createUI);
    }

    /**
     * Called when the {@link WebEngine engine} document has loaded
     * successfully. This method is executed on the JavaFX thread.
     *
     * @param webEngine {@link WebEngine}
     */
    protected abstract void onDocumentLoaded(WebEngine webEngine);

    /**
     * Executes the {@link String jsCommand} within the context of the loaded
     * document.
     *
     * @param jsCommand {@link String} javascript command (i.e. {@code create();})
     * @return the {@link Object} response from javascript
     * @throws ExecutionException
     * @throws InterruptedException
     */
    protected Object callJavascript(String jsCommand) throws ExecutionException, InterruptedException {
        FutureTask future = new FutureTask(() -> {
            return webEngine.executeScript(jsCommand);
        });
        Platform.runLater(future);
        return future.get();
    }

    protected void createUI() {
        WebView browser = new WebView();
        ZoomingPane zpane = new ZoomingPane(browser);
        Scene scene = new Scene(new BorderPane(zpane, null, null, null, null));
        webEngine = browser.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        onDocumentLoaded(webEngine);
                    }
                }
        );

        webEngine.loadContent(htmlContent);
        this.setScene(scene);
    }

    protected static String read(InputStream stream) {
        StringBuilder bldr = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = r.readLine()) != null)
                bldr.append(line).append("\n");
        } catch (IOException e) {
            return null;
        }

        return bldr.toString();
    }
}
