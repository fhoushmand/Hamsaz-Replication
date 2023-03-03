package main.java.language.type;

import main.java.language.visitor.Visitor;

public class TupleType implements Type {

   public int arity;
   public Type[] tpars;


   public TupleType(Type... types) {
      arity = types.length;
      tpars = types;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TupleType pairType = (TupleType) o;

      for (int i = 0; i < tpars.length; i++)
      {
         if(!tpars[i].equals(pairType.tpars[i]))
            return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      int result = tpars[0].hashCode();
      result = 31 * result + tpars[1].hashCode();
      return result;
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
      return "[" + tpars[0] + "]";
   }
}
