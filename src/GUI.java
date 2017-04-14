import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import Helpers.Point;

import Helpers.Rectangle;

/**
 * Created by Tristan on 2017-04-13.
 */
public class GUI extends JFrame implements KeyListener, MouseListener{

    Canvas canvas;


    public GUI(){
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 600));
        addKeyListener(this);
        addMouseListener(this);

        canvas = new Canvas();
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
            canvas.repaint();
        }else if(e.getKeyCode() == KeyEvent.VK_Q){
            canvas.toggleTreeDraw();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println(e.getX() + "," + e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}


class Canvas extends JComponent {

    int numObstacles = 50;
    int planeSize = 500;
    int minObstacleSize = 10;
    int maxObstacleSize = 50;

    boolean drawTree = true;

    Point start;
    Point end;

    ArrayList<Rectangle> obstacles;
    BitSet filledin;
    QuadTree tree;

    public Canvas(){
        obstacles = new ArrayList<>();
        filledin = new BitSet(planeSize * planeSize);
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
                finalX = planeSize - obsWidth;
            }

            if (finalY + obsHeight >= planeSize){
                finalY = planeSize - obsHeight;
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

    }

    public void solvePath(Graphics2D g){
        AStar astar = new AStar(start, end, tree, g);
        if (!astar.findPath()){
            System.out.println("No Path found");
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

        solvePath(p);

        revalidate();
    }
}
