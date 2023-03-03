package main.java.language.type;

import main.java.language.visitor.Visitor;

public class DirType implements Type {
   private static DirType ourInstance = new DirType();

   public static DirType getInstance() {
      return ourInstance;
   }

   private DirType() {
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }
}

