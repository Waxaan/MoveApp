package example.dkamphake.moveapp;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;

public class ShapeGenerator {

    public static List<PointF> getStandardRectangleList() {
        return getRectangleList(40, 40, 340, 340);
    }

    public static List<PointF> getRectangleList(float left, float top, float right, float bottom) {
        List<PointF> rect = new ArrayList<>();

        final int LineResolution = 60; // amount of points in a line

        float horizontalStep = (right - left)/LineResolution;
        float verticalStep = (bottom - top)/LineResolution;


        /*  Adds the points     1<--4
         *  of a Rectangle      |   |
         *  like this:          2-->3
         */

        //1->2
        for (int i = 0; i < LineResolution; i++) {
            PointF p = new PointF(left, top + i * verticalStep); //20 is half the paint thiccness
            rect.add(p);
        }
        //2->3
        for (int i = 0; i < LineResolution; i++) {
            PointF p = new PointF(left + i * horizontalStep, bottom);
            rect.add(p);
        }
        //3->4
        for (int i = 0; i < LineResolution; i++) {
            PointF p = new PointF(right, bottom - i * verticalStep);
            rect.add(p);
        }
        //4->1
        for (int i = 0; i < LineResolution; i++) { //connect the last point to the begin
            PointF p = new PointF(right - i * horizontalStep, top);
            rect.add(p);
        }
        return rect;
    }

    public static List<PointF> getStandardCircleList() {
        return getCircleList(190, 190, 150);
    }

    public static List<PointF> getCircleList(int x, int y, int radius) {
        List<PointF> circle = new ArrayList<>();
        int resolution = 180;
        for(int i = 0; i < resolution; i++) {
            int new_x = x;
            int new_y = y;
            double pi_val = i*(2*PI/resolution);
            new_x += Math.sin(pi_val)*radius;
            new_y += Math.cos(pi_val)*radius;
            PointF point = new PointF(new_x, new_y);
            circle.add(point);
        }
        return circle;
    }

    public static List<PointF> getShapeWList() {
        List<PointF> list = new ArrayList<>();
        float resolution = 100;
        //sets the starting position to the top left corner
        float x = 35; //(390-320)/2
        float y = 35;
        final float unit = 100/resolution;
        for (int i = 0; i < resolution; i++) {
            x += unit;
            y += 3*unit;
            PointF point = new PointF(x, y);
            list.add(point);
        }
        for (int i = 0; i < resolution/2; i++) {
            x += unit;
            y -= 3*unit;
            PointF point = new PointF(x, y);
            list.add(point);
        }
        for (int i = 0; i < resolution/2; i++) {
            x += unit;
            y += 3*unit;
            PointF point = new PointF(x, y);
            list.add(point);
        }
        for (int i = 0; i < resolution; i++) {
            x += unit;
            y -= 3*unit;
            PointF point = new PointF(x, y);
            list.add(point);
        }
        return list;
    }
}
