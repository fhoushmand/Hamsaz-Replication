package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class VerticesIn extends UOp {
   public VerticesIn(Exp arg) {
      super(arg);
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "VerticesIn";
   }

}

