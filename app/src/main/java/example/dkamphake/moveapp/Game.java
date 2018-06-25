package example.dkamphake.moveapp;

import android.graphics.Point;
import android.graphics.drawable.shapes.Shape;

import java.util.List;

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class Game {

    public static int getScoreV2(float x_current, float y_current, float x_delta, float y_delta, state gamemode) {

        int score;
        List<Point> skeleton;
        switch (gamemode) {
            case RECTANGLE:
                skeleton = ShapeGenerator.getStandardRectangleList();
                break;
            case CIRCLE:
                skeleton = ShapeGenerator.getStandardCircleList();
                break;
            case NOTHING:
            default: return 0;
        }
        double distance = getMinDistance(x_current, y_current, skeleton);
        double scalingFactor = min(4,20/distance);
        if(distance > 10) {
            scalingFactor *= (-1);
        }
        score = (int) (speedbonus(x_delta, y_delta) * scalingFactor);
        return score;
    }

    public static double getMinDistance(float x, float y, List<Point> skeleton) {

        double minDistance = Integer.MAX_VALUE;
        int minNode; // might use it later for prediction but problems with more complex structures
        for (int i = 0; i < skeleton.size(); i++) {
            double curDistance = sqrt(Math.pow(Math.abs(skeleton.get(i).x - x), 2) + Math.pow(Math.abs(skeleton.get(i).y - y), 2));
            if (curDistance < minDistance) {
                minDistance = curDistance;
                minNode = i;
            }
        }
        return minDistance;
    }

    private static double speedbonus(float x_delta, float y_delta) {
        return Math.min(0.25 + 10* Math.sqrt(Math.abs(x_delta*y_delta)), 3);
    }


}
