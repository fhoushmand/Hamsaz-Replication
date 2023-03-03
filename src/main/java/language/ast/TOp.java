package main.java.language.ast;

import main.java.language.visitor.Visitor;


public abstract class TOp extends Exp {
   public Exp arg1;
   public Exp arg2;
   public Exp arg3;

   public TOp(Exp arg1, Exp arg2, Exp arg3) {
      this.arg1 = arg1;
      this.arg2 = arg2;
      this.arg3 = arg3;
   }

   public <R> R accept(Visitor.ExpVisitor<R> v) {
          return v.visit(this);
      }

   public abstract <R> R accept(Visitor.ExpVisitor.TOpVisitor<R> v);

   @Override
   public String toString() {
      return arg1 + " " + opName() + " " + arg2;
   }

   protected abstract String opName();

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TOp bOp = (TOp) o;

      if (arg1.equals(bOp.arg1)) return true;
      return false;
   }

   @Override
   public int hashCode() {
      int result = arg1.hashCode();
      result = 31 * result + arg2.hashCode();
      return result;
   }
}


