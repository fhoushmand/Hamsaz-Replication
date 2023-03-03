/**
 * User: lesani, Date: 9-Nov-2009, Time: 9:10:46 PM
 */
package main.java.utils.compiler.texttree;

import java.io.Writer;
import java.io.IOException;

public class Indent extends Text {
    private static Indent ourInstance = new Indent();

    public static Indent instance() {
        return ourInstance;
    }

    private Indent() {
    }

    public String print() {
        return "\t";
    }

    public void print(Writer writer) throws IOException {
        writer.write("\t");
    }
    
}
