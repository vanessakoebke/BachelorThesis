package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import model.NC_Algorithm;
import model.PTIME_Algorithm;

public class RunTimeAnalysis {
    public static void equivDis_NCvsPTIME(int inputSize, int repetitions) {
        // Warm-up for JIT
        DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(10, 0.8);
        for (int i = 0; i < 1000; i++) {
            double[][] array = generator.next().getAdjacencyArray();

            int a = ThreadLocalRandom.current().nextInt(0, 10);
            int b = ThreadLocalRandom.current().nextInt(0, 10);

            PTIME_Algorithm.equivDis_Optimal(array, a, b);
            NC_Algorithm.equivDis_Optimal(array, a, b);
        }
        //Input generation for real input
        generator = new DefaultDungTheoryGenerator(inputSize, 0.8);
        SimpleMatrix matrix = new SimpleMatrix(generator.next().getAdjacencyArray());
        
        //Actual tests
        String[] output = new String[repetitions +1];
        output[0] = "PTime, NC";
        for (int i = 1; i <= repetitions; i++) {
            int a = ThreadLocalRandom.current().nextInt(0, inputSize);
            int b = ThreadLocalRandom.current().nextInt(0, inputSize);
            long startP = System.nanoTime();
            PTIME_Algorithm.EquivDis(matrix, a, b);
            long endP = System.nanoTime();
            long startNC = System.nanoTime();
            NC_Algorithm.equivDis_Sequential_DoubleMatrix(matrix, a, b);
            long endNC = System.nanoTime();
            long durationP = endP - startP;
            long durationNC = endNC - startNC;
            output[i] = String.valueOf(durationP) + ", " + String.valueOf(durationNC);
        }
        String fileName = "n" + inputSize + "_rep" + repetitions + "_" + LocalDate.now() + "_" + LocalTime.now();
        try {
            Files.write(Path.of("Output/" + fileName + ".csv"), java.util.Arrays.asList(output));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
