package main.java.language.type;

import main.java.language.visitor.Visitor;

public class OptionType implements Type {
   public Type tpar;

   public OptionType(Type tpar) {
      this.tpar = tpar;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OptionType that = (OptionType) o;

      return tpar.equals(that.tpar);
   }

   @Override
   public int hashCode() {
      return tpar.hashCode();
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
      return "Option[" + tpar + "]";
   }
}
