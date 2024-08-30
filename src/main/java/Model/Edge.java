package Model;

import javafx.scene.shape.Polyline;
import javafx.util.Pair;

import java.util.ArrayList;

public class Edge {
    private Router head, tail;
    private int weight;
    private Polyline polyline;
    private ArrayList<Pair<Double, Double>> points;
    private Pair<Double, Double> lp; //label position
    public Edge(Router head, Router tail, int weight) {
        this.head = head;
        this.tail = tail;
        this.weight = weight;
    }
    public int getWeight() {
        return weight;
    }
    public Router getHead(){
        return head;
    }
    public Router getTail(){
        return tail;
    }
    public Router getOther(Router self){
        if (self == head) return tail;
        if (self == tail) return head;
        return null;
    }
    public ArrayList<Pair<Double, Double>> getPoints() {
        return points;
    }
    public void setPoints(ArrayList<Pair<Double, Double>> points) {
        this.points = points;
    }
    public Pair<Double, Double> getLp() {
        return lp;
    }
    public void setLp(Pair<Double, Double> lp) {
        this.lp = lp;
    }
    @Override
    public String toString() {
        return "Edge [head=" + head + ", tail=" + tail + ", weight=" + weight + "]";
    }
    public void setPolyline(Polyline p){
        this.polyline = p;
    }
    public Polyline getPolyline(){
        return polyline;
    }
}
