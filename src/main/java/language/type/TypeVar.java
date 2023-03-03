package main.java.language.type;

import main.java.language.visitor.Visitor;

public class TypeVar implements Type {

   public TypeVar() {
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

}

