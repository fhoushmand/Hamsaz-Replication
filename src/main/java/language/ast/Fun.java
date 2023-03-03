package main.java.language.ast;


import main.java.language.visitor.CVCIPrinter;

import java.util.HashMap;

public class Fun implements java.io.Serializable {
   public String name;
   public Sig sig;
   public Statement body;
   public Fun gaurd;
   public Fun locaGaurd;
   public Exp preCondition;

   public Fun(String name, Sig sig, Statement body) {
      this.name = name;
      this.sig = sig;
      this.body = body;
   }

   public Fun(String name, Sig sig, Statement body, Fun g) {
      this.name = name;
      this.sig = sig;
      this.body = body;
      this.gaurd = g;
   }

   public Fun(String name, Sig sig, Statement body, Fun g, Fun lg) {
      this.name = name;
      this.sig = sig;
      this.body = body;
      this.gaurd = g;
      this.locaGaurd = lg;
   }

   @Override
   public String toString() {
      return name + "(" + sig.toString() + ")" + body.toString();
   }

   public String functionApplication(HashMap<String, String> args)
   {


      StringBuilder builder = new StringBuilder();

      builder.append(name + "(");
      for (int i = 0; i < sig.pars.length; i++)
      {
         String input = args.get(sig.pars[i].name);
         if(i == sig.pars.length - 1)
            builder.append(input);
         else
            builder.append(input + ",");
      }
      builder.append(")");
      return builder.toString();
   }

   public String functionApplicationCVC(HashMap<String, Exp> args)
   {
      StringBuilder builder = new StringBuilder();

      builder.append("(" + name + " ");
      for (int i = 0; i < sig.pars.length; i++)
      {
         Exp input = args.get(sig.pars[i].name);
         if(i == sig.pars.length - 1)
            builder.append(CVCIPrinter.print(input));
         else
            builder.append(input + " ");
      }
      builder.append(")");
      return builder.toString();
   }


}


