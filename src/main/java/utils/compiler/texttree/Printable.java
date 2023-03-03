package main.java.utils.compiler.texttree;

import java.io.Writer;
import java.io.IOException;

/**
 * Mohsen, Date: Nov 14, 2009, Time: 1:19:30 AM
 */
public interface Printable {
    public abstract void print(Writer writer) throws IOException;
}
