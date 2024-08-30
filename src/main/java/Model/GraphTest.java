package Model;

import Model.Data.GraphJSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.nidi.graphviz.attribute.GraphAttr;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static guru.nidi.graphviz.attribute.GraphAttr.splines;

public class GraphTest {
    public static void main(String[] args) {
        Graph g = new Graph("Test");
        Router[] v = new Router[5];
        v[0] = new Router("1", g);
        v[1] = new Router("2", g);
        v[2] = new Router("3", g);
        v[3] = new Router("4", g);
        v[4] = new Router("5", g);
        Edge[] e = new Edge[6];
        e[0] = (new Edge(v[0],v[1],10));
        e[1] = (new Edge(v[1],v[2],5));
        e[2] = (new Edge(v[2],v[3],3));
        e[3] = (new Edge(v[0],v[3],1));
        e[4] = (new Edge(v[2],v[4],1));
        e[5] = (new Edge(v[1],v[4],1));
        g.init("Routing Network", v, e);
        Router r = g.getRouter("1");
        ArrayList<PathProperty[]> table = r.doRouting();
        for (PathProperty[] p : table) {
            for (PathProperty pp : p) {
                System.out.print(pp.pathLength + " ");
            }
            System.out.println();
        }
        MutableGraph mg = g.toGraphViz();
        mg.graphAttrs().add(splines(GraphAttr.SplineMode.SPLINE));
        String s = String.valueOf(Graphviz.fromGraph(mg).render(Format.JSON0));
        System.out.println(s);
        try {
            Graphviz.fromGraph(mg).render(Format.PNG).toFile(new File("ex2.png"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
