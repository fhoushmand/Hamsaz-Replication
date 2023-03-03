package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class CapOfEdge extends UOp {
   final String s = "cedge";
   public CapOfEdge(Exp arg) {
      super(arg);
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "CapOfEdge";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      CapOfEdge some = (CapOfEdge) o;

      return s.equals(some.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }
}

