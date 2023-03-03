package main.java.language.type;

import main.java.language.visitor.Visitor;

public interface Type extends java.io.Serializable {

   <R> R accept(Visitor.TypeVisitor<R> v);

}
