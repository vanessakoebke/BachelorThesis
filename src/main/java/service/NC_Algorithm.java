package service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.ejml.simple.SimpleMatrix;

public class NC_Algorithm {
    
    public static boolean EquivDis(SimpleMatrix F, int a, int b) {
        int K = 1000; // K = 1000 leads to a confidence level of (K-1)/K = 99.9%
        //construct Q automaton A_
        int statesF = F.getNumRows();
        int statesA_ = statesF * 2;
        SimpleMatrix MA_ = new SimpleMatrix(statesA_, statesA_);
        MA_.insertIntoThis(0, 0, F);
        MA_.insertIntoThis(statesF, statesF, F);
        double[][] MA_array = MA_.toArray2(); //Extracting the array for more efficient access of cells
        double[] eta = new double[statesA_];
        eta[a] = 1; //first accepting state in the first half of the vector
        eta[b + statesF] = 1; //second accepting state in the second half of the vector
        //Test for zeroness
        double[] v = eta.clone();
        for (int iter = 1; iter <= statesA_; iter++) {
            int r = ThreadLocalRandom.current().nextInt(1, K * statesA_ + 1);
            //Parallelization starts here
            //Calculate each line of v parallely
            double[] vOld = v.clone(); //necessary because Lambda expression requires final or effectively final variable
            double[] vNew =new double[statesA_]; 
            IntStream.range(0, statesA_).parallel().forEach(i -> {
                double sum = 0;
                for (int j = 0; j < statesA_; j++) {
                    sum += MA_array[i][j] * vOld[j];
                }
                vNew[i] = sum * r;
            });
            v = vNew;
            // the vector multiplication alpha * v can be simplified to checking whether the first half and the second half of v sum to the same,
            //since alpha is never changed and remains constant with only 1 on the first half, and only -1 on the second half
            double sum1 = 0;
            double sum2 = 0;
            for (int k = 0; k < statesF; k++) {
                sum1 += v[k];
                sum2 += v[k + statesF];
            }
            if (Math.abs(sum1 - sum2) > 1e-9) { //For big matrices and big r, risk of rounding errors, therefore aproximate comparison
                return false;
            }
        }
        return true;
    }
}
