package main.java.language.ast;

import main.java.language.visitor.Visitor;

import java.util.Arrays;

public class StSeq extends Statement {

   public Statement[] sts;

   public StSeq(Statement[] sts) {
      this.sts = sts;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      for (Statement st : sts)
         s.append(st).append(";");
      return s.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StSeq stSeq = (StSeq) o;

      // Probably incorrect - comparing Object[] arrays with Arrays.equals
      return Arrays.equals(sts, stSeq.sts);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(sts);
   }
}

