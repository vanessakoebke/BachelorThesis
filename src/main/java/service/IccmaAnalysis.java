package service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class IccmaAnalysis {
    public static void main(String[] args) {
        List<Path> files = new ArrayList<>();
        try {
            Path folder = Paths.get("input/ICCMA");
            Files.list(folder).filter(path -> path.toString().endsWith(".af")).forEach(x -> files.add(x));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(files.getFirst());
    }
}
