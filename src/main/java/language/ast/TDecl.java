package main.java.language.ast;

import main.java.language.type.Type;


public class TDecl implements java.io.Serializable {
   public String name;
   public Type type;

   public TDecl(String name, Type type) {
      this.name = name;
      this.type = type;
   }

   @Override
   public String toString() {
      return name + ": " + type;
   }
}


