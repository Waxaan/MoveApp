package example.dkamphake.moveapp;

import android.graphics.PointF;
import java.util.LinkedList;
import java.util.List;
import static java.lang.Math.sqrt;

public class Game {

    public static int getScoreV2(float x_current, float y_current, float x_delta, float y_delta, state gamemode) {

        int score;
        List<PointF> skeleton;
        switch (gamemode) {
            case RECTANGLE:
                skeleton = ShapeGenerator.getStandardRectangleList();
                break;
            case CIRCLE:
                skeleton = ShapeGenerator.getStandardCircleList();
                break;
            case WSHAPE:
                skeleton = ShapeGenerator.getShapeWList();
                break;
            case NOTHING:
            default: return 0;
        }
        //the further away from the middle of the road the less points are awarded;
        //if its over 10units away get more negative points for increasing distance
        double distance = getMinDistance(x_current, y_current, skeleton);
        score = (int) (speedbonus(x_delta, y_delta) * (20-distance));
        return score;
    }

    //calculates the lowest distance between the courser and the current shape
    public static double getMinDistance(float x, float y, List<PointF> skeleton) {

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

    //awards additional bonus- or malus-points based on the speed the players is playing the game
    private static double speedbonus(float x_delta, float y_delta) {
        double speed = Math.abs(x_delta+y_delta);
        return Math.max(Math.min(speed, 0.5),4);
    }

    //calculates the new position for the courser based on the gyro-data
    public static PointF getNewPosition(PointF point, float delta_x, float delta_y, boolean inverted) {
        float new_x = point.x;
        float new_y = point.y;
        if(inverted) {
            new_x += delta_x *10;
            new_y += delta_y *10;
        } else {
            new_x -= delta_x *10;
            new_y -= delta_y *10;
        }
        //fits the coordinates to the canvas (values between 0 and 380)
        new_x =  (new_x < 0)? 0 : (new_x > 379)? 379 : new_x;
        new_y =  (new_y < 0)? 0 : (new_y > 379)? 379 : new_y;

        return new PointF(new_x, new_y);
    }

    //returns the starting positions for each game-mode so that the Point-List isn't empty
    public static LinkedList<PointF> getStartPosition(state state) {
        LinkedList<PointF> positions = new LinkedList<>();
        switch (state) {
            case RECTANGLE:
                positions.add(new PointF(190, 40));
                positions.add(new PointF(190, 40));
                break;
            case CIRCLE:
                positions.add(new PointF(190, 40));
                positions.add(new PointF(190, 40));
                break;
            case WSHAPE:
                positions.add(new PointF(35, 35));
                positions.add(new PointF(35, 35));
                break;
            default:
                positions.add(new PointF(190, 190));
                positions.add(new PointF(190, 190));
                break;
        }
        return positions;
    }
}
