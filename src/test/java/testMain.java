import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import service.PTIME_Algorithm;

public class testMain {
    public static void main(String[] args) {
        DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(10, 0.5);
        double[][] array = generator.next().getAdjacencyArray();
        System.out.println(PTIME_Algorithm.StrongerDis(new SimpleMatrix(array), 0, 1));
    }
}
