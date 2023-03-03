package main.java.language.ast;

import main.java.language.visitor.Visitor;

import java.util.HashMap;


public abstract class NOp extends Exp {
   public HashMap<String, Exp> args;
   public int numberOfOperands;

   public NOp(HashMap<String, Exp> a) {
      numberOfOperands = a.size();
      this.args = a;
   }

   public <R> R accept(Visitor.ExpVisitor<R> v) {
          return v.visit(this);
      }

   public abstract <R> R accept(Visitor.ExpVisitor.NOpVisitors<R> v);

   @Override
   public String toString() {
      return "this is n operands";
   }

   protected abstract String opName();

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NOp nOp = (NOp) o;

      if (args.equals(nOp.args)) return true;
      return false;
   }

   @Override
   public int hashCode() {
      int result = args.hashCode();
      result = 31 * result + args.hashCode();
      return result;
   }
}


