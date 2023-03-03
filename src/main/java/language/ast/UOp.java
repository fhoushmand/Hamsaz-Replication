package main.java.language.ast;

import main.java.language.visitor.Visitor;

public abstract class UOp extends Exp {
   public Exp arg;

   public UOp(Exp arg) {
      this.arg = arg;
   }

   public <R> R accept(Visitor.ExpVisitor<R> v) {
       return v.visit(this);
   }

   public abstract <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v);

   public String toString() {
      return opName() + "(" + arg + ")";
   }

   abstract String opName();

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UOp uOp = (UOp) o;

      return arg.equals(uOp.arg);
   }

   @Override
   public int hashCode() {
      return arg.hashCode();
   }
}
