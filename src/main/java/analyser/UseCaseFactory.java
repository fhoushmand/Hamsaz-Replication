package main.java.analyser;

import main.java.language.ast.Sig;
import main.java.language.ast.TDecl;
import main.java.language.ast.TypeDecl;
import main.java.language.ast.Var;
import main.java.language.type.RecordType;

import java.util.ArrayList;

public class UseCaseFactory {


    public static ObjectAST initUseCase(String name, Sig signature, ArrayList<TypeDecl> decls)
    {
        ObjectAST useCase = new ObjectAST();
        useCase.name = name;
        useCase.stateSig = signature;
        useCase.stateType = new RecordType("State", useCase.stateSig);
        useCase.stateVar = new Var("state");
        TDecl stateTypeDefinition = new TDecl(useCase.stateType.sortName, useCase.stateType);

        useCase.stateDecl = stateTypeDefinition;

        useCase.stateTypeDecl = new TypeDecl("State", useCase.stateType);

        useCase.declerations = decls;

        return useCase;
    }

    public static ObjectAST initUseCase(String name, Sig signature)
    {
        ObjectAST useCase = new ObjectAST();
        useCase.name = name;
        useCase.stateSig = signature;
        useCase.stateType = new RecordType("State", useCase.stateSig);
        useCase.stateVar = new Var("state");
        TDecl stateTypeDefinition = new TDecl(useCase.stateType.sortName, useCase.stateType);

        useCase.stateDecl = stateTypeDefinition;

        useCase.stateTypeDecl = new TypeDecl("State", useCase.stateType);

        return useCase;
    }

}
