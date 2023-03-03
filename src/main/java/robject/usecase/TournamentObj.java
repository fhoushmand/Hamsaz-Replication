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

public class TournamentObj extends ReplicatedObject {
    public ObjectAST getASTFormat()
    {
        Type playerRow = new TupleType(IntType.getInstance(), IntType.getInstance());
        Type tournamentRow = new TupleType(IntType.getInstance(), IntType.getInstance(), BoolType.getInstance());
        Type enrolmentRow = new TupleType(IntType.getInstance(), IntType.getInstance());
        TDecl playerTable = new TDecl("players", new SetType(playerRow));
        TDecl tournametTable = new TDecl("tournaments", new SetType(tournamentRow));
        TDecl enrolmentTable = new TDecl("enrolments", new SetType(enrolmentRow));

        Sig sig = new Sig(new TDecl[]{playerTable, tournametTable, enrolmentTable}, null);

//        ObjectAST useCase = UseCaseFactory.initUseCase("courseware", sig, "/home/farzin/Documents/MyWorkspace/Synthesizer/src/consistency/cvclib-courseware-2");
        ObjectAST useCase = UseCaseFactory.initUseCase("tournament", sig);


        Exp I1 = new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{new TDecl("p", playerRow)}, null),
                new Implication(
                        new SetMembership(new Var("p"), new Selector(new Var("state"), "players")),
                        new Gt(new Selector(new Var("p"), "1"), new IntLiteral(0))
                )
        );
        Exp I2 = new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{new TDecl("t", tournamentRow)}, null),
                new Implication(
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments")),
                        new Lt(new Selector(new Var("t"), "1"), new IntLiteral(5))
                )

        );

        Exp I3 = new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{new TDecl("t", tournamentRow)}, null),
                new Implication(
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments")),
                        new Implication(
                                new Selector(new Var("t"), "2"),
                                new Gt(new Selector(new Var("t"), "1"), new IntLiteral(0))
                        )
                )
        );

        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", useCase.stateType)}, BoolType.getInstance()), new Return(new And(new And(I1,I2), I3)));






        Fun addplayer = new Fun("addPlayer", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow)}, useCase.stateType), null);
        Fun removeplayer = new Fun("removePlayer", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow)}, useCase.stateType), null);
        Fun addtour = new Fun("addTour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, useCase.stateType), null);
        Fun removetour = new Fun("removeTour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, useCase.stateType), null);
        Fun enroltour = new Fun("enrolTour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow), new TDecl("t", tournamentRow)}, useCase.stateType), null);
        Fun disenroltour = new Fun("disEnrolTour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow), new TDecl("t", tournamentRow)}, useCase.stateType), null);
        Fun begintour = new Fun("beginTour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, useCase.stateType), null);
        Fun endtour = new Fun("endTour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, useCase.stateType), null);
        Fun addfund = new Fun("addFund", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow), new TDecl("fund", IntType.getInstance())}, useCase.stateType), null);


        HashMap<String, Exp> addplayerBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetUnion(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(addplayer.sig.argsMap.get("p"))));
            put("tournaments", new Selector(useCase.stateVar, "tournaments"));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        addplayer.body = new Return(new RecordTypeConstructor(useCase.stateType, addplayerBodyArgs));

        HashMap<String, Exp> removeplayerBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetMinus(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(removeplayer.sig.argsMap.get("p"))));
            put("tournaments", new Selector(useCase.stateVar, "tournaments"));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        removeplayer.body = new Return(new RecordTypeConstructor(useCase.stateType, removeplayerBodyArgs));

        HashMap<String, Exp> addtourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetUnion(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(addtour.sig.argsMap.get("t"))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        addtour.body = new Return(new RecordTypeConstructor(useCase.stateType, addtourBodyArgs));

        HashMap<String, Exp> removetourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(removetour.sig.argsMap.get("t"))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        removetour.body = new Return(new RecordTypeConstructor(useCase.stateType, removetourBodyArgs));

        HashMap<String, Exp> enroltourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(enroltour.sig.argsMap.get("p"))), new SingeltonLiteral(new Tuple(new Selector(enroltour.sig.argsMap.get("p"), "0"), new Minus(new Selector(enroltour.sig.argsMap.get("p"), "1"), new IntLiteral(1))))));
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(enroltour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(enroltour.sig.argsMap.get("t"), "0"), new Plus(new Selector(enroltour.sig.argsMap.get("t"), "1"), new IntLiteral(1)), new Selector(enroltour.sig.argsMap.get("t"), "2")))));
            put("enrolments", new SetUnion(new Selector(useCase.stateVar, "enrolments"), new SingeltonLiteral(new Tuple(new Selector(enroltour.sig.argsMap.get("p"), "0"), new Selector(enroltour.sig.argsMap.get("t"), "0")))));
        }};
        enroltour.body = new Return(new RecordTypeConstructor(useCase.stateType, enroltourBodyArgs));

        HashMap<String, Exp> disenroltourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(disenroltour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(disenroltour.sig.argsMap.get("t"), "0"), new Minus(new Selector(disenroltour.sig.argsMap.get("t"), "1"), new IntLiteral(1)), new Selector(disenroltour.sig.argsMap.get("t"), "2")))));
            put("enrolments", new SetMinus(new Selector(useCase.stateVar, "enrolments"), new SingeltonLiteral(new Tuple(new Selector(disenroltour.sig.argsMap.get("p"), "0"), new Selector(disenroltour.sig.argsMap.get("t"), "0")))));
        }};
        disenroltour.body = new Return(new RecordTypeConstructor(useCase.stateType, disenroltourBodyArgs));

        HashMap<String, Exp> begintourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(endtour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(endtour.sig.argsMap.get("t"), "0"), new Selector(endtour.sig.argsMap.get("t"), "1"), True.getInstance()))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        begintour.body = new Return(new RecordTypeConstructor(useCase.stateType, begintourBodyArgs));

        HashMap<String, Exp> endtourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
