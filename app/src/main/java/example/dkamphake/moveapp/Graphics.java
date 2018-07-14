package example.dkamphake.moveapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

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

        /*
            draws the shape onto the map
            add different modi here
         */
        switch (gamemode) {
            case RECTANGLE:
                canvas.drawRect(40,40, 340, 340, purple);
                break;
            case CIRCLE:
                canvas.drawCircle(190, 190,150, purple);
                break;
            case WSHAPE:
                List<PointF> lW = ShapeGenerator.getShapeWList();
                map = drawPointsToCanvas(lW, map);
                break;
            case NOTHING:
            default:
                canvas.drawRect(20,20, 360, 360, green);
                break;
        }

        return map;
    }

    //may be used to draw more complex vector based maps
    public static Bitmap drawPointsToCanvas(List<PointF> positions, Bitmap map) {

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

        //canvas.drawRect(40,40, 340, 340, purple);
        for (int i = 1; i < positions.size(); i++) {
            //canvas.drawLine(positions.get(i - 1).x, positions.get(i - 1).y, positions.get(i).x, positions.get(i).y, purple);
        }
        //canvas.drawLine(positions.get(positions.size()-1).x, positions.get(positions.size()-1).y, positions.get(0).x, positions.get(0).y, purple);
        for (int i = 0; i < positions.size(); i++) {
            canvas.drawCircle(positions.get(i).x, positions.get(i).y, 10, purple); //visualize Point-skeleton
        }

        return map;
    }

    public static Bitmap drawCourserToCanvas(List<PointF> pos, state gamemode) {
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
            if(i < 50) {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, blue);
            } else {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, green);
            }
        }
        return map;
    }

    public static Bitmap drawCourserToCanvas(List<PointF> pos, Bitmap prevMap) {

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

        Canvas canvas = new Canvas(prevMap);
        canvas.drawBitmap(prevMap, 0, 0, null);

        //connect old_position to new_position
        for(int i = 1; i < pos.size(); i++) {
            if(i < 50) {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, blue);
            } else {
                canvas.drawLine(pos.get(i - 1).x, pos.get(i - 1).y, pos.get(i).x, pos.get(i).y, green);
            }
        }
        return prevMap;
    }

}
