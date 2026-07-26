package service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import io.Util;
import model.*;

public class RunTimeAnalysis {
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
                    try {
                        double[][] matrix = Util.readICCMA(file.toPath());
                        // Warm-up
                        for (int i = 0; i < 10; i++) {
                            PTIME_Algorithm.equivDis_Optimal(matrix, 1, 2);
                            NC_Algorithm.equivDis_Optimal(matrix, 1, 2);
                        }
                        // Actual test
                        for (int i = 1; i <= 1000; i++) {
                            System.out.println("Processing: " + file.getName() + ", Iteration " + i);
                            int a = ThreadLocalRandom.current().nextInt(0, matrix.length);
                            int b = ThreadLocalRandom.current().nextInt(0, matrix.length);
                            long start_NC = System.nanoTime();
                            // boolean r = PTIME_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                            NC_Algorithm.equivDis_Optimal(matrix, a, b);
                            long end_NC = System.nanoTime();
                            double time_NC = end_NC - start_NC;
                            long start_P = System.nanoTime();
                            // boolean r = PTIME_Algorithm.strongDis_Sequential(matrix, inst.a(), inst.b());
                            NC_Algorithm.equivDis_Optimal(matrix, a, b);
                            long end_P = System.nanoTime();
                            double time_P = end_P - start_P;
                            // CSV line
                            results.add(file.getName() + "," + i + "," + time_NC + "," + time_P);
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
}
