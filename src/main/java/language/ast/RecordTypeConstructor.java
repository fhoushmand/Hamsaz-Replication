package main.java.language.ast;


import main.java.language.type.RecordType;
import main.java.language.visitor.Visitor;

import java.util.HashMap;

public class RecordTypeConstructor extends NOp {

   final String s = "payload";
   public RecordType refType;
   public RecordTypeConstructor(RecordType rType, HashMap<String, Exp> args)
   {
      super(args);
      refType = rType;
      for (TDecl tDecl : rType.tDeclPars)
      {
//         if(!(args.containsKey(tDecl.name)))
//         {
//            throw new RuntimeException("cannot instantiate record type, field names don't match...");
//         }
      }

   }
   public <R> R accept(Visitor.ExpVisitor.NOpVisitors<R> v) { return v.visit(this); }

   @Override
   protected String opName() {
      return "";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      RecordTypeConstructor minus = (RecordTypeConstructor) o;

      return s.equals(minus.s);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + s.hashCode();
      return result;
   }
}


