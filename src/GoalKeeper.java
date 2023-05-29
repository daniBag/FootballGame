import java.awt.*;
import java.util.Random;

public class GoalKeeper extends Player{
    private int iQ;
    public GoalKeeper(int name, int xStart, int yStart, Team team, int iq) {
        super(name, xStart, yStart, team);
        this.iQ = iq;
    }

    public void run() {
        while (!this.getTeam().gameRunning()){
            Utils.sleep(1);
        }
        while (true){
            if (this.hasTheBall()){
                kick(generateShotTarget());
            }else{
                if (this.getTeam().isThreat()){
                    tryForSave();
                }else {
                    move(true);
                }
            }
        }
    }
    protected void move(boolean tackle){
        int y = this.getLastBallLocation().y;
        if (y > Constants.GOAL_LOWER_POST){
            y = Constants.GOAL_LOWER_POST + Constants.POINTS_MARGIN;
        } else if (y < Constants.GOAL_UPPER_POST) {
            y = Constants.GOAL_UPPER_POST + Constants.POINTS_MARGIN;
        }
        this.setRunTarget(new Point(this.getLocation().x, y));
        super.move(tackle);
    }

    protected void pass(Point target) {

    }
    private Point generateShotTarget(){
        Random random = new Random();
        return new Point(random.nextInt(Constants.PITCH_WIDTH / 5, Constants.PITCH_WIDTH / 2), random.nextInt(Constants.PITCH_HEIGHT));
    }
    protected void kick(Point goalKick) {
       this.getTeam().ballDelivered(goalKick, Constants.BALL_DELIVERY);
        this.noBall();
    }


    private void tryForSave(){
        Random random = new Random();
        int speed = Constants.BASIC_SPEED;
        switch (this.iQ){
            case Constants.EASY -> speed = random.nextInt(Constants.EASY_MIN, Constants.EASY_MAX);
            case Constants.MID -> speed = random.nextInt(Constants.MID_MIN, Constants.MID_MAX);
            case Constants.HARD -> speed = random.nextInt(Constants.HARD_MIN, Constants.HARD_MAX);
        }
        this.changeSpeed(speed);
        this.getBall();
    }
}
