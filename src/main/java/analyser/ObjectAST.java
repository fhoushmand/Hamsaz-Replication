package main.java.analyser;

import main.java.language.ast.*;
import main.java.utils.Constants;
import main.java.language.type.RecordType;
import main.java.language.type.Type;
import main.java.language.visitor.CVCIPrinter;
import main.java.utils.collection.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ObjectAST {



    public String name;
    public TDecl stateDecl;
    public TypeDecl stateTypeDecl;
    public Exp stateVar;
    protected int id = 1;
    public  int invarPartiId = 1;
    public Fun invariant;
    public ArrayList<Fun> invariantPartitions = new ArrayList<>();
    public HashMap<Fun, TDecl> invariantPartitionTypes= new HashMap<>();
    public ArrayList<Fun> uniqueKeyPartitions = new ArrayList<>();
    public ArrayList<TypeDecl> declerations = new ArrayList<>();
    public RecordType stateType;
    public Sig stateSig;
    protected ArrayList<Fun> operations = new ArrayList<>();
    public ArrayList<Fun> utilityFunctions = new ArrayList<>();
    public ArrayList<Assertion> utilityAssertions = new ArrayList<>();
    public ArrayList<Assertion> relationalAlgebraAssertions = new ArrayList<>();


    public ArrayList<Fun> getOperations() {
        return operations;
    }

    public ObjectAST() {}

    public String getSMTLib()
    {
        StringBuilder builder = new StringBuilder();
        try {
            String text = Files.readString(Paths.get(Constants.CVC_LIB_PATH));
            builder.append(text);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (TypeDecl decl : declerations) {
            builder.append(CVCIPrinter.print(decl)).append("\n");
        }

        builder.append(CVCIPrinter.print(stateTypeDecl)).append("\n");

        for (Fun f : utilityFunctions) {
            builder.append(CVCIPrinter.print(f)).append("\n");
        }

        for (Assertion a : utilityAssertions) {
            builder.append(CVCIPrinter.print(a)).append("\n");
        }

        for (Fun f : getOperations()) {
            builder.append(CVCIPrinter.print(f)).append("\n");
            builder.append(CVCIPrinter.print(f.gaurd)).append("\n");
        }

        for (Fun a : invariantPartitions) {
            builder.append(CVCIPrinter.print(a)).append("\n");
        }

        for (Fun u : uniqueKeyPartitions){
            builder.append(CVCIPrinter.print(u)).append("\n");
        }

        builder.append(CVCIPrinter.print(invariant)).append("\n");
        builder.append("state: State;\n");

        //for relational algebra, we have to declare the unique key in the state.
        for (Assertion ra : relationalAlgebraAssertions) {
            builder.append(CVCIPrinter.print(ra)).append("\n");
        }
        return builder.toString();
    }


    public Pair<String, String> createNewConst(TDecl tdecl)
    {
        Pair<String, String> out;
        if(!tdecl.name.equals("state")) {
            String newName = tdecl.name + (id++);
            VarDecl newVarDecl = new VarDecl(new Var(newName), tdecl.type);
            out = new Pair<>(newName, CVCIPrinter.print(newVarDecl)+"\n" );
        }
        else
            out = new Pair<>("state", "");
        return out;
    }

    //need to assert I(state) and P(f2) and P(f1)
    private boolean applyTheoremForInvariantCommutativity(Fun f1, Fun f2, Fun invariant, Exp cond)
    {
        StringBuilder definitions = new StringBuilder();
        // _f1 and g_f1
        HashMap<String,String> f1Input = new HashMap<>();
        HashMap<String,String> g_f1Input = new HashMap<>();

        //store potential tag in the input
        String t1 = "";
        String t2 = "";
        //store potential rSet for cancel method's effect
        String r1 = "";
        String r2 = "";
        for (TDecl t : f1.sig.pars) {

            Pair newName = createNewConst(t);
            f1Input.put(t.name, (String) newName._1());
            g_f1Input.put(t.name, (String) newName._1());
            definitions.append(newName._2()).append("\n");

            if(t.name.equals("t")){
                t1 = f1Input.get(t.name);
            }
            else if(t.name.equals("rSet")){
                r1 = f1Input.get(t.name);
            }
        }
        String _f1 = f1.functionApplication(f1Input);
        String g1_f1 = f1.gaurd.functionApplication(g_f1Input);

        // _f2 and g_f2
        HashMap<String,String> f2Input = new HashMap<>();
        HashMap<String,String> g_f2Input = new HashMap<>();
        for (TDecl t : f2.sig.pars)
        {
            Pair newName = createNewConst(t);
            f2Input.put(t.name, (String) newName._1());
            g_f2Input.put(t.name, (String) newName._1());
            definitions.append(newName._2()).append("\n");
            if(t.name.equals("t")){
                t2 = f2Input.get(t.name);
            }
            else if(t.name.equals("rSet")){
                r2 = f2Input.get(t.name);
            }
        }
        String _f2 = f2.functionApplication(f2Input);
        String g1_f2 = f2.gaurd.functionApplication(g_f2Input);

        // _f1_f2 and _f2_f1
        HashMap<String,String> f1InputRev = new HashMap<>(f1Input);
        f1InputRev.put("state", _f2);

        String _f1_f2 = f1.functionApplication(f1InputRev);
        HashMap<String,String> g1_f1InputRev = new HashMap<>(g_f1Input);
        g1_f1InputRev.put("state", _f2);

        String _gf1f2 = f1.gaurd.functionApplication(g1_f1InputRev);
        //I(f1) I(f2)
        HashMap<String,String> I1Input = new HashMap<>();
        HashMap<String,String> I_Update_f1_f2state = new HashMap<>();
        HashMap<String,String> I2Input = new HashMap<>();

        I1Input.put("state", _f1);
        I2Input.put("state", _f2);
        //this input may need to change
        String _I1Call = invariant.functionApplication(I1Input);
        String _I2Call = invariant.functionApplication(I2Input);

        I_Update_f1_f2state.put("state", _f1_f2);
        String _I_f1_f2 = invariant.functionApplication(I_Update_f1_f2state);

        String conjecture = "((%s AND %s AND %s AND %s) => (%s AND %s));\n ";
        String z3Input = String.format(conjecture, g1_f1, _I1Call, g1_f2, _I2Call, _gf1f2, _I_f1_f2);

        //find all the input involves t and make them all different
        String argumentAssertion = "";
        String effectAssertion1 = "";
        String effectAssertion2 = "";
        if((t1 != null) && (t1.length() != 0) && (t2 != null) && (t2.length() != 0)){
            Exp aa = new NEq(new Var(t1), new Var(t2));
            argumentAssertion = CVCIPrinter.print(aa);
        }
        if((r1 != null) && (r1.length() != 0)){
            Exp ea = new Eq(new Var(r1), new ArraySelect(new Projection(new Var("state"), "reservations"), new Var(f1Input.get("r"))));
            effectAssertion1 = CVCIPrinter.print(ea);
        }
        if((r2 != null) && (r2.length() != 0)){
            Exp ea = new Eq(new Var(r2), new ArraySelect(new Projection(new Var("state"), "reservations"), new Var(f2Input.get("r"))));
            effectAssertion2 = CVCIPrinter.print(ea);
        }
        return CVC4Lib.solve(this.name, getSMTLib()+ definitions,z3Input, "invar-commute-local-normal-"+ f1.name+"-"+f2.name, argumentAssertion, effectAssertion1, effectAssertion2);
    }

    public boolean checkInvariantCommutativity(Fun f1, Fun f2)
    {
        //first step
        if(isInvariantPreserving(f1))
            return true;

        //second step
        return applyTheoremForInvariantCommutativity(f1, f2, invariant, True.getInstance());
    }

    public boolean checkDependency(Fun f1, Fun f2)
    {
        //first step
        if(isInvariantPreserving(f2)){
            return true;
        }
        return applyTheoremForDependency(f1, f2, invariant, True.getInstance());
    }

    private boolean applyTheoremForDependency(Fun f1, Fun f2, Fun invariant, Exp conditions)
    {
        Type type = null;
        String splitArg = null;
        String splitArgInvar = null;
        if(invariant.sig.pars.length > 1) {
            type = invariant.sig.pars[1].type;
        }
        StringBuilder definitions = new StringBuilder();
        // _f1
        HashMap<String,String> f1Input = new HashMap<>();
        HashMap<String,String> g_f1Input = new HashMap<>();

        //store potential tag in the input
        String t1 = "";
        String t2 = "";
        //store potential rSet for cancel method's effect
        String r1 = "";
        String r2 = "";
        for (TDecl t : f1.sig.pars)
        {
            Pair newName = createNewConst(t);
            f1Input.put(t.name, (String) newName._1());
            g_f1Input.put(t.name, (String) newName._1());
            definitions.append((String) newName._2()).append("\n");

            if(t.name.equals("t")){
                t1 = f1Input.get(t.name);
            }
            else if(t.name.equals("rSet")){
                r1 = f1Input.get(t.name);
            }
        }
        String _f1 = f1.functionApplication(f1Input);
        String _gf1 = f1.gaurd.functionApplication(g_f1Input);

        // _f2 and g_f2(f1)
        HashMap<String,String> f2Input = new HashMap<>();
        for (TDecl t : f2.sig.pars) {
            Pair newName = createNewConst(t);
            f2Input.put(t.name, (String) newName._1());
            definitions.append(newName._2()).append("\n");

            if(t.name.equals("t")){
                t2 = f2Input.get(t.name);
            }
            else if(t.name.equals("rSet")){
                r2 = f2Input.get(t.name);
            }
            try {
                if(type != null && type.equals(t.type))
                    splitArg = (String) newName._1();
            }
            catch (ClassCastException e) {}
        }
        String _f2 = f2.functionApplication(f2Input);
        HashMap<String, String> _gf2_f1_Input = new HashMap<>(f2Input);
        _gf2_f1_Input.put("state", _f1);
        String _gf2_f1 = f2.gaurd.functionApplication(_gf2_f1_Input);
        String _gf2 = f2.gaurd.functionApplication(f2Input);

        HashMap<String,String> _I_f2_f1_Input = new HashMap<>();
        HashMap<String,String> _I_f2_Input = new HashMap<>();
        if(splitArg != null)
        {
            Pair newName = createNewConst(new TDecl(((SortType)type).type.name, type));
            _I_f2_f1_Input.put(invariant.sig.pars[1].name, (String) newName._1());
            _I_f2_Input.put(invariant.sig.pars[1].name, (String) newName._1());
            splitArgInvar = (String)newName._1();
            definitions.append(newName._2()).append("\n");
        }

        // I(f2(f1))
        HashMap<String,String> f2InputRev = new HashMap<>(f2Input);
        f2InputRev.put("state", _f1);
        String _f2_f1 = f2.functionApplication(f2InputRev);
        _I_f2_f1_Input.put("state", _f2_f1);
        String _I_f2_f1 = invariant.functionApplication(_I_f2_f1_Input);
        _I_f2_Input.put("state", _f2);
        String _I_f2 = invariant.functionApplication(_I_f2_Input);
        //

        String cond;

        //find all the input involves t and make them all different
        String argumentAssertion = "";
        String effectAssertion1 = "";
        String effectAssertion2 = "";
        if((t1 != null) && (t1.length() != 0) && (t2 != null) && (t2.length() != 0)){
            Exp aa = new NEq(new Var(t1), new Var(t2));
            argumentAssertion = CVCIPrinter.print(aa);
        }
        if((r1 != null) && (r1.length() != 0)){
            Exp ea = new Eq(new Var(r1), new ArraySelect(new Projection(new Var("state"), "reservations"), new Var(f1Input.get("r"))));
            effectAssertion1 = CVCIPrinter.print(ea);
        }
        if((r2 != null) && (r2.length() != 0)){
            Exp ea = new Eq(new Var(r2), new ArraySelect(new Projection(new Var("state"), "reservations"), new Var(f2Input.get("r"))));
            effectAssertion2 = CVCIPrinter.print(ea);
        }
        if(splitArg != null)
        {
            cond = CVCIPrinter.print(new Eq(new Var(splitArg), new Var(splitArgInvar)));
            //check for bypass
            _I_f2_f1_Input.put("state", "state");
            String preStateInvar = invariant.functionApplication(_I_f2_f1_Input);
            String bypassCond = String.format("((%s AND %s) => (%s));", cond,preStateInvar, _I_f2);
            boolean canBypassFirstBranch = CVC4Lib.solve(this.name, getSMTLib()+definitions.toString(),bypassCond, "dep-bypass1-"+invariant.name+"-"+f1.name+"-"+f2.name, argumentAssertion, effectAssertion1, effectAssertion2);
            String conjecture = "((%s AND %s AND %s AND %s) =>  (%s AND %s)); \n";
            boolean firstBranchEqual = CVC4Lib.solve(this.name,getSMTLib() + definitions.toString(), String.format(conjecture, cond, _gf2_f1, _I_f2_f1, _gf1, _gf2, _I_f2), "dep-split1-"+ f1.name+"-"+f2.name, argumentAssertion, effectAssertion1, effectAssertion2);

            cond = CVCIPrinter.print(new NEq(new Var(splitArg), new Var(splitArgInvar)));
            //check for bypass
            _I_f2_f1_Input.put("state", "state");
            preStateInvar = invariant.functionApplication(_I_f2_f1_Input);
            bypassCond = String.format("((%s AND %s) => (%s));", cond,preStateInvar, _I_f2);
            boolean canBypassSecondBranch = CVC4Lib.solve(this.name, getSMTLib()+ definitions,bypassCond, "dep-bypass2-"+invariant.name+"-"+f1.name+"-"+f2.name, argumentAssertion, effectAssertion1, effectAssertion2);
            conjecture = "((%s AND %s AND %s AND %s) =>  (%s AND %s)); \n";
            boolean secondBranchNotEqual = CVC4Lib.solve(this.name, getSMTLib() + definitions, String.format(conjecture, cond, _gf2_f1, _I_f2_f1, _gf1, _gf2, _I_f2), "dep-split2-"+ f1.name+"-"+f2.name, argumentAssertion, effectAssertion1, effectAssertion2);

            if((canBypassFirstBranch || firstBranchEqual) && (canBypassSecondBranch || secondBranchNotEqual))
                return true;
        }

        String conjecture = "((%s AND %s AND %s) =>  (%s AND %s)); \n";

        return CVC4Lib.solve(this.name, getSMTLib() + definitions, String.format(conjecture, _gf2_f1, _I_f2_f1, _gf1, _gf2, _I_f2), "dep-normal-"+ f1.name+"-"+f2.name, argumentAssertion, effectAssertion1, effectAssertion2);
    }

    //need to add guard in the conclusion
    //_f1 means executing f1 with default arguments -> f1
    //_f1_f2 means executing f1 with output state of f2 -> f1(f2)
    public boolean checkStateCommutativity(Fun f1, Fun f2)
    {
        StringBuilder definitions = new StringBuilder();
        // _f1 and lg_f1
        HashMap<String,String> f1Input = new HashMap<>();
        //store potential tag in the input
        String t1 = "";
        String t2 = "";
        //store potential rSet for cancel method's effect
        String r1 = "";
        String r2 = "";
        for (TDecl t : f1.sig.pars)
        {
            Pair newName = createNewConst(t);
            f1Input.put(t.name, (String) newName._1());
            definitions.append(newName._2()).append("\n");
            if(t.name.equals("t")){
                t1 = f1Input.get(t.name);
            }
            else if(t.name.equals("rSet")){
                r1 = f1Input.get(t.name);
            }
        }
        String _f1 = f1.functionApplication(f1Input);
        String _g_f1 = f1.gaurd.functionApplication(f1Input);

        // _f2 and lg_f2
        HashMap<String,String> f2Input = new HashMap<>();
        for (TDecl t : f2.sig.pars)
        {
            Pair newName = createNewConst(t);
            f2Input.put(t.name, (String) newName._1());
            definitions.append((String) newName._2()).append("\n");
            if(t.name.equals("t")){
                t2 = f2Input.get(t.name);
            }
            else if(t.name.equals("rSet")){
                r2 = f2Input.get(t.name);
            }
        }
        String _f2 = f2.functionApplication(f2Input);
        String _g_f2 = f2.gaurd.functionApplication(f2Input);

        // _f1_f2 and _f2_f1
        HashMap<String,String> f2InputRev = new HashMap<>(f2Input);
        f2InputRev.put("state", _f1);

        HashMap<String,String> f1InputRev = new HashMap<>(f1Input);
        f1InputRev.put("state", _f2);

        String _f1_f2 = f1.functionApplication(f1InputRev);
        String _f2_f1 = f2.functionApplication(f2InputRev);

        String _g_f1_f2 = f2.gaurd.functionApplication(f2InputRev);
        String _g_f2_f1 = f1.gaurd.functionApplication(f1InputRev);

        String conjecture = "((%s AND %s) => (%s AND %s AND (%s = %s)));\n";
        String z3Input = String.format(conjecture, _g_f1, _g_f2, _g_f1_f2, _g_f2_f1,  _f1_f2, _f2_f1);

        //find all the input involves t and make them all different
        String argumentAssertion = "";
        String effectAssertion1 = "";
        String effectAssertion2 = "";
        if((t1 != null) && (t1.length() != 0) && (t2 != null) && (t2.length() != 0)){
            Exp aa = new NEq(new Var(t1), new Var(t2));
            argumentAssertion = CVCIPrinter.print(aa);
        }
        if((r1 != null) && (r1.length() != 0)){
            Exp ea = new Eq(new Var(r1), new ArraySelect(new Projection(new Var("state"), "reservations"), new Var(f1Input.get("r"))));
            effectAssertion1 = CVCIPrinter.print(ea);
        }
        if((r2 != null) && (r2.length() != 0)){
            Exp ea = new Eq(new Var(r2), new ArraySelect(new Projection(new Var("state"), "reservations"), new Var(f2Input.get("r"))));
            effectAssertion2 = CVCIPrinter.print(ea);
        }
        return CVC4Lib.solve(this.name,getSMTLib() + definitions.toString(), z3Input,"state-commute-"+ f1.name+"-"+f2.name, argumentAssertion, effectAssertion1, effectAssertion2);

    }


    private boolean isInvariantPreserving(Fun f)
    {
        StringBuilder definitions = new StringBuilder();
        HashMap<String,String> f1Input = new HashMap<>();
        HashMap<String,String> g_f1Input = new HashMap<>();
        String r = "";
        for (TDecl t : f.sig.pars)
        {
            Pair newName = createNewConst(t);
            f1Input.put(t.name, (String) newName._1());
            g_f1Input.put(t.name, (String) newName._1());
            definitions.append(newName._2()).append("\n");
            if(t.name.equals("rSet")){
                r = f1Input.get(t.name);
            }
        }
        String _f1 = f.functionApplication(f1Input);
        String g1_f1 = f.gaurd.functionApplication(g_f1Input);
        HashMap<String,String> I1Input = new HashMap<>();
        I1Input.put("state", _f1);
        String _I1Call = invariant.functionApplication(I1Input);
        String conjecture = "((%s AND %s) => (%s));";
        HashMap<String,String> globalStateInvariantInput = new HashMap<>();
        globalStateInvariantInput.put("state", "state");
        String globalStateInvariant = invariant.functionApplication(globalStateInvariantInput);

        String effectAssertion1 = "";
        if((r != null) && (r.length() != 0)){
            Exp ea = new Eq(new Var(r), new ArraySelect(new Projection(new Var("state"), "reservations"), new Var(f1Input.get("r"))));
            effectAssertion1 = CVCIPrinter.print(ea);
        }
        return CVC4Lib.solve(this.name, getSMTLib() + definitions, String.format(conjecture, globalStateInvariant, g1_f1, _I1Call), "invariantpres-"+f.name, effectAssertion1);
    }

    public boolean checkDependency22(Fun f1, Fun f2)
    {
        //first step
        if(isInvariantPreserving(f2))
            return true;
        //second step
        boolean midRes = applyTheoremForDependency(f1, f2, invariant, True.getInstance());

        if(midRes)
            return true;
            //second step: split
        else
        {
            if(invariantPartitions.size() == 0)
                return false;
            for (TDecl decl : f2.sig.pars)
            {
                if(!decl.name.equals("state"))
                {
                    SortType type;
                    try
                    {
                        type = (SortType) decl.type;
                    }
                    catch (ClassCastException e)
                    {
                        continue;
                    }

                    for(Fun invar : invariantPartitions)
                    {
                        if(((SortType)invariantPartitionTypes.get(invar).type).equals(type))
                        //if(((SortType)invar.sig.pars[1].type).equals(type))
                        {
                            if(!applyTheoremForDependency(f1, f2, invar, True.getInstance()))
                            {
                                return false;
                            }
                        }

                    }
                }
            }
            return true;
        }
    }
}