package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Var extends ZOp {
   public String name;

   public Var(String name) {
      this.name = name;
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
//      return "var(" + name + ")";
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Var var = (Var) o;

      return name.equals(var.name);
   }

   @Override
   public int hashCode() {
      return name.hashCode();
   }
}
