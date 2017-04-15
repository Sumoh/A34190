import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Created by Tristan on 2017-04-14.
 */

public class AStar {

    Point start;
    Point end;
    QuadTree tree;
    Graphics2D g;

    public AStar(Point start, Point end, QuadTree tree, Graphics2D g){
        this.start = tree.getTreePoint(start.x, start.y);
        this.end = tree.getTreePoint(end.x, end.y);
        this.tree = tree;
        this.g = g;

    }

    public boolean findPath(){

        TreePath treePath = new TreePath(start, 0, Math.hypot(end.x - start.x, end.y - start.y), null);
        ArrayList<TreePath> openTreePath = new ArrayList<>();
        openTreePath.add(treePath);
        ArrayList<TreePath> closedTreePath = new ArrayList<>();


        TreePath current;

        while (openTreePath.size() > 0){
            current = openTreePath.get(0);
            for (int i = 0; i < openTreePath.size(); i++){
                if (openTreePath.get(i).end < current.end){
                    current = openTreePath.get(i);
                }
            }

            if (current.p.equals(end)){

                while (current.prev != null) {
                    g.setColor(new Color(0, 255, 255));
                    Rectangle r = tree.getTreeNode(current.prev.x, current.prev.y);
                    Rectangle rc = tree.getTreeNode(current.p.x, current.p.y);

                    int ax = (r.point.x + (r.w/2));
                    int ay = (r.point.y + (r.h/2));
                    int bx = (rc.point.x + (rc.w/2));
                    int by = (rc.point.y + (rc.h/2));

                    g.drawLine(ax,ay,bx,by);

                    //g.drawLine((current.prev.x, current.prev.y, current.p.x, current.p.y);
                    boolean bail = false;
                    for (TreePath sets : openTreePath) {
                        if (current.prev != null && current.prev.equals(sets.p) && bail != true) {
                            current = sets;
                            bail = true;
                        }
                    }
                    for (TreePath sets : closedTreePath) {
                        if (current.prev != null && current.prev.equals(sets.p) && bail != true) {
                            current = sets;
                            bail = true;
                        }
                    }
                }
                return true;
            }

            openTreePath.remove(current);
            closedTreePath.add(current);
            ArrayList<Rectangle> neighbors = tree.getNeighborNodes(current.p.x, current.p.y);
            for (Rectangle neighbor : neighbors){
                Point p = neighbor.point;
                boolean found = false;
                for (TreePath sets : closedTreePath){
                    if (sets.p.equals(p)){
                        found = true;
                    }
                }
                if (!found){
                    double tenativeFromStart = current.start + Math.hypot(neighbor.point.x - current.p.x, neighbor.point.y - current.p.y);
                    boolean foundOpen = false;
                    for (int i = 0; i < openTreePath.size(); i++){
                        TreePath sets = openTreePath.get(i);
                        if (sets.p.equals(p)){
                            foundOpen = true;
                            if (tenativeFromStart < sets.start){
                                TreePath temp = new TreePath(sets.p, tenativeFromStart, tenativeFromStart + Math.hypot(end.x - p.x, end.y - p.y), current.p);
                                openTreePath.set(i, sets);
                            }
                        }
                    }
                    if (!foundOpen){
                        openTreePath.add(new TreePath(neighbor.point, tenativeFromStart, tenativeFromStart + Math.hypot(end.x - neighbor.point.x, end.y - neighbor.point.y), current.p));
                    }
                }
            }
        }

        return false;
    }

}
