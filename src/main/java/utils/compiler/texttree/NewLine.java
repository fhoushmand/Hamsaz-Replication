/**
 * User: lesani, Date: 9-Nov-2009, Time: 7:54:29 PM
 */
package main.java.utils.compiler.texttree;

import java.io.Writer;
import java.io.IOException;

public class NewLine extends Text {
    private static NewLine theInstance = new NewLine();

    public static NewLine instance() {
        return theInstance;
    }

    private NewLine() {
    }

    public String print() {
        return "\n";
    }

    public void print(Writer writer) throws IOException {
        writer.write("\n");
    }
}
