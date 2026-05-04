package model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class NC_Algorithm {
    
    public static boolean equivDis_Sequential_DoubleMatrix(SimpleMatrix F, int a, int b) {
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
            if (Math.abs(sum1 - sum2) > 1e-9) { //For big matrices and big r, risk of rounding errors, therefore approximate comparison
                return false;
            }
        }
        return true;
    }
    
    public static boolean equivDis_Parallel(double[][] F, int a, int b) {
        int K = 1000; // K = 1000 leads to a confidence level of (K-1)/K = 99.9%
        int num_arguments = F.length;
        double[] v1 = new double[num_arguments];
        double[] v2 = new double[num_arguments];
        v1[a] = 1; //first argument
        v2[b] = 1; //second argument
        //Optimized test for zeroness
        for (int iter = 1; iter <= 2*num_arguments; iter++) {
            int r = ThreadLocalRandom.current().nextInt(1, 2*K * num_arguments + 1);
            //Parallelization starts here
            //Calculate each line of v parallely
            double[] v1Old = v1.clone(); //necessary because Lambda expression requires final or effectively final variable
            double[] v2Old = v2.clone();
            double[] v1New =new double[num_arguments]; 
            double[] v2New =new double[num_arguments]; 
            IntStream.range(0, num_arguments).parallel().forEach(i -> {
                double sum1 = 0;
                double sum2 = 0;
                for (int j = 0; j < num_arguments; j++) {
                    sum1 += F[i][j] * v1Old[j];
                    sum2 += F[i][j] * v2Old[j];
                }
                v1New[i] = sum1 * r;
                v2New[i] = sum2 * r;
            });
            v1 = v1New;
            v2 = v2New;
            double sumV1 = 0;
            double sumV2 = 0;
            for (int k = 0; k < num_arguments; k++) {
                sumV1 += v1[k];
                sumV2 += v2[k];
            }
            if (Math.abs(sumV1 - sumV2) > 1e-9) { //For big matrices and big r, risk of rounding errors, therefore approximate comparison
                System.out.println("i = " + iter);
                boolean z = sumV1 > sumV2;
                System.out.println("z = " + z);
                return false;
            }
        }
        return true;
    }
    
    public static boolean equivDis_SpaceOptimized(DungTheory F, int a, int b) {
        int K = 1000; // K = 1000 leads to a confidence level of (K-1)/K = 99.9%
        int num_arguments = F.getNumberOfNodes();
        double[] v1 = new double[num_arguments];
        double[] v2 = new double[num_arguments];
        v1[a] = 1; //first argument
        v2[b] = 1; //second argument
        //Optimized test for zeroness
        for (int iter = 1; iter <= 2*num_arguments; iter++) {
            int r = ThreadLocalRandom.current().nextInt(1, 2*K * num_arguments + 1);
            //Parallelization starts here
            //Calculate each line of v parallely
            double[] v1Old = v1.clone(); //necessary because Lambda expression requires final or effectively final variable
            double[] v2Old = v2.clone();
            double[] v1New =new double[num_arguments]; 
            double[] v2New =new double[num_arguments]; 
            IntStream.range(0, num_arguments).parallel().forEach(i -> {
                double sum1 = 0;
                double sum2 = 0;
                for (int j = 0; j < num_arguments; j++) {
                    if (true) {
                        //F.isAttackedBy(j, i) implement with integers instead of arguments
                        sum1 += v1Old[j];
                        sum2 += v2Old[j];
                    }
                }
                v1New[i] = sum1 * r;
                v2New[i] = sum2 * r;
            });
            v1 = v1New;
            v2 = v2New;
            double sumV1 = 0;
            double sumV2 = 0;
            for (int k = 0; k < num_arguments; k++) {
                sumV1 += v1[k];
                sumV2 += v2[k];
            }
            if (Math.abs(sumV1 - sumV2) > 1e-9) { //For big matrices and big r, risk of rounding errors, therefore approximate comparison
                System.out.println("i = " + iter);
                boolean z = sumV1 > sumV2;
                System.out.println("z = " + z);
                return false;
            }
        }
        return true;
    }
    
    public static boolean equivDis_Sequential(SimpleMatrix F, int a, int b) {
        int K = 1000; // K = 1000 leads to a confidence level of (K-1)/K = 99.9%
        int num_arguments = F.getNumRows();
        SimpleMatrix v1 = new SimpleMatrix(num_arguments, 1);
        SimpleMatrix v2 = new SimpleMatrix(num_arguments, 1);
        v1.set(a, 0, 1); //first argument
        v2.set(b, 0, 1); //second argument
        //Optimized test for zeroness
        for (int iter = 1; iter <= 2*num_arguments; iter++) {
            int r = ThreadLocalRandom.current().nextInt(1, 2*K * num_arguments + 1);
            SimpleMatrix v1New =F.mult(v1).scale(r);
            SimpleMatrix v2New =F.mult(v2).scale(r);
            v1 = v1New;
            v2 = v2New;
            if (Math.abs(v1.elementSum() - v2.elementSum()) > 1e-9) { //For big matrices and big r, risk of rounding errors, therefore approximate comparison
                return false;
            }
        }
        return true;
    }
    
    public static boolean equivDis_Optimal(double[][] F, int a, int b) {
        //Implement the optimal version of the method
        return true;
    }
    
    public static boolean strongDis_Parallel(double[][] F, int a, int b) {
        int K = 1000; // K = 1000 leads to a confidence level of (K-1)/K = 99.9%
        int num_arguments = F.length;
        double[] v1 = new double[num_arguments];
        double[] v2 = new double[num_arguments];
        v1[a] = 1; //first argument
        v2[b] = 1; //second argument
        //Optimized test for zeroness
        for (int iter = 1; iter <= 2*num_arguments; iter++) {
            int r = ThreadLocalRandom.current().nextInt(1, 2*K * num_arguments + 1);
            //Parallelization starts here
            //Calculate each line of v parallely
            double[] v1Old = v1.clone(); //necessary because Lambda expression requires final or effectively final variable
            double[] v2Old = v2.clone();
            double[] v1New =new double[num_arguments]; 
            double[] v2New =new double[num_arguments]; 
            IntStream.range(0, num_arguments).parallel().forEach(i -> {
                double sum1 = 0;
                double sum2 = 0;
                for (int j = 0; j < num_arguments; j++) {
                    sum1 += F[i][j] * v1Old[j];
                    sum2 += F[i][j] * v2Old[j];
                }
                v1New[i] = sum1 * r;
                v2New[i] = sum2 * r;
            });
            v1 = v1New;
            v2 = v2New;
            double sumV1 = 0;
            double sumV2 = 0;
            for (int k = 0; k < num_arguments; k++) {
                sumV1 += v1[k];
                sumV2 += v2[k];
            }
            if (Math.abs(sumV1 - sumV2) > 1e-9) { //For big matrices and big r, risk of rounding errors, therefore approximate comparison
                if (iter % 2 != 0) {
                    if (sumV1 > sumV2) {
                        System.out.println("i ungerade und v1 < v2");
                        System.out.println("v1 = " + sumV1 + ", v2 = " + sumV2);
                        return false;
                    } else {
                        return true;
                    }
                }
                if (iter % 2 == 0) {
                    if (sumV1 > sumV2) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
