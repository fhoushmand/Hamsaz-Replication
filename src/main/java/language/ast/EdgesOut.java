package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class EdgesOut extends UOp {
   public EdgesOut(Exp arg) {
      super(arg);
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "EdgesOut";
   }

}

