
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.RepeatedTest;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import org.tweetyproject.comparator.LatticePartialOrder;

import model.NC_Algorithm;
import model.PTIME_Algorithm;

public class AlgorithmTest {
 
    
    @RepeatedTest(1000)
    void testStrongDisAgainstReference(){
        DungTheory af = new DefaultDungTheoryGenerator(10, 0.5).next();
        Iterator<Argument> iterator = af.getNodes().iterator();
        Argument a = iterator.next();
        Argument b = iterator.next();
        DiscussionBasedRankingReasoner referenceAlgo = new DiscussionBasedRankingReasoner();
        LatticePartialOrder<Argument, DungTheory> ranking = referenceAlgo.getModel(af);
        boolean reference = ranking.isStrictlyMoreOrEquallyAcceptableThan(a, b);
        boolean ptime = PTIME_Algorithm.strongDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        boolean nc = NC_Algorithm.strongDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        assertTrue(reference ==ptime && reference == nc);
    }

    @RepeatedTest(1000)
    void testEquivDisAgainstReference(){
        DungTheory af = new DefaultDungTheoryGenerator(10, 0.5).next();
        Iterator<Argument> iterator = af.getNodes().iterator();
        Argument a = iterator.next();
        Argument b = iterator.next();
        DiscussionBasedRankingReasoner referenceAlgo = new DiscussionBasedRankingReasoner();
        LatticePartialOrder<Argument, DungTheory> ranking = referenceAlgo.getModel(af);
        boolean reference = ranking.isEquallyAcceptableThan(a, b);
        boolean ptime = PTIME_Algorithm.equivDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        boolean nc = NC_Algorithm.equivDis_Sequential(new SimpleMatrix(af.getAdjacencyArray()), 0, 1);
        assertTrue(reference ==ptime && reference == nc);
    }

}
