package main.java.language.ast;


import main.java.language.visitor.Visitor;

public class SetCardinality extends UOp {
   final String s = "SetMinus";
   public SetCardinality(Exp arg1) {
      super(arg1);
   }
   public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   protected String opName() {
      return "SetMinus";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
//      if (!super.equals(o)) return false;

      SetCardinality setCardinality = (SetCardinality) o;

      if (!super.equals(o))
      {
         if (arg.equals(setCardinality.arg)) return true;
         else return false;
      }

      return s.equals(setCardinality.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }
}


