package io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import model.*;
import service.RunTimeAnalysis;

public class Util {
    
    public static Graph<Integer, DefaultEdge> getGraph(Path path) {
        Graph<Integer, DefaultEdge> graph =
        new DefaultDirectedGraph<>(DefaultEdge.class);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            ;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.startsWith("p")) {
                    continue;
                }
                String[] lineArr = line.split(" ");
                try {
                    graph.addVertex(Integer.parseInt(lineArr[0]));
                    graph.addVertex(Integer.parseInt(lineArr[1]));
                    graph.addEdge(Integer.parseInt(lineArr[0]),
                            Integer.parseInt(lineArr[1])
                        );
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println(path.getFileName());
                    e.printStackTrace();
                    continue;
                }
            }
            
            } catch (Exception e) {
                System.out.println("Iterating through the ICCMA folder failed.");
                e.printStackTrace();
            }
        return graph;
    }
    
    public static int parseICCMAdimensions(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
        
        String line;
        List<int[]> inputList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.startsWith("p")) {
                continue;
            }
            String[] lineArr = line.split(" ");
            inputList.add(new int[]{
                    Integer.parseInt(lineArr[0]),
                    Integer.parseInt(lineArr[1])
                });
        }
        int max = 0;
        for (int[] elem: inputList) {
            if (elem[0] > max) {
                max = elem[0];
            }
            if (elem[1] > max) {
                max = elem[1];
            }
        }
        return max;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }}
    
    public static double[][] readICCMA(Path path) {
        double[][] matrix = null;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
        ;
        String line;
        List<int[]> inputList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.startsWith("p")) {
                continue;
            }
            String[] lineArr = line.split(" ");
            inputList.add(new int[]{
                    Integer.parseInt(lineArr[0]),
                    Integer.parseInt(lineArr[1])
                });
        }
        int max = 0;
        for (int[] elem: inputList) {
            if (elem[0] > max) {
                max = elem[0];
            }
            if (elem[1] > max) {
                max = elem[1];
            }
        }
        matrix = new double[max][max];
        
        for (int[] elem: inputList) {
            matrix[elem[0]-1][elem[1]-1] = 1;
        }
        } catch (IOException e) {
            System.out.println("Iterating through the ICCMA folder failed.");
            e.printStackTrace();
        }
        return matrix;
    }

    public static void testGenerator() throws Exception {
        int[] inputsizes = { 5000 };
        Random r = new Random();
        for (int size : inputsizes) {
            DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(size, 0.5);
            for (int iter = 1; iter <= 500; iter++) {
                DungTheory af = generator.next();
                double[][] A_double = af.getAdjacencyArray();
                int[][] A = new int[size][size];
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        A[i][j] = (int) A_double[i][j];
                    }
                }
                int a = r.nextInt(size);
                int b = r.nextInt(size);
                String file = "../instances/n" + size + "_test" + iter + ".csv";
                try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                    w.write(size + "," + a + "," + b);
                    w.newLine();
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            w.write(Integer.toString(A[i][j]));
                            if (j < size - 1) w.write(" ");
                        }
                        w.newLine();
                    }
                }
            }
        }
    }

    public static Instance loadInstance(String path) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] header = br.readLine().split(",");
            int n = Integer.parseInt(header[0].trim());
            int a = Integer.parseInt(header[1].trim());
            int b = Integer.parseInt(header[2].trim());
            double[][] A = new double[n][n];
            for (int i = 0; i < n; i++) {
                String[] parts = br.readLine().trim().split("\\s+");
                for (int j = 0; j < n; j++) {
                    A[i][j] = Double.parseDouble(parts[j]);
                }
            }
            return new Instance(A, a, b);
        }
    }
    
    public static void writeOutput(String fileName, List<String> output) {
        
        try {
            Files.write(Path.of("Output/" + fileName + ".csv"), output);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void writeGraphStructure(List<GraphStructure> input
            ) throws IOException {

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("Output/ICCMA_graphStructure.csv"))) {

                    // Header
                    writer.write(
                        "file,category,nodes,edges,density,sccs,largest_scc," +
                        "is_cyclic,max_width,max_depth"
                    );
                    writer.newLine();

                    // Daten
                    for (GraphStructure structure: input) {
                        writer.write(structure.file() +"," + structure.category() + ","+ structure.nodes() + "," + structure.edges() + "," + structure.density() + ","
                                + structure.sccs() + "," + structure.largest_scc() + "," + structure.isCyclic() + "," + structure.max_width() + ","
                                + structure.max_depth());
                        writer.newLine();
                    }
                }}

    public static List<TestInstance> readTestInstances(java.nio.file.Path csv)
            throws IOException {
    
        List<TestInstance> instances = new ArrayList<>();
    
        try (BufferedReader reader = Files.newBufferedReader(csv); BufferedReader reader2 = Files.newBufferedReader(Path.of("Output/ICCMA_categories.csv"))) {
    
            // Header überspringen
            reader.readLine();
    
            String line;
    
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
    
                String file = parts[0];
                int inputSize = Integer.parseInt(parts[1]);
                String category = parts[2];
    
                instances.add(new TestInstance(file, inputSize, category));
            }
        }
    
        return instances;
    }
}
