import java.awt.*;
import java.util.ArrayList;

public class Utils {
    public static ArrayList<Point> calculatePointRoute(Point current, Point target, int distance){
        double yDiff = target.getY() - current.getY();
        double xDiff = target.getX() - current.getX();
        boolean xGrow = xDiff > 0;
        double slope = yDiff / xDiff;
        double add = ((slope * current.getX()) - current.getY());
        ArrayList<Point> route = new ArrayList<>();
        if (!xGrow){
            distance *= -1;
        }
        for (int i = (int) current.getX();(xGrow ? i < target.getX() : i > target.getX()); i += distance){
            route.add(new Point(i, (int) ((slope * i) + add)));
        }
        return route;
    }
    //TODO: change speed- distance impacts
    public static void sleep(int mili){
        try {
            Thread.sleep(mili);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean playersProximity(Player first, Player other){
        return first.getRectangle().getLocation().distance(other.getRectangle().getLocation()) < 25;
    }
    public static boolean playerBallCollision(Player player, Point ball){
        return player.getRectangle().intersects(new Rectangle(ball.x, ball.y, Constants.BALL_SIZE, Constants.BALL_SIZE));
    }
}
