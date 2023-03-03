package main.java.robject.usecase;

import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.utils.Utils;
import main.java.language.ast.*;
import main.java.language.type.*;
import main.java.robject.ReplicatedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PayrollObj extends ReplicatedObject {
    public ObjectAST getASTFormat()
    {
        TypeDecl employeeRow = new TypeDecl("employee", new TupleType(IntType.getInstance(), IntType.getInstance(), IntType.getInstance()));
        TypeDecl departmentRow = new TypeDecl("department", new TupleType(IntType.getInstance()));

        Type employeeRowSortType = new SortType(employeeRow);
        Type departmentRowSortType = new SortType(departmentRow);

        TDecl employeeTable = new TDecl("employees", new SetType(employeeRow.type));
        TDecl departmentTable = new TDecl("departments", new SetType(departmentRow.type));

        ArrayList<TypeDecl> decls = new ArrayList<>();
        decls.add(employeeRow);
        decls.add(departmentRow);

        Sig sig = new Sig(new TDecl[]{employeeTable, departmentTable}, null);
        ObjectAST useCase = UseCaseFactory.initUseCase("payroll", sig, decls);


        useCase.invariantPartitions.add(Utils.referentialIntegrity(employeeTable, "1", departmentTable, departmentRow, useCase));

//        Exp I1 = new Quantifier(
//                Quantifier.QUANTIFIER_TYPE.FORALL,
//                new Sig(new TDecl[]{new TDecl("p", employeeRow.type)}, null),
//                new Implication(
//                        new SetMembership(new Var("p"), new Selector(new Var("state"), "players")),
//                        new Gte(new Selector(new Var("p"), "1"), new IntLiteral(0))
//                )
//        );
//        Exp I2 = new Quantifier(
//                Quantifier.QUANTIFIER_TYPE.FORALL,
//                new Sig(new TDecl[]{new TDecl("t", departmentRow.type)}, null),
//                new Implication(
//                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments")),
//                        new Lt(new Selector(new Var("t"), "1"), new IntLiteral(6))
//                )
//
//        );

        Exp I3 = new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{new TDecl("e", employeeRow.type)}, null),
                new Implication(
                        new SetMembership(new Var("e"), new Selector(new Var("state"), "employees")),
                        new Gte(new Selector(new Var("e"), "2"), new IntLiteral(0))
                )
        );

        Exp part1 = Utils.universallyGeneralize(useCase.invariantPartitions.get(0), 1);

        SortType stateSortType = new SortType(useCase.stateTypeDecl);

        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(new And(I3, part1)));

        Fun addEmployee = new Fun("addEmp", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType)}, stateSortType), null);
        Fun removeEmployee = new Fun("removeEmp", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType)}, stateSortType), null);
        Fun addDep = new Fun("addDep", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("d", departmentRowSortType)}, stateSortType), null);
        Fun removeDep = new Fun("removeDep", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("d", departmentRowSortType)}, stateSortType), null);
        Fun incSalary = new Fun("incSalary", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType), new TDecl("salary", IntType.getInstance())}, stateSortType), null);
        Fun decSalary = new Fun("decSalary", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType), new TDecl("salary", IntType.getInstance())}, stateSortType), null);


        HashMap<String, Exp> addEmployeeBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("employees", new SetUnion(new Selector(useCase.stateVar, "employees"), new SingeltonLiteral(addEmployee.sig.argsMap.get("e"))));
            put("departments", new Selector(useCase.stateVar, "departments"));

        }};
        addEmployee.body = new Return(new RecordTypeConstructor(useCase.stateType, addEmployeeBodyArgs));

        HashMap<String, Exp> removeEmployeeBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("employees", new SetMinus(new Selector(useCase.stateVar, "employees"), new SingeltonLiteral(removeEmployee.sig.argsMap.get("e"))));
            put("departments", new Selector(useCase.stateVar, "departments"));

        }};
        removeEmployee.body = new Return(new RecordTypeConstructor(useCase.stateType, removeEmployeeBodyArgs));

        HashMap<String, Exp> addDepBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("employees", new Selector(useCase.stateVar, "employees"));
            put("departments", new SetUnion(new Selector(useCase.stateVar, "departments"), new SingeltonLiteral(addDep.sig.argsMap.get("d"))));

        }};
        addDep.body = new Return(new RecordTypeConstructor(useCase.stateType, addDepBodyArgs));

        HashMap<String, Exp> removeDepBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("employees", new Selector(useCase.stateVar, "employees"));
            put("departments", new SetMinus(new Selector(useCase.stateVar, "departments"), new SingeltonLiteral(removeDep.sig.argsMap.get("d"))));
        }};
        removeDep.body = new Return(new RecordTypeConstructor(useCase.stateType, removeDepBodyArgs));

        HashMap<String, Exp> incSalaryBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("employees", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "employees"), new SingeltonLiteral(incSalary.sig.argsMap.get("e"))), new SingeltonLiteral(new Tuple(new Selector(incSalary.sig.argsMap.get("e"), "0"), new Selector(incSalary.sig.argsMap.get("e"), "1"), new Plus(new Selector(incSalary.sig.argsMap.get("e"), "2"), incSalary.sig.argsMap.get("salary"))))));
            put("departments", new Selector(useCase.stateVar, "departments"));
        }};
        incSalary.body = new Return(new RecordTypeConstructor(useCase.stateType, incSalaryBodyArgs));

        HashMap<String, Exp> decSalaryBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("employees", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "employees"), new SingeltonLiteral(incSalary.sig.argsMap.get("e"))), new SingeltonLiteral(new Tuple(new Selector(incSalary.sig.argsMap.get("e"), "0"), new Selector(incSalary.sig.argsMap.get("e"), "1"), new Minus(new Selector(incSalary.sig.argsMap.get("e"), "2"), incSalary.sig.argsMap.get("salary"))))));
            put("departments", new Selector(useCase.stateVar, "departments"));
        }};
        decSalary.body = new Return(new RecordTypeConstructor(useCase.stateType, decSalaryBodyArgs));


        Fun g_addEmployee = new Fun("g_addEmp", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType)}, stateSortType), new Return(new Gte(new Selector(new Var("e"), "2"), new IntLiteral(0))));
        Fun g_removeEmployee = new Fun("g_removeEmp", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType)}, stateSortType), new Return(True.getInstance()));
        Fun g_addDep = new Fun("g_addDep", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("d", departmentRowSortType)}, stateSortType), new Return(True.getInstance()));
        Fun g_removeDep = new Fun("g_removeDep", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("d", departmentRowSortType)}, stateSortType), new Return(True.getInstance()));
        Fun g_incSalary = new Fun("g_incSalary", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType), new TDecl("salary", IntType.getInstance())}, stateSortType), new Return(
                new And(
                        new SetMembership(new Var("e"), new Selector(new Var("state"), "employees")),
                        True.getInstance()
                )));
        Fun g_decSalary = new Fun("g_decSalary", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("e", employeeRowSortType), new TDecl("salary", IntType.getInstance())}, stateSortType), new Return(
                new And(
                        new SetMembership(new Var("e"), new Selector(new Var("state"), "employees")),
                        True.getInstance()
                )));


        addEmployee.gaurd = g_addEmployee;
        removeEmployee.gaurd = g_removeEmployee;
        addDep.gaurd = g_addDep;
        removeDep.gaurd = g_removeDep;
        incSalary.gaurd = g_incSalary;
        decSalary.gaurd = g_decSalary;

        useCase.getOperations().add(addEmployee);
        useCase.getOperations().add(removeEmployee);
        useCase.getOperations().add(addDep);
        useCase.getOperations().add(removeDep);
        useCase.getOperations().add(incSalary);
        useCase.getOperations().add(decSalary);

        return useCase;

    }
}
