import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tristan on 2017-04-13.
 */
public class GUI extends JFrame implements KeyListener{

    Canvas canvas;
    int numObjects = 50;
    int planeSize = 500;

    public GUI(int numObjects, int planeSize){
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(700, 600));
        addKeyListener(this);

        this.numObjects = numObjects;
        this.planeSize = planeSize;

        canvas = new Canvas(this.numObjects, this.planeSize);
        add(canvas);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            canvas.setRandomPoints();
        }else if(e.getKeyCode() == KeyEvent.VK_Q){
            canvas.toggleTreeDraw();
        }else if(e.getKeyCode() == KeyEvent.VK_EQUALS){
            numObjects += 10;
            remakeCanvas();
        }else if(e.getKeyCode() == KeyEvent.VK_MINUS){
            numObjects -= 10;
            if (numObjects <= 10){
                numObjects = 10;
            }
            remakeCanvas();
        }else if(e.getKeyCode() == KeyEvent.VK_R){
            remakeCanvas();
        }else if(e.getKeyCode() == KeyEvent.VK_UP){
            planeSize += 100;
            remakeCanvas();
        }else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            planeSize -= 100;
            if (planeSize < 100){
                planeSize = 100;
            }
            remakeCanvas();
        }else if(e.getKeyCode() == KeyEvent.VK_A){
            canvas.toggleAStar();
        }
    }

    public void remakeCanvas(){

        canvas.numObstacles = numObjects;
        canvas.mapSize = planeSize;
        canvas.setup();
        canvas.repaint();

    }

}


class Canvas extends JComponent {

    int numObstacles = 50;
    int mapSize = 500;
    int minObstacleSize = 10;
    int maxObstacleSize = 50;

    boolean aStar = true;

    long preTime = 0;
    long postTime = 0;

    boolean drawTree = true;

    Point start;
    Point end;

    ArrayList<Rectangle> obstacles;
    BitSet filledin;
    QuadTree tree;

    public Canvas(int numObstacles, int mapSize){
        this.numObstacles = numObstacles;
        this.mapSize = mapSize;

        obstacles = new ArrayList<>();

        setup();
    }

    public void setup(){

        filledin = new BitSet(mapSize * mapSize);
        obstacles.clear();
        filledin.clear();

        for (int i = 0; i < numObstacles; i++){
            int iX = randInt(0, mapSize -1);
            int iY = randInt(0, mapSize -1);
            int obsWidth = randInt(minObstacleSize, maxObstacleSize);
            int obsHeight = randInt(minObstacleSize, maxObstacleSize);

            int finalX = iX - (obsWidth/2);
            int finalY = iY - (obsHeight/2);

            if (finalX < 0){
                finalX = 0;
            }

            if (finalY < 0){
                finalY = 0;
            }

            if (finalX + obsWidth >= mapSize){
                obsWidth = mapSize - finalX;
            }

            if (finalY + obsHeight >= mapSize){
                obsHeight = mapSize - finalY;
            }

            obstacles.add(new Rectangle(finalX,finalY,obsWidth,obsHeight));

        }

        tree = new QuadTree(0,0, mapSize, mapSize);

        for (int i = 0; i < obstacles.size(); i++){
            Rectangle rect = obstacles.get(i);
            int x = rect.point.x;
            int y = rect.point.y;

            int width = rect.w;
            int height = rect.h;

            for (int z = 0; z < width; z++){
                for (int w = 0; w < height; w++){
                    if (filledin.get((x+z)+((y+w)* mapSize)) == false) {
                        filledin.set((x + z) + ((y + w) * mapSize));
                        tree.insert(x+z, y+w);
                    }
                }
            }

        }

        setRandomPoints();
    }

