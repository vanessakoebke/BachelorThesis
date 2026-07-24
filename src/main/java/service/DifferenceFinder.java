package service;

import org.ejml.simple.SimpleMatrix;

import io.Util;
import model.*;

public class DifferenceFinder {
    
    public static void main(String[] args) {
        String file = "instances/n10_test170.csv";
        try {
            int counter = 0;
            Instance inst = Util.loadInstance(file);
            SimpleMatrix matrix = new SimpleMatrix(inst.A());
            boolean ptime;
            boolean nc;
            for (int i = 0; i < 1; i++) {
                ptime = PTIME_Algorithm.strongerDis_Sequential(matrix, inst.a(), inst.b());
                nc = NC_Algorithm.strongerDis_Sequential(matrix, inst.a(), inst.b());
                if (ptime != nc) {
                    counter++;
                }
            }
            //System.out.println("PTime: " + ptime);
            //System.out.println("NC: " + nc);
            System.out.println(counter);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
