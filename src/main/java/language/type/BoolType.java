package main.java.language.type;

import main.java.language.visitor.Visitor;

public class BoolType implements Type {
   private static BoolType ourInstance = new BoolType();

   public static BoolType getInstance() {
      return ourInstance;
   }

   private BoolType() {
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }
}

