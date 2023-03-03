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

public class MatchOSt extends Statement {
   public Exp matchExp;
   public Var someVar;
   public Statement someSt;
   public Statement noneSt;


   public MatchOSt(Exp matchExp, Var someVar, Statement someSt, Statement noneSt) {
      this.matchExp = matchExp;
      this.someVar = someVar;
      this.someSt = someSt;
      this.noneSt = noneSt;
   }

   public <R> R accept(Visitor.StVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return "MatchOSt{" +
            "matchVar=" + matchExp +
            ", someVar=" + someVar +
            ", someSt=" + someSt +
            ", noneSt=" + noneSt +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MatchOSt matchOSt = (MatchOSt) o;

      if (!matchExp.equals(matchOSt.matchExp)) return false;
      if (!someVar.equals(matchOSt.someVar)) return false;
      if (!someSt.equals(matchOSt.someSt)) return false;
      return noneSt.equals(matchOSt.noneSt);
   }

   @Override
   public int hashCode() {
      int result = matchExp.hashCode();
      result = 31 * result + someVar.hashCode();
      result = 31 * result + someSt.hashCode();
      result = 31 * result + noneSt.hashCode();
      return result;
   }
}
