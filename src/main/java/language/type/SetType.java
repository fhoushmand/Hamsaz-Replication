package main.java.language.type;

import main.java.language.visitor.Visitor;

public class SetType implements Type {
   public Type tpar;

   public SetType(Type tpar) {
      this.tpar = tpar;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SetType pairType = (SetType) o;

      if (!tpar.equals(pairType.tpar)) return false;
      return true;
   }

   @Override
   public int hashCode() {
      int result = tpar.hashCode();
      return result;
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
      return "SET";
   }
}
