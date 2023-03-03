package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class False extends ZOp {
   private static False ourInstance = new False();

   public static False getInstance() {
      return ourInstance;
   }

   private False() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "true";
   }

}
