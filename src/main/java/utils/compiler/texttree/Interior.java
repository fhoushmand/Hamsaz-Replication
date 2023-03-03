package main.java.utils.compiler.texttree;


import java.io.Writer;
import java.io.IOException;

/**
 * User: lesani, Date: 9-Nov-2009, Time: 7:46:36 PM
 */
public class Interior extends Text {
   Text[] children;

   public Interior(Text... nodes) {
      children = nodes;
//        nodes = new TextNode[2 * nodes.length + 2];
//        for (int i = 0; i < nodes.length; i++) {
//            children[2 * i] = nodes[i];
//            children[2 * i + 1] = NewLine.instance();
//        }
   }

   public String print() {
      String s = "";
      for (Text child : children) {
         s += child.print();
      }
      return s;
   }

   public void print(Writer writer) throws IOException {
      for (Text child : children) {
         if (child == null)
            writer.write("<!>");
         else
            child.print(writer);
      }
   }

}
