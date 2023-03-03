package main.java.language.ast;

import main.java.language.visitor.Visitor;

/*
match b with
| some bv ⇒
   exp
| none ⇒
   exp
end
*/

public class MatchOExp extends Exp {
   public Exp matchExp;
   public Var someVar;
   public Exp someExp;
   public Exp noneExp;

   public MatchOExp(Exp matchExp, Var someVar, Exp someExp, Exp noneExp) {
      this.matchExp = matchExp;
      this.someVar = someVar;
      this.someExp = someExp;
      this.noneExp = noneExp;
   }

   public <R> R accept(Visitor.ExpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "OMatch{" +
            "matchVar=" + matchExp +
            ", someVar=" + someVar +
            ", someExp=" + someExp +
            ", noneExp=" + noneExp +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MatchOExp oMatch = (MatchOExp) o;

      if (!matchExp.equals(oMatch.matchExp)) return false;
      if (!someVar.equals(oMatch.someVar)) return false;
      if (!someExp.equals(oMatch.someExp)) return false;
      return noneExp.equals(oMatch.noneExp);
   }

   @Override
   public int hashCode() {
      int result = matchExp.hashCode();
      result = 31 * result + someVar.hashCode();
      result = 31 * result + someExp.hashCode();
      result = 31 * result + noneExp.hashCode();
      return result;
   }
}
