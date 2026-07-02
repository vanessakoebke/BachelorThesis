package service;
import io.Util;

public class Main {
    public static void main(String[] args) {
        int[] inputSizes = new int[] {10, 50, 100, 500, 1000, 5000, 10000};
        try {
            //RQ 1.1
            //RunTimeAnalysisNC.equivDis_parallel_vs_sequential_size(inputSizes, 0.5, 1000);
            RunTimeAnalysisPTIME.equivDis_parallel_vs_sequential_size(inputSizes, 0.5, 1000);
            
            //RQ 2.1
            RunTimeAnalysisNC.strongerDis_parallel_vs_sequential_size(inputSizes, 0.5, 1000);
            RunTimeAnalysisPTIME.strongerDis_parallel_vs_sequential_size(inputSizes, 0.5, 1000);
            
            //RQ 1.2 and 2.2
            //RunTimeAnalysisNC.equivDis_optimal1_vs_spaceOptimized_size(inputSizes, 0.5, 1000);
            
            
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
