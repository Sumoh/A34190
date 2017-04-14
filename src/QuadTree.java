import Helpers.Point;
import Helpers.Rectangle;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Tristan on 2017-04-13.
 */
public class QuadTree {

    Point topLeftPoint;
    int width = 0;
    int height = 0;

    int value = 0;

    QuadTree topLeft, topRight, bottomLeft, bottomRight;


    public QuadTree(int tlx, int tly, int w, int h){
        topLeftPoint = new Point(tlx, tly);
        width = w;
        height = h;
    }

    public void draw(Graphics2D g){
        if (value != 1) {
            g.drawRect(topLeftPoint.x, topLeftPoint.y, width, height);
            if (topLeft != null) {
                topLeft.draw(g);
            }
            if (topRight != null) {
                topRight.draw(g);
            }
            if (bottomRight != null) {
                bottomRight.draw(g);
            }
            if (bottomLeft != null) {
                bottomLeft.draw(g);
            }
        }
    }


    public boolean insert(int x,int y){

        if (x < topLeftPoint.x || x >= (topLeftPoint.x + width) || y < topLeftPoint.y || y >= (topLeftPoint.y + height)){
            return false;
        }

        if (width == 1 && height == 1){
            value = 1;
            return true;
        }

        if (topLeft == null){
            subdivide();
        }

        if (topLeft.insert(x,y)){
            return true;
        }
        if (topRight.insert(x,y)){
            return true;
        }
        if (bottomLeft.insert(x,y)){
            return true;
        }
        if (bottomRight.insert(x,y)){
            return true;
        }


        return false;
    }

    public void subdivide(){
        if (height == 1){
            topLeft = new QuadTree(topLeftPoint.x, topLeftPoint.y,1,1);
            topRight = new QuadTree(topLeftPoint.x + 1, topLeftPoint.y,1,1);
        }else if(width == 1){
            topLeft = new QuadTree(topLeftPoint.x, topLeftPoint.y, 1,1);
            topRight = new QuadTree(topLeftPoint.x, topLeftPoint.y + 1,1,1);
        }else{
            int halfWidth = width/2;
            int otherHalfWidth = (int)Math.ceil((double)width/2.0);

            int halfHeight = height/2;
            int otherHalfHeight = (int)Math.ceil((double)height/2.0);

            topLeft = new QuadTree(topLeftPoint.x, topLeftPoint.y, halfWidth, halfHeight);
            topRight = new QuadTree(topLeftPoint.x + halfWidth, topLeftPoint.y, otherHalfWidth, halfHeight);

            bottomLeft = new QuadTree(topLeftPoint.x, topLeftPoint.y + halfHeight, halfWidth, otherHalfHeight);
            bottomRight = new QuadTree(topLeftPoint.x + halfWidth, topLeftPoint.y + halfHeight, otherHalfWidth, otherHalfHeight);
        }
    }

     public Point getPointLocation(int x, int y){
         if (x < topLeftPoint.x || x >= (topLeftPoint.x + width) || y < topLeftPoint.y || y >= (topLeftPoint.y + height)){
             return null;
         }

         if (topLeft == null){
             return topLeftPoint;
         }

         Point ret = null;
         ret = topLeft.getPointLocation(x,y);
         if (ret != null){
             return ret;
         }
         ret = topRight.getPointLocation(x,y);
         if (ret != null){
             return ret;
         }
         ret = bottomLeft.getPointLocation(x,y);
         if (ret != null){
             return ret;
         }
         ret = bottomRight.getPointLocation(x,y);
         if (ret != null){
             return ret;
         }

         return ret;
     }

    public Rectangle getPointLocationAndDimensions(int x, int y){

        if (x < topLeftPoint.x || x >= (topLeftPoint.x + width) || y < topLeftPoint.y || y >= (topLeftPoint.y + height)){
            return null;
        }

        if (topLeft == null){
            if (value == 1){
                return null;
            }
            return new Rectangle(topLeftPoint, width, height);
        }

        Rectangle ret = topLeft.getPointLocationAndDimensions(x,y);
        if (ret != null){
            return ret;
        }
        ret = topRight.getPointLocationAndDimensions(x,y);
        if (ret != null){
            return ret;
        }
        if (bottomLeft != null){
            ret = bottomLeft.getPointLocationAndDimensions(x,y);
            if (ret != null){
                return ret;
            }
            ret = bottomRight.getPointLocationAndDimensions(x,y);
            if (ret != null) {
                return ret;
            }
        }

        return ret;
    }

    public ArrayList<Rectangle> getNeighbors(int x, int y){

        ArrayList<Rectangle> neighbors = new ArrayList<>();

        Rectangle currentData = getPointLocationAndDimensions(x, y);

        int checkX = currentData.point.x;
        int checkY = currentData.point.y - 1;

        Rectangle neighbor = getPointLocationAndDimensions(checkX, checkY);
        while (neighbor != null && checkX < currentData.point.x + currentData.w){
            checkX += neighbor.w;
            neighbors.add(neighbor);
            neighbor = getPointLocationAndDimensions(checkX, checkY);
        }

        checkX = currentData.point.x;
        checkY = currentData.point.y + currentData.h;
        neighbor = getPointLocationAndDimensions(checkX, checkY);
        while (neighbor != null && checkX < currentData.point.x + currentData.w){
            checkX += neighbor.w;
            neighbors.add(neighbor);
            neighbor = getPointLocationAndDimensions(checkX, checkY);
        }

        checkX = currentData.point.x - 1;
        checkY = currentData.point.y;
        neighbor = getPointLocationAndDimensions(checkX, checkY);
        while (neighbor != null && checkY < currentData.point.y + currentData.h){
            checkY += neighbor.h;
            neighbors.add(neighbor);
            neighbor = getPointLocationAndDimensions(checkX, checkY);
        }


        checkX = currentData.point.x + currentData.w;
        checkY = currentData.point.y;
        neighbor = getPointLocationAndDimensions(checkX, checkY);
        while (neighbor != null && checkY < currentData.point.y + currentData.h){
            checkY += neighbor.h;
            neighbors.add(neighbor);
            neighbor = getPointLocationAndDimensions(checkX, checkY);
        }

        return neighbors;

    }


}
