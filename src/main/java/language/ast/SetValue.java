package main.java.language.ast;

import main.java.language.visitor.Visitor;


public class SetValue extends Statement {

   public Exp arg1;
   public Exp arg2;

   public SetValue(Exp arg1, Exp arg2) {
      this.arg1 = arg1;
      this.arg2 = arg2;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "SetValue( " + arg1 + ", " + arg2 + ")";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SetValue signal = (SetValue) o;

      if (!arg1.equals(signal.arg1)) return false;
      return arg2.equals(signal.arg2);
   }

   @Override
   public int hashCode() {
      int result = arg1.hashCode();
      result = 31 * result + arg2.hashCode();
      return result;
   }
}

