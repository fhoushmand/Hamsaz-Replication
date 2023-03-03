package main.java.language.ast;


public class Assertion implements java.io.Serializable {

   public Exp assertion;

   public Assertion(Exp exp) {
      this.assertion = exp;
   }

   @Override
   public String toString() {
      return "assertion";
   }


}


