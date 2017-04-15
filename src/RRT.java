import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tristan on 2017-04-14.
 */
public class RRT {

    Point start;
    Point end;
    ArrayList<Rectangle> obstacles;
    int size;
    int MAXNODES = 5000;
    double step = 10.0;

    public RRT(Point start, Point end, ArrayList<Rectangle> obstacles, int size){

        this.obstacles = obstacles;
        this.start = start;
        this.end = end;
        this.size = size;

    }

    public Point getNextPoint(Point p1, Point p2){

        if (p1.distance(p2) < step){
            return p2;
        }

        double theta = Math.atan2(p2.y-p1.y, p2.x - p1.x);
        return new Point((int)(p1.x + step * Math.cos(theta)), (int)(p1.y + step *Math.sin(theta)) );
    }

    public boolean intersectsObstacles(Point p){
        for (Rectangle obs : obstacles){
            if (obs.pointIntersects(p)){
                return true;
            }
        }

        return false;
    }

    public Point getRandomPoint(){
        int tries = 0;
        Point p;
        while (tries <= 1000){ //incase the grid is ALL obstacles.
            p = new Point(randInt(0,size), randInt(0, size));
            if (!intersectsObstacles(p)){
                return p;
            }
            tries++;
        }

        return null;
    }

    public boolean pointInGoal(Point p1, double radius){
        double dist = p1.distance(end);
        if (dist <= radius){
            return true;
        }

        return false;
    }


    public boolean findPath(Graphics2D g){

        g.setColor(new Color(255,0,0));

        Node startNode = new Node(start, null);
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(startNode);

        int count = 0;

        while (count <= MAXNODES){

            boolean foundNext = false;
            Node parent = nodes.get(0);
            Point rand = getRandomPoint();
            while (!foundNext){
                rand = getRandomPoint();
                parent = nodes.get(0);
                for (Node p : nodes){
                    if (p.position.distance(rand) <= parent.position.distance(rand)){
                        Point newPoint = getNextPoint(p.position, rand);
                        if (!intersectsObstacles(newPoint)){
                            parent = p;
                            foundNext = true;
                        }
                    }
                }
            }

            Point newNode = getNextPoint(parent.position, rand);
            Node add = new Node(newNode, parent);
            nodes.add(add);
            g.drawLine(parent.position.x, parent.position.y, newNode.x, newNode.y);

            if (pointInGoal(newNode, step)){
                Node goal = new Node(end, add);
                nodes.add(goal);

                g.setColor(new Color(0,255,255));
                Node currNode = goal;

                while (currNode.parent != null){
                    g.drawLine(currNode.position.x, currNode.position.y, currNode.parent.position.x, currNode.parent.position.y);
                    currNode = currNode.parent;
                }


                return true;
            }

            count++;

        }

        return false;

    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}

class Node{
    Node parent;
    Point position;

    public Node(Point pos, Node par){
        parent = par;
        position = pos;
    }

}