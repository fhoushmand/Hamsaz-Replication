package main.java.language.type;

import main.java.language.visitor.Visitor;

public class PairType implements Type {
   public Type tpar1;
   public Type tpar2;

   public PairType(Type tpar1, Type tpar2) {
      this.tpar1 = tpar1;
      this.tpar2 = tpar2;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PairType pairType = (PairType) o;

      if (!tpar1.equals(pairType.tpar1)) return false;
      return tpar2.equals(pairType.tpar2);
   }

   @Override
   public int hashCode() {
      int result = tpar1.hashCode();
      result = 31 * result + tpar2.hashCode();
      return result;
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
      return "<" + tpar1 + ", " + tpar2 + ">";
   }
}
