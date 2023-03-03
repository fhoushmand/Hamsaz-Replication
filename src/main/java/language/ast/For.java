package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class For extends Statement {

   public Var var;
   public Exp set;
   public Statement st;

   public For(Var var, Exp set, Statement st) {
      this.var = var;
      this.set = set;
      this.st = st;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "for (" + var + " in " + set + ") " + st;
   }

}

