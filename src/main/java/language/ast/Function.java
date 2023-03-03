package main.java.language.ast;



import java.util.HashMap;

public class Function implements java.io.Serializable {
   public String name;
   public Sig sig;
   public Statement body;
   public Function gaurd;

   public Function(String name, Sig sig, Statement body) {
      this.name = name;
      this.sig = sig;
      this.body = body;
   }

   public Function(String name, Sig sig, Statement body, Function g) {
      this.name = name;
      this.sig = sig;
      this.body = body;
      this.gaurd = g;
   }

   @Override
   public String toString() {
      return "function";
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

   public String functionApplicationCVC(HashMap<String, String> args)
   {
      StringBuilder builder = new StringBuilder();

      builder.append("(" + name + " ");
      for (int i = 0; i < sig.pars.length; i++)
      {
         String input = args.get(sig.pars[i].name);
         if(i == sig.pars.length - 1)
            builder.append(input);
         else
            builder.append(input + " ");
      }
      builder.append(")");
      return builder.toString();
   }


}


