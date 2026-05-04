package model;

import java.util.stream.IntStream;

import org.ejml.simple.SimpleMatrix;

public class PTIME_Algorithm {
    
    // Implementation using the SimpleMatrix of the EJML library
    public static boolean strongDis_Sequential(SimpleMatrix F, int a, int b) {
        SimpleMatrix M = F.copy();
        for (int i = 1; i <= 2 * F.getNumRows() - 1; i++) {
            int Ma = 0;
            int Mb = 0;
            for (int j = 0; j < F.getNumRows(); j++) {
                Ma += M.get(j, a);
                Mb += M.get(j, b);
            }
            if ((i % 2 != 0 && Ma < Mb) || (i % 2 == 0 && Mb < Ma)) {
                return true;
            }
            if (Ma != Mb) {
                return false;
            }
            M = M.mult(F);
        }
        return false;
    }
    
    public static boolean strongDis_Parallel(double[][] F, int a, int b) {
        int n = F.length;
        double[][] M = new double[n][];
        for (int i = 0; i < n; i++) {
            M[i] = F[i].clone();
        }
        for (int iter = 1; iter <= 2 * n - 1; iter++) {
            int Ma = 0;
            int Mb = 0;
            for (int j = 0; j < n; j++) {
                Ma += M[j][a];
                Mb += M[j][b];
            }
            if ((iter % 2 != 0 && Ma < Mb) || (iter % 2 == 0 && Mb < Ma)) {
                return true;
            }
            if (Ma != Mb) {
                return false;
            }
            //Copy necessary because Lambda expression requires effectively final variable
            double[][] Mold = new double[n][];
            for (int i = 0; i < n; i++) {
                Mold[i] = M[i].clone();
            }
            double[][] Mnew = new double[n][n];
            //Parallelization starts here
            IntStream.range(0, n).parallel().forEach(i -> {
                for (int j = 0; j < n; j++) {
                    double sum = 0;
                    for (int k = 0; k < n; k++) {
                        sum += Mold[i][k] * F[k][j];
                    }
                    Mnew[i][j] = sum;
                }
            });
            M = Mnew;
        }
        return false;
    }
    // Implementation using the Matrix class of the TweetyProject library
//    public static boolean StrongerDis(DungTheory aaf, int a, int b) {
//        Matrix F = aaf.getAdjacencyMatrix();
//        Matrix M = aaf.getAdjacencyMatrix();
//        for (int i = 1; i <= 2 * F.getXDimension() - 1; i++) {
//            int Ma = 0;
//            int Mb = 0;
//          for (int j = 0; j < F.getXDimension(); j++) {
//              Ma += M.getEntry(j, a).doubleValue();
//              Mb += M.getEntry(j, b).doubleValue();
//          }
//          if ((i % 2 != 0 && Ma < Mb) || (i % 2 == 0 && Mb < Ma)) {
//              return true;
//          }
//          if (Ma != Mb) {
//              return false;
//          }
//          M = M.mult(F);
//        }
//        System.out.println("PTIME algorithm terminated but no result was found.");
//        return false;
//    }

    // Implementation using the SimpleMatrix of the EJML library
    public static boolean equivDis_Sequential(SimpleMatrix F, int a, int b) {
        SimpleMatrix M = F.copy();
        for (int i = 1; i <= 2 * F.getNumRows() - 1; i++) {
            int Ma = 0;
            int Mb = 0;
            for (int j = 0; j < F.getNumRows(); j++) {
                Ma += M.get(j, a);
                Mb += M.get(j, b);
            }
            if ((i % 2 != 0 && Ma < Mb) || (i % 2 == 0 && Mb < Ma)) {
                return false;
            }
            if (Ma != Mb) {
                return false;
            }
            M = M.mult(F);
        }
        return true;
    }

    public static boolean equivDis_Parallel(double[][] F, int a, int b) {
        int n = F.length;
        double[][] M = new double[n][];
        for (int i = 0; i < n; i++) {
            M[i] = F[i].clone();
        }
        for (int iter = 1; iter <= 2 * n - 1; iter++) {
            int Ma = 0;
            int Mb = 0;
            for (int j = 0; j < n; j++) {
                Ma += M[j][a];
                Mb += M[j][b];
            }
            if ((iter % 2 != 0 && Ma < Mb) || (iter % 2 == 0 && Mb < Ma)) {
                return false;
            }
            if (Ma != Mb) {
                return false;
            }
            //Copy necessary because Lambda expression requires effectively final variable
            double[][] Mold = new double[n][];
            for (int i = 0; i < n; i++) {
                Mold[i] = M[i].clone();
            }
            double[][] Mnew = new double[n][n];
            //Parallelization starts here
            IntStream.range(0, n).parallel().forEach(i -> {
                for (int j = 0; j < n; j++) {
                    double sum = 0;
                    for (int k = 0; k < n; k++) {
                        sum += Mold[i][k] * F[k][j];
                    }
                    Mnew[i][j] = sum;
                }
            });
            M = Mnew;
        }
        return true;
    }

    public static void equivDis_Optimal(double[][] array, int a, int b) {
        // TODO Auto-generated method stub
    }
}