//            new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(endtour.sig.argsMap.get("t")))
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(endtour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(endtour.sig.argsMap.get("t"), "0"), new Selector(endtour.sig.argsMap.get("t"), "1"), False.getInstance()))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        endtour.body = new Return(new RecordTypeConstructor(useCase.stateType, endtourBodyArgs));

        HashMap<String, Exp> addfundBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(addfund.sig.argsMap.get("p"))), new SingeltonLiteral(new Tuple(new Selector(addfund.sig.argsMap.get("p"), "0"), new Plus(new Selector(addfund.sig.argsMap.get("p"), "1"), addfund.sig.argsMap.get("fund"))))));
            put("tournaments", new Selector(useCase.stateVar, "tournaments"));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        addfund.body = new Return(new RecordTypeConstructor(useCase.stateType, addfundBodyArgs));



        Fun g_addplayer = new Fun("g_addplayer", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow)}, BoolType.getInstance()), new Return(new Gt(new Selector(new Var("p"), "1"), new IntLiteral(0)) ));
        Fun g_removeplayer = new Fun("g_removeplayer", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow)}, BoolType.getInstance()), new Return(True.getInstance()));
        Fun g_addtour = new Fun("g_addtour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, BoolType.getInstance()), new Return(new And(new Not(new Selector(new Var("t"), "2")), new Lt(new Selector(new Var("t"), "1"), new IntLiteral(5)))));
        Fun g_removetour = new Fun("g_removetour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, BoolType.getInstance()), new Return(True.getInstance()));
        Fun g_enroltour = new Fun("g_enroltour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow), new TDecl("t", tournamentRow)}, BoolType.getInstance()), new Return(
                new And(
                        new And(
                                new And(
                                        new Lt(new Selector(new Var("t"), "1"), new IntLiteral(5)),
                                        new Gt(new Selector(new Var("p"), "1"), new IntLiteral(0))
                                ),
                                new SetMembership(new Var("p"), new Selector(new Var("state"), "players"))),
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))
                )));
        Fun g_disenroltour = new Fun("g_disenroltour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow), new TDecl("t", tournamentRow)}, BoolType.getInstance()), new Return(
                new And(
                        new SetMembership(new Var("p"), new Selector(new Var("state"), "players")),
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))
                )));
        Fun g_begintour = new Fun("g_begintour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, BoolType.getInstance()), new Return(new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))));
        Fun g_endtour = new Fun("g_endtour", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("t", tournamentRow)}, BoolType.getInstance()), new Return(new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))));
        Fun g_addfund = new Fun("g_addfund", new Sig(new TDecl[]{new TDecl("state", useCase.stateType), new TDecl("p", playerRow), new TDecl("fund", IntType.getInstance())}, BoolType.getInstance()), new Return(new SetMembership(new Var("p"), new Selector(new Var("state"), "players"))));



        addplayer.gaurd = g_addplayer;
        removeplayer.gaurd = g_removeplayer;
        addtour.gaurd = g_addtour;
        removetour.gaurd = g_removetour;
        enroltour.gaurd = g_enroltour;
        disenroltour.gaurd = g_disenroltour;
        begintour.gaurd = g_begintour;
        endtour.gaurd = g_endtour;
        addfund.gaurd = g_addfund;
