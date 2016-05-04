package edu.harvard.hul.ois.fits.tools.cad;

import javax.activation.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class CadExtractor {
    protected final Set<String> extensions;
    protected final String name;

    protected CadExtractor(String name, String... extensions) {
        this.name = name;

        //Make sure every extension is lowercase and prefixed by a period
        final Set<String> temp = new HashSet<>(extensions.length);
        for(String extension: extensions) {
            extension = extension.toLowerCase();
            if (extension.charAt(0) == '.') {
                temp.add(extension);
            } else {
                temp.add("." + extension);
            }
        }
        this.extensions = Collections.unmodifiableSet(temp);
    }

    public boolean accepts(String filename) {
        filename = filename.toLowerCase();
        for(String extension: extensions) {
            if (filename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public abstract CadToolResult run(DataSource ds, String filename) throws IOException, ValidationException;

    public String getName() {
        return name;
    }

    public Set<String> getExtensions() {
        return extensions;
    }
}
