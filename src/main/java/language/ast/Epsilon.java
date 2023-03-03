package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Epsilon extends ZOp {
   private static Epsilon ourInstance = new Epsilon();

   public static Epsilon getInstance() {
      return ourInstance;
   }

   private Epsilon() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "true";
   }

}
