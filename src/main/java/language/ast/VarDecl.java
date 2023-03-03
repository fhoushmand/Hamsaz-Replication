package main.java.language.ast;

import main.java.language.type.Type;

import java.io.Serializable;


public class VarDecl implements Serializable {

   public Var var;
   public Type type;

   public VarDecl(Var var, Type type) {
      this.var = var;
      this.type = type;
   }

   @Override
   public String toString() {
      return var + ":" + type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VarDecl decl = (VarDecl) o;

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

