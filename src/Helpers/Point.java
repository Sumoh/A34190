package Helpers;

/**
 * Created by Tristan on 2017-04-14.
 */
public class Point {

    public int x;
    public int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean equals(Point p){
        return (x == p.x) && (y == p.y);
    }

    public double distance(Point p){
        return Math.hypot(x - p.x, y - p.y);
    }
}
