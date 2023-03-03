package main.java.robject.usecase;

import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.analyser.UseCaseFactory;
import main.java.language.ast.*;
import main.java.language.type.BoolType;
import main.java.language.type.IntType;
import main.java.language.type.SetType;
import main.java.robject.ReplicatedObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AuctionObj extends ReplicatedObject {

    public ObjectAST getASTFormat()
    {
        Sig sig = new Sig(new TDecl[]{
                new TDecl("bids", new SetType(IntType.getInstance())),
                new TDecl("w", IntType.getInstance())},null);
//                new TDecl("open", BoolType.getInstance())}, null);


//        ObjectAST useCase = UseCaseFactory.initUseCase("auction", sig, "/home/farzin/Documents/MyWorkspace/Synthesizer/src/consistency/cvclib-auction");
//        ObjectAST useCase = UseCaseFactory.initUseCase("account", sig, "C:\\Users\\Farzin\\Documents\\Projects\\Synthesizer-master\\src\\consistency\\cvclib-auction");
        ObjectAST useCase = UseCaseFactory.initUseCase("auction", sig);


        Fun place = new Fun("place", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("b", IntType.getInstance())}, useCase.stateType), null);
        Fun close = new Fun("close", new Sig(new TDecl[]{new TDecl("state", useCase.stateType)}, useCase.stateType), null);
        Fun query = new Fun("query", new Sig(new TDecl[]{new TDecl("state", useCase.stateType)}, useCase.stateType), null);

        Fun max = new Fun("max", new Sig(new TDecl[]{new TDecl("state", new SetType(IntType.getInstance()))}, IntType.getInstance()), null);
//        max.preCondition = new Not(new Eq(new SetCardinality(new Selector(useCase.stateVar, "bids")),new IntLiteral(0)));
        useCase.utilityFunctions.add(max);

        useCase.utilityAssertions.add(
                new Assertion(
                        new Quantifier(
                                Quantifier.QUANTIFIER_TYPE.FORALL,
                                new Sig(
                                        new TDecl[]{
                                                new TDecl("s", new SetType(IntType.getInstance())),
                                                new TDecl("i", IntType.getInstance())
                                        },
                                        null
                                ),
                                new Implication(
                                        new SetMembership(new Var("i"), new Var("s")),
                                        new Gte(
                                                new Call(max, new Exp[]{new Var("s")}),
                                                new Var("i")
                                        )
                                )
                        )
                )
        );

        useCase.utilityAssertions.add(
                new Assertion(
                        new Quantifier(
                                Quantifier.QUANTIFIER_TYPE.FORALL,
                                new Sig(
                                        new TDecl[]{
                                                new TDecl("s", new SetType(IntType.getInstance()))
                                        },
                                        null
                                ),
                                new Implication(
                                        new Not(new Eq(new SetCardinality(new Var("s")), new IntLiteral(0))),
                                        new SetMembership(new Call(max, new Exp[]{new Var("s")}), new Var("s"))
                                )
                        )
                )
        );


        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", useCase.stateType)}, BoolType.getInstance()), null);
        Statement invariantBody = new Return(new Implication(new Not(new Eq(new Selector(useCase.stateVar, "w"), new IntLiteral(0))), new And(new Not(new Eq(new SetCardinality(new Selector(useCase.stateVar, "bids")),new IntLiteral(0))), new Eq(new Selector(useCase.stateVar, "w"), new Call(max, new Exp[]{new Selector(useCase.stateVar, "bids")})))));
        useCase.invariant.body = invariantBody;

        HashMap<String, Exp> placeFunctionRecrodArgs = new LinkedHashMap<String, Exp>(){{
            put("bids", new SetUnion(new Selector(useCase.stateVar, "bids"), new SingeltonLiteral(place.sig.argsMap.get("b"))));
            put("w", new Selector(useCase.stateVar, "w"));
//           put("open", new Selector(useCase.stateVar, "open"));
        }};
        Statement placeBody = new Return(new RecordTypeConstructor(useCase.stateType, placeFunctionRecrodArgs));
        place.body = placeBody;

        HashMap<String, Exp> closeFunctionRecrodArgs = new LinkedHashMap<String, Exp>(){{
            put("bids", new Selector(useCase.stateVar, "bids"));
            put("w", new Call(max, new Exp[]{new Selector(useCase.stateVar, "bids")}));
//            put("open", False.getInstance());
        }};
        Statement closeBody = new Return(new RecordTypeConstructor(useCase.stateType, closeFunctionRecrodArgs));
        close.body = closeBody;


        Statement queryBody = new Return(useCase.stateVar);
        query.body = queryBody;

//        Exp placeWP = new Selector(useCase.stateVar, "open");
//        Exp closeWP = new Selector(useCase.stateVar, "open");
        Exp placeWP = new Eq(new Selector(useCase.stateVar, "w"), new IntLiteral(0));
        Exp closeWP = new Eq(new Selector(useCase.stateVar, "w"), new IntLiteral(0));
        Exp queryWP = True.getInstance();


        Fun g_place = new Fun("g_place", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("b", IntType.getInstance())}, BoolType.getInstance()), new Return(placeWP));
        Fun g_close = new Fun("g_close", new Sig(new TDecl[]{new TDecl("state", useCase.stateType)}, BoolType.getInstance()), new Return(closeWP));
        Fun g_query = new Fun("g_query", new Sig(new TDecl[]{new TDecl("state", useCase.stateType)}, BoolType.getInstance()), new Return(queryWP));
//
        place.gaurd = g_place;
        close.gaurd = g_close;
        query.gaurd = g_query;

        useCase.getOperations().add(place);
        useCase.getOperations().add(close);
        useCase.getOperations().add(query);

        return useCase;

    }
}
