package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class DirBoth extends ZOp {
   private static DirBoth ourInstance = new DirBoth();

   public static DirBoth getInstance() {
      return ourInstance;
   }

   private DirBoth() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "DirBoth";
   }

}
