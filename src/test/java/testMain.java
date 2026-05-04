import service.RunTimeAnalysisNC;

public class testMain {
    public static void main(String[] args) {
        int[] sizes = {10, 50, 100, 500, 1000, 5000};
        RunTimeAnalysisNC.equivDis_parallel_vs_sequential_size(sizes, 0.5, 500);
        
//        DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(10, 0.5);
//        for (int i = 0; i < 100; i++) {
//            double[][] m = generator.next().getAdjacencyArray();
//
//            int a = ThreadLocalRandom.current().nextInt(0, 10);
//            int b = ThreadLocalRandom.current().nextInt(0, 10);
//            System.out.println("Run " + i);
//            System.out.println("StrongDis: " + PTIME_Algorithm.StrongerDis(new SimpleMatrix(m), a, b));
//            System.out.println("EquivDis: " + NC_Algorithm.equivDis_Parallel(m, a, b));
//        }
        
        // RunTimeAnalysis.NCFparavsNCFejml(500, 20);
    }
}
