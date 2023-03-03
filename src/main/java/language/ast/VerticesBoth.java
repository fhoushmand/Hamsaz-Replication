package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class VerticesBoth extends UOp {
   public VerticesBoth(Exp arg) {
      super(arg);
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "VerticesBoth";
   }

}

