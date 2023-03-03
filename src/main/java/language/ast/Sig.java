package main.java.language.ast;

import main.java.language.type.Type;

import java.util.Arrays;
import java.util.HashMap;

public class Sig implements java.io.Serializable {
   public TDecl[] pars;
   public Type rType;
   public HashMap<String, Exp> argsMap;

   public Sig(TDecl[] pars, Type rType) {
      this.pars = pars;
      this.rType = rType;
      argsMap = new HashMap<>();
      for (TDecl tDecl : pars)
         argsMap.put(tDecl.name,new Var(tDecl.name));
   }

   @Override
   public String toString() {
      return Arrays.toString(pars) + " -> " + rType;
   }

   @Override
   public boolean equals(Object obj) {
      Sig sig = (Sig)obj;
      if(sig.pars.length != this.pars.length)
         return false;
      for (int i = 0; i < pars.length; i++)
      {
         if(!pars[i].type.equals(sig.pars[i].type))
            return false;
      }
      return true;
   }
}
