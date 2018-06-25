package example.dkamphake.moveapp;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;

public class ShapeGenerator {

    public static List<Point> getStandardRectangleList() {
        return getRectangleList(40, 40, 340, 340);
    }

    public static List<Point> getRectangleList(float left, float top, float right, float bottom) {
        List<Point> rect = new ArrayList<Point>();

        final int LineResolution = 60; // amount of points in a line

        float horizontalStep = (right - left)/LineResolution;
        float verticalStep = (bottom - top)/LineResolution;


        /*  Adds the points     1<--4
         *  of a Rectangle      |   |
         *  like this:          2-->3
         */

        //1->2
        for (int i = 0; i < LineResolution; i++) {
            Point p = new Point((int) (left), (int) (top + i * verticalStep)); //20 is half the paint thiccness
            rect.add(p);
        }
        //2->3
        for (int i = 0; i < LineResolution; i++) {
            Point p = new Point((int) (left + i * horizontalStep), (int) (bottom));
            rect.add(p);
        }
        //3->4
        for (int i = 0; i < LineResolution; i++) {
            Point p = new Point((int) (right), (int) (bottom - i * verticalStep));
            rect.add(p);
        }
        //4->1
        for (int i = 0; i < LineResolution; i++) { //connect the last point to the begin
            Point p = new Point((int) (right - i * horizontalStep), (int) (top));
            rect.add(p);
        }
        return rect;
    }

    public static List<Point> getStandardCircleList() {
        return getCircleList(190, 190, 150);
    }

    public static List<Point> getCircleList(int x, int y, int radius) {
        List<Point> circle = new ArrayList<Point>();
        int resolution = 180;
        for(int i = 0; i < resolution; i++) {
            int new_x = x;
            int new_y = y;
            double pi_val = i*(2*PI/resolution);
            new_x += Math.sin(pi_val)*radius;
            new_y += Math.cos(pi_val)*radius;
            Point point = new Point(new_x, new_y);
            circle.add(point);
        }
        return circle;
    }

    public static List<Point> getShapeWList() {
        List<Point> list = new ArrayList<Point>();
        float resolution = 100;
        float x = 35; //(390-320)/2
        float y = 35;
        final float unit = 100/resolution;
        for (int i = 0; i < resolution; i++) {
            x += unit;
            y += 3*unit;
            Point point = new Point((int)x, (int)y);
            list.add(point);
        }
        for (int i = 0; i < resolution/2; i++) {
            x += unit;
            y -= 3*unit;
            Point point = new Point((int)x, (int)y);
            list.add(point);
        }
        for (int i = 0; i < resolution/2; i++) {
            x += unit;
            y += 3*unit;
            Point point = new Point((int)x, (int)y);
            list.add(point);
        }
        for (int i = 0; i < resolution; i++) {
            x += unit;
            y -= 3*unit;
            Point point = new Point((int)x, (int)y);
            list.add(point);
        }
        return list;
    }
}
