package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Fst extends UOp {
   final String s = "fst";
   public String op;
   public Fst(Exp arg) {
      super(arg);
   }

   public Fst(Exp arg, String operand)
   {
      super(arg);
      this.op = operand;
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "fst";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Fst some = (Fst) o;

      return s.equals(some.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }

   @Override
   public String toString() {
      return op;
   }
}

