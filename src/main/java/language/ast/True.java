package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class True extends ZOp {
   private static True ourInstance = new True();

   public static True getInstance() {
      return ourInstance;
   }

   private True() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "true";
   }

}
