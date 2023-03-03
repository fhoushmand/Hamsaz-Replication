package main.java.language.type;

import main.java.language.visitor.Visitor;

public class IntType implements Type {
   private static IntType ourInstance = new IntType();

   public static IntType getInstance() {
      return ourInstance;
   }

   private IntType() {
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
      return "Int";
   }

   @Override
   public boolean equals(Object obj) {
      return true;
   }
}

