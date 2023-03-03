package main.java.utils.compiler.texttree;

import java.io.Writer;
import java.io.IOException;

/**
 * User: lesani, Date: 9-Nov-2009, Time: 7:46:42 PM
 */
public class Snippet extends Text {
    public String text;

    public Snippet(String s) {
        if (s == null)
            throw new RuntimeException("Snippet: null passed instead of text");
        this.text = s;
    }

    public String print() {
        return text;
    }

    public void print(Writer writer) throws IOException {
        writer.write(text);
    }
}
