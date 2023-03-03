package main.java.robject.usecase;

import main.java.analyser.ObjectAST;
import main.java.analyser.UseCaseFactory;
import main.java.utils.Utils;
import main.java.language.ast.*;
import main.java.language.type.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import main.java.robject.Guard;
import main.java.robject.ReplicatedObject;
import robject.ReplicatedObjectState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CoursewareObj extends ReplicatedObject {


    public CoursewareObj()
    {
        super();
        setState(new CoursewareState());
    }

    @Guard("g_register")
    public ReplicatedObjectState register(Integer x)
    {
        CoursewareState state = new CoursewareState();
        state.students.addAll(((CoursewareState)(getState())).students);
        state.courses.addAll(((CoursewareState)(getState())).courses);
        state.enrolments.addAll(((CoursewareState)(getState())).enrolments);
        state.students.add(x);
        return state;
    }

    public boolean g_register(Integer x) { return true; }

    @Guard("g_addCourse")
    public ReplicatedObjectState addCourse(Integer x)
    {
        CoursewareState state = new CoursewareState();
        state.students.addAll(((CoursewareState)(getState())).students);
        state.courses.addAll(((CoursewareState)(getState())).courses);
        state.enrolments.addAll(((CoursewareState)(getState())).enrolments);
        state.courses.add(x);
        return state;
    }

    public boolean g_addCourse(Integer x)
    {
        return true;
    }

    @Guard("g_enroll")
    public ReplicatedObjectState enroll(Integer s, Integer c)
    {
        CoursewareState state = new CoursewareState();
        state.students.addAll(((CoursewareState)(getState())).students);
        state.courses.addAll(((CoursewareState)(getState())).courses);
        state.enrolments.addAll(((CoursewareState)(getState())).enrolments);
        state.enrolments.add(new ImmutablePair<>(s,c));
        return state;
    }

    public boolean g_enroll(Integer s, Integer c)
    {
        return true;
    }

    @Guard("g_deleteCourse")
    public ReplicatedObjectState deleteCourse(Integer x)
    {
        CoursewareState state = new CoursewareState();
        state.students.addAll(((CoursewareState)(getState())).students);
        state.courses.addAll(((CoursewareState)(getState())).courses);
        state.enrolments.addAll(((CoursewareState)(getState())).enrolments);
        state.courses.remove(x);
        return state;
    }

    public boolean g_deleteCourse(Integer x)
    {
        return true;
    }

    @Guard("g_query")
    public ReplicatedObjectState query()
    {
        CoursewareState state;
        state = (CoursewareState) getState();
        return state;
    }

    public boolean g_query() { return true;}

    public boolean invariant(ReplicatedObjectState s)
    {
        for (ImmutablePair<Integer, Integer> e : ((CoursewareState)s).enrolments)
        {
            if(!((CoursewareState)s).students.contains(e.getKey()) ||!((CoursewareState)s).courses.contains(e.getValue()))
                return false;
        }
        return true;
    }

    public ObjectAST getASTFormat()
    {
        TypeDecl studentRow = new TypeDecl("student", new TupleType(IntType.getInstance()));
        TypeDecl courseRow = new TypeDecl("course", new TupleType(IntType.getInstance()));
        TypeDecl enrolmentRow = new TypeDecl("enrolment", new TupleType(IntType.getInstance(), IntType.getInstance()));

        TDecl studentTable = new TDecl("students", new SetType(studentRow.type));
        TDecl courseTable = new TDecl("courses", new SetType(courseRow.type));
        TDecl enrolmentTable = new TDecl("enrolments", new SetType(enrolmentRow.type));

        ArrayList<TypeDecl> decls = new ArrayList<>();
        decls.add(studentRow);
        decls.add(courseRow);
        decls.add(enrolmentRow);

        Sig sig = new Sig(new TDecl[]{studentTable, courseTable, enrolmentTable}, null);

//        ObjectAST useCase = UseCaseFactory.initUseCase("courseware", sig, "/home/farzin/Documents/MyWorkspace/Synthesizer/src/consistency/cvclib-courseware");
        ObjectAST useCase = UseCaseFactory.initUseCase("courseware", sig, decls);

        Type sortState = new SortType(useCase.stateTypeDecl);

        Type studentRowSort = new SortType(studentRow);
        Type courseRowSort = new SortType(courseRow);
        Type enrolmentRowSort = new SortType(enrolmentRow);

        useCase.invariantPartitions.add(Utils.referentialIntegrity(enrolmentTable, "0", studentTable, studentRow, useCase));
        useCase.invariantPartitions.add(Utils.referentialIntegrity(enrolmentTable, "1", courseTable, courseRow, useCase));

        Exp ref1 = Utils.universallyGeneralize(useCase.invariantPartitions.get(0), 1);
        Exp ref2 = Utils.universallyGeneralize(useCase.invariantPartitions.get(1), 1);

        Statement invariantBody = new Return(new And(ref1, ref2));

        useCase.invariant = new Fun("I", new Sig(new TDecl[]{new TDecl("state", sortState)}, BoolType.getInstance()), invariantBody);

        Fun register = new Fun("register", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("s", studentRowSort)}, sortState), null);
        Fun addcourse = new Fun("addCourse", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("c", courseRowSort)}, sortState), null);
        Fun enrol = new Fun("enroll", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("s", studentRowSort), new TDecl("c", courseRowSort)}, sortState), null);
        Fun remcourse = new Fun("deleteCourse", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("c", courseRowSort)}, sortState), null);
        Fun query = new Fun("query", new Sig(new TDecl[]{new TDecl("state", sortState)}, sortState), null);

        HashMap<String, Exp> registerBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("students", new SetUnion(new Selector(useCase.stateVar, "students"), new SingeltonLiteral(register.sig.argsMap.get("s"))));
            put("courses", new Selector(useCase.stateVar, "courses"));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        Statement registerBody = new Return(new RecordTypeConstructor(useCase.stateType, registerBodyArgs));
        register.body = registerBody;

        HashMap<String, Exp> addCourseBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("students", new Selector(useCase.stateVar, "students"));
            put("courses", new SetUnion(new Selector(useCase.stateVar, "courses"), new SingeltonLiteral(addcourse.sig.argsMap.get("c"))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        Statement addcourseBody = new Return(new RecordTypeConstructor(useCase.stateType, addCourseBodyArgs));
        addcourse.body = addcourseBody;


        HashMap<String, Exp> enrolBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("students", new Selector(useCase.stateVar, "students"));
            put("courses", new Selector(useCase.stateVar, "courses"));
            put("enrolments", new SetUnion(new Selector(useCase.stateVar, "enrolments"), new SingeltonLiteral(new Tuple(new Selector(new Var("s"), "0"),new Selector(new Var("c"), "0")))));
        }};
        Statement enrolBody = new Return(new RecordTypeConstructor(useCase.stateType, enrolBodyArgs));
        enrol.body = enrolBody;


        HashMap<String, Exp> remcourseBodyArgs = new LinkedHashMap<String, Exp>(){{
            put("students", new Selector(useCase.stateVar, "students"));
            put("courses", new SetMinus(new Selector(useCase.stateVar, "courses"), new SingeltonLiteral(remcourse.sig.argsMap.get("c"))));
            put("enrolments", new Selector(useCase.stateVar, "enrolments"));
        }};
        Statement remcourseBody = new Return(new RecordTypeConstructor(useCase.stateType, remcourseBodyArgs));
        remcourse.body = remcourseBody;

        Statement queryBody = new Return(useCase.stateVar);
        query.body = queryBody;

        Exp registerWP = True.getInstance();
        Exp addcourseWP = True.getInstance();
        Exp enrolWP = True.getInstance();
        Exp remcourseWP = True.getInstance();
        Exp queryWP = True.getInstance();

        register.gaurd = new Fun("g_register", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("s", studentRowSort)}, BoolType.getInstance()), new Return(registerWP));
        addcourse.gaurd = new Fun("g_addCourse", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("c", courseRowSort)}, BoolType.getInstance()), new Return(addcourseWP));
        enrol.gaurd = new Fun("g_enroll", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("s", studentRowSort), new TDecl("c", courseRowSort)}, BoolType.getInstance()), new Return(enrolWP));
        remcourse.gaurd = new Fun("g_deleteCourse", new Sig(new TDecl[]{new TDecl("state", sortState), new TDecl("c", courseRowSort)}, BoolType.getInstance()), new Return(remcourseWP));
        query.gaurd = new Fun("g_query", new Sig(new TDecl[]{new TDecl("state", sortState)}, BoolType.getInstance()), new Return(queryWP));

        useCase.getOperations().add(register);
        useCase.getOperations().add(addcourse);
        useCase.getOperations().add(enrol);
        useCase.getOperations().add(remcourse);
        useCase.getOperations().add(query);

        return useCase;

    }


}

