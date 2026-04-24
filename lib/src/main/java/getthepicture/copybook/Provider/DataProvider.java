package getthepicture.copybook.Provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import getthepicture.copybook.compiler.CbCompiler;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;
import getthepicture.copybook.resolver.CbResolver;
import getthepicture.copybook.resolver.storage.CbStorage;

public class DataProvider {
    private final CbLayout layout;
    private final CbStorage storage;

    /**
     * Initializes a new instance of {@link DataProvider} using a {@link BufferedReader} that reads a COBOL Copybook.
     *
     * @param reader for the Copybook file (e.g., .cpy or .cbl).
     * @throws IOException if the file cannot be opened or read
     */
    public DataProvider(BufferedReader reader) throws IOException {
        this.layout  = CbCompiler.fromReader(reader);
        this.storage = CbResolver.fromLayout(this.layout);
    }

    /**
     * Initializes a new instance of {@link DataProvider} using a file path and charset.
     *
     * @param fileName the path to the Copybook file
     * @param charset  the character encoding of the file
     * @throws IOException if the file cannot be opened or read
     */
    public DataProvider(String fileName, Charset charset) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName), charset)) {
            this.layout  = CbCompiler.fromReader(reader);
            this.storage = CbResolver.fromLayout(this.layout);
        }
    }

    /**
     * Gets the {@link CbLayout} object representing the structure of the Copybook.
     *
     * @return the {@link CbLayout} instance
     */
    public CbLayout getLayout() {
        return layout;
    }

    /**
     * Gets the {@link CbStorage} object representing the resolved storage map of the Copybook.
     *
     * @return the {@link CbStorage} instance
     */
    public CbStorage getStorage() {
        return storage;
    }
}
