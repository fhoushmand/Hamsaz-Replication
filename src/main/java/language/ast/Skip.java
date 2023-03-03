package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Skip extends Statement {
   private static Skip ourInstance = new Skip();

   public static Skip getInstance() {
      return ourInstance;
   }

   public Skip() {
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return ";";
   }

}

