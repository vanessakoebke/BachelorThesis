
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import org.tweetyproject.comparator.LatticePartialOrder;

import model.NC_Algorithm;
import model.PTIME_Algorithm;

public class AlgorithmTest {
 
    
    //@Test
    void testAgainstReference(){
        DungTheory af = new DefaultDungTheoryGenerator(10, 0.8).next();
        Iterator<Argument> iterator = af.getNodes().iterator();
        Argument a = iterator.next();
        Argument b = iterator.next();
        DiscussionBasedRankingReasoner referenceAlgo = new DiscussionBasedRankingReasoner();
        LatticePartialOrder<Argument, DungTheory> ranking = referenceAlgo.getModel(af);
        boolean reference = ranking.isStrictlyMoreOrEquallyAcceptableThan(a, b);
        boolean ptime = PTIME_Algorithm.strongDis_Seq(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        assertTrue(reference ==ptime);
    }
    
    @RepeatedTest(50)
    void testEquivDisA_(){
        DungTheory af = new DefaultDungTheoryGenerator(10, 0.5).next();
        Iterator<Argument> iterator = af.getNodes().iterator();
        Argument a = iterator.next();
        Argument b = iterator.next();
        DiscussionBasedRankingReasoner referenceAlgo = new DiscussionBasedRankingReasoner();
        LatticePartialOrder<Argument, DungTheory> ranking = referenceAlgo.getModel(af);
        boolean reference = ranking.isEquallyAcceptableThan(a, b);
        boolean ptime = PTIME_Algorithm.equivDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        boolean nc = NC_Algorithm.equivDis_Sequential_DoubleMatrix(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        System.out.println(reference + " " + ptime + " " + nc);
        assertTrue(reference == ptime && ptime == nc);
    }
    
    @RepeatedTest(50)
    void testEquivDisF(){
        DungTheory af = new DefaultDungTheoryGenerator(100, 0.5).next();
        boolean ptime = PTIME_Algorithm.equivDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        boolean nc = NC_Algorithm.equivDis_Sequential_DoubleMatrix(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        System.out.println( ptime + " " + nc);
        assertTrue(ptime == nc);
    }
    
    @RepeatedTest(1000)
    void testStrongDis_PTime_vs_NC() {
        DungTheory af = new DefaultDungTheoryGenerator(100, 0.5).next();
        boolean ptime = PTIME_Algorithm.strongDis_Seq(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        boolean nc = NC_Algorithm.strongDis_Parallel(af.getAdjacencyArray(), 0, 1);
        System.out.println("StrongDis: " + ptime + " " + nc);
        assertTrue(ptime == nc);
    }
}
