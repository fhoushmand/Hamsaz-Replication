package main.java.robject.usecase;

import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.language.ast.*;
import main.java.language.type.*;
import main.java.robject.ReplicatedObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CSetObj extends ReplicatedObject {
    public ObjectAST getASTFormat()
    {

        TypeDecl elementRow = new TypeDecl("element", new TupleType(IntType.getInstance()));

        Type elementSortType = new SortType(elementRow);

        TDecl availSet= new TDecl("elements", new SetType(elementRow.type));

        ArrayList<TypeDecl> decls = new ArrayList<>();
        decls.add(elementRow);


        //defining state
        Sig sig = new Sig(new TDecl[]{availSet}, null);


        ObjectAST useCase = UseCaseFactory.initUseCase("cset", sig, decls);
        TypeDecl stateTypeDefinition = new TypeDecl(useCase.stateType.sortName, useCase.stateType);

        SortType stateSortType = new SortType(useCase.stateTypeDecl);


        Statement invariantBody = new Return(True.getInstance());
        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), invariantBody);

        Fun add = new Fun("add", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, stateSortType), null);
        Fun remove = new Fun("remove", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, stateSortType), null);
        Fun contains = new Fun("contains", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, stateSortType), null);


        HashMap<String, Exp> addBodyArgs = new HashMap<>();
        addBodyArgs.put("elements", new SetUnion(new Selector(useCase.stateVar, "elements"), new SingeltonLiteral(new Var("elem"))));
        Statement addBody = new Return(new RecordTypeConstructor(useCase.stateType, addBodyArgs));
        add.body = addBody;

        HashMap<String, Exp> removeBodyArgs = new HashMap<>();
        removeBodyArgs.put("elements", new SetMinus(new Selector(useCase.stateVar, "elements"), new SingeltonLiteral(new Var("elem"))));
        Statement removeBody = new Return(new RecordTypeConstructor(useCase.stateType, removeBodyArgs));
        remove.body = removeBody;

//        Statement queryBody = new Return(new SetMembership(new Var("elem"), new SetMinus(new Selector(useCase.stateVar, "avail"), new Selector(useCase.stateVar, "tomb"))));
        Statement queryBody = new Return(useCase.stateVar);
        contains.body = queryBody;

        Exp incrementWP = True.getInstance();
        Exp removeWP = True.getInstance();
        Exp queryWP = True.getInstance();

        Fun g_increment = new Fun("g_add", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, BoolType.getInstance()), new Return(incrementWP));
        Fun g_remove = new Fun("g_remove", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, BoolType.getInstance()), new Return(incrementWP));
        Fun g_query = new Fun("g_contains", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, BoolType.getInstance()), new Return(queryWP));

        add.gaurd = g_increment;
        remove.gaurd = g_remove;
        contains.gaurd = g_query;

        useCase.getOperations().add(add);
        useCase.getOperations().add(remove);
        useCase.getOperations().add(contains);

        return useCase;

    }
}
