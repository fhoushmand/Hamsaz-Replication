package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class ITE extends Exp {
   public Exp condExp;
   public Exp thenExp;
   public Exp elseExp;

   public ITE(Exp condExp, Exp thenExp, Exp elseExp) {
      this.condExp = condExp;
      this.thenExp = thenExp;
      this.elseExp = elseExp;
   }

   public <R> R accept(Visitor.ExpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "If " +
            "(" + condExp + ")" +
            " then " + thenExp + " else " +
            elseExp;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ITE that = (ITE) o;

      if (!condExp.equals(that.condExp)) return false;
      if (!thenExp.equals(that.thenExp)) return false;
      return elseExp.equals(that.elseExp);
   }

   @Override
   public int hashCode() {
      int result = condExp.hashCode();
      result = 31 * result + thenExp.hashCode();
      result = 31 * result + elseExp.hashCode();
      return result;
   }
}
