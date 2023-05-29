import javax.swing.*;
import java.awt.*;

public class Constants {
    public static final int BALL_SIZE = 25;
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 100;
    public static final int TEAM_SIZE = 3;
    public static final int PITCH_WIDTH = 1600;
    public static final int CENTER_LINE = 800;
    public static final int GOAL_UPPER_POST = 300;
    public static final int GOAL_LOWER_POST = 500;
    public static final int BASIC_SPEED = 200;
    public static final int POINTS_MARGIN = 5;
    public static final int RUN_SPEED = 150;
    public static final int MOVE_SLEEP = 200;
    //TODO: ball images list; players images list;
    public static final ImageIcon[] BALL_STATES = {null, null, null, null, null};
    public static final int BALL_DELIVERY = 20;
    public static final int BALL_PASS_SPEED = 115;
    public static final int BALL_SHOT_SPEED = 50;
    public static final int ACCURACY_MIN = -10;
    public static final int ACCURACY_MAX = 10;
    public static final int SHOT_ACCURACY = 50;
    public static final int ROUTE_CHECK = 1;
    public static final int INVALID = -1;
    public static final int PITCH_HEIGHT = 900;
    public static final int EASY = 1;
    public static final int EASY_MIN = 275;
    public static final int EASY_MAX = 350;
    public static final int MID = 2;
    public static final int MID_MIN = 200;
    public static final int MID_MAX = 275;
    public static final int HARD_MAX = 200;
    public static final int HARD_MIN = 150;
    public static final int HARD = 3;
    public static final int FIRST_PLAYER = 0;
    public static final Point BALL_START_LOCATION = new Point(800, 450);
}
