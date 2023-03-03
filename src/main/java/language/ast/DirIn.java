package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class DirIn extends ZOp {
   private static DirIn ourInstance = new DirIn();

   public static DirIn getInstance() {
      return ourInstance;
   }

   private DirIn() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "DirIn";
   }

}
