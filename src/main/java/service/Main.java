package service;
import io.Util;

public class Main {
    public static void main(String[] args) {
        //Test ICCMA files
        try {
            Util.testGenerator();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        List<DungTheory> inputICCMA = Util.readICCMA();
//        for (DungTheory af : inputICCMA) {
//            //TODO test ICCMA
//        }
    }
}
