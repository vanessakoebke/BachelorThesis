package service;

import java.io.*;
import java.util.*;

import org.ejml.simple.SimpleMatrix;

import io.Util;
import model.*;

public class JuliaComparison {

    public static void main(String[] args) {

        File folder = new File("instances");
        File[] files = folder.listFiles();

        if (files == null) {
            System.out.println("Folder not found or empty");
            return;
        }

        List<String> results = new ArrayList<>();

        // Header (wie in Julia)
        results.add("file,time,result");

        for (File file : files) {

            if (file.isFile() && file.getName().endsWith(".csv")) {
                System.out.println("Processing: " + file.getName());

                try {
                    Instance inst = Util.loadInstance(file.getPath());
                    SimpleMatrix matrix = new SimpleMatrix(inst.A());

                    // Warm-up
                    for (int i = 0; i < 10; i++) {
                        //PTIME_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                        NC_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                    }

                    // Actual test
                    long start = System.nanoTime();
                    //boolean r = PTIME_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                    boolean r = NC_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                    long end = System.nanoTime();

                    double time = end - start;

                    // CSV line
                    results.add(file.getName() + "," + time + "," + r);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        
        // write file
        File outDir = new File("results");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try (BufferedWriter w = new BufferedWriter(
                
                new FileWriter("results/java_results_ejml_nc.csv"))) {

            for (String line : results) {
                w.write(line);
                w.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}