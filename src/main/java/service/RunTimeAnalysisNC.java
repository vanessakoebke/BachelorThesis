package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import model.NC_Algorithm;

public class RunTimeAnalysisNC {
   
    
    public static void equivDis_parallel_vs_sequential_size(int[] inputSize, double inputDensity, int repetitions) {
        // Warm-up for JIT
        DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(10, inputDensity);
        for (int i = 0; i < 1000; i++) {
            double[][] array = generator.next().getAdjacencyArray();
            SimpleMatrix m = new SimpleMatrix(array);

            int a = ThreadLocalRandom.current().nextInt(0, 10);
            int b = ThreadLocalRandom.current().nextInt(0, 10);

            NC_Algorithm.equivDis_Sequential(m, a, b);
            NC_Algorithm.equivDis_Parallel(array, a, b);
        }
        //Actual tests
        List<String> output = new ArrayList<>();
        output.add("n,run,time_seq,time_para");
        for (int size : inputSize) {
            //Input generation for real input
            generator = new DefaultDungTheoryGenerator(size, inputDensity);
            double[][] array = generator.next().getAdjacencyArray();
            for (int i = 1; i <= repetitions; i++) {
                int a = ThreadLocalRandom.current().nextInt(0, size);
                int b = ThreadLocalRandom.current().nextInt(0, size);
                long startSeq = System.nanoTime();
                NC_Algorithm.equivDis_Sequential(new SimpleMatrix(array), a, b);
                long endSeq = System.nanoTime();
                long startPara = System.nanoTime();
                NC_Algorithm.equivDis_Parallel(array, a, b);
                long endPara = System.nanoTime();
                long durationSeq = endSeq - startSeq;
                long durationPara = endPara - startPara;
                output.add( size + "," + i + "," + String.valueOf(durationSeq) + "," + String.valueOf(durationPara));
            } 
        }
        String fileName = "NC_EquivDis_sequential_parallel" + "_" + LocalDate.now() + "_" + LocalTime.now();
        try {
            Files.write(Path.of("Output/" + fileName + ".csv"), output);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
