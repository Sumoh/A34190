import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;
import Helpers.Point;

import Helpers.Rectangle;

/**
 * Created by Tristan on 2017-04-13.
 */
public class GUI extends JFrame implements KeyListener{

    Canvas canvas;
    int numObjects = 50;
    int planeSize = 500;

    public GUI(int numObjects, int planeSize){
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 600));
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
        }
    }

    public void remakeCanvas(){

        canvas.numObstacles = numObjects;
        canvas.planeSize = planeSize;
        canvas.setup();
        canvas.repaint();

    }

}


class Canvas extends JComponent {

    int numObstacles = 50;
    int planeSize = 500;
    int minObstacleSize = 10;
    int maxObstacleSize = 50;

    long preTime = 0;
    long postTime = 0;

    boolean drawTree = true;

    Point start;
    Point end;

    ArrayList<Rectangle> obstacles;
    BitSet filledin;
    QuadTree tree;

    public Canvas(int numObstacles, int planeSize){
        this.numObstacles = numObstacles;
        this.planeSize = planeSize;

        obstacles = new ArrayList<>();

        setup();
    }

    public void setup(){

        filledin = new BitSet(planeSize * planeSize);
        obstacles.clear();
        filledin.clear();

        for (int i = 0; i < numObstacles; i++){
            int iX = randInt(0, planeSize-1);
            int iY = randInt(0, planeSize-1);
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

            if (finalX + obsWidth >= planeSize){
                obsWidth = planeSize - finalX;
            }

            if (finalY + obsHeight >= planeSize){
                obsHeight = planeSize - finalY;
            }

            obstacles.add(new Rectangle(finalX,finalY,obsWidth,obsHeight));

        }

        tree = new QuadTree(0,0,planeSize, planeSize);

        for (int i = 0; i < obstacles.size(); i++){
            Rectangle rect = obstacles.get(i);
            int x = rect.point.x;
            int y = rect.point.y;

            int width = rect.w;
            int height = rect.h;

            for (int z = 0; z < width; z++){
                for (int w = 0; w < height; w++){
                    if (filledin.get((x+z)+((y+w)*planeSize)) == false) {
                        filledin.set((x + z) + ((y + w) * planeSize));
                        tree.insert(x+z, y+w);
                    }
                }
            }

        }

        setRandomPoints();
    }

    public void setRandomPoints(){
        Point p = new Point(randInt(0, planeSize-1), randInt(0, planeSize-1));
        while (filledin.get(p.x + (p.y * planeSize))){
            p = new Point(randInt(0, planeSize-1), randInt(0, planeSize-1));
        }

        Point p2 = new Point(randInt(0, planeSize-1), randInt(0, planeSize-1));
        while (filledin.get(p2.x + (p2.y  * planeSize))){
            p2 = new Point(randInt(0, planeSize-1), randInt(0, planeSize-1));
        }

        start = p;
        end = p2;

        if (tree.getPointLocation(p.x, p.y) == tree.getPointLocation(p2.x, p2.y)){
            setRandomPoints();
        }

        repaint();

    }

    public void solvePath(Graphics2D g){
        AStar astar = new AStar(start, end, tree, g);
        if (astar.findPath()){
            g.setColor(new Color(0,0,0));
            g.drawString("Path Found!", 375, planeSize+15);
        }else{
            g.setColor(new Color(0,0,0));
            g.drawString("Couldn't find path!", 375, planeSize+15);
        }
    }

    public void toggleTreeDraw(){
        drawTree = !drawTree;
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

        if (drawTree) {
            g.setColor(new Color(18, 7, 255));
            tree.draw(p);
        }

        preTime = System.nanoTime();
        solvePath(p);
        postTime = System.nanoTime();

        g.setColor(new Color(0,0,0));
        p.drawString("Execution Time: " + ((double)(postTime-preTime)/1000000000.0), 20,planeSize+15);
        p.drawString("Num Objects: " + numObstacles, 225, planeSize+15);
        p.drawString("R = New Map     Space = New Path    Up Arrow = Larger Map    Q = Toggle drawing quadtree", 20, planeSize+50);
//        p.drawString("Space = New Path", 150, 540);
        p.drawString("+ = More Objects    - = Less Objects      Down Arrow = Smaller Map", 20, planeSize+75);
//        p.drawString("- = Less Objects", 150, 575);


        revalidate();
    }
}
