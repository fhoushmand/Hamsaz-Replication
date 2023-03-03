package main.java.robject.usecase;

import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.language.ast.*;
import main.java.language.type.*;
import main.java.robject.Guard;
import main.java.robject.ReplicatedObject;
import robject.ReplicatedObjectState;

import java.util.ArrayList;
import java.util.HashMap;

public class TwoPhaseSetObj extends ReplicatedObject {


    public TwoPhaseSetObj() {
        super();
        setState(new TwoPhaseSetState());
    }

    public boolean g_add(Integer x) { return true; }

    @Guard("g_add")
    public ReplicatedObjectState add(Integer x)
    {
        TwoPhaseSetState state = new TwoPhaseSetState();
        state.avail.addAll(((TwoPhaseSetState)(this.getState())).avail);
        state.tomb.addAll(((TwoPhaseSetState)(this.getState())).tomb);
        state.avail.add(x);
        return state;
    }

    public boolean g_remove(Integer x) { return true; }

    @Guard("g_remove")
    public ReplicatedObjectState remove(Integer x)
    {
        TwoPhaseSetState state = new TwoPhaseSetState();
        state.avail.addAll(((TwoPhaseSetState)(this.getState())).avail);
        state.tomb.addAll(((TwoPhaseSetState)(this.getState())).tomb);
        state.tomb.add(x);
        return state;
    }


    public boolean g_contains(Integer x) { return true; }

    @Guard("g_add")
    public ReplicatedObjectState contains(Integer x)
    {
        return getState();
    }


    public boolean invariant(ReplicatedObjectState s)
    {
        return true;
    }

    public ObjectAST getASTFormat()
    {
        TypeDecl elementRow = new TypeDecl("element", new TupleType(IntType.getInstance()));

        Type elementSortType = new SortType(elementRow);

        TDecl availSet= new TDecl("avail", new SetType(elementRow.type));
        TDecl tombstoneSet = new TDecl("tomb", new SetType(elementRow.type));

        ArrayList<TypeDecl> decls = new ArrayList<>();
        decls.add(elementRow);

        //defining state
        Sig sig = new Sig(new TDecl[]{availSet, tombstoneSet}, null);
        ObjectAST useCase = UseCaseFactory.initUseCase("2pset", sig, decls);

        SortType stateSortType = new SortType(useCase.stateTypeDecl);

        Statement invariantBody = new Return(True.getInstance());
        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), invariantBody);

        Fun add = new Fun("add", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, stateSortType), null);
        Fun remove = new Fun("rem", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, stateSortType), null);
        Fun contains = new Fun("contains", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, stateSortType), null);

        HashMap<String, Exp> addBodyArgs = new HashMap<>();
        addBodyArgs.put("avail", new SetUnion(new Selector(useCase.stateVar, "avail"), new SingeltonLiteral(new Var("elem"))));
        addBodyArgs.put("tomb", new Selector(useCase.stateVar, "tomb"));
        Statement addBody = new Return(new RecordTypeConstructor(useCase.stateType, addBodyArgs));
        add.body = addBody;

        HashMap<String, Exp> remBodyArgs = new HashMap<>();
        remBodyArgs.put("avail", new Selector(useCase.stateVar, "avail"));
        remBodyArgs.put("tomb", new SetUnion(new Selector(useCase.stateVar, "tomb"), new SingeltonLiteral(new Var("elem"))));
        Statement remBody = new Return(new RecordTypeConstructor(useCase.stateType, remBodyArgs));
        remove.body = remBody;

        Statement queryBody = new Return(useCase.stateVar);
        contains.body = queryBody;

        Exp incrementWP = True.getInstance();
        Exp decrementWP = True.getInstance();
        Exp queryWP = True.getInstance();

        Fun g_increment = new Fun("g_add", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, BoolType.getInstance()), new Return(incrementWP));
        Fun g_decrement = new Fun("g_remove", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, BoolType.getInstance()), new Return(decrementWP));
        Fun g_query = new Fun("g_contains", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("elem", elementSortType)}, BoolType.getInstance()), new Return(queryWP));

        add.gaurd = g_increment;
        remove.gaurd = g_decrement;
        contains.gaurd = g_query;

        useCase.getOperations().add(add);
        useCase.getOperations().add(remove);
        useCase.getOperations().add(contains);

        return useCase;

    }


}

