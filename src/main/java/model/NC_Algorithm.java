package model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class NC_Algorithm {
    private static int K = 1000; //K = 1000 leads to a confidence level of (K-1)/K = 99.9%
    
    public static boolean equivDis_Parallel(double[][] F, int a, int b) {
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
                return false;
            }
        }
        return true;
    }
    
    public static boolean equivDis_SpaceOptimized(DungTheory F, int a, int b) {
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
                    if (F.isAttackedBy(new Argument("a" +j), new Argument("a" +i))) {
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
                return false;
            }
        }
        return true;
    }
    
    public static boolean equivDis_Sequential(SimpleMatrix F, int a, int b) {
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
    
    public static boolean equivDis_Optimal1(double[][] F, int a, int b) {
        if (F.length < 100) {
            return equivDis_Sequential(new SimpleMatrix(F), a, b);
        } else {
            return equivDis_Parallel(F, a, b);
        }
    }
    
    public static boolean strongDis_Parallel(double[][] F, int a, int b) {
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
    
    public static boolean strongDis_spaceOptimized(DungTheory F, int a, int b) {
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
                    if (F.isAttackedBy(new Argument("a" +j), new Argument("a" +i))) {
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
                if (iter % 2 != 0) {
                    if (sumV1 > sumV2) {
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
    
    public static boolean strongDis_Sequential(SimpleMatrix F, int a, int b) {
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
            double sumV1 = v1.elementSum();
            double sumV2 = v2.elementSum();
            if (Math.abs(sumV1 - sumV2) > 1e-9) { //For big matrices and big r, risk of rounding errors, therefore approximate comparison
                if (iter % 2 != 0) {
                    if (sumV1 > sumV2) {
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
