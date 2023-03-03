package main.java.robject.usecase;

import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.language.ast.*;
import main.java.language.type.BoolType;
import main.java.language.type.IntType;
import main.java.robject.ReplicatedObject;

import java.util.HashMap;

public class RegisterObj extends ReplicatedObject {
    public ObjectAST getASTFormat()
    {
        //defining state
        Sig bankAcountState = new Sig(new TDecl[]{
                new TDecl("reg", IntType.getInstance())}, null);


        ObjectAST useCase = UseCaseFactory.initUseCase("register", bankAcountState);
        TypeDecl stateTypeDefinition = new TypeDecl(useCase.stateType.sortName, useCase.stateType);

        SortType stateSortType = new SortType(useCase.stateTypeDecl);

        Statement invariantBody = new Return(True.getInstance());
        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), invariantBody);

        Fun read = new Fun("read", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, stateSortType), null);
        Fun write = new Fun("write", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("val", IntType.getInstance())}, stateSortType), null);


        HashMap<String, Exp> writeBodyArgs = new HashMap<>();
        writeBodyArgs.put("reg", new Var("val"));
        Statement writeBody = new Return(new RecordTypeConstructor(useCase.stateType, writeBodyArgs));
        write.body = writeBody;

        Statement queryBody = new Return(useCase.stateVar);
        read.body = queryBody;

        Exp readWP = True.getInstance();
        Exp writeWP = True.getInstance();

        Fun g_read = new Fun("g_inc", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(readWP));
        Fun g_write = new Fun("g_dec", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(writeWP));

        read.gaurd = g_read;
        write.gaurd = g_write;

        useCase.getOperations().add(read);
        useCase.getOperations().add(write);

        return useCase;

    }
}
