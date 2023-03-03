package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Msg extends ZOp {
   private static Msg ourInstance = new Msg();

   public static Msg getInstance() {
      return ourInstance;
   }

   private Msg() {
//      super("src");
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
      return v.visit(this);
   }

   @Override
   public String toString() {
      return "msg";
   }

}
