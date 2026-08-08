package service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import io.Util;
import model.*;

public class RunTimeAnalysis {
    
    
    
    
    public static void orderedTest() {

        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of("Output/test.csv"),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            List<TestInstance> instances = Util.readTestInstances(Path.of("Output/ICCMA_dimensions.csv"));
            
            instances.sort(Comparator.comparingInt(TestInstance::inputSize));
            writer.write(("file,run,time_PTime_equivDis, time_NC_equivDis,time_PTime_strongerDis, time_NC_strongerDis"));
            writer.newLine();
            for (TestInstance instance : instances) {
                System.out.println("Running " + instance.file() + " (input size: " + instance.inputSize() + ")");
                // Actual test
                try {
                    double[][] matrix = Util.readICCMA(Path.of("ICCMA/" +instance.file()));
                    // Warm-up
                    for (int i = 0; i < 10; i++) {
                        MM_Algorithm.equivDis_Optimal(matrix, 1, 2);
                        MV_Algorithm.equivDis_Optimal(matrix, 1, 2);
                    }
                    for (int i = 1; i <= 100; i++) {
                        int a = ThreadLocalRandom.current().nextInt(0, matrix.length);
                        int b = ThreadLocalRandom.current().nextInt(0, matrix.length);
                        long start_NC_equivDis = System.nanoTime();
                        MV_Algorithm.equivDis_Optimal(matrix, a, b);
                        long end_NC_equivDis = System.nanoTime();
                        double time_NC_equivDis = end_NC_equivDis - start_NC_equivDis;
                        long start_P_equivDis = System.nanoTime();
                        MM_Algorithm.equivDis_Optimal(matrix, a, b);
                        long end_P_equivDis = System.nanoTime();
                        double time_P_equivDis = end_P_equivDis - start_P_equivDis;
                        long start_NC_strongerDis = System.nanoTime();
                        MV_Algorithm.strongerDis_Optimal(matrix, a, b);
                        long end_NC_strongerDis = System.nanoTime();
                        double time_NC_strongerDis = end_NC_strongerDis - start_NC_strongerDis;
                        long start_P_strongerDis = System.nanoTime();
                        MM_Algorithm.strongerDis_Optimal(matrix, a, b);
                        long end_P_strongerDis = System.nanoTime();
                        double time_P_strongerDis = end_P_strongerDis - start_P_strongerDis;
                        System.out.println("Completed: " + instance.file() + ", Iteration " + i);
                        
                        
                        // Ergebnis SOFORT in Ergebnis-CSV schreiben
                        writer.write(instance.file() +", " + i + ", " + time_P_equivDis + ", " + time_NC_equivDis + ", " +
                                time_P_strongerDis +", "+ time_NC_strongerDis);
                        writer.newLine();

                        // WICHTIG: Ergebnis sofort auf die Platte schreiben
                        writer.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
    
    public static void equivDis_NCvsPTIME() {
        File folder = new File("ICCMA");
        File[] subfolders = folder.listFiles();
        if (subfolders == null) {
            System.out.println("Folder not found or empty");
            return;
        }
        for (File subfolder : subfolders) {
            List<String> results = new ArrayList<>();
            results.add("file,run,time_NC,time_PTime");
            File[] files = subfolder.listFiles();
            if (files == null) {
                System.out.println("Subfolder not found or empty");
                continue;
            }
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".af")) {
                    System.out.println("Starting with file " + file.getName());
                    try {
                        double[][] matrix = Util.readICCMA(file.toPath());
                        // Warm-up
                        for (int i = 0; i < 10; i++) {
                            MM_Algorithm.equivDis_Optimal(matrix, 1, 2);
                            MV_Algorithm.equivDis_Optimal(matrix, 1, 2);
                        }
                        // Actual test
                        for (int i = 1; i <= 500; i++) {
                            int a = ThreadLocalRandom.current().nextInt(0, matrix.length);
                            int b = ThreadLocalRandom.current().nextInt(0, matrix.length);
                            long start_NC = System.nanoTime();
                            // boolean r = PTIME_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                            MV_Algorithm.equivDis_Optimal(matrix, a, b);
                            long end_NC = System.nanoTime();
                            double time_NC = end_NC - start_NC;
                            long start_P = System.nanoTime();
                            // boolean r = PTIME_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                            MM_Algorithm.equivDis_Optimal(matrix, a, b);
                            long end_P = System.nanoTime();
                            double time_P = end_P - start_P;
                            
                            
                            // CSV line
                            results.add(file.getName() + "," + i + "," + time_NC + "," + time_P);
                            System.out.println("Completed: " + file.getName() + ", Iteration " + i);
                        }
                    } catch (Exception e) {
                        System.out.println(file.getName());
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            
            // write file
            
            try (BufferedWriter w = new BufferedWriter(
                    new FileWriter("Output/EquivDis_NC_PTime_" + subfolder.getName() + "_" + LocalDate.now() + "_" + LocalTime.now() + ".csv"))) {
                for (String line : results) {
                    w.write(line);
                    w.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void strongerDis_NCvsPTIME() {
        File folder = new File("ICCMA");
        File[] subfolders = folder.listFiles();
        if (subfolders == null) {
            System.out.println("Folder not found or empty");
            return;
        }
        for (File subfolder : subfolders) {
            List<String> results = new ArrayList<>();
            results.add("file,run,time_NC,time_PTime");
            File[] files = subfolder.listFiles();
            if (files == null) {
                System.out.println("Subfolder not found or empty");
                continue;
            }
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".af")) {
                    System.out.println("Starting with file " + file.getName());
                    try {
                        double[][] matrix = Util.readICCMA(file.toPath());
                        // Warm-up
                        for (int i = 0; i < 10; i++) {
                            MM_Algorithm.strongerDis_Optimal(matrix, 1, 2);
                            MV_Algorithm.strongerDis_Optimal(matrix, 1, 2);
                        }
                        // Actual test
                        for (int i = 1; i <= 500; i++) {
                            int a = ThreadLocalRandom.current().nextInt(0, matrix.length);
                            int b = ThreadLocalRandom.current().nextInt(0, matrix.length);
                            long start_NC = System.nanoTime();
                            MV_Algorithm.strongerDis_Optimal(matrix, a, b);
                            long end_NC = System.nanoTime();
                            double time_NC = end_NC - start_NC;
                            long start_P = System.nanoTime();
                            MM_Algorithm.strongerDis_Optimal(matrix, a, b);
                            long end_P = System.nanoTime();
                            double time_P = end_P - start_P;
                            // CSV line
                            results.add(file.getName() + "," + i + "," + time_NC + "," + time_P);
                            System.out.println("Completed: " + file.getName() + ", Iteration " + i);
                        }
                    } catch (Exception e) {
                        System.out.println(file.getName());
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            
            // write file
            
            try (BufferedWriter w = new BufferedWriter(
                    new FileWriter("Output/StrongerDis_NC_PTime_" + subfolder.getName() + "_" + LocalDate.now() + "_" + LocalTime.now() + ".csv"))) {
                for (String line : results) {
                    w.write(line);
                    w.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
