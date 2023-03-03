package main.java.language.ast;

import main.java.language.visitor.Visitor;

import java.util.Arrays;

public class Projection extends UOp {
    final String s = "projection";
    public String[] op;

    public Projection(Exp arg, String... field)
    {
        super(arg);
        this.op = field;
    }

    public <R> R accept(Visitor.ExpVisitor.UOpVisitor<R> v) {
        return v.visit(this);
    }

    @Override
    String opName() {
        return "projection";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Projection some = (Projection) o;

        return s.equals(some.s);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + s.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Arrays.stream((String[])op).toString();
    }
}