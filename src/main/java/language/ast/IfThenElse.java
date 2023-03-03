package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class IfThenElse extends Statement {

   public Exp cond;
   public Statement ifSt;
   public Statement elseSt;

   public IfThenElse(Exp cond, Statement ifSt, Statement elseSt) {
      this.cond = cond;
      this.ifSt = ifSt;
      this.elseSt = elseSt;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "If" +
            "(" + cond + ") " +
            ifSt +
            " else " +
            elseSt;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IfThenElse that = (IfThenElse) o;

      if (!cond.equals(that.cond)) return false;
      if (!ifSt.equals(that.ifSt)) return false;
      return elseSt.equals(that.elseSt);
   }

   @Override
   public int hashCode() {
      int result = cond.hashCode();
      result = 31 * result + ifSt.hashCode();
      result = 31 * result + elseSt.hashCode();
      return result;
   }
}

