package main.java.language.ast;


import main.java.language.visitor.Visitor;

public class Store extends TOp {

   final String s = "store";
   public Store(Exp arg1, Exp arg2, Exp arg3) {
      super(arg1, arg2, arg3);
   }
   public <R> R accept(Visitor.ExpVisitor.TOpVisitor<R> v) { return v.visit(this); }

   @Override
   protected String opName() {
      return "store";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Store minus = (Store) o;

      return s.equals(minus.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }
}


