package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.RepeatedTest;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

public class NC_Algorithm_Test {
    
    @RepeatedTest(1000)
    void testEquivDis(){
        DungTheory af = new DefaultDungTheoryGenerator(100, 0.5).next();
        boolean seq = NC_Algorithm.equivDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        boolean parallel = NC_Algorithm.equivDis_Parallel(af.getAdjacencyArray(), 0, 1);
        System.out.println( seq + " " + parallel);
        assertTrue(seq == parallel);
    }
    
    @RepeatedTest(1000)
    void testStrongDis(){
        DungTheory af = new DefaultDungTheoryGenerator(100, 0.5).next();
        boolean seq = NC_Algorithm.strongDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        boolean parallel = NC_Algorithm.strongDis_Parallel(af.getAdjacencyArray(), 0, 1);
        System.out.println( seq + " " + parallel);
        assertTrue(seq == parallel);
    }
}
