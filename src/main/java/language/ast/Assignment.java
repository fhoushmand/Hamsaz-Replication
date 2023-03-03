package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Assignment extends Statement {

//   public Var var;
   public Exp var;
   public Exp exp;

   public Assignment(Exp var, Exp exp) {
      this.var = var;
      this.exp = exp;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return var + ":= " + exp;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Assignment that = (Assignment) o;

      if (!var.equals(that.var)) return false;
      return exp.equals(that.exp);
   }

   @Override
   public int hashCode() {
      int result = var.hashCode();
      result = 31 * result + exp.hashCode();
      return result;
   }
}

