package main.java.language.ast;

import main.java.language.type.Type;

import java.io.Serializable;


public class TypeDecl implements Serializable {

   public String name;
   public Type type;

   public TypeDecl(String name, Type type) {
      this.name = name;
      this.type = type;
   }


   @Override
   public String toString() {
      return name + ": TYPE = " + type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TypeDecl decl = (TypeDecl) o;

      if (!type.equals(decl.type)) return false;
      return true;
   }

   @Override
   public int hashCode() {
      int result = type.hashCode();
      return result;
   }
}

