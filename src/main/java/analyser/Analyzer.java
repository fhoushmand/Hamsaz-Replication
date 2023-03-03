package main.java.analyser;

import main.java.language.ast.Fun;
import main.java.utils.Utils;
import main.java.utils.Constants;
import main.java.utils.Utils;
import main.java.language.ast.Fun;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import robject.Clique;

import java.io.*;
import java.util.*;

public class Analyzer {

    Workbook workbook = new HSSFWorkbook();
    ObjectAST useCase;
    private HashMap<String, ArrayList<Clique>> cliquesMap = new HashMap<>(); //methodName -> list of cliques
    private HashSet<String> cover;
    private HashMap<String, ArrayList<String>> conflictMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> dependencyMap = new HashMap<>();

    public Analyzer(ObjectAST useCaseCVC) {
        this.useCase = useCaseCVC;
        for (Fun f : useCase.getOperations()){
            dependencyMap.put(f.name, new ArrayList<>());
        }
        analyze();
    }


    public static boolean[][] stateCommutativityTable(ObjectAST useCase)
    {
        boolean[][] commutativityArray = new boolean[useCase.getOperations().size()][useCase.getOperations().size()];
        for (int i = 0; i < useCase.getOperations().size(); i ++)
        {
            for (int j = 0; j < useCase.getOperations().size(); j ++) {
                commutativityArray[i][j] = useCase.checkStateCommutativity(useCase.getOperations().get(i), useCase.getOperations().get(j));
            }
        }
        return commutativityArray;
    }

    public boolean[][] conflictTable(ObjectAST useCase)
    {
        boolean[][] stateComm = stateCommutativityTable(useCase);
        printTable(stateComm, useCase, "state-conflict");
        boolean[][] invarComm = invarCommutativityTable(useCase);
        printTable(invarComm, useCase, "permissible-conflict");
        boolean[][] conflict = new boolean[stateComm.length][stateComm[0].length];
        for(int i = 0 ; i < stateComm[0].length; i++){
            for(int j = 0; j < stateComm[0].length; j++) {
                conflict[i][j] = stateComm[i][j] && invarComm[i][j];
            }
        }
        printTable(conflict, useCase, "conflict");
        return conflict;
    }

    public static boolean[][] invarCommutativityTable(ObjectAST useCase)
    {
        boolean[][] commutativityArray = new boolean[useCase.getOperations().size()][useCase.getOperations().size()];
        for (int i = 0; i < useCase.getOperations().size(); i ++) {
            for (int j = 0; j < useCase.getOperations().size(); j ++) {
                commutativityArray[i][j] = useCase.checkInvariantCommutativity(useCase.getOperations().get(i), useCase.getOperations().get(j));
            }
        }
        return commutativityArray;
    }

    public boolean[][] dependencyTable(ObjectAST useCase)
    {
        boolean[][] dependencyTable = new boolean[useCase.getOperations().size()][useCase.getOperations().size()];
        for (int i = 0; i < useCase.getOperations().size(); i ++) {
            for (int j = 0; j < useCase.getOperations().size(); j ++) {
                dependencyTable[j][i] = useCase.checkDependency(useCase.getOperations().get(i), useCase.getOperations().get(j));
            }
        }
        printTable(dependencyTable, useCase, "dependency");
        return dependencyTable;
    }
    private Graph buildConflictGraphNoSelfEdge(boolean[][] array) {
        Graph graph = new SimpleGraph<>(DefaultEdge.class);
        for (int i = 0; i < useCase.getOperations().size(); i++) {
            String source = useCase.getOperations().get(i).name;
            graph.addVertex(source);
        }
        for (int i = 0; i < useCase.getOperations().size(); i++)
        {
            String source = useCase.getOperations().get(i).name;
            for (int j = 0; j < useCase.getOperations().size(); j++)
            {
                String dest = useCase.getOperations().get(j).name;
                // conflict, must create an edge
                if(!array[i][j] && !source.equals(dest)){
                    graph.addEdge(source, dest);
                }
            }
        }
        return graph;
    }

