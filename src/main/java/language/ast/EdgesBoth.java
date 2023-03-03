package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class EdgesBoth extends UOp {
   public EdgesBoth(Exp arg) {
      super(arg);
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "EdgesBoth";
   }

}

