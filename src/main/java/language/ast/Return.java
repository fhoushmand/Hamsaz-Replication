package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class Return extends Statement {

   public Exp arg;

   public Return(Exp arg) {
      this.arg = arg;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "Return " + arg;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Return aReturn = (Return) o;

      return arg.equals(aReturn.arg);
   }

   @Override
   public int hashCode() {
      return arg.hashCode();
   }
}

