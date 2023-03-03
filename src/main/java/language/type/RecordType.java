package main.java.language.type;

import main.java.language.ast.Sig;
import main.java.language.ast.TDecl;
import main.java.language.visitor.Visitor;

public class RecordType implements Type {

   public int arity;
   public TDecl[] tDeclPars;
   public String sortName;

   public RecordType(String name, Sig sig) {
      arity = sig.pars.length;
      tDeclPars = sig.pars;
      this.sortName = name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      RecordType recordType = (RecordType) o;

      if (!tDeclPars.equals(recordType.tDeclPars)) return false;
      return true;
   }

   @Override
   public int hashCode() {
      int result = tDeclPars.hashCode();
      return result;
   }

   public <R> R accept(Visitor.TypeVisitor<R> v) {
       return v.visit(this);
   }

   @Override
   public String toString() {
      return "[#" + " record " + "#]";
   }
}
