package service;
import java.util.List;

import org.tweetyproject.arg.dung.syntax.DungTheory;

import io.Util;

public class Main {
    public static void main(String[] args) {
        //Test ICCMA files
        List<DungTheory> inputICCMA = Util.readICCMA();
        for (DungTheory af : inputICCMA) {
            //TODO test ICCMA
        }
    }
}
