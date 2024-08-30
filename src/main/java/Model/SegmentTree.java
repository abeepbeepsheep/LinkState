package Model;

public class SegmentTree {
    private final int start;
    private final int end;
    private final int mid;
    private SegmentTree left, right;

    private PathProperty pathProperty;

    public SegmentTree(int start, int end) {
        this.start = start;
        this.end = end;
        mid = start + (end - start) / 2;
        if (start != end){
            left = new SegmentTree(this.start, mid);
            right = new SegmentTree(mid + 1, this.end);
        }
        pathProperty = new PathProperty();
    }
    public void update(int position, PathProperty pathProperty, int accessed){
        if (start == end) {
            if (accessed > 0){
                this.pathProperty.accessed = accessed;
                return;
            }
            if (this.pathProperty.accessed > 0) return; //do not update this node, shortest length found
            if (this.pathProperty.pathLength > pathProperty.pathLength)
                this.pathProperty = pathProperty.clone();
            return;
        }
        if (position > mid) right.update(position, pathProperty, accessed);
        else left.update(position, pathProperty, accessed);
        if (left.pathProperty.compareTo(right.pathProperty) < 0)
            this.pathProperty = left.pathProperty.clone();
        else
            this.pathProperty = right.pathProperty.clone();
    }
    public PathProperty query(){
        return pathProperty;
    }
    public PathProperty[] toArray(){
        PathProperty[] children = new PathProperty[end - start + 1];
        if (start == end) children[0] = pathProperty.clone();
        else{
            PathProperty[] leftChildren = left.toArray();
            PathProperty[] rightChildren = right.toArray();
            System.arraycopy(leftChildren, 0, children, 0, leftChildren.length);
            System.arraycopy(rightChildren, 0, children, leftChildren.length, rightChildren.length);
        }
        return children;
    }
}
