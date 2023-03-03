package main.java.language.ast;


import main.java.language.visitor.Visitor;

public class Tuple extends Exp {

   final String s = "tuple";
   public Exp[] exps;
   public Tuple(Exp... args)
   {
      this.exps = args;
   }
   public <R> R accept(Visitor.ExpVisitor<R> v) { return v.visit(this); }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Tuple minus = (Tuple) o;

      return s.equals(minus.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }

   @Override
   public String toString() {
      return "tuple";
   }
}


