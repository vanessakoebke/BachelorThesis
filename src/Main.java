import java.util.List;

import org.tweetyproject.arg.dung.syntax.DungTheory;

public class Main {
    public static void main(String[] args) {
        //Test ICCMA files
        List<DungTheory> inputICCMA = Util.readICCMA();
        for (DungTheory af : inputICCMA) {
            System.out.println(PTIME_Algorithm.StrongerDis(af, 0, 1));
        }
    }
}
