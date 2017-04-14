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
        this.start = start;
        this.end = end;
        this.tree = tree;
        this.g = g;

    }

    public boolean findPath(){
        start = tree.getPointLocation(start.x, start.y);
        end = tree.getPointLocation(end.x, end.y);

        Set set = new Set(start, 0, Math.hypot(end.x - start.x, end.y - start.y), null);
        ArrayList<Set> openSet = new ArrayList<>();
        openSet.add(set);
        ArrayList<Set> closedSet = new ArrayList<>();


        g.setColor(new Color(0,255,0));
        g.drawOval(start.x, start.y, 10,10);
        g.drawOval(end.x, end.y, 10,10);


        Set current;

        while (openSet.size() > 0){
            current = openSet.get(0);
            for (int i = 0; i < openSet.size(); i++){
                if (openSet.get(i).end < current.end){
                    current = openSet.get(i);
                }
            }

            if (current.p.equals(end)){
                System.out.println("FOUND IT");

                while (current.prev != null) {
                    g.setColor(new Color(255, 0, 0));
                    g.drawLine(current.prev.x, current.prev.y, current.p.x, current.p.y);
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
