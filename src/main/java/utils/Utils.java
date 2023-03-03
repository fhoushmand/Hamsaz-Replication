package main.java.utils;

import main.java.analyser.ObjectAST;
import main.java.language.ast.*;
import main.java.language.type.BoolType;
import main.java.language.type.SetType;
import main.java.language.type.TupleType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {


    public final static Properties configFile = new Properties() {
        private final static long serialVersionUID = 1L; {
            try {
                load(new FileInputStream(Constants.cfgProp));
            } catch (Exception e) {}
        }
    };

    public static void exportToPdf(Graph graph, String name){
        DOTExporter<String, DefaultEdge> exporter =
                new DOTExporter<>();
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        Graphviz gv = new Graphviz();
        gv.decreaseDpi();
        gv.decreaseDpi();
        File out = new File(Constants.TMP_PATH + name);
        gv.writeGraphToFile( gv.getGraph( writer.toString(), "pdf" ), out );
    }
    public static Fun referentialIntegrity(TDecl table1, String field, TDecl table2, TypeDecl row2, ObjectAST useCaseCVC) {
        TupleType childType = ((TupleType)((SetType)table1.type).tpar);
        String baseVarName = "xx";
        int id = 0;
        Exp[] childTupleArgs = new Exp[childType.arity];
        String argName = "i";
        TDecl[] quantifierVariables = new TDecl[childType.arity-1];
        for (int x = 0; x < childType.arity; x++) {
            if(x == Integer.parseInt(field))
                childTupleArgs[x] = new Selector(new Var(argName), "0");
            else {
                String name = baseVarName + id;
                quantifierVariables[id] = new TDecl(name, childType.tpars[x]);
                childTupleArgs[x] = new Var(name);
                id++;
            }
        }
        Exp childTuple = new Tuple(childTupleArgs);

        Statement I1 = new Return(
                new Quantifier(
                        Quantifier.QUANTIFIER_TYPE.FORALL,
                        new Sig(quantifierVariables,null),
                        new Implication(
                                new SetMembership(childTuple, new Selector(new Var("state"), table1.name)),
                                new SetMembership(new Var(argName), new Selector(new Var("state"), table2.name))
                        )
                )
        );
        return new Fun("I"+useCaseCVC.invarPartiId++, new Sig(new TDecl[]{new TDecl("state", new SortType(useCaseCVC.stateTypeDecl)), new TDecl(argName, new SortType(row2))}, BoolType.getInstance()), I1);
    }

    public static Exp universallyGeneralize(Fun f, int argNum) {
        String argName = f.sig.pars[argNum].name;
        return new Quantifier(
                Quantifier.QUANTIFIER_TYPE.FORALL,
                new Sig(new TDecl[]{f.sig.pars[argNum]}, null),
                new Call(f, new Exp[]{new Var("state"), new Var(argName)})
        );
    }
}
