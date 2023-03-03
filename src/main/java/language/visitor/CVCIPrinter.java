package main.java.language.visitor;

import main.java.utils.compiler.texttree.seq.TextSeq;
import main.java.language.ast.*;
import main.java.language.type.*;

public class CVCIPrinter implements Visitor {

    TextSeq seq;

    public CVCIPrinter() {
        seq = new TextSeq();
    }

    public static String print(Fun fun) {
        CVCIPrinter p = new CVCIPrinter();
        p.visit(fun);
        return p.getText();
    }

    public static String print(Exp exp) {
        CVCIPrinter p = new CVCIPrinter();
        p.visitDisptch(exp);
        return p.getText();
    }

    public static String print(TDecl tDecl) {
        CVCIPrinter p = new CVCIPrinter();
        p.visit(tDecl);
        return p.getText();
    }

    public static String print(TypeDecl typeDecl) {
        CVCIPrinter p = new CVCIPrinter();
        p.visit(typeDecl);
        return p.getText();
    }

    public static String print(SortType sortType) {
        CVCIPrinter p = new CVCIPrinter();
        p.visit(sortType);
        return p.getText();
    }

    public static String print(VarDecl varDecl) {
        CVCIPrinter p = new CVCIPrinter();
        p.visit(varDecl);
        return p.getText();
    }

    public static String print(Assertion assertion) {
        CVCIPrinter p = new CVCIPrinter();
        p.visit(assertion);
        return p.getText();
    }



    public static String printNone(String name, Type type) {
        CVCIPrinter p = new CVCIPrinter();
        Fun fun = new Fun(
                name,
                new Sig(new TDecl[]{}, new OptionType(type)),
                new Return (new None(type)));
        p.visit(fun);
        return p.getText();
    }

    public static String printNone(String name, Type t1, Type t2) {
        CVCIPrinter p = new CVCIPrinter();
        Fun fun = new Fun(
                name,
                new Sig(
                        new TDecl[]{},
                        new PairType(new OptionType(t1), new OptionType(t2))),
                new Return (new Pair (new None(t1), new None(t2))));
        p.visit(fun);
        return p.getText();
    }

    @Override
    public Object visit(TDecl tDecl) {
        seq.put(tDecl.name);
        seq.put(": ");
        visitDisptch(tDecl.type);
        return null;
    }

    @Override
    public Object visit(TypeDecl typeDecl) {
        seq.put(typeDecl.name);
        seq.put(": TYPE = ");
        visitDisptch(typeDecl.type);
        seq.put(";");
        return null;
    }

    @Override
    public Object visit(SortType sortType) {
        seq.put(sortType.type.name);
        return null;
    }

    @Override
    public Object visit(VarDecl varDecl) {
        visitDisptch(varDecl.var);
        seq.put(": ");
        visitDisptch(varDecl.type);
        seq.put(";");
        return null;
    }

    @Override
    public Object visit(Fun fun) {
        seq.put(fun.name);
        seq.put(": ");
        seq.put("(");
        for (int i = 0; i < fun.sig.pars.length; i++) {
            TDecl tDecl = fun.sig.pars[i];
            visitDisptch(tDecl.type);
            if(i != fun.sig.pars.length-1)
                seq.put(", ");
        }
        seq.put(")");
        seq.put(" ");
        seq.put("-> ");
        visitDisptch(fun.sig.rType);
        if(fun.body != null) {
            seq.put(" = LAMBDA(");
            for (int i = 0; i < fun.sig.pars.length; i++) {
                TDecl tDecl = fun.sig.pars[i];
                seq.put(tDecl.name);
                seq.put(": ");
                visitDisptch(tDecl.type);
                if (i != fun.sig.pars.length - 1)
                    seq.put(", ");
            }
            seq.put(") : ");
            seq.startLine();
            seq.incIndent();
            visitDisptch(fun.body);
            seq.put(";");
        }
        else
            seq.put(";");
        return null;
    }

    @Override
    public Object visit(Assertion assertion) {
        seq.put("ASSERT ");
        visitDisptch(assertion.assertion);
        seq.put(";");
        return null;
    }

    public Object visitDisptch(Type type) {
        return type.accept(typePrinter);
    }

    TypePrinter typePrinter = new TypePrinter();

    public class TypePrinter implements TypeVisitor<Object> {

        @Override
        public Object visit(SortType sortType) {
            seq.put(sortType.type.name);
            return null;
        }

        @Override
        public Object visit(BoolType boolType) {
            seq.put("BOOLEAN");
            return null;
        }

        @Override
        public Object visit(IntType intType) {
//         seq.put("BInt");
//            seq.put("[INT]");
            seq.put("INT");
            return null;
        }

        @Override
        public Object visit(FloatType floatType) {
            seq.put("FLOAT");
            return null;
        }

        @Override
        public Object visit(OptionType optionType) {
            seq.put("(Option ");
            visitDisptch(optionType.tpar);
            seq.put(")");
            return null;
        }

        @Override
        public Object visit(VertexType vertexType) {
            seq.put("V");
            return null;
        }

        @Override
        public Object visit(EdgeType edgeType) {
            seq.put("E");
            return null;
        }

        @Override
        public Object visit(VoidType voidType) {
            return null;
        }

        @Override
        public Object visit(TypeVar typeVar) {
            throw new RuntimeException("Error: TypeVar for SMT.");
//         return null;
        }

        @Override
        public Object visit(DirType dirType) {
            return null;
        }

        @Override
        public Object visit(VIdType idType) {
            seq.put("Int");
            return null;
        }

        @Override
        public Object visit(PairType pairType) {
            seq.put("(Pair ");
            visitDisptch(pairType.tpar1);

            seq.put(" ");
            visitDisptch(pairType.tpar2);
            seq.put(")");
            return null;
        }

        @Override
        public Object visit(ArrayType arrayType) {
            seq.put("(Array ");
            visitDisptch(arrayType.tpar1);
            seq.put(" ");
            visitDisptch(arrayType.tpar2);
            seq.put(")");
            return null;
        }

        @Override
        public Object visit(SetType setType) {
            seq.put("SET OF ");
            visitDisptch(setType.tpar);
            return null;
        }

        @Override
        public Object visit(RecordType recordType) {

            seq.put("[# ");
            for (int i = 0; i < recordType.tDeclPars.length; i++)
            {
                CVCIPrinter.this.visit(recordType.tDeclPars[i]);
                if(i != recordType.tDeclPars.length-1)
                    seq.put(", ");
            }
            seq.put(" #]");
            return null;
        }

        @Override
        public Object visit(TupleType tupleType) {
            seq.put("[");
            for (int i = 0; i < tupleType.tpars.length; i++) {
                visitDisptch(tupleType.tpars[i]);
                if(i != tupleType.tpars.length-1)
                seq.put(", ");
            }
            seq.put("]");
            return null;
        }
    }

    public Object visitDisptch(Exp exp) {
        return exp.accept(expPrinter);
    }

    ExpPrinter expPrinter = new ExpPrinter();
    public class ExpPrinter implements ExpVisitor<Object> {

        @Override
        public Object visit(Tuple tuple) {
            seq.put("(");
            int i = 0;
            for (Exp axp : tuple.exps) {
                visitDisptch(axp);
                if(i != tuple.exps.length - 1)
                    seq.put(", ");
                i++;
            }
            seq.put(")");
            return null;
        }

        class ZOpPrinter implements ZOpVisitor<Object> {
            public Object visit(Var var) {
                seq.put(var.name);
                return null;
            }
            public Object visit(IntLiteral intLiteral) {
            seq.put(intLiteral.i + "");
//                seq.put("(num " + intLiteral.i + ")");
                return null;
            }

            @Override
            public Object visit(Inf inf) {
                seq.put("inf");
                return null;
            }

            public Object visit(Src src) {
                seq.put("src");
                return null;
            }

            @Override
            public Object visit(Vertices vertices) {
                return null;
            }

            public Object visit(None none) {
//            seq.put("none");
                seq.put("(as ");
                seq.put("none ");
                seq.put("(Option ");
                visitDisptch(none.type);
                seq.put("))");
                return null;
            }

            @Override
            public Object visit(SingeltonLiteral singeltonLiteral) {
                seq.put("{");
                visitDisptch(singeltonLiteral.i);
                seq.put("}");
                return null;
            }

            @Override
            public Object visit(DirIn dirIn) {
                return null;
            }

            @Override
            public Object visit(DirOut dirOut) {
                return null;
            }

            @Override
            public Object visit(DirBoth dirBoth) {
                return null;
            }

            @Override
            public Object visit(DirNone dirNone) {
                return null;
            }

            @Override
            public Object visit(True aTrue) {
                seq.put("TRUE");
                return null;
            }

            @Override
            public Object visit(False aFalse) {
                seq.put("FALSE");
                return null;
            }

            @Override
            public Object visit(Epsilon epsilon) {
                return null;
            }

            @Override
            public Object visit(Msg msg) {
                return null;
            }
        }

        ZOpPrinter zOpPrinter = new ZOpPrinter();


