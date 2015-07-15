package info.json_graph_format.jgfapp.api;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import info.json_graph_format.jgfapp.api.model.Graph;

import java.util.Arrays;

public class GraphsWithValidation {

    private final Graph[] graphs;
    private final ProcessingReport validationReport;

    public GraphsWithValidation(Graph[] graphs, ProcessingReport validationReport) {
        if (graphs == null)
            throw new NullPointerException("graphs cannot be null");
        if (validationReport == null)
            throw new NullPointerException("validationReport cannot be null");

        this.graphs = graphs;
        this.validationReport = validationReport;
    }

    public boolean validatedSuccessful() {
        return validationReport.isSuccess();
    }

    public Graph[] getGraphs() {
        return graphs;
    }

    public ProcessingReport getValidationReport() {
        return validationReport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(graphs);
        result = 31 * result + validationReport.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphsWithValidation that = (GraphsWithValidation) o;

        return Arrays.equals(graphs, that.graphs) && validationReport.equals(that.validationReport);
    }
}
