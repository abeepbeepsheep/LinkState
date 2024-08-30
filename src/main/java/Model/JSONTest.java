package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class JSONTest {
    public static void main(String[] args) throws IOException {
        String json = Files.readString(Path.of("JSONTest.json"));
        Graph graph = new Graph();
        graph.fromJSON(json);
        Router r = graph.getRouter("1");
        ArrayList<PathProperty[]> table = r.doRouting();
        for (PathProperty[] p : table) {
            for (PathProperty pp : p) {
                System.out.print(pp.pathLength + " ");
            }
            System.out.println();
        }
    }
}
