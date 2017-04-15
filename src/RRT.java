import Helpers.*;
import Helpers.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;
import Helpers.Rectangle;

/**
 * Created by Tristan on 2017-04-14.
 */
public class RRT {

    Point start;
    Point end;
    QuadTree tree;
    BitSet filledIn;
    ArrayList<Rectangle> obstacles;
    int size;
    int count = 0;
    int MAXNODES = 5000;


    double delta = 10.0;

    public RRT(Point start, Point end, ArrayList<Rectangle> obstacles, BitSet filled, int size){

        this.obstacles = obstacles;
        this.start = start;
        this.end = end;
        this.filledIn = filled;
        this.size = size;

    }

    public Point setp_from_to(Point p1, Point p2){
        if (p1.distance(p2) < delta){
            return p2;
        }

        double theta = Math.atan2(p2.y-p1.y, p2.x - p1.x);
        return new Point((int)(p1.x + delta * Math.cos(theta)), (int)(p1.y + delta*Math.sin(theta)) );
    }

    public boolean collides(Point p){
        for (Rectangle obs : obstacles){
            if (obs.pointIntersects(p)){
                return true;
            }
        }

        return false;
    }

    public Point get_random_clear(){
        int tries = 0;
        Point p;
        while (tries <= 5000){
            p = new Point(randInt(0,size), randInt(0, size));
            if (!collides(p)){
                return p;
            }
            tries++;
        }

        return null;
    }

   /* def point_circle_collision(p1, p2, radius):
    distance = dist(p1,p2)
    if (distance <= radius):
            return True
    return False*/


    public boolean findPath(Graphics2D g){

        Node startNode = new Node(start, null);
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(startNode);

        while (count <= MAXNODES){

            boolean foundNext = false;
            Node parent = nodes.get(0);
            Point rand = get_random_clear();
            while (!foundNext){
                rand = get_random_clear();
                parent = nodes.get(0);
                for (Node p : nodes){
                    if (p.position.distance(rand) <= parent.position.distance(rand)){
                        Point newPoint = setp_from_to(p.position, rand);
                        if (!collides(newPoint)){
                            parent = p;
                            foundNext = true;
                        }
                    }
                }
            }

            if (parent != null && rand != null) {
                Point newNode = setp_from_to(parent.position, rand);
                nodes.add(new Node(newNode, parent));
                g.drawLine(parent.position.x, parent.position.y, newNode.x, newNode.y);
            }

            count++;

        }

        System.out.println("No More nodes...");
        return false;

    }

/*

    newnode = step_from_to(parentNode.point,rand)
    nodes.append(Node(newnode, parentNode))
            pygame.draw.line(screen,cyan,parentNode.point,newnode)

            if point_circle_collision(newnode, goalPoint.point, GOAL_RADIUS):
    currentState = 'goalFound'

    goalNode = nodes[len(nodes)-1]


            else:
    print("Ran out of nodes... :(")
    return;

    #handle events
    for e in pygame.event.get():
            if e.type == QUIT or (e.type == KEYUP and e.key == K_ESCAPE):
            sys.exit("Exiting")
            if e.type == MOUSEBUTTONDOWN:
    print('mouse down')
    if currentState == 'init':
            if initPoseSet == False:
    nodes = []
            if collides(e.pos) == False:
    print('initiale point set: '+str(e.pos))

    initialPoint = Node(e.pos, None)
    nodes.append(initialPoint) # Start in the center
    initPoseSet = True
    pygame.draw.circle(screen, red, initialPoint.point, GOAL_RADIUS)
    elif goalPoseSet == False:
    print('goal point set: '+str(e.pos))
            if collides(e.pos) == False:
    goalPoint = Node(e.pos,None)
    goalPoseSet = True
    pygame.draw.circle(screen, green, goalPoint.point, GOAL_RADIUS)
    currentState = 'buildTree'
            else:
    currentState = 'init'
    initPoseSet = False
            goalPoseSet = False
    reset()

    pygame.display.update()
            fpsClock.tick(10000)*/


    /*

    public boolean findPath(Graphics2D g){

        g.setColor(new Color(255,255,255));

        Point prev = start;
        Point next = start;
        for (int i = 0; i < 100; i++){
            next = findNearPoint(next);
            g.drawLine(prev.x, prev.y, next.x, next.y);
            prev = next;
        }

        return false;

    }
*/
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
/*
    public Point findNearPoint(Point near){

        int x = near.x + randInt(-5, 5);
        int y = near.y + randInt(-5, 5);

        if (x < 0){
            x = 0;
        }
        if (y < 0){
            y = 0;
        }
        if (y >= size){
            y = size;
        }
        if (x >= size){
            x = size;
        }

        int count = 0;

        Point p2 = new Point(x,y);
        while (filledIn.get(x + (y  * size)) && count <= 1000){
             p2 = new Point(x,y);
            x = near.y + randInt(-20, 20);
            y = near.y + randInt(-20, 20);
            count++;
        }

        if (count >= 1000){
            return null;
        }

        return p2;

    }*/

}

class Node{
    ArrayList<Node> children;
    Node parent;
    Point position;

    public Node(Point pos, Node par){
        parent = par;
        position = pos;
        children = new ArrayList<>();
    }

    public void addChild(Node add){
        children.add(add);
    }
}