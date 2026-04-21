import org.ejml.simple.SimpleMatrix;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

public class testMain {
    public static void main(String[] args) {
        DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(10, 0.5);
        for (int i = 1; i <= 10; i++) {
            DungTheory aaf = generator.next();
            for (int x = 0; x < aaf.getNumberOfNodes(); x++) {
                for (int y = 0; y < aaf.getNumberOfNodes(); y++) {                    
                    System.out.println("AAF " + i + ": Is argument " + x + " at least as strong as argument " + y + "? " + PTIME_Algorithm.StrongerDis(aaf, x, y));
                }
            }
        }
    }
}

