package main.java.utils.compiler.texttree.seq;

import main.java.utils.compiler.texttree.*;


import java.util.LinkedList;

public class TextSeq {
    LinkedList<Text> list = new LinkedList<Text>();

    private IntHolder indentCount;
    //private VisitDispatch
    // er visitorDispatcher;

    public TextSeq(IntHolder indentCount /*VisitDispatcher visitorDispatcher*/) {
      this.indentCount = indentCount;
        //this.visitorDispatcher = visitorDispatcher;}
    }

    public TextSeq(int indentCount) {
      this.indentCount = new IntHolder(indentCount);
        //this.visitorDispatcher = visitorDispatcher;}
    }

    public TextSeq() {
        indentCount = new IntHolder(0);
    }

    public void incIndent() {
        indentCount.i = indentCount.i + 1;
    }
    public void decIndent() {
        indentCount.i = indentCount.i - 1;
    }
    public void add(Text node) {
        list.add(node);
    }
    public void add(String snippet) {
        add(new Snippet(snippet));
    }

    public void goToNextLine() {
        add(NewLine.instance());
        atBeg = true;
    }
    public void indent() {
        for (int i = 0; i < indentCount.i; i++)
              add(Indent.instance());
        atBeg = false;
    }

    public int indentCount() {
        return indentCount.i;
    }

    // To use:

    private boolean atBeg = true;

    public boolean atBeg() {
        return atBeg;
    }

    public void startLine() {
        if (!atBeg)
            goToNextLine();
        indent();
        atBeg = false;
    }
    public void startLine(String s) {
        if (!atBeg)
            goToNextLine();
        indent();
        put(s);
        atBeg = false;
    }
    public void startLine(Text text) {
        if (!atBeg)
            goToNextLine();
        indent();
        put(text);
        atBeg = false;
    }
    public void startLine(Object textNode) {
        if (!atBeg)
            goToNextLine();
        if (textNode instanceof Text)
            startLine((Text)textNode);
        else
            throw new RuntimeException();
        atBeg = false;
    }


    public void put(Object node) {
        if (atBeg)
            startLine();
        if (node instanceof String)
            put((String)node);
        else if (node instanceof Text)
            list.add((Text)node);
        else
            throw new RuntimeException();
        atBeg = false;
    }

    public void put(Text node) {
        if (atBeg)
            startLine();
        list.add(node);
        atBeg = false;
    }

    public void put(String s) {
        if (atBeg)
            startLine();
        add(s);
        atBeg = false;
    }

    public void print(Text text) {
        if (atBeg)
            startLine();
        add(text);
        atBeg = false;
    }
    public void print(String s) {
        if (atBeg)
            startLine();
        add(s);
        atBeg = false;
    }

    public void endLine(String s) {
        put(s);
        goToNextLine();
        atBeg = true;
    }
    public void endLine(Text t) {
        put(t);
        goToNextLine();
        atBeg = true;
    }
    public void endLine() {
        goToNextLine();
        atBeg = true;
    }
    public void newLine() {
        goToNextLine();
        indent();
        atBeg = false;
    }
    public void skipLine() {
        goToNextLine();
        atBeg = true;
    }
    public void fullLine(String s) {
        startLine();
        put(s);
        endLine();
    }

    public void println(String s) {
        endLine(s);
    }
    public void println(Text t) {
        endLine(t);
    }

    public void println() {
        goToNextLine();
    }

    public void putParList(Text[] nodes) {
        put("(");
        putCommaList(nodes);
        put(")");
    }

    public void putParList(String[] strings) {
        put("(");
        putCommaList(strings);
        put(")");
    }


    public void putBracketList(Text[] nodes) {
        put("[");
        putCommaList(nodes);
        put("]");
    }

    public void putBracketList(String[] strings) {
        put("[");
        putCommaList(strings);
        put("]");
    }


    public void putCommaList(Text[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            put(nodes[i]);
            if (i != nodes.length - 1)
                put(", ");
        }
    }

    public void putCommaList(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            put(strings[i]);
            if (i != strings.length - 1)
                put(", ");
        }
    }

    public Interior get() {
        Text[] nodes = new Text[list.size()];
        int i = 0;
        for (Text text : list) {
            nodes[i] = text;
            i++;
        }
        return new Interior(nodes);
    }

    /*
    public void indentedText(String s) {
        indent();
        add(s);
    }

    public void incIndentAndLine(String s) {
        incIndent();
        indentedText(s);
    }
    public void decIndentAndLine(String s) {
        decIndent();
        indentedText(s);
    }
    */

//    private TextNode visitDispatch(Node node) {
//        return visitorDispatcher.visitDispatch(node);
//    }

}