    public void setRandomPoints(){
        Point p = new Point(randInt(0, mapSize -1), randInt(0, mapSize -1));
        while (filledin.get(p.x + (p.y * mapSize))){
            p = new Point(randInt(0, mapSize -1), randInt(0, mapSize -1));
        }

        Point p2 = new Point(randInt(0, mapSize -1), randInt(0, mapSize -1));
        while (filledin.get(p2.x + (p2.y  * mapSize))){
            p2 = new Point(randInt(0, mapSize -1), randInt(0, mapSize -1));
        }

        start = p;
        end = p2;

        if (tree.getTreePoint(p.x, p.y) == tree.getTreePoint(p2.x, p2.y)){
            setRandomPoints();
        }

        repaint();

    }

    public void solvePath(Graphics2D g){
        if (aStar) {
            AStar astar = new AStar(start, end, tree, g);
            if (astar.findPath()) {
                g.setColor(new Color(0, 0, 0));
                g.drawString("Path Found!", 375, mapSize + 15);
            } else {
                g.setColor(new Color(0, 0, 0));
                g.drawString("Couldn't find path!", 375, mapSize + 15);
            }
        }else{
            RRT rrt = new RRT(start,end,obstacles, mapSize);
            if (rrt.findPath(g)){
                g.setColor(new Color(0, 0, 0));
                g.drawString("Path Found!", 375, mapSize + 15);
            }else{
                g.setColor(new Color(0, 0, 0));
                g.drawString("Couldn't find path!", 375, mapSize + 15);
            }
        }
    }

    public void toggleTreeDraw(){
        drawTree = !drawTree;
        repaint();
    }

    public void toggleAStar(){
        aStar = !aStar;
        repaint();
    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D p = (Graphics2D) g;
        p.clearRect(0, 0, getWidth(), getHeight());

        p.setColor(new Color(163, 144, 41));
        p.fillRect(0, 0, getWidth(), getHeight());


        for (int i = 0; i < obstacles.size(); i++){
            g.setColor(new Color(0,0,0));
            obstacles.get(i).draw(p);
        }

        if (drawTree && aStar) {
            g.setColor(new Color(18, 7, 255));
            tree.draw(p);
        }


        int ovalWidth = 10;
        int ovalHeight = 10;

        if (!aStar){
            ovalHeight = 20;
            ovalWidth = 20;
        }

        if (aStar) {
            Rectangle centerStart = tree.getTreeNode(start.x, start.y);
            Rectangle centerEnd = tree.getTreeNode(end.x, end.y);

            int ax = (centerStart.point.x + (centerStart.w/2)) - (ovalWidth/2);
            int ay = (centerStart.point.y + (centerStart.h/2)) - (ovalHeight/2);
            int bx = (centerEnd.point.x + (centerEnd.w/2)) - (ovalWidth/2);
            int by = (centerEnd.point.y + (centerEnd.h/2)) - (ovalHeight/2);

            g.setColor(new Color(0, 255, 0));
            g.fillOval(ax, ay, ovalWidth, ovalHeight);
            g.setColor(new Color(255, 0, 0));
            g.fillOval(bx, by, ovalWidth, ovalHeight);
        }else{
            g.setColor(new Color(0, 255, 0));
            g.fillOval(start.x, start.y, ovalWidth, ovalHeight);
            g.setColor(new Color(255, 0, 0));
            g.fillOval(end.x, end.y, ovalWidth, ovalHeight);
        }

        preTime = System.nanoTime();
        solvePath(p);
        postTime = System.nanoTime();

        g.setColor(new Color(0,0,0));
        p.drawString("Execution Time: " + ((double)(postTime-preTime)/1000000000.0), 20, mapSize +15);
        p.drawString("Num Objects: " + numObstacles, 225, mapSize +15);
        p.drawString("R = New Map     Space = New Path    Up Arrow = Larger Map    Q = Toggle drawing quadtree", 20, mapSize +50);
//        p.drawString("Space = New Path", 150, 540);
        p.drawString("A = Toggle ASTAR/RRT    + = More Objects    - = Less Objects      Down Arrow = Smaller Map", 20, mapSize +75);
//        p.drawString("- = Less Objects", 150, 575);


        revalidate();
    }
}
