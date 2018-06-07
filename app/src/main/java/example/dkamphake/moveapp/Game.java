package example.dkamphake.moveapp;

public class Game {

    //TODO calculate score based on the distance to the closest point in the point-skeleton
    public static int getScore(float x_current, float y_current, float x_delta, float y_delta, state gamemode) {
        switch (gamemode) {
            case RECTANGLE:
                return scoreRectangle(x_current, y_current, x_delta, y_delta);
            case CIRCLE:
                return scoreCircle(x_current, y_current, x_delta, y_delta);
            case NOTHING:
            default:
                return 0;
        }
    }

    private static int scoreRectangle(float x_current, float y_current, float x_delta, float y_delta) {
        if(y_current > 20 && y_current < 60 || y_current > 320 && y_current < 360) {
            return (int) (2*speedbonus(x_delta, y_delta));
        } else if (x_current > 20 && x_current < 60 || x_current > 320 && x_current < 360) {
            return (int) (2*speedbonus(x_delta, y_delta));
        } else {
            return -5;
        }
    }

    //TODO
    private static int scoreCircle(float x_current, float y_current, float x_delta, float y_delta) {
        if(y_current > 20 && y_current < 60 || y_current > 320 && y_current < 360) {
            return (int) (2*speedbonus(x_delta, y_delta));
        } else if (x_current > 20 && x_current < 60 || x_current > 320 && x_current < 360) {
            return (int) (2*speedbonus(x_delta, y_delta));
        } else {
            return -5;
        }
    }

    private static double speedbonus(float x_delta, float y_delta) {
        return (0.25 + 100* Math.abs(x_delta*y_delta));
    }


}
