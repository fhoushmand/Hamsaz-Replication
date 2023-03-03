package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Vertices extends ZOp {
   private static Vertices ourInstance = new Vertices();

   public static Vertices getInstance() {
      return ourInstance;
   }

   private Vertices() {
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "vs";
   }
   
}
