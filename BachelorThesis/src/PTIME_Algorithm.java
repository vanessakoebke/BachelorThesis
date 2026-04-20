import org.ejml.simple.SimpleMatrix;

public class PTIME_Algorithm {
    public static boolean StrongerDis(SimpleMatrix F, int a, int b) {
        SimpleMatrix M = F.copy();
        for (int i = 1; i <= 2 * F.getNumRows() - 1; i++) {
            int Ma = 0;
            int Mb = 0;
          for (int j = 0; j < F.getNumRows(); j++) {
              Ma += M.getIndex(j, a-1);
              Mb += M.getIndex(j, b-1);
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
