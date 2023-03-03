package main.java.language.ast;

import main.java.language.type.Type;
import main.java.language.visitor.Visitor;

public class SortType implements Type {

   @Override
   public <R> R accept(Visitor.TypeVisitor<R> v) {
      return v.visit(this);
   }

   public TypeDecl type;

   public SortType(TypeDecl decl) {
      type = decl;
   }


   @Override
   public String toString() {
      return "sort type: " + type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SortType decl = (SortType) o;

      if (decl.type.name.equals(this.type.name)) return true;
      return false;
   }

   @Override
   public int hashCode() {
      int result = type.hashCode();
      return result;
   }
}

