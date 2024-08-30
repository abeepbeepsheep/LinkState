package Model;

import Model.Data.GraphJSON;
import Model.Data.RouterJSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.nidi.graphviz.attribute.GraphAttr;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Node;
import javafx.scene.shape.Polyline;
import javafx.util.Pair;

import java.util.*;

import static guru.nidi.graphviz.attribute.GraphAttr.splines;
import static guru.nidi.graphviz.model.Factory.*;

public class Graph {
    private String name;
    private Router[] routers;
    private HashMap<String, Integer> routerToInt;
    private HashMap<String, Router> stringToRouter;
    private Edge[] edges;
    private boolean hasCoordinates = false;
    public Graph(){}
    public Graph(String name){
        this.name = name;
    }
    public void init(String name, Router[] routers, Edge[] edges){
        this.name = name;
        Arrays.sort(routers, Comparator.comparing(Router::getRouterID));
        this.routers = routers;
        this.edges = edges;
        routerToInt = new HashMap<>();
        stringToRouter = new HashMap<>();
        for (Router router : routers){
            routerToInt.put(router.getRouterID(), routerToInt.size() + 1);
            stringToRouter.put(router.getRouterID(), router);
            router.setRouterInt();
        }
        for (Edge edge : edges){
            edge.getHead().addEdge(edge);
            edge.getTail().addEdge(edge);
        }
    }
    public void generateCoordinates(){
        MutableGraph mg = this.toGraphViz().graphAttrs().add(splines(GraphAttr.SplineMode.SPLINE));
        String s = String.valueOf(Graphviz.fromGraph(mg).render(Format.JSON0));
        try {
            fromJSON(s);
        }catch (JsonProcessingException ex){
            System.out.println("Could not generate coordinates");
        }
    }
    public guru.nidi.graphviz.model.MutableGraph toGraphViz(){
        guru.nidi.graphviz.model.MutableGraph graph = mutGraph(name)
                .setDirected(false);
        HashMap<Router, Node> routerToNode = new HashMap<>();
        for (Router router : routers){
            Node routerNode = node(router.getRouterID());
            routerToNode.put(router,routerNode);
        }

        for (Edge edge : edges){
            Node start = routerToNode.get(edge.getHead());
            Node end = routerToNode.get(edge.getTail());
            int weight = edge.getWeight();
            graph.add(start.link(to(end).with(Label.of(Integer.toString(weight)))));
        }

        return graph;
    }
    public void fromJSON(String json) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        GraphJSON graphJson = mapper.readValue(json, GraphJSON.class);
        this.name = name;
        routers = new Router[graphJson.objects.length];
        for (int i = 0; i < graphJson.objects.length; i++){
            RouterJSON routerJSON = graphJson.objects[i];
            routers[i] = new Router(routerJSON.name, this);
            routers[i].setPosition(toCoordinate(routerJSON.pos));
        }
        edges = new Edge[graphJson.edges.length];
        for (int i = 0; i < graphJson.edges.length; i++){
            edges[i] = new Edge(routers[graphJson.edges[i].head],
                                routers[graphJson.edges[i].tail],
                                Integer.parseInt(graphJson.edges[i].label));

            ArrayList<Pair<Double, Double>> points = new ArrayList<>();
            String[] tokens = graphJson.edges[i].pos.split(" ");
            for (String token : tokens){
                points.add(toCoordinate(token));
            }
            edges[i].setPoints(points);
            edges[i].setLp(toCoordinate(graphJson.edges[i].lp));
            //TODO set position of edges
        }
        hasCoordinates = true;
        init(name, routers, edges);
    }
    public Pair<Double, Double> toCoordinate(String s){
        String[] tokens = s.split(",");
        return new Pair<>(Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]));
    }
    public Router getRouter(String routerID){
        return stringToRouter.get(routerID);
    }
    public int getRouterInt(String routerID){
        return routerToInt.get(routerID);
    }
    public int getNumRouters(){
        return routers.length;
    }
    public Router[] getRouters(){
        if (!hasCoordinates) generateCoordinates();
        return routers;
    }
    public Edge[] getEdges(){
        if (!hasCoordinates) generateCoordinates();
        return edges;
    }
    public Edge getEdge(Router head, Router tail){
        for (Edge edge : edges){
            if (edge.getOther(head) == tail){
                return edge;
            }
        }
        return null;
    }
}
