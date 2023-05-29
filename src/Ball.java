import javax.swing.*;
import java.awt.*;

public class Ball extends JComponent implements Runnable {
    private Rectangle location;
    private ImageIcon currentState;
    private Player lastInPosition;
    private Point[] route;
    private int currBallState;
    private boolean inPossession;
    private boolean ballDelivered;
    private int waitMili;

    public Ball(Point startLoc){
        this.currBallState = 0;
        this.location = new Rectangle(startLoc.x, startLoc.y, Constants.BALL_SIZE, Constants.BALL_SIZE);
        this.setBounds(this.location);
        this.currentState = Constants.BALL_STATES[this.currBallState];
        this.route = new Point[0];
    }
    private void ballImageRoll(){
        if (this.currBallState < (Constants.BALL_STATES.length - 1)){
            this.currBallState++;
        }else {
            this.currBallState = 0;
        }
        this.currentState = Constants.BALL_STATES[this.currBallState];
    }
    public void ballDelivered(Point target, int speed){
        this.inPossession = false;
        this.ballDelivered = true;
        this.route = Utils.calculatePointRoute(new Point(this.location.x, this.location.y), target, Constants.POINTS_MARGIN).toArray(this.route);
        Point next = null;
        this.waitMili = speed;
    }
    public void setLastInPosition(Player player){
        this.lastInPosition = player;
    }
    public void setInPossession(){
        this.inPossession = true;
    }
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setColor(Color.ORANGE.darker().darker());
        graphics2D.fillOval(this.getX(), this.getY(), Constants.BALL_SIZE, Constants.BALL_SIZE);
        //graphics2D.drawImage(this.currentState.getImage(), this.location.x, this.location.y, this.currentState.getImageObserver());
    }

    @Override
    public void run() {
        while (true){
            if (this.inPossession){
                int x = this.lastInPosition.getRectangle().x;
                int y = this.lastInPosition.getRectangle().y + Constants.PLAYER_HEIGHT - Constants.BALL_SIZE;
                if (this.lastInPosition.getTeam().attackToRight()){
                    x += Constants.PLAYER_WIDTH;
                }else {
                    x -= Constants.BALL_SIZE;
                }
                this.location = new Rectangle(x, y, Constants.BALL_SIZE, Constants.BALL_SIZE);
                this.setBounds(this.location);
            }
            if (this.ballDelivered){
                for (int i = 0; i < this.route.length; i++){
                    if (this.inPossession){
                        break;
                    }
                    Point next = this.route[i];
                    Utils.sleep(this.waitMili);
                    this.location = new Rectangle(next.x, next.y, Constants.BALL_SIZE, Constants.BALL_SIZE);
                    this.setBounds(this.location);
                    ballImageRoll();
                }
                this.ballDelivered = false;
            }
        }
    }
}
