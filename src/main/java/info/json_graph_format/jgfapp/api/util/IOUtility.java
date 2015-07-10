package info.json_graph_format.jgfapp.api.util;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.String.format;

/**
 * {@link IOUtility} provides convenient input/output methods.
 */
public class IOUtility {

    /**
     * Extract a {@link ZipInputStream} to a {@link File directory}.
     *
     * @param zip         {@link ZipInputStream}; cannot be {@code null}
     * @param destination {@link File}; cannot be {@code null}
     * @throws java.lang.NullPointerException if {@code zip} or {@code destination}
     *                                        is {@code null}
     * @throws java.io.IOException            if {@code destination} cannot be created when
     *                                        it doesn't exist, the {@code destination} sub-directories cannot be created, the jar
     *                                        entry could not be read, or the jar entry could not be written to the destination
     *                                        directory
     */
    public static void extract(ZipInputStream zip, File destination) throws IOException {
        if (zip == null) throw new NullPointerException("zip cannot be null");
        if (destination == null)
            throw new NullPointerException("destination cannot be null");
        if (!destination.exists()) {
            if (!destination.mkdirs()) {
                throw new IOException("The destination directory could not be created.");
            }
        }

        byte[] buffer = new byte[1024];
        try {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String fileName = entry.getName();
                File newFile = new File(destination, fileName);

                if (entry.isDirectory()) {
                    if (!newFile.exists() && !newFile.mkdirs()) {
                        String fmt = "The destination directories \"%s\" could not be created.";
                        throw new IOException(format(fmt, newFile.getAbsolutePath()));
                    }
                } else {
                    File newParentDir = new File(newFile.getParent());
                    if (newParentDir.mkdirs()) {
                        String fmt = "The destination directories \"%s\" could not be created.";
                        throw new IOException(format(fmt, newParentDir.getAbsolutePath()));
                    }

                    FileOutputStream newFileOutputStream = null;
                    try {
                        newFileOutputStream = new FileOutputStream(newFile);
                        int len;
                        while ((len = zip.read(buffer)) > 0) {
                            newFileOutputStream.write(buffer, 0, len);
                        }
                    } finally {
                        if (newFileOutputStream != null)
                            newFileOutputStream.close();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Copy {@link File source directory} to {@link File destination directory}
     * recursively.
     *
     * @param source      {@link File}; cannot be {@code null}
     * @param destination {@link File}; cannot be {@code null}
     * @throws java.lang.NullPointerException     when {@code source} or {@code destination}
     *                                            is {@code null}
     * @throws java.lang.IllegalArgumentException when {@code source} is not a
     *                                            directory
     * @throws IOException                        when {@code destination} directory could not be created
     *                                            in the case that it doesn't exist
     */
    public static void copyDir(File source, File destination) throws IOException {
        if (source == null)
            throw new NullPointerException("source cannot be null");
        if (destination == null)
            throw new NullPointerException("destination cannot be null");
        if (!source.isDirectory())
            throw new IllegalArgumentException("source is not a directory");
        if (!destination.exists()) {
            if (!destination.mkdirs()) {
                throw new IOException("The destination directory could not be created.");
            }
        }

        recursiveCopyDir(source, destination);
    }

    private static void recursiveCopyDir(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                if (!destination.mkdirs()) {
                    throw new IOException("The destination directory could not be created.");
                }
            }

            String files[] = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);
                recursiveCopyDir(srcFile, destFile);
            }
        } else {
            Files.copy(source, destination);
        }
    }
}
