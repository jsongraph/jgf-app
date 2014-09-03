package info.json_graph_format.jgfapp.api;

import info.json_graph_format.jgfapp.api.model.Graph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link BELGraphReader} reads <em>JSON Graph Format</em> content into object form.
 * The input content is expected to be <em>JSON Graph Format</em> that adheres to
 * the BEL JSON Graph child schema.
 */
public interface BELGraphReader {

    /**
     * Returns an array of {@link Graph} from a <em>JSON Graph Format</em>-encoded
     * {@link InputStream}. The array may contain 0, 1, or more {@link Graph} objects.
     *
     * @param input the <em>JSON Graph Format</em>-encoded {@link InputStream};
     * cannot be {@code null}
     * @return the array of {@link Graph}; will not be {@code null} and may contain
     * 0, 1, or more {@link Graph} objects
     * @throws IOException when an IO error occurs reading from {@code input}
     * @throws java.lang.NullPointerException when {@code input} is {@code null}
     */
    public Graph[] read(InputStream input) throws IOException;

    /**
     * Returns an array of {@link Graph} from a <em>JSON Graph Format</em>-encoded
     * {@link File}. The array may contain 0, 1, or more {@link Graph} objects.
     *
     * @param input the <em>JSON Graph Format</em>-encoded {@link File};
     * cannot be {@code null}
     * @return the array of {@link Graph}; will not be {@code null} and may contain
     * 0, 1, or more {@link Graph} objects
     * @throws IOException when an IO error occurs reading from {@code input}
     * @throws java.lang.NullPointerException when {@code input} is {@code null}
     * @throws java.io.FileNotFoundException when {@code input} does not exist or
     * cannot be read
     */
    public Graph[] read(File input) throws IOException;

    /**
     * Returns a {@link GraphsWithValidation} object from a
     * <em>JSON Graph Format</em>-encoded {@link InputStream}. The {@code input} is
     * first validated against the BEL JSON Graph child schema.
     * <br><br>
     * If validation was successful (e.g. no JSON schema errors) then the returned
     * array may contain 0, 1, or more {@link Graph} objects. If validation is not
     * successful (e.g. JSON schema errors occurred) then the returned array will be
     * {@code non-null} and empty. Schema validation errors can be obtained from
     * {@link GraphsWithValidation}.
     *
     * @param input the <em>JSON Graph Format</em>-encoded {@link InputStream};
     * cannot be {@code null}
     * @return a {@link GraphsWithValidation} object providing an array of
     * {@link Graph} and schema validation errors; will not be {@code null}
     * @throws IOException when an IO error occurs reading from {@code input}
     * @throws java.lang.NullPointerException when {@code input} is {@code null}
     */
    public GraphsWithValidation validatingRead(InputStream input) throws IOException;

    /**
     * Returns a {@link GraphsWithValidation} object from a
     * <em>JSON Graph Format</em>-encoded {@link File}. The {@code input} is
     * first validated against the BEL JSON Graph child schema.
     * <br><br>
     * If validation was successful (e.g. no JSON schema errors) then the returned
     * array may contain 0, 1, or more {@link Graph} objects. If validation is not
     * successful (e.g. JSON schema errors occurred) then the returned array will be
     * {@code non-null} and empty. Schema validation errors can be obtained from
     * {@link GraphsWithValidation}.
     *
     * @param input the <em>JSON Graph Format</em>-encoded {@link File};
     * cannot be {@code null}
     * @return a {@link GraphsWithValidation} object providing an array of
     * {@link Graph} and schema validation errors; will not be {@code null}
     * @throws IOException when an IO error occurs reading from {@code input}
     * @throws java.lang.NullPointerException when {@code input} is {@code null}
     * @throws java.io.FileNotFoundException when {@code input} does not exist or
     * cannot be read
     */
    public GraphsWithValidation validatingRead(File input) throws IOException;
}
