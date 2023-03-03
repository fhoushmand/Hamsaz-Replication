package main.java.language.ast;

import main.java.language.type.Type;
import main.java.language.visitor.Visitor;



public class Decl extends Statement {

   public Var var;
   public Type type;

   public Decl(Var var, Type type) {
      this.var = var;
      this.type = type;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return var + ":" + type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Decl decl = (Decl) o;

      if (!type.equals(decl.type)) return false;
      return var.equals(decl.var);
   }

   @Override
   public int hashCode() {
      int result = type.hashCode();
      result = 31 * result + var.hashCode();
      return result;
   }
}