    private Graph buildDependencyGraph(boolean[][] array) {
        Graph graph = new DefaultDirectedGraph(DefaultEdge.class);
        for (int i = 0; i < useCase.getOperations().size(); i++) {
            String source = useCase.getOperations().get(i).name;
            graph.addVertex(source);
        }
        for (int i = 0; i < useCase.getOperations().size(); i++)
        {
            String source = useCase.getOperations().get(i).name;
            for (int j = 0; j < useCase.getOperations().size(); j++)
            {
                String dest = useCase.getOperations().get(j).name;
                // conflict, must create an edge
                if(!array[i][j]){
                    graph.addEdge(source, dest);
                }
            }
        }
        return graph;
    }

    private Graph buildConflictGraph(boolean[][] array) {
        Graph graph = new DefaultUndirectedGraph(DefaultEdge.class);
        for (int i = 0; i < useCase.getOperations().size(); i++) {
            String source = useCase.getOperations().get(i).name;
            graph.addVertex(source);
        }
        for (int i = 0; i < useCase.getOperations().size(); i++)
        {
            String source = useCase.getOperations().get(i).name;
            for (int j = 0; j < useCase.getOperations().size(); j++)
            {
                String dest = useCase.getOperations().get(j).name;
                // conflict, must create an edge
                if(!array[i][j]){
                    graph.addEdge(source, dest);
                }
            }
        }
        return graph;
    }

    private void analyze() {
        boolean[][] conflictTable = conflictTable(useCase);
        boolean[][] dependencyTable = dependencyTable(useCase);
        Graph conflictGraphNoSelfEdge = buildConflictGraphNoSelfEdge(conflictTable);
        BronKerboschCliqueFinder<String, DefaultEdge> maximalCliques = new BronKerboschCliqueFinder<>(conflictGraphNoSelfEdge);
        ArrayList<Clique> allCliques = new ArrayList<>();
        int i = 0;
        for(Set<String> ms : maximalCliques) {
            if(ms.size() == 1) {
                int index = 0;
                for (; i < useCase.getOperations().size(); index++){
                    if(useCase.getOperations().get(index).name.equals(ms.iterator().next())) break;
                }
                // if it does not have self conflict there is no clique
                if(conflictTable[index][index])
                    continue;
            }
            Clique clique = new Clique(String.valueOf(i++));
            clique.addMethods(ms);
            allCliques.add(clique);
        }
        for (int x = 0; x < useCase.getOperations().size(); x++) {
            String method = useCase.getOperations().get(x).name;
            // calculate the cliques map (method -> List(cliques))
            for(Clique cl : allCliques){
                if(cl.getMethods().contains(method)) {
                    if (cliquesMap.containsKey(method))
                        cliquesMap.get(method).add(cl);
                    else
                        cliquesMap.put(method, new ArrayList<>(){{add(cl);}});
                }
            }
            for (int y = 0; y < useCase.getOperations().size(); y++) {
                // calculate conflict map (method -> list of conflicting methods)
                if(!conflictTable[x][y]) {
                    if(conflictMap.containsKey(method))
                        conflictMap.get(method).add(useCase.getOperations().get(y).name);
                    else {
                        int finalY = y;
                        conflictMap.put(method, new ArrayList<>(){{add(useCase.getOperations().get(finalY).name);}});
                    }
                }
                // calculate dependency map
                if(!dependencyTable[x][y]) {
                    if(dependencyMap.containsKey(method))
                        dependencyMap.get(method).add(useCase.getOperations().get(y).name);
                    else {
                        int finalY = y;
                        dependencyMap.put(method, new ArrayList<>(){{add(useCase.getOperations().get(finalY).name);}});
                    }
                }
            }
        }
        Graph conflictGraph = buildConflictGraph(conflictTable);
        Utils.exportToPdf(conflictGraph, "conflict.pdf");
        // calculate vertex cover
        VertexCoverAlgorithm vertexCover = new GreedyVCImpl(conflictGraph);
        cover = new HashSet<>(vertexCover.getVertexCover());
        // calculate dependencies
        Graph dependencyGraph = buildDependencyGraph(dependencyTable);
        Utils.exportToPdf(dependencyGraph, "dependency.pdf");
    }

//    public void readTable() throws IOException {
//        FileInputStream inputStream = new FileInputStream(Constants.ANALYSIS_OUTPUT_PATH);
//
//        Workbook workbook = new HSSFWorkbook(inputStream);
//        Sheet firstSheet = workbook.getSheetAt(0);
//        Iterator<Row> iterator = firstSheet.iterator();
//
//        while (iterator.hasNext()) {
//            Row nextRow = iterator.next();
//            Iterator<Cell> cellIterator = nextRow.cellIterator();
//
//            while (cellIterator.hasNext()) {
//                Cell cell = cellIterator.next();
//
//                switch (cell.getCellType()) {
//                    case Cell.CELL_TYPE_STRING:
//                        System.out.print(cell.getStringCellValue());
//                        break;
//                    case Cell.CELL_TYPE_BOOLEAN:
//                        System.out.print(cell.getBooleanCellValue());
//                        break;
//                    case Cell.CELL_TYPE_NUMERIC:
//                        System.out.print(cell.getNumericCellValue());
//                        break;
//                }
//                System.out.print(" - ");
//            }
//            System.out.println();
//        }
//
//        workbook.close();
//        inputStream.close();
//    }

