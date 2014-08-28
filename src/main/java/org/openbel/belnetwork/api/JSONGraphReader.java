package org.openbel.belnetwork.api;

import org.openbel.belnetwork.model.Graph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface JSONGraphReader {

    public Graph[] read(InputStream input) throws IOException;

    public Graph[] read(File input) throws IOException;

    public GraphsWithValidation validatingRead(InputStream input) throws IOException;

    public GraphsWithValidation validatingRead(File input) throws IOException;
}
