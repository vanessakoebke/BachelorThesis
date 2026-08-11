package service;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

public class SpaceTester {
    
    public static void spaceLimit() {
        int n = 30000;
        while (true) {
            DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(n, 0.5);
            DungTheory aaf = generator.next();
            System.out.println(n + " successful DungTheory! " + aaf.getNumberOfNodes());
            //double[][] matrix = aaf.getAdjacencyArray();
            //System.out.println(n + " successful Adjacency Matrix! " + matrix[0][0]);
            n += 1000;
        }
    }
}
