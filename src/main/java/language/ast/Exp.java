package main.java.language.ast;

import main.java.language.visitor.Visitor;

public abstract class Exp implements java.io.Serializable {
//   Option<Type> type;
   public abstract <R> R accept(Visitor.ExpVisitor<R> v);

   public abstract String toString();

}
