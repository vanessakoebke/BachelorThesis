package service;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.arg.dung.parser.Iccma23Parser;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class Util {
    public static List<DungTheory> readICCMA() {
        List<DungTheory> output = new ArrayList<>();
        try {
            Path folder = Paths.get("input/ICCMA");
            Iccma23Parser parser = new Iccma23Parser();
            Files.list(folder)
                 .filter(path -> path.toString().endsWith(".af"))
                 .forEach(path -> {
                    try {
                        output.add(
                                 parser.parse(Files.newBufferedReader(path))
                                 );
                    } catch (IOException e) {
                        System.out.println("Parsing file " + path + " failed.");
                        e.printStackTrace();
                    }
                });

        } catch (IOException e) {
            System.out.println("Iterating through the ICCMA folder failed.");
            e.printStackTrace();
        }
        return output;
    }
    
}
