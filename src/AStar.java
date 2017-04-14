import Helpers.Point;
import Helpers.Set;

import java.awt.*;
import java.util.ArrayList;
import Helpers.Rectangle;

/**
 * Created by Tristan on 2017-04-14.
 */

public class AStar {

    Point start;
    Point end;
    QuadTree tree;
    Graphics2D g;

    public AStar(Point start, Point end, QuadTree tree, Graphics2D g){
        this.start = tree.getPointLocation(start.x, start.y);
        this.end = tree.getPointLocation(end.x, end.y);
        this.tree = tree;
        this.g = g;

    }

    public boolean findPath(){

        Set set = new Set(start, 0, Math.hypot(end.x - start.x, end.y - start.y), null);
        ArrayList<Set> openSet = new ArrayList<>();
        openSet.add(set);
        ArrayList<Set> closedSet = new ArrayList<>();

        Rectangle centerStart = tree.getPointLocationAndDimensions(start.x, start.y);
        Rectangle centerEnd = tree.getPointLocationAndDimensions(end.x, end.y);

        int ovalWidth = 10;
        int ovalHeight = 10;

        int ax = (centerStart.point.x + (centerStart.w/2)) - (ovalWidth/2);
        int ay = (centerStart.point.y + (centerStart.h/2)) - (ovalHeight/2);
        int bx = (centerEnd.point.x + (centerEnd.w/2)) - (ovalWidth/2);
        int by = (centerEnd.point.y + (centerEnd.h/2)) - (ovalHeight/2);

        g.setColor(new Color(0,255,0));
        g.drawOval(ax, ay, 10,10);
        g.drawOval(bx, by, 10,10);

        Set current;

        while (openSet.size() > 0){
            current = openSet.get(0);
            for (int i = 0; i < openSet.size(); i++){
                if (openSet.get(i).end < current.end){
                    current = openSet.get(i);
                }
            }

            if (current.p.equals(end)){

                while (current.prev != null) {
                    g.setColor(new Color(255, 0, 0));
                    Rectangle r = tree.getPointLocationAndDimensions(current.prev.x, current.prev.y);
                    Rectangle rc = tree.getPointLocationAndDimensions(current.p.x, current.p.y);

                    ax = (r.point.x + (r.w/2));
                    ay = (r.point.y + (r.h/2));
                    bx = (rc.point.x + (rc.w/2));
                    by = (rc.point.y + (rc.h/2));

                    g.drawLine(ax,ay,bx,by);

                    //g.drawLine((current.prev.x, current.prev.y, current.p.x, current.p.y);
                    boolean bail = false;
                    for (Set sets : openSet) {
                        if (current.prev != null && current.prev.equals(sets.p) && bail != true) {
                            current = sets;
                            bail = true;
                        }
                    }
                    for (Set sets : closedSet) {
                        if (current.prev != null && current.prev.equals(sets.p) && bail != true) {
                            current = sets;
                            bail = true;
                        }
                    }
                }
                return true;
            }

            openSet.remove(current);
            closedSet.add(current);
            ArrayList<Rectangle> neighbors = tree.getNeighbors(current.p.x, current.p.y);
            for (Rectangle neighbor : neighbors){
                Point p = neighbor.point;
                boolean found = false;
                for (Set sets : closedSet){
                    if (sets.p.equals(p)){
                        found = true;
                    }
                }
                if (!found){
                    double tenativeFromStart = current.start + Math.hypot(neighbor.point.x - current.p.x, neighbor.point.y - current.p.y);
                    boolean foundOpen = false;
                    for (int i = 0; i < openSet.size(); i++){
                        Set sets = openSet.get(i);
                        if (sets.p.equals(p)){
                            foundOpen = true;
                            if (tenativeFromStart < sets.start){
                                Set temp = new Set(sets.p, tenativeFromStart, tenativeFromStart + Math.hypot(end.x - p.x, end.y - p.y), current.p);
                                openSet.set(i, sets);
                            }
                        }
                    }
                    if (!foundOpen){
                        openSet.add(new Set(neighbor.point, tenativeFromStart, tenativeFromStart + Math.hypot(end.x - neighbor.point.x, end.y - neighbor.point.y), current.p));
                    }
                }
            }
        }

        return false;
    }

}
