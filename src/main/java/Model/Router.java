package Model;

import javafx.util.Pair;

import java.util.ArrayList;

public class Router {
    private Graph graph;
    private String routerID;
    private int routerInt;
    private Pair<Double, Double> position;
    private ArrayList<Edge> edges; //router, weight
    private SegmentTree segmentTree;
    private ArrayList<PathProperty[]> table;
    //each row is 1 time interval, column is router, pair is <pathLength, path>
    public Router(String routerID, Graph parent){
        this.graph = parent;
        this.routerID = routerID;
        edges = new ArrayList<>();
    }

//TODO sort table based on labels as integers
    public void setRouterInt(){
        routerInt = graph.getRouterInt(routerID);
    }
    public String getRouterID(){
        return routerID;
    }
    public ArrayList<Edge> getEdges() {
        return edges;
    }
    public void addEdge(Edge edge){
        edges.add(edge);
    }
    public ArrayList<PathProperty[]> doRouting(){
        segmentTree = new SegmentTree(1, graph.getNumRouters());
        PathProperty selfPath = new PathProperty();
        selfPath.path = new ArrayList<>();
        selfPath.pathLength = 0;
        selfPath.accessed = 0;
        selfPath.routerID = routerID;
        selfPath.path.add(routerID);
        segmentTree.update(routerInt, selfPath, 0);
        //initialize self to nothing
        PathProperty nextPath = new PathProperty();
        nextPath.accessed = 0;
        nextPath = segmentTree.query();
        table = new ArrayList<>();
        int numIter = 1;
        while (nextPath.accessed == 0){
            int pos = graph.getRouterInt(nextPath.routerID);
            segmentTree.update(pos, null, numIter++);
            Router startRouter = graph.getRouter(nextPath.routerID);
            for (Edge edge : startRouter.getEdges()){
                Router endRouter = edge.getOther(startRouter);
                PathProperty pathThroughNextRouter = nextPath.clone();
                pathThroughNextRouter.pathLength += edge.getWeight();
                pathThroughNextRouter.accessed = 0;
                pathThroughNextRouter.path.add(endRouter.routerID);
                pathThroughNextRouter.routerID = endRouter.routerID;
                segmentTree.update(endRouter.routerInt, pathThroughNextRouter, 0);
            }
            table.add(segmentTree.toArray());
            //pull shortest path node
            //for each node:
            //do pathProperty + this edge
            //update the router with this node
            //pull the current state of segtree into the table
            nextPath = segmentTree.query();
        }
        return table;
    }

    public void setPosition(Pair<Double, Double> position) {
        this.position = position;
    }
    public Pair<Double, Double> getPosition(){
        return position;
    }

    @Override
    public String toString() {
        return routerID + " Router";
    }
}