    private void printTable(boolean[][] array, ObjectAST useCase, String sheetName)
    {
        HSSFSheet sheet = (HSSFSheet) workbook.createSheet(sheetName);
        int rowNum = 0;
        int colNum = 1;
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < array.length; i++)
        {
            Cell opNameCell = row.createCell(colNum);
            opNameCell.setCellValue(useCase.getOperations().get(i).name);
            colNum++;

            if(i == 0)
                System.out.print("    ");
            if(i > 0)
                System.out.print("     ");
            String newName = "";
            for (String w : useCase.getOperations().get(i).name.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                newName += w.substring(0,1);
            }

            System.out.print(newName);
            System.out.print("\t");
        }
        rowNum++;
        System.out.println();
        for (int i = 0; i < array.length; i++) {
            System.out.print("---------");
        }
        System.out.println();

        for (int i = 0; i < array.length; i++)
        {
            Row data = sheet.createRow(rowNum);
            colNum = 0;
            Cell rel = data.createCell(colNum);
            rel.setCellValue(useCase.getOperations().get(i).name);
            colNum++;
            for (int j = 0; j < array[0].length; j++) {
                Cell rel2 = data.createCell(colNum);
                rel2.setCellValue(array[i][j]);
                colNum++;

                if(j == 0) {
                    String newName = "";
                    for (String w : useCase.getOperations().get(i).name.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                        newName += w.substring(0,1);
                    }
                    System.out.print(newName);
                    System.out.print("|");
                }
                if(j < array[0].length -1)
                    System.out.print(array[i][j]+"\t");
                else System.out.print(array[i][j]);

                if(j == array[0].length -1)
                    System.out.print("|");
            }
            rowNum++;
            System.out.println();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Constants.ANALYSIS_OUTPUT_PATH);
            workbook.write(fileOutputStream);
            workbook.close();
        }
        catch (IOException e) {
            System.out.println("Cannot save the analysis result to the file");
        }
        return;
    }
    public HashSet<String> getCover() {
        return cover;
    }

    public HashMap<String, ArrayList<String>> getDependencyMap() {
        return dependencyMap;
    }

    public ArrayList<Clique> getAllCliques() {
        ArrayList<Clique> cls = new ArrayList<>();
        for (Map.Entry entry : cliquesMap.entrySet()) {
            for (Clique cl : (ArrayList<Clique>)entry.getValue())
                if(!cls.contains(cl))
                    cls.add(cl);
        }
        return cls;
    }

    public ArrayList<Clique> getCliquesForMethod(String name) {
        return cliquesMap.get(name);
    }

    public HashMap<String, ArrayList<String>> getConflictMap() {
        return conflictMap;
    }

    public ArrayList<String> getDependenciesForMethod(String name) {
        return dependencyMap.get(name);
    }

    public Clique getCliqueByName(String name) {
        for(Clique cl : getAllCliques()) {
            if(cl.name.equals(name))
                return cl;
        }
        return null;
    }
}
