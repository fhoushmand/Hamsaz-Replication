package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Src extends ZOp {
   private static Src ourInstance = new Src();

   public static Src getInstance() {
      return ourInstance;
   }

   private Src() {
//      super("src");
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "src";
   }

}
