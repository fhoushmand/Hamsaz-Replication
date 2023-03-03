package main.java.language.ast;

import main.java.language.visitor.CVCIPrinter;
import main.java.language.visitor.Visitor;

import java.util.Arrays;
import java.util.HashMap;

public class Call extends Exp {

   public String funName;
   public Fun fun;
   public Exp[] args;
   public HashMap<String, String> argsMap = new HashMap<>();

   public Call(Fun f, Exp[] args) {
      this.fun = f;
      this.funName = f.name;
      this.args = args;
      int i = 0;
      for (TDecl tDecl : fun.sig.pars)
      {
         argsMap.put(tDecl.name, CVCIPrinter.print(args[i]));
         i++;
      }
   }

   public <R> R accept(Visitor.ExpVisitor<R> v) {
          return v.visit(this);
      }

   @Override
   public String toString() {
      return funName + "(" + Arrays.toString(args) + ')'; }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Call call = (Call) o;

      if (!funName.equals(call.funName)) return false;
      // Probably incorrect - comparing Object[] arrays with Arrays.equals
      return Arrays.equals(args, call.args);
   }

   @Override
   public int hashCode() {
      int result = funName.hashCode();
      result = 31 * result + Arrays.hashCode(args);
      return result;
   }
}
