package main.java.language.ast;

import main.java.language.type.Type;
import main.java.language.visitor.Visitor;

public class None extends ZOp {
//   private static None ourInstance = new None();

//   public static None getInstance() {
//      return ourInstance;
//   }

   public Type type;
   public None() {
   }

   public None(Type type) {
      this.type = type;
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "none";
   }
}
