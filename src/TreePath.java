/**
 * Created by Tristan on 2017-04-14.
 */
public class TreePath {

    public Point p;
    public double start;
    public double end;
    public Point prev;

    public TreePath(Point p, double start, double end, Point prev){
        this.p = p;
        this.start = start;
        this.end = end;
        this.prev = prev;
    }

}
