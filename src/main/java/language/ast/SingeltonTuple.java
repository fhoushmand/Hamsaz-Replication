package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class SingeltonTuple extends UOp {
   public Exp i;

   public SingeltonTuple(Exp i) {
      super(i);
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "TUPLE()";
   }

   @Override
   public String toString() {
      return "" + i;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SingeltonTuple that = (SingeltonTuple) o;

      return i == that.i;
   }

   @Override
   public int hashCode() {
      return i.hashCode();
   }
}
