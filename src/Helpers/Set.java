package Helpers;

/**
 * Created by Tristan on 2017-04-14.
 */
public class Set {

    public Point p;
    public double start;
    public double end;
    public Point prev;

    public Set(Point p, double start, double end, Point prev){
        this.p = p;
        this.start = start;
        this.end = end;
        this.prev = prev;
    }

}
