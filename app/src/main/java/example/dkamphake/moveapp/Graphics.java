package example.dkamphake.moveapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Graphics {

    public static Bitmap getMap(state gamemode) {
        //initialize bitmap and colors
        Bitmap map = Bitmap.createBitmap(380, 380, Bitmap.Config.ARGB_8888);

        Paint gray = new Paint();
        gray.setColor(Color.parseColor("#929292"));
        gray.setStyle(Paint.Style.FILL);

        Paint purple = new Paint();
        purple.setColor(Color.parseColor("#800080"));
        purple.setStyle(Paint.Style.STROKE);
        purple.setStrokeWidth(40);
        purple.setAntiAlias(true);

        Paint green = new Paint();
        green.setColor(Color.parseColor("#a6e6a6"));
        green.setStyle(Paint.Style.STROKE);
        green.setStrokeWidth(40);
        green.setAntiAlias(true);

        //put the bitmap onto the canvas
        Canvas canvas = new Canvas(map);
        canvas.drawBitmap(map, 0, 0, null);
        canvas.drawRect(0,0,380, 380, gray);

        //add different modi here
        switch (gamemode) {
            case RECTANGLE:
                canvas.drawRect(40,40, 340, 340, purple);
                //List<Point> rectList = getRectangleList(40, 40, 340, 340);
                //map = drawPointsToCanvas(rectList, map);
                break;
            case CIRCLE:
                canvas.drawCircle(190, 190,150, purple);
                break;
            case NOTHING:
            default:
                canvas.drawRect(20,20, 360, 360, green);
                break;
        }

        return map;
    }

    //may be used to draw more complex vector based maps
    public static Bitmap drawPointsToCanvas(List<Point> pos, Bitmap map) {

        Paint purple = new Paint();
        purple.setColor(Color.parseColor("#800080"));
        purple.setStyle(Paint.Style.STROKE);
        purple.setStrokeWidth(40);
        purple.setAntiAlias(true);

        Paint green = new Paint();
        green.setColor(Color.parseColor("#a6e6a6"));
        green.setStyle(Paint.Style.STROKE);
        green.setStrokeWidth(2);
        green.setAntiAlias(true);

        Canvas canvas = new Canvas(map);
        canvas.drawBitmap(map, 0, 0, null);

        canvas.drawRect(40,40, 340, 340, purple);
        for (int i = 1; i < pos.size(); i++) {
            canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, purple);
        }
        canvas.drawLine(pos.get(pos.size()).x, pos.get(pos.size()).y, pos.get(0).x, pos.get(0).y, purple);
        for (int i = 0; i < pos.size(); i++) {
            canvas.drawCircle(pos.get(i).x, pos.get(i).y, 2, green); //visualize Point-skeleton
        }

        return map;
    }

    public static Bitmap drawCourserToCanvas(List<Point> pos, state gamemode) {
        Bitmap map = getMap(gamemode);

        Paint green = new Paint();
        green.setColor(Color.GREEN);
        green.setAntiAlias(true);

        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setAntiAlias(true);

        Canvas canvas = new Canvas(map);
        canvas.drawBitmap(map, 0, 0, null);

        //connect old_position to new_position
        for(int i = 1; i < pos.size(); i++) {
            if(i < pos.size()-60) {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, green);
            } else {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, blue);
            }
        }
        return map;
    }

    public static Bitmap drawCourserToCanvas(List<Point> pos, Bitmap prepMap) {

        Paint green = new Paint();
        green.setColor(Color.GREEN);
        green.setAntiAlias(true);
        green.setFilterBitmap(true);
        green.setDither(true);

        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setAntiAlias(true);
        blue.setFilterBitmap(true);
        blue.setDither(true);

        Canvas canvas = new Canvas(prepMap);
        canvas.drawBitmap(prepMap, 0, 0, null);

        //connect old_position to new_position
        for(int i = 1; i < pos.size(); i++) {
            if(i < pos.size()-60) {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, green);
            } else {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, blue);
            }
        }
        return prepMap;
    }

    private static List<Point> getRectangleList(float left, float top, float right, float bottom) {
        List<Point> rect = new ArrayList<Point>();

        final int LineResolution = 40; // amount of points in a line

        float horizontalStep = (right - left)/LineResolution;
        float verticalStep = (bottom - top)/LineResolution;


        /*  Adds the points     1<--4
         *  of a Rectangle      |   |
         *  like this:          2---3
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
        for (int i = 0; i < LineResolution; i++) { //connect the last point to the beginn
            Point p = new Point((int) (right - i * horizontalStep), (int) (top));
            rect.add(p);
        }
        return rect;
    }
}
