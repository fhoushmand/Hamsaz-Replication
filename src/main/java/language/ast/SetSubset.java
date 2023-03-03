package main.java.language.ast;


import main.java.language.visitor.Visitor;

public class SetSubset extends BOp {
   final String s = "SetSubset";
   public SetSubset(Exp arg1, Exp arg2) {
      super(arg1, arg2);
   }
   public <R> R accept(Visitor.ExpVisitor.BOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   protected String opName() {
      return "SetSubset";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
//      if (!super.equals(o)) return false;

      SetSubset arrayMin = (SetSubset) o;

      if (!super.equals(o))
      {
         if (arg1.equals(arrayMin.arg2) && arg2.equals(arrayMin.arg1)) return true;
         else return false;
      }

      return s.equals(arrayMin.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }
}


