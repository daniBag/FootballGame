import java.awt.*;
import java.util.Random;

public class ControlledPlayer extends FieldPlayer{
    private Movement movement;
    private Point shotTarget;
    private boolean lastModeAttack;
    public ControlledPlayer(int name, int xStart, int yStart, Team team) {
        super(name, xStart, yStart, team, true);
        if (team.attacking()){
            this.movement = new AttackMovement(this, team);
            this.lastModeAttack = true;
        }else {
            this.movement = new DefenceMovement(this, team);
            this.lastModeAttack = false;
        }
        this.addKeyListener(this.movement);
        this.setFocusable(true);
        this.requestFocus();
        this.shotTarget = new Point(Constants.PITCH_WIDTH, (Constants.GOAL_LOWER_POST - Constants.GOAL_UPPER_POST) / 2);
        System.out.println("done");
    }
    public void calibrateShotTargetDown(){
        this.shotTarget.move(this.shotTarget.x, this.shotTarget.y + Constants.BALL_SIZE);
    }
    public void calibrateShotTargetUp(){
        this.shotTarget.move(this.shotTarget.x, this.shotTarget.y - Constants.BALL_SIZE);
    }
    protected void actionsAttack() {
        if (!this.lastModeAttack){
            this.requestFocus();
            this.addKeyListener(new AttackMovement(this, this.getTeam()));
            this.lastModeAttack = true;
        }
    }
    protected void actionsDefence() {
        if (this.lastModeAttack){
            this.requestFocus();
            this.addKeyListener(new DefenceMovement(this, this.getTeam()));
            this.lastModeAttack = false;
        }
    }

    protected void move(boolean tackle) {
        super.move(tackle);
    }

    @Override
    protected void pass(Point passTarget) {
        super.pass(passTarget);
    }

    @Override
    protected void kick(Point shotTarget) {
        super.kick(shotTarget);
    }

    @Override
    protected void tackle() {
        super.tackle();
    }

    public void kickProcess() {
        kick(generateShotTarget());
    }

    private Point generateShotTarget() {
        Random random = new Random();
        Point result = this.shotTarget;
        int add = random.nextInt(Constants.SHOT_ACCURACY);
        int addOrDeduct = random.nextInt(2);
        switch (addOrDeduct){
            case 0 -> result.move(this.shotTarget.x, this.shotTarget.y - add);
            case 1 -> result.move(this.shotTarget.x, this.shotTarget.y + add);
        }
        return result;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }
}
