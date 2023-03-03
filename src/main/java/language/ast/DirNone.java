package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class DirNone extends ZOp {
   private static DirNone ourInstance = new DirNone();

   public static DirNone getInstance() {
      return ourInstance;
   }

   private DirNone() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "DirNone";
   }

}
