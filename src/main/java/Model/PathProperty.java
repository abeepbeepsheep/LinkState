package Model;

import java.util.ArrayList;

public class PathProperty implements Comparable<PathProperty>, Cloneable{
    public String routerID;
    public int pathLength;
    public ArrayList<String> path;
    public int accessed;
    public PathProperty() {
        routerID = "";
        pathLength = Integer.MAX_VALUE;
        path = new ArrayList<>();
        accessed = 0;
    }
    public int compareTo(PathProperty o) {
        if (accessed != o.accessed){
            if (accessed > o.accessed) return 1;
            else return -1;
        }
        if (pathLength != o.pathLength){
            if (pathLength > o.pathLength) return 1;
            else return -1;
        }
        if (path.hashCode()== o.path.hashCode()) return 0;
        return path.size() < o.path.size() ? -1 : 1;
    }

    @Override
    public PathProperty clone() {
        PathProperty clone = new PathProperty();
        clone.routerID = this.routerID;
        clone.path = new ArrayList<>(path);
        clone.accessed = this.accessed;
        clone.pathLength = this.pathLength;
        return clone;
    }
    @Override
    public String toString() {
        return "RouterID: " + routerID +
                "\nPath Length: " +pathLength +
                "\nAccessed Iteration: " + accessed +
                "\nPath Taken: " + path.toString();
    }
}
