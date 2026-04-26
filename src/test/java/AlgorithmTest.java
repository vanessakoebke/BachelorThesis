
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import org.tweetyproject.comparator.LatticePartialOrder;

import service.PTIME_Algorithm;

public class AlgorithmTest {
 
    
    @Test
    void testAgainstReference(){
        DungTheory af = new DefaultDungTheoryGenerator(10, 0.8).next();
        Iterator<Argument> iterator = af.getNodes().iterator();
        Argument a = iterator.next();
        Argument b = iterator.next();
        DiscussionBasedRankingReasoner referenceAlgo = new DiscussionBasedRankingReasoner();
        LatticePartialOrder<Argument, DungTheory> ranking = referenceAlgo.getModel(af);
        boolean reference = ranking.isStrictlyMoreOrEquallyAcceptableThan(a, b);
        boolean ptime = PTIME_Algorithm.StrongerDis(af, 0, 1);
        assertTrue(reference ==ptime);
    }
}
