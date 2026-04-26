package service;
import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.math.matrix.Matrix;

public class PTIME_Algorithm {
    
    //Implementation using the SimpleMatrix of the EJML library
    public static boolean StrongerDis(SimpleMatrix F, int a, int b) {
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
        System.out.println("PTIME algorithm terminated but no result was found.");
        return false;
    }
    
    //Implementation using the Matrix class of the TweetyProject library
    public static boolean StrongerDis(DungTheory aaf, int a, int b) {
        Matrix F = aaf.getAdjacencyMatrix();
        Matrix M = aaf.getAdjacencyMatrix();
        for (int i = 1; i <= 2 * F.getXDimension() - 1; i++) {
            int Ma = 0;
            int Mb = 0;
          for (int j = 0; j < F.getXDimension(); j++) {
              Ma += M.getEntry(j, a).doubleValue();
              Mb += M.getEntry(j, b).doubleValue();
          }
          if ((i % 2 != 0 && Ma < Mb) || (i % 2 == 0 && Mb < Ma)) {
              return true;
          }
          if (Ma != Mb) {
              return false;
          }
          M = M.mult(F);
        }
        System.out.println("PTIME algorithm terminated but no result was found.");
        return false;
    }
}
