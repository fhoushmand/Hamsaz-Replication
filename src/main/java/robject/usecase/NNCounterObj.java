package main.java.robject.usecase;

import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.language.ast.*;
import main.java.language.type.BoolType;
import main.java.language.type.IntType;
import main.java.language.visitor.CVCIPrinter;
import main.java.robject.ReplicatedObject;

import java.util.HashMap;

public class NNCounterObj extends ReplicatedObject {
    public ObjectAST getASTFormat()
    {
        //defining state
        Sig bankAcountState = new Sig(new TDecl[]{
                new TDecl("counter", IntType.getInstance())}, null);


        ObjectAST useCase = UseCaseFactory.initUseCase("nncounter", bankAcountState);
        TypeDecl stateTypeDefinition = new TypeDecl(useCase.stateType.sortName, useCase.stateType);

        SortType stateSortType = new SortType(useCase.stateTypeDecl);

        System.out.println(CVCIPrinter.print(new Selector(useCase.stateVar, "counter")));

        Statement invariantBody = new Return(new Gte(new Selector(useCase.stateVar, "counter"), new IntLiteral(0)));
        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), invariantBody);

        Fun increment = new Fun("inc", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, stateSortType), null);
        Fun decrement = new Fun("dec", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, stateSortType), null);
        Fun query = new Fun("query", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, stateSortType), null);


        HashMap<String, Exp> incrementBodyArgs = new HashMap<>();
        incrementBodyArgs.put("counter", new Plus(new Selector(useCase.stateVar, "counter"), new IntLiteral(1)));
        Statement incrementBody = new Return(new RecordTypeConstructor(useCase.stateType, incrementBodyArgs));
        increment.body = incrementBody;

        HashMap<String, Exp> decrementBodyArgs = new HashMap<>();
        decrementBodyArgs.put("counter", new Minus(new Selector(useCase.stateVar, "counter"), new IntLiteral(1)));
        Statement decrementBody = new Return(new RecordTypeConstructor(useCase.stateType, decrementBodyArgs));
        decrement.body = decrementBody;

        Statement queryBody = new Return(useCase.stateVar);
        query.body = queryBody;

        Exp incrementWP = True.getInstance();
        Exp decrementWP = True.getInstance();
        Exp queryWP = True.getInstance();

        Fun g_increment = new Fun("g_inc", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(incrementWP));
        Fun g_decrement = new Fun("g_dec", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(decrementWP));
        Fun g_query = new Fun("g_query", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(queryWP));

        increment.gaurd = g_increment;
        decrement.gaurd = g_decrement;
        query.gaurd = g_query;

        useCase.getOperations().add(increment);
        useCase.getOperations().add(decrement);
        useCase.getOperations().add(query);

        return useCase;

    }
}
