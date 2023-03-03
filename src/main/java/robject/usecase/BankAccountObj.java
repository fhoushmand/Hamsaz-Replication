package main.java.robject.usecase;


import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.language.ast.*;
import main.java.language.type.BoolType;
import main.java.language.type.IntType;
import main.java.robject.Guard;
import main.java.robject.ReplicatedObject;
import robject.ReplicatedObjectState;

import java.util.HashMap;

public class BankAccountObj extends ReplicatedObject {


    public BankAccountObj() {
        super();
        setState(new BankAccountState(0));
    }

    @Guard("g_deposit")
    public ReplicatedObjectState deposit(Integer x) {
        return new BankAccountState(((BankAccountState) getState()).balance + x);
    }

    public boolean g_deposit(Integer x) {
        return x >= 0;
    }

    @Guard("g_query")
    public ReplicatedObjectState query() {
        return getState();
    }

    public boolean g_query() {
        return true;
    }

    @Guard("g_withdraw")
    public ReplicatedObjectState withdraw(Integer x) {
        return new BankAccountState(((BankAccountState) getState()).balance - x);
    }

    public boolean g_withdraw(Integer x) {
        return x >= 0;
    }

    public boolean invariant(ReplicatedObjectState s) {
        return (((BankAccountState) s).balance >= 0);
    }

    public ObjectAST getASTFormat() {
        //defining state
        Sig bankAcountState = new Sig(new TDecl[]{
                new TDecl("balance", IntType.getInstance())}, null);

        ObjectAST useCase = UseCaseFactory.initUseCase("account", bankAcountState);

        SortType stateSortType = new SortType(useCase.stateTypeDecl);

        Statement invariantBody = new Return(new Gt(new Selector(useCase.stateVar, "balance"), new IntLiteral(0)));
        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), invariantBody);

        Fun getBalance = new Fun("query", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, stateSortType), null);
        Fun withdraw = new Fun("withdraw", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("a", IntType.getInstance())}, stateSortType), null);
        Fun deposit = new Fun("deposit", new Sig(new TDecl[]{new TDecl("state", stateSortType),new TDecl("a", IntType.getInstance())}, stateSortType), null);


        HashMap<String, Exp> withdrawBodyArgs = new HashMap<>();
        withdrawBodyArgs.put("balance", new Minus(new Selector(useCase.stateVar, "balance"), withdraw.sig.argsMap.get("a")));
        Statement withdrawBody = new Return(new RecordTypeConstructor(useCase.stateType, withdrawBodyArgs));
        withdraw.body = withdrawBody;

        HashMap<String, Exp> depositBodyArgs = new HashMap<>();
        depositBodyArgs.put("balance", new Plus(new Selector(useCase.stateVar, "balance"), deposit.sig.argsMap.get("a")));
        Statement depositBody = new Return(new RecordTypeConstructor(useCase.stateType, depositBodyArgs));
        deposit.body = depositBody;

        Statement getBalanceBody = new Return(useCase.stateVar);
        getBalance.body = getBalanceBody;

        Exp getBalanceWP = True.getInstance();
        Exp depositWP = new Gt(new Var("a"), new IntLiteral(0));
        Exp withdrawWP = new Gt(new Var("a"), new IntLiteral(0)) ;

        getBalance.gaurd = new Fun("g_query", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(getBalanceWP));
        withdraw.gaurd = new Fun("g_withdraw", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("a", IntType.getInstance())}, BoolType.getInstance()), new Return(withdrawWP));
        deposit.gaurd = new Fun("g_deposit", new Sig(new TDecl[]{new TDecl("state", stateSortType),new TDecl("a", IntType.getInstance())}, BoolType.getInstance()), new Return(depositWP));

        useCase.getOperations().add(withdraw);
        useCase.getOperations().add(deposit);
        useCase.getOperations().add(getBalance);

        return useCase;

    }


}

