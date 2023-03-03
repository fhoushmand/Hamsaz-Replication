package main.java.language.ast;


import main.java.language.visitor.Visitor;

public class Max extends BOp {
   final String s = "Max";
   public Max(Exp arg1, Exp arg2) {
      super(arg1, arg2);
   }
   public <R> R accept(Visitor.ExpVisitor.BOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   protected String opName() {
      return "Max";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
//      if (!super.equals(o)) return false;

      Max max = (Max) o;
      if (!super.equals(o))
      {
         if (arg1.equals(max.arg2) && arg2.equals(max.arg1)) return true;
         else return false;
      }

      return s.equals(max.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }
}


