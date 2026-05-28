package io;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.tweetyproject.arg.dung.parser.Iccma23Parser;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import model.Instance;

public class Util {
    public static List<DungTheory> readICCMA() {
        List<DungTheory> output = new ArrayList<>();
        try {
            Path folder = Paths.get("input/ICCMA");
            Iccma23Parser parser = new Iccma23Parser();
            Files.list(folder).filter(path -> path.toString().endsWith(".af")).forEach(path -> {
                try {
                    output.add(parser.parse(Files.newBufferedReader(path)));
                } catch (IOException e) {
                    System.out.println("Parsing file " + path + " failed.");
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.out.println("Iterating through the ICCMA folder failed.");
            e.printStackTrace();
        }
        return output;
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
}
