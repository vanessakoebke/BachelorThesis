package service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import io.Util;
import model.GraphStructure;
import model.TestInstance;

public class IccmaAnalysis {
    public static void parseDimensions() {
        File folder = new File("ICCMA");
        File[] subfolders = folder.listFiles();
        List<String> results = new ArrayList<>();
        results.add("file,dimension,category");
        if (subfolders == null) {
            System.out.println("Folder not found or empty");
            return;
        }
        for (File subfolder : subfolders) {
            File[] files = subfolder.listFiles();
            if (files == null) {
                System.out.println("Subfolder not found or empty");
                continue;
            }
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".af")) {
                    int dimension;
                    try {
                        dimension = Util.parseICCMAdimensions(file.toPath());
                    } catch (Exception e) {
                        System.out.println(file.getName());
                        e.printStackTrace();
                        continue;
                    }
                    results.add(file.getName() + "," + dimension + subfolder.getName());
                }
            }
        }
        // write file
        try (BufferedWriter w = new BufferedWriter(new FileWriter("Output/ICCMA_dimensions_categories.csv"))) {
            for (String line : results) {
                w.write(line);
                w.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseCategories() {
        File folder = new File("input/ICCMA");
        File[] subfolders = folder.listFiles();
        List<String> results = new ArrayList<>();
        results.add("file,category");
        if (subfolders == null) {
            System.out.println("Folder not found or empty");
            return;
        }
        for (File subfolder : subfolders) {
            File[] files = subfolder.listFiles();
            if (files == null) {
                System.out.println("Subfolder not found or empty");
                continue;
            }
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".af")) {
                    String category = subfolder.getName();
                    results.add(file.getName() + "," + category);
                }
            }
            // }
            // write file
            try (BufferedWriter w = new BufferedWriter(new FileWriter("Output/ICCMA_categories.csv"))) {
                for (String line : results) {
                    w.write(line);
                    w.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getSCCnumber(Graph graph) {
        KosarajuStrongConnectivityInspector<Integer, DefaultEdge> inspector = new KosarajuStrongConnectivityInspector<>(
                graph);
        return inspector.stronglyConnectedSets().size();
    }

    private static boolean isCyclic(Graph graph) {
        CycleDetector<Integer, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
        return cycleDetector.detectCycles();
    }

    private static double getDensity(Graph graph) {
        double n = graph.vertexSet().size();
        double m = graph.edgeSet().size();
        return m / (n * (n - 1));
    }

    private static int condensationDepth(Graph<Integer, DefaultEdge> graph) {
        KosarajuStrongConnectivityInspector<Integer, DefaultEdge> inspector = new KosarajuStrongConnectivityInspector<>(
                graph);
        Graph<Graph<Integer, DefaultEdge>, DefaultEdge> condensation = inspector.getCondensation();
        Map<Graph<Integer, DefaultEdge>, Integer> depth = new HashMap<>();
        for (Graph<Integer, DefaultEdge> vertex : condensation.vertexSet()) {
            depth.put(vertex, 0);
        }
        TopologicalOrderIterator<Graph<Integer, DefaultEdge>, DefaultEdge> iterator = new TopologicalOrderIterator<>(
                condensation);
        int maxDepth = 0;
        while (iterator.hasNext()) {
            Graph<Integer, DefaultEdge> vertex = iterator.next();
            for (DefaultEdge edge : condensation.outgoingEdgesOf(vertex)) {
                Graph<Integer, DefaultEdge> successor = condensation.getEdgeTarget(edge);
                int newDepth = depth.get(vertex) + 1;
                if (newDepth > depth.get(successor)) {
                    depth.put(successor, newDepth);
                    maxDepth = Math.max(maxDepth, newDepth);
                }
            }
        }
        return maxDepth;
    }

    private static int condensationWidth(Graph<Integer, DefaultEdge> graph) {
        KosarajuStrongConnectivityInspector<Integer, DefaultEdge> inspector = new KosarajuStrongConnectivityInspector<>(
                graph);
        Graph<Graph<Integer, DefaultEdge>, DefaultEdge> condensation = inspector.getCondensation();
        Map<Graph<Integer, DefaultEdge>, Integer> depth = new HashMap<>();
        for (Graph<Integer, DefaultEdge> vertex : condensation.vertexSet()) {
            depth.put(vertex, 0);
        }
        TopologicalOrderIterator<Graph<Integer, DefaultEdge>, DefaultEdge> iterator = new TopologicalOrderIterator<>(
                condensation);
        while (iterator.hasNext()) {
            Graph<Integer, DefaultEdge> vertex = iterator.next();
            for (DefaultEdge edge : condensation.outgoingEdgesOf(vertex)) {
                Graph<Integer, DefaultEdge> successor = condensation.getEdgeTarget(edge);
                int newDepth = depth.get(vertex) + 1;
                if (newDepth > depth.get(successor)) {
                    depth.put(successor, newDepth);
                }
            }
        }
        Map<Integer, Integer> width = new HashMap<>();
        for (int level : depth.values()) {
            width.merge(level, 1, Integer::sum);
        }
        return width.values().stream().max(Integer::compareTo).orElse(0);
    }

    private static int largestSccSize(Graph<Integer, DefaultEdge> graph) {
        KosarajuStrongConnectivityInspector<Integer, DefaultEdge> inspector = new KosarajuStrongConnectivityInspector<>(
                graph);
        return inspector.stronglyConnectedSets().stream().mapToInt(Set::size).max().orElse(0);
    }

    private static GraphStructure getStructure(String file, String category) {
        Graph graph = Util.getGraph(Path.of("Input/ICCMA/" + category + "/" + file));
        return new GraphStructure(file, category, graph.vertexSet().size(), graph.edgeSet().size(), getDensity(graph),
                getSCCnumber(graph), largestSccSize(graph), isCyclic(graph), condensationWidth(graph),
                condensationDepth(graph));
    }

    public static void parseGraphStructures() {
        File folder = new File("input/ICCMA");
        File[] subfolders = folder.listFiles();
        if (subfolders == null) {
            System.out.println("Folder not found or empty");
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of("Output/GraphStructure.csv"),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            List<TestInstance> instances = Util.readTestInstances(Path.of("Output/ICCMA_dimensions_categories.csv"));
            instances.sort(Comparator.comparingInt(TestInstance::inputSize));
            // Header
            writer.write("file,category,nodes,edges,density,sccs,largest_scc," + "is_cyclic,max_width,max_depth");
            writer.newLine();
            for (TestInstance instance : instances) {
                System.out.println("Running " + instance.file() + " (input size: " + instance.inputSize() + ")");
                // Actual test
                try {
                    double[][] matrix = Util.readICCMA(Path.of("Input/ICCMA/" + instance.category() + "/"+ instance.file()));
                    System.out.println("Starting with file " + instance.file());
                    if (instance.file().endsWith(".af")) {
                        String category = instance.category();
                        GraphStructure structure = getStructure(instance.file(), category);
                        
                     // Ergebnis SOFORT in Ergebnis-CSV schreiben
                        writer.write(structure.file() +"," + structure.category() + ","+ structure.nodes() + "," + structure.edges() + "," + structure.density() + ","
                                + structure.sccs() + "," + structure.largest_scc() + "," + structure.isCyclic() + "," + structure.max_width() + ","
                                + structure.max_depth());
                        writer.newLine();
                        // WICHTIG: Ergebnis sofort auf die Platte schreiben
                        writer.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
}
}
