import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Player extends JComponent implements Runnable{
    private int id;
    private Rectangle location;
    private List<Point> route;
    private boolean hasTheBall;
    private Point runTarget;
    private int speed;
    private Team team;
    private Point ballLastLocation;

    public Player(int id, int xStart, int yStart, Team team){
        this.speed = Constants.BASIC_SPEED;
        this.id = id;
        this.location = new Rectangle(xStart, yStart, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        this.setBounds(this.location);
        this.team = team;
        this.route = new ArrayList<>();
        this.runTarget = new Point();
        this.ballLastLocation = Constants.BALL_START_LOCATION;
    }
    public Point getRunTarget(){
        return this.runTarget;
    }
    public void setRunTarget(Point target){
        this.runTarget = target;
        setRoute();
    }
    protected void setRoute(){
        this.route = Utils.calculatePointRoute(this.getLocation(), this.runTarget, Constants.POINTS_MARGIN);
    }
    protected void move(boolean tackle){
        for (Point point :
                this.route) {
            if (this.team.checkForCollisions(this, false)){
                if (tackle){
                    this.getTeam().didFoul();
                }
                break;
            }else if (!this.hasTheBall && Utils.playerBallCollision(this, this.ballLastLocation)) {
                if (tackle){
                    //TODO: PICS
                }
                this.hasTheBall = true;
                this.team.setBallLastInPossession(this);
                this.team.setBallInPossession();
            }else{
               // Utils.sleep(this.speed);
                this.location = new Rectangle(point.x, point.y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            }
        }
    }
    public void updateLocation(){
        this.setBounds(this.location);
    }
    public int getSpeed() {
        return this.speed;
    }
    protected void changeSpeed(int speed){
        this.speed = speed;
    }
    public boolean equals(Object o) {
        return this.id == ((Player)o).id;
    }
    protected boolean attack(){
        return this.team.attacking();
    }
    public boolean doesHaveTHeBall(){
        return this.hasTheBall;
    }
    protected boolean attackToRight(){
        return this.team.attackToRight();
    }
    protected Team getTeam(){
        return this.team;
    }
    public Rectangle getRectangle(){
        return this.location;
    }
    public boolean hasTheBall(){
        return this.hasTheBall;
    }
    public abstract void run();
    protected abstract void kick(Point shotTarget);

    protected abstract void pass(Point passTarget);

    protected void getBall() {
        this.runTarget = this.ballLastLocation;
        this.move(true);
    }

    protected void noBall() {
        this.hasTheBall = false;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.gray.darker());
        g.fillRect(this.location.x, this.location.y, this.location.width, this.location.height);
    }

    public int getId() {
        return this.id;
    }

    public void updateBallLoc(Point ballLoc){
        this.ballLastLocation = ballLoc;
    }

    protected Point getLastBallLocation() {
        return this.ballLastLocation;
    }
}
