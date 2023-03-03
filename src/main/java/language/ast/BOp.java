package main.java.language.ast;

import main.java.language.visitor.Visitor;


public abstract class BOp extends Exp {
   public Exp arg1;
   public Exp arg2;

   public BOp(Exp arg1, Exp arg2) {
      this.arg1 = arg1;
      this.arg2 = arg2;
   }

   public <R> R accept(Visitor.ExpVisitor<R> v) {
          return v.visit(this);
      }

   public abstract <R> R accept(Visitor.ExpVisitor.BOpVisitor<R> v);

   @Override
   public String toString() {
      return arg1 + " " + opName() + " " + arg2;
   }

   protected abstract String opName();

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BOp bOp = (BOp) o;

      if (!arg1.equals(bOp.arg1)) return false;
      return arg2.equals(bOp.arg2);
   }

   @Override
   public int hashCode() {
      int result = arg1.hashCode();
      result = 31 * result + arg2.hashCode();
      return result;
   }
}


