package main.java.language.type;

import main.java.language.visitor.Visitor;

public class VertexType implements Type {
   private static VertexType ourInstance = new VertexType();

   public static VertexType getInstance() {
      return ourInstance;
   }

   private VertexType() {
   }
   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
      return "V";
   }
}
