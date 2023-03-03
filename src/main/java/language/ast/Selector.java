package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Selector extends UOp {
   final String s = "selector";
   public String op;

   public Selector(Exp arg, String field)
   {
      super(arg);
      this.op = field;
   }

   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   String opName() {
      return "selector";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Selector some = (Selector) o;

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

