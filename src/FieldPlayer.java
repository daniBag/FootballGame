import java.awt.*;
import java.util.List;
import java.util.Random;

public class FieldPlayer extends Player{
    private boolean leader;
    private boolean tactics;
    private boolean passedTo;
    public FieldPlayer(int name, int xStart, int yStart,
                       Team team, boolean leader) {
        super(name, xStart, yStart, team);
        this.leader = leader;
        this.passedTo = false;
    }
    public void setLeader(){
        this.leader = true;
    }
    public boolean isLeader(){
        return this.leader;
    }
    public void notLeader(){
        this.leader = false;
    }
    private boolean isInRange(){
        List<Point> check = Utils.calculatePointRoute(Game.PLAYER_GOAL.getLocation(), this.getLocation(), Constants.ROUTE_CHECK);
        return (this.getTeam().cleanRangeCheck(check));
    }
    @Override
    public void run() {
        while (true){
            Utils.sleep(200);
            if (attack()){
                if (this.hasTheBall()){
                    this.getTeam().changeLead();
                }
                actionsAttack();
            }else {
                actionsDefence();
            }
        }
    }
    protected void actionsAttack(){
        if (!this.leader){
            if (!this.tactics){
                updateMoveTarget();
            }else {
                this.tactics = false;
            }
            this.move(false);
        } else if (!this.passedTo){
            if (isInRange()) {
                Point shotTarget = generateShotTargetPoint(true);
                kick(shotTarget);
            }else{
                Point passTarget = this.getTeam().passTarget(this);
                if (passTarget != null){
                    pass(passTarget);
                }else{
                    updateMoveTarget();
                    this.move(false);
                }
            }
        }else {
            this.getBall();
            this.setPassedTo();
        }
    }

    private Point generateShotTargetPoint(boolean toPlayerSide) {
        Random random = new Random();
        int targetX = Game.PLAYER_GOAL.getX();
        if (!toPlayerSide){
            targetX = Game.CPU_GOAL.getX();
        }
        int targetY = random.nextInt(Constants.GOAL_UPPER_POST - Constants.SHOT_ACCURACY, Constants.GOAL_LOWER_POST + Constants.SHOT_ACCURACY);
        Point shotTarget = new Point(targetX, targetY);
        return shotTarget;
    }

    protected void actionsDefence(){
        if (this.getTeam().canTackle(this)){
            tackle();
        } else {
            updateMoveTarget();
            this.move(false);
        }
    }
    @Override
    protected void move(boolean tackle) {
        super.move(tackle);
    }
    private Point moveTargetAttack(Point goal){
        Random random = new Random();
        int tarX = this.getRectangle().x;
        int tarY = this.getRectangle().y;
        int xDis;
        int yDis;
        Point target;
        do {
            xDis = goal.x - this.getRectangle().x;
            yDis = goal.y - this.getRectangle().y;
            if (xDis > 0){
                tarX += random.nextInt(xDis);
            }else {
                tarX -= random.nextInt(xDis);
            }
            if (yDis > 0){
                tarY += random.nextInt(yDis);
            }else {
                tarY -= random.nextInt(yDis);
            }
            target = new Point(tarX, tarY);
        }while (!this.getTeam().cleanRangeCheck(Utils.calculatePointRoute(this.getLocation(), target, Constants.POINTS_MARGIN)));
        return target;
    }
    private Point moveTargetDefense(Point goal){
        int tarX = this.getTeam().getEnemyLeader().getLocation().x;
        int tarY = this.getTeam().getEnemyLeader().getLocation().y;
        int xDis = goal.x - this.getLocation().x;
        int yDis = goal.y - this.getLocation().y;
        tarX += xDis / 2;
        tarY += yDis / 2;
        return new Point(tarX, tarY);
    }
    protected void updateMoveTarget(){
        Point attackGoal;
        Point defenseGoal;
        if (this.getTeam().isPlayerTeam()){
            attackGoal = Game.CPU_GOAL.getLocation();
            defenseGoal = Game.PLAYER_GOAL.getLocation();
        }
        else {
            attackGoal = Game.PLAYER_GOAL.getLocation();
            defenseGoal = Game.CPU_GOAL.getLocation();
        }
        if (attack()){
            this.setRunTarget(moveTargetAttack(attackGoal));
            this.move(false);
        }else {
            this.setRunTarget(moveTargetDefense(defenseGoal));
            this.move(true);
        }
    }
    public void setPassedTo(){
        this.passedTo = !this.passedTo;
    }
    protected void pass(Point passTarget) {
        Random random = new Random();
        int accurate = random.nextInt(Constants.ACCURACY_MIN, Constants.ACCURACY_MAX);
        passTarget.move(passTarget.x + accurate, passTarget.y + accurate);
        this.getTeam().ballDelivered(passTarget, Constants.BALL_PASS_SPEED);
        this.noBall();
        this.getTeam().changeLead();
        this.notLeader();
    }

    protected void kick(Point shotTarget) {
        this.getTeam().ballDelivered(shotTarget, Constants.BALL_SHOT_SPEED);
        this.getTeam().threatOnGoal();
        this.noBall();
        this.notLeader();
        this.getTeam().changeLead();
    }
    protected void tackle(){
        this.setRunTarget(this.getLastBallLocation());
        this.move(true);
    }
}