//
        useCase.getOperations().add(addplayer);
        useCase.getOperations().add(removeplayer);
        useCase.getOperations().add(addtour);
        useCase.getOperations().add(removetour);
        useCase.getOperations().add(enroltour);
        useCase.getOperations().add(disenroltour);
        useCase.getOperations().add(begintour);
        useCase.getOperations().add(endtour);
        useCase.getOperations().add(addfund);


        return useCase;

    }


    public static ObjectAST createTournament2UseCase()
    {
        TypeDecl playerRow = new TypeDecl("player", new TupleType(IntType.getInstance(), IntType.getInstance()));
        TypeDecl tournamentRow = new TypeDecl("tournament", new TupleType(IntType.getInstance(), IntType.getInstance(), BoolType.getInstance()));
        TypeDecl enrolmentRow = new TypeDecl("enrolment", new TupleType(IntType.getInstance(), IntType.getInstance()));

        Type playerRowSortType = new SortType(playerRow);
        Type tournamentRowSortType = new SortType(tournamentRow);
        Type enrolmentRowSortType = new SortType(enrolmentRow);

        TDecl playerTable = new TDecl("players", new SetType(playerRow.type));
        TDecl tournametTable = new TDecl("tournaments", new SetType(tournamentRow.type));
        TDecl enrolmentTable = new TDecl("enrolments", new SetType(enrolmentRow.type));

        ArrayList<TypeDecl> decls = new ArrayList<>();
        decls.add(playerRow);
        decls.add(tournamentRow);
        decls.add(enrolmentRow);

        Sig sig = new Sig(new TDecl[]{playerTable, tournametTable, enrolmentTable}, null);

//        ObjectAST useCase = UseCaseFactory.initUseCase("courseware", sig, "/home/farzin/Documents/MyWorkspace/Synthesizer/src/consistency/cvclib-courseware-2");
        ObjectAST useCase = UseCaseFactory.initUseCase("tournament", sig, decls);


        useCase.invariantPartitions.add(Utils.referentialIntegrity(enrolmentTable, "0", playerTable, playerRow, useCase));
        useCase.invariantPartitions.add(Utils.referentialIntegrity(enrolmentTable, "1", tournametTable, tournamentRow, useCase));

        Exp I1 = new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{new TDecl("p", playerRow.type)}, null),
                new Implication(
                        new SetMembership(new Var("p"), new Selector(new Var("state"), "players")),
                        new Gte(new Selector(new Var("p"), "1"), new IntLiteral(0))
                )
        );
        Exp I2 = new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{new TDecl("t", tournamentRow.type)}, null),
                new Implication(
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments")),
                        new Lt(new Selector(new Var("t"), "1"), new IntLiteral(6))
                )

        );

        Exp I3 = new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{new TDecl("t", tournamentRow.type)}, null),
                new Implication(
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments")),
                        new Implication(
                                new Selector(new Var("t"), "2"),
                                new Gt(new Selector(new Var("t"), "1"), new IntLiteral(0))
                        )
                )
        );

        Exp part1 = Utils.universallyGeneralize(useCase.invariantPartitions.get(0), 1);
        Exp part2 = Utils.universallyGeneralize(useCase.invariantPartitions.get(1), 1);

        SortType stateSortType = new SortType(useCase.stateTypeDecl);

        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", stateSortType)}, BoolType.getInstance()), new Return(new And(new And(new And(new And(I1,I2), I3), part1), part2)));






        Fun addplayer = new Fun("addPlayer", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType)}, stateSortType), null);
        Fun removeplayer = new Fun("removePlayer", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType)}, stateSortType), null);
        Fun addtour = new Fun("addTour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, stateSortType), null);
        Fun removetour = new Fun("removeTour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, stateSortType), null);
        Fun enroltour = new Fun("enrolTour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType), new TDecl("t", tournamentRowSortType)}, stateSortType), null);
        Fun disenroltour = new Fun("disEnrolTour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType), new TDecl("t", tournamentRowSortType)}, stateSortType), null);
        Fun begintour = new Fun("beginTour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, stateSortType), null);
        Fun endtour = new Fun("endTour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, stateSortType), null);
        Fun addfund = new Fun("addFund", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType), new TDecl("fund", IntType.getInstance())}, stateSortType), null);


        HashMap<String, Exp> addplayerBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetUnion(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(addplayer.sig.argsMap.get("p"))));
            put("tournaments", new Selector(useCase.stateVar, "tournaments"));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        addplayer.body = new Return(new RecordTypeConstructor(useCase.stateType, addplayerBodyArgs));

        HashMap<String, Exp> removeplayerBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetMinus(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(removeplayer.sig.argsMap.get("p"))));
            put("tournaments", new Selector(useCase.stateVar, "tournaments"));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        removeplayer.body = new Return(new RecordTypeConstructor(useCase.stateType, removeplayerBodyArgs));

        HashMap<String, Exp> addtourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetUnion(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(addtour.sig.argsMap.get("t"))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        addtour.body = new Return(new RecordTypeConstructor(useCase.stateType, addtourBodyArgs));

        HashMap<String, Exp> removetourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(removetour.sig.argsMap.get("t"))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        removetour.body = new Return(new RecordTypeConstructor(useCase.stateType, removetourBodyArgs));

        HashMap<String, Exp> enroltourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(enroltour.sig.argsMap.get("p"))), new SingeltonLiteral(new Tuple(new Selector(enroltour.sig.argsMap.get("p"), "0"), new Minus(new Selector(enroltour.sig.argsMap.get("p"), "1"), new IntLiteral(1))))));
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(enroltour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(enroltour.sig.argsMap.get("t"), "0"), new Plus(new Selector(enroltour.sig.argsMap.get("t"), "1"), new IntLiteral(1)), new Selector(enroltour.sig.argsMap.get("t"), "2")))));
            put("enrolments", new SetUnion(new Selector(useCase.stateVar, "enrolments"), new SingeltonLiteral(new Tuple(new Selector(enroltour.sig.argsMap.get("p"), "0"), new Selector(enroltour.sig.argsMap.get("t"), "0")))));
        }};
        enroltour.body = new Return(new RecordTypeConstructor(useCase.stateType, enroltourBodyArgs));

        HashMap<String, Exp> disenroltourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(disenroltour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(disenroltour.sig.argsMap.get("t"), "0"), new Minus(new Selector(disenroltour.sig.argsMap.get("t"), "1"), new IntLiteral(1)), new Selector(disenroltour.sig.argsMap.get("t"), "2")))));
            put("enrolments", new SetMinus(new Selector(useCase.stateVar, "enrolments"), new SingeltonLiteral(new Tuple(new Selector(disenroltour.sig.argsMap.get("p"), "0"), new Selector(disenroltour.sig.argsMap.get("t"), "0")))));
        }};
        disenroltour.body = new Return(new RecordTypeConstructor(useCase.stateType, disenroltourBodyArgs));

        HashMap<String, Exp> begintourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(endtour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(endtour.sig.argsMap.get("t"), "0"), new Selector(endtour.sig.argsMap.get("t"), "1"), True.getInstance()))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        begintour.body = new Return(new RecordTypeConstructor(useCase.stateType, begintourBodyArgs));

        HashMap<String, Exp> endtourBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new Selector(useCase.stateVar, "players"));
