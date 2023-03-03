package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class IntLiteral extends ZOp {
   public int i;

   public IntLiteral(int i) {
      this.i = i;
   }

   public <R> R accept(Visitor.ExpVisitor.ZOpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "" + i;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IntLiteral that = (IntLiteral) o;

      return i == that.i;
   }

   @Override
   public int hashCode() {
      return i;
   }
}
