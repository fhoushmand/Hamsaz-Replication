package main.java.language.ast;

import main.java.language.visitor.Visitor;

public class SingeltonLiteral extends ZOp {
   public Exp i;

   public SingeltonLiteral(Exp i) {
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

      SingeltonLiteral that = (SingeltonLiteral) o;

      return i == that.i;
   }

   @Override
   public int hashCode() {
      return i.hashCode();
   }
}