        class UOpPrinter implements UOpVisitor<Object> {

            public Object visit(Some some) {
                seq.put("(some ");
                visitDisptch(some.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(WeightOfEdge weightOfEdge) {
                seq.put("(weight-of-e ");
                visitDisptch(weightOfEdge.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(CapOfEdge capOfEdge) {
                seq.put("(cap-of-e ");
                visitDisptch(capOfEdge.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(VerticesIn verticesIn) {
                return null;
            }

            @Override
            public Object visit(VerticesOut verticesOut) {
                return null;
            }

            @Override
            public Object visit(VerticesBoth verticesBoth) {
                return null;
            }

            @Override
            public Object visit(SrcOfEdge srcOfEdge) {
                seq.put("(src-of-e ");
                visitDisptch(srcOfEdge.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(DestOfEdge destOfEdge) {
                seq.put("(dest-of-e ");
                visitDisptch(destOfEdge.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(VValue value) {
                return null;
            }

            @Override
            public Object visit(VId id) {
                seq.put("(id ");
                visitDisptch(id.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Fst fst) {
                seq.put("(fst ");
                visitDisptch(fst.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Snd snd) {
                seq.put("(array-dec ");
                visitDisptch(snd.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Projection projection) {
                visitDisptch(projection.arg);
                seq.put(".");
                //seq.put(projection.op);
                seq.put(projection.op[0]);
                return null;
            }

            @Override
            public Object visit(ArraySelect arraySelect) {
                visitDisptch(arraySelect.arg);
                seq.put("[");
                visitDisptch(arraySelect.op);
                seq.put("]");
                return null;
            }

            @Override
            public Object visit(Selector selector) {
                visitDisptch(selector.arg);
                seq.put(".");
                seq.put(selector.op);
                return null;
            }

            @Override
            public Object visit(EdgesOut edgesOut) {
                return null;
            }

            @Override
            public Object visit(EdgesIn edgesIn) {
                return null;
            }

            @Override
            public Object visit(EdgesBoth edgesBoth) {
                return null;
            }

            @Override
            public Object visit(Not not) {
                seq.put("(NOT ");
                visitDisptch(not.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Abs abs) {
                return null;
            }

            @Override
            public Object visit(SetComplement setComplement) {
                seq.put("~");
                visitDisptch(setComplement.arg);
                return null;
            }

            @Override
            public Object visit(SetCardinality setCardinality) {
                seq.put("CARD(");
                visitDisptch(setCardinality.arg);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(SingeltonTuple singeltonTuple) {
                seq.put("TUPLE(");
                visitDisptch(singeltonTuple.arg);
                seq.put(")");
                return null;
            }
        }

        UOpPrinter uOpPrinter = new UOpPrinter();

        class BOpPrinter implements BOpVisitor<Object> {



            @Override
            public Object visit(ArrayMax arrayMax) {
                seq.put("(max-arr ");
                visitDisptch(arrayMax.arg1);
                seq.put(" ");
                visitDisptch(arrayMax.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(ArrayMin arrayMin) {
                seq.put("(min-arr ");
                visitDisptch(arrayMin.arg1);
                seq.put(" ");
                visitDisptch(arrayMin.arg2);
                seq.put(")");
                return null;
            }

            public Object visit(Plus plus) {
                seq.put("(");
                visitDisptch(plus.arg1);
                seq.put(" + ");
                visitDisptch(plus.arg2);
                seq.put(")");
                return null;
            }

            public Object visit(Minus minus) {
                seq.put("(");
                visitDisptch(minus.arg1);
                seq.put(" - ");
                visitDisptch(minus.arg2);
                seq.put(")");
                return null;
            }

            public Object visit(Eq eq) {
                seq.put("(");
                visitDisptch(eq.arg1);
                seq.put(" = ");
                visitDisptch(eq.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(NEq nEq) {
                seq.put("(NOT ( ");
                visitDisptch(nEq.arg1);
                seq.put(" = ");
                visitDisptch(nEq.arg2);
                seq.put("))");
                return null;
            }

            @Override
            public Object visit(Lt lt) {
                seq.put("(");
                visitDisptch(lt.arg1);
                seq.put(" < ");
                visitDisptch(lt.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Gt gt) {
                seq.put("(");
                visitDisptch(gt.arg1);
                seq.put(" > ");
                visitDisptch(gt.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Gte gte) {
                seq.put("(");
                visitDisptch(gte.arg1);
                seq.put(" >= ");
                visitDisptch(gte.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Min min) {
//            seq.put("(min ");
                seq.put("(min-b ");
                visitDisptch(min.arg1);
                seq.put(" ");
                visitDisptch(min.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Max max) {
//            seq.put("(max ");
                seq.put("(max-b ");
                visitDisptch(max.arg1);
                seq.put(" ");
                visitDisptch(max.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Pair pair) {
//            seq.put("(max ");
                seq.put("(payload ");
//                seq.put("\n");
//                seq.put("\t");
                visitDisptch(pair.arg1);
                seq.put(" ");
//                seq.put("\n");
//                seq.put("\t");
                visitDisptch(pair.arg2);
//                seq.put("\n");
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(And and) {
                seq.put("(");
                visitDisptch(and.arg1);
                seq.put(" AND ");
                visitDisptch(and.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Or or) {
                seq.put("(");
                visitDisptch(or.arg1);
                seq.put(" OR ");
                visitDisptch(or.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Select select) {
                seq.put("(select ");
                visitDisptch(select.arg1);
                seq.put(" ");
                visitDisptch(select.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(SetUnion setUnion) {
                seq.put("(");
                visitDisptch(setUnion.arg1);
                seq.put(" | ");
                visitDisptch(setUnion.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(SetMinus setMinus) {
                seq.put("(");
                visitDisptch(setMinus.arg1);
                seq.put(" - ");
                visitDisptch(setMinus.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(SetIntersection setIntersection) {
                seq.put("(");
                visitDisptch(setIntersection.arg1);
                seq.put(" & ");
                visitDisptch(setIntersection.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(SetMembership setMembership) {
                seq.put("(");
                visitDisptch(setMembership.arg1);
                seq.put(" IS_IN ");
                visitDisptch(setMembership.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(SetSubset setSubset) {
                seq.put("(");
                visitDisptch(setSubset.arg1);
                seq.put(" <= ");
                visitDisptch(setSubset.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(SetProduct setProduct) {
                seq.put("(");
                visitDisptch(setProduct.arg1);
                seq.put(" PRODUCT ");
                visitDisptch(setProduct.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(Implication implication) {
                seq.put("(");
                visitDisptch(implication.arg1);
                seq.put(" => ");
                visitDisptch(implication.arg2);
                seq.put(")");
                return null;
            }

            @Override
            public Object visit(MinOI minOI) {
                return null;
            }

            @Override
            public Object visit(MaxOI maxOI) {
                return null;
            }

            @Override
            public Object visit(MinOF minOF) {
                return null;
            }

            @Override
            public Object visit(MaxOF maxOF) {
                return null;
            }

            @Override
            public Object visit(IsMinOI isMinOI) {
                return null;
            }

            @Override
            public Object visit(IsMaxOI isMaxOI) {
                return null;
            }

            @Override
            public Object visit(IsMinOF isMinOF) {
                return null;
            }

            @Override
            public Object visit(IsMaxOF isMaxOF) {
                return null;
            }

            @Override
            public Object visit(MinPII minPII) {
                return null;
            }

            @Override
            public Object visit(MinPIF minPIF) {
                return null;
            }

            @Override
            public Object visit(MinPFI minPFI) {
                return null;
            }

            @Override
            public Object visit(MinPFF minPFF) {
                return null;
            }

            @Override
            public Object visit(MaxPII maxPII) {
                return null;
            }

            @Override
            public Object visit(MaxPIF maxPIF) {
                return null;
            }

            @Override
            public Object visit(MaxPFI maxPFI) {
                return null;
            }

            @Override
            public Object visit(MaxPFF maxPFF) {
                return null;
            }

            @Override
            public Object visit(IsMinPII isMinPII) {
                return null;
            }

            @Override
            public Object visit(IsMinPFI isMinPFI) {
                return null;
            }

            @Override
            public Object visit(IsMinPIF isMinPIF) {
                return null;
            }

            @Override
            public Object visit(IsMinPFF isMinPFF) {
                return null;
            }

            @Override
            public Object visit(IsMaxPII isMaxPII) {
                return null;
            }

            @Override
            public Object visit(IsMaxPIF isMaxPIF) {
                return null;
            }

            @Override
            public Object visit(IsMaxPFI isMaxPFI) {
                return null;
            }

            @Override
            public Object visit(IsMaxPFF isMaxPFF) {
                return null;
            }

            @Override
            public Object visit(MinPIV minPIV) {
                return null;
            }

            @Override
            public Object visit(MaxPIV maxPIV) {
                return null;
            }

            @Override
            public Object visit(IsMinPIV isMinPIV) {
                return null;
            }

            @Override
            public Object visit(IsMaxPIV isMaxPIV) {
                return null;
            }
        }

        BOpPrinter bOpPrinter = new BOpPrinter();

        class TOpPrinter implements TOpVisitor<Object> {


            @Override
            public Object visit(Store store) {
                seq.put("(store ");
                visitDisptch(store.arg1);
                seq.put(" ");
                visitDisptch(store.arg2);
                seq.put(" ");
                visitDisptch(store.arg3);
                seq.put(")");
                return null;
            }


        }

        TOpPrinter tOpPrinter = new TOpPrinter();

        class NOpPrinter implements NOpVisitors<Object> {


            @Override
            public Object visit(RecordTypeConstructor recordTypeConstructor) {
                seq.put("(# ");
                int i = 0;
                for (String field : recordTypeConstructor.args.keySet()) {
                    seq.put(field);
                    seq.put(" := ");
                    visitDisptch(recordTypeConstructor.args.get(field));
                    if(i != recordTypeConstructor.numberOfOperands - 1)
                        seq.put(", ");
                    i++;
                }
                seq.put(" #)");
                return null;
            }

        }

        NOpPrinter nOpPrinter = new NOpPrinter();

        public Object visit(ZOp zOp) {
            return zOp.accept(zOpPrinter);
        }

        public Object visit(UOp uOp) {
            return uOp.accept(uOpPrinter);
        }

        public Object visit(BOp bOp) {
            return bOp.accept(bOpPrinter);
        }

        @Override
        public Object visit(TOp tOp) {
            return tOp.accept(tOpPrinter);
        }


        @Override
        public Object visit(NOp nOp) {
            return nOp.accept(nOpPrinter);
        }

        public Object visit(ITE ite) {
            seq.put("(ite");
            seq.endLine();
            seq.incIndent();
//         seq.put("(");
            visitDisptch(ite.condExp);
//         seq.put(")");
            seq.endLine();
            visitDisptch(ite.thenExp);
            seq.endLine();
            visitDisptch(ite.elseExp);
            seq.endLine();
            seq.decIndent();
            seq.put(")");
//         seq.endLine();
            return null;
        }

        @Override
        public Object visit(MatchOExp oMatch) {
            return null;
        }

        @Override
        public Object visit(Call call) {
//            if (call.fun.preCondition != null)

            seq.put(call.funName);
            seq.put("(");
            for (int i = 0; i < call.fun.sig.pars.length; i++)
            {
                String input = call.argsMap.get(call.fun.sig.pars[i].name);
                if(i == call.fun.sig.pars.length - 1)
                    seq.put(input);
                else
                    seq.put(input + ", ");
            }
            seq.put(")");
            return null;
        }

        @Override
        public Object visit(Quantifier quantifier)
        {
            seq.put("(");
            if(quantifier.type == Quantifier.QUANTIFIER_TYPE.FORALL)
            {
                seq.put("FORALL ");
            }
            else if(quantifier.type == Quantifier.QUANTIFIER_TYPE.EXISTS)
            {
                seq.put("EXISTS ");
            }
            seq.put("(");
            for (int i = 0; i < quantifier.sig.pars.length; i++) {
                CVCIPrinter.this.visit(quantifier.sig.pars[i]);
                if(i != quantifier.sig.pars.length-1)
                    seq.put(", ");
            }
            seq.put(") : ");
            visitDisptch(quantifier.assertion);
            seq.put(")");
            return null;
        }

        //private Object visitDisptch(Exp exp) {
        //   return exp.accept(this);
        //}
    }

    private Object visitDisptch(Statement st) {
        return st.accept(stPrinter);
    }

    StVisitor stPrinter = new StVisitor();
    public class StVisitor implements Visitor.StVisitor {

        @Override
        public Object visit(Assignment assignment) {
            return null;
        }

        @Override
        public Object visit(IfThen ifThen) {
            return null;
        }

        @Override
        public Object visit(IfThenElse ifThenElse) {
            return null;
        }

        @Override
        public Object visit(For aFor) {
            return null;
        }

        @Override
        public Object visit(StSeq stSeq) {
            return null;
        }

        @Override
        public Object visit(MatchOSt matchOSt) {
            return null;
        }

        @Override
        public Object visit(Signal signal) {
            return null;
        }

        @Override
        public Object visit(Return aReturn) {
            visitDisptch(aReturn.arg);
            return null;
        }

        @Override
        public Object visit(Skip skip) {
            return null;
        }

        @Override
        public Object visit(Decl decl) {
            return null;
        }

        @Override
        public Object visit(SetValue setValue) {
            return null;
        }
    }


    public String getText() {
        return seq.get().print();
    }

}

