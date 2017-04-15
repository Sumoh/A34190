import java.awt.*;

/**
 * Created by Tristan on 2017-04-14.
 */
public class Rectangle {

    public Point point;
    public int w, h;

    public Rectangle(Point p, int w, int h){
        point = p;
        this.w = w;
        this.h = h;
    }

    public Rectangle(int x, int y, int w, int h){
        point = new Point(x,y);
        this.w = w;
        this.h = h;
    }

    public void draw(Graphics2D g){
        g.fillRect(point.x, point.y, w, h);
    }

    public boolean pointIntersects(Point p){
        if (p.x >= point.x && p.x <= point.x + w && p.y >= point.y && p.y <= point.y + h){
            return true;
        }

        return false;
    }

}
