package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class DirOut extends ZOp {
   private static DirOut ourInstance = new DirOut();

   public static DirOut getInstance() {
      return ourInstance;
   }

   private DirOut() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "DirOut";
   }

}
