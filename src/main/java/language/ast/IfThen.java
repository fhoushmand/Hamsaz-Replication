package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class IfThen extends Statement {

   public Exp cond;
   public Statement ifSt;

   public IfThen(Exp cond, Statement ifSt) {
      this.cond = cond;
      this.ifSt = ifSt;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "If" +
            "(" + cond + ") " +
            ifSt;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IfThen ifThen = (IfThen) o;

      if (!cond.equals(ifThen.cond)) return false;
      return ifSt.equals(ifThen.ifSt);
   }

   @Override
   public int hashCode() {
      int result = cond.hashCode();
      result = 31 * result + ifSt.hashCode();
      return result;
   }
}