package Model;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class DOTtoJSON {
    public static String parseDOT(String dot) throws IOException {
        MutableGraph g = new Parser().read(dot);
        return Graphviz.fromGraph(g).render(Format.JSON0).toString();
    }
    public static void main(String[] args) throws IOException {
        MutableGraph g = new Parser().read("graph G {\n" +
                "    1 -- 2 [label=\"5\"];\n" +
                "    1 -- 3 [label=\"8\"];\n" +
                "    1 -- 4 [label=\"2\"];\n" +
                "    2 -- 5 [label=\"7\"];\n" +
                "    2 -- 6 [label=\"3\"];\n" +
                "    3 -- 7 [label=\"4\"];\n" +
                "    3 -- 8 [label=\"6\"];\n" +
                "    4 -- 9 [label=\"1\"];\n" +
                "    4 -- 10 [label=\"9\"];\n" +
                "    5 -- 11 [label=\"3\"];\n" +
                "    5 -- 12 [label=\"2\"];\n" +
                "    6 -- 13 [label=\"7\"];\n" +
                "    6 -- 14 [label=\"5\"];\n" +
                "    7 -- 15 [label=\"8\"];\n" +
                "    7 -- 16 [label=\"4\"];\n" +
                "    8 -- 10 [label=\"6\"];\n" +
                "    9 -- 11 [label=\"5\"];\n" +
                "    9 -- 12 [label=\"1\"];\n" +
                "    10 -- 13 [label=\"2\"];\n" +
                "    11 -- 14 [label=\"3\"];\n" +
                "    12 -- 15 [label=\"9\"];\n" +
                "    13 -- 16 [label=\"7\"];\n" +
                "    14 -- 15 [label=\"4\"];\n" +
                "    15 -- 16 [label=\"8\"];\n" +
                "}");
        FileWriter out = new FileWriter("tc3.json");
        System.out.println(Graphviz.fromGraph(g).render(Format.JSON0));
        out.write(Graphviz.fromGraph(g).render(Format.JSON0).toString());
        out.close();
        Graphviz.fromGraph(g).height(1000).render(Format.PNG).toFile(new File("tc3.png"));
    }
}