//            new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(endtour.sig.argsMap.get("t")))
            put("tournaments", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "tournaments"), new SingeltonLiteral(endtour.sig.argsMap.get("t"))), new SingeltonLiteral(new Tuple(new Selector(endtour.sig.argsMap.get("t"), "0"), new Selector(endtour.sig.argsMap.get("t"), "1"), False.getInstance()))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        endtour.body = new Return(new RecordTypeConstructor(useCase.stateType, endtourBodyArgs));

        HashMap<String, Exp> addfundBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("players", new SetUnion(new SetMinus(new Selector(useCase.stateVar, "players"), new SingeltonLiteral(addfund.sig.argsMap.get("p"))), new SingeltonLiteral(new Tuple(new Selector(addfund.sig.argsMap.get("p"), "0"), new Plus(new Selector(addfund.sig.argsMap.get("p"), "1"), addfund.sig.argsMap.get("fund"))))));
            put("tournaments", new Selector(useCase.stateVar, "tournaments"));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        addfund.body = new Return(new RecordTypeConstructor(useCase.stateType, addfundBodyArgs));



        Fun g_addplayer = new Fun("g_addplayer", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType)}, BoolType.getInstance()), new Return(new Gte(new Selector(new Var("p"), "1"), new IntLiteral(0)) ));
        Fun g_removeplayer = new Fun("g_removeplayer", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType)}, BoolType.getInstance()), new Return(True.getInstance()));
        Fun g_addtour = new Fun("g_addtour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, BoolType.getInstance()), new Return(new And(new Not(new Selector(new Var("t"), "2")), new Lt(new Selector(new Var("t"), "1"), new IntLiteral(5)))));
        Fun g_removetour = new Fun("g_removetour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, BoolType.getInstance()), new Return(True.getInstance()));
        Fun g_enroltour = new Fun("g_enroltour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType), new TDecl("t", tournamentRowSortType)}, BoolType.getInstance()), new Return(
                new And(
                        new And(
                                new And(
                                        new Lt(new Selector(new Var("t"), "1"), new IntLiteral(5)),
                                        new Gt(new Selector(new Var("p"), "1"), new IntLiteral(0))
                                ),
                                new SetMembership(new Var("p"), new Selector(new Var("state"), "players"))),
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))
                )));
        Fun g_disenroltour = new Fun("g_disenroltour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType), new TDecl("t", tournamentRowSortType)}, BoolType.getInstance()), new Return(
                new And(
                        new SetMembership(new Var("p"), new Selector(new Var("state"), "players")),
                        new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))
                )));
        Fun g_begintour = new Fun("g_begintour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, BoolType.getInstance()), new Return(new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))));
        Fun g_endtour = new Fun("g_endtour", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("t", tournamentRowSortType)}, BoolType.getInstance()), new Return(new SetMembership(new Var("t"), new Selector(new Var("state"), "tournaments"))));
        Fun g_addfund = new Fun("g_addfund", new Sig(new TDecl[]{new TDecl("state", stateSortType), new TDecl("p", playerRowSortType), new TDecl("fund", IntType.getInstance())}, BoolType.getInstance()), new Return(new And(new SetMembership(new Var("p"), new Selector(new Var("state"), "players")), new Gte(new Var("fund"), new IntLiteral(0)))));



        addplayer.gaurd = g_addplayer;
        removeplayer.gaurd = g_removeplayer;
        addtour.gaurd = g_addtour;
        removetour.gaurd = g_removetour;
        enroltour.gaurd = g_enroltour;
        disenroltour.gaurd = g_disenroltour;
        begintour.gaurd = g_begintour;
        endtour.gaurd = g_endtour;
        addfund.gaurd = g_addfund;
//
        useCase.getOperations().add(addplayer);
        useCase.getOperations().add(removeplayer);
        useCase.getOperations().add(addtour);
        useCase.getOperations().add(removetour);
        useCase.getOperations().add(enroltour);
        useCase.getOperations().add(disenroltour);
        useCase.getOperations().add(begintour);
        useCase.getOperations().add(endtour);
        useCase.getOperations().add(addfund);


        return useCase;

    }
}
