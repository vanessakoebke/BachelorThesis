package io;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import model.Instance;

public class Util {
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
            matrix[elem[0]][elem[1]] = 1;
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
            for (int iter = 385; iter <= 500; iter++) {
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
}
