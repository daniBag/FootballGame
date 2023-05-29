import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.util.List;

public class Game extends JPanel {
    public static final Goal CPU_GOAL = new Goal(new Rectangle(Constants.PITCH_WIDTH - 30, Constants.GOAL_UPPER_POST, 30, Constants.GOAL_LOWER_POST - Constants.GOAL_UPPER_POST));
    public static final Goal PLAYER_GOAL = new Goal(new Rectangle(0, Constants.GOAL_UPPER_POST, 30, Constants.GOAL_LOWER_POST - Constants.GOAL_UPPER_POST));
    private Team cpuTeam;
    private Thread gameLoop;
    private boolean cpuTeamThreat;
    private Team playerTeam;
    private boolean foul;
    private boolean playerTeamThreat;
    //TODO: decide static ball or not;
    private Ball ball;
    public Game(int width, int height){
        this.setBounds(0, 0, width, height);
        this.setFocusable(true);
        this.requestFocus();
        this.ball = new Ball(Constants.BALL_START_LOCATION);
        this.cpuTeam = new Team(false, false, this);
        this.playerTeam = new Team(true, true, this);
        this.cpuTeam.teamStart();
        this.playerTeam.teamStart();
        this.setVisible(true);
        this.setLayout(null);
        this.add(this.ball);
        while (!this.isRunning()){
            Utils.sleep(500);
        }
        this.gameLoop = new Thread(()->{
            while (true){
                update();
                repaint();
                Utils.sleep(200);
            }
        });
        this.gameLoop.start();
    }
    public Point getBallLocation(){
        return this.ball.getLocation();
    }

    private void update() {
        this.updateBallLocation();
        this.playerTeam.update();
        this.cpuTeam.update();
        if (this.foul){
            //TODO: implement fouls
        }
    }

    private void updateBallLocation() {
        Point ballLoc = this.getBallLocation();
        this.cpuTeam.updateBallLoc(ballLoc);
        this.playerTeam.updateBallLoc(ballLoc);
    }

    public Point getCPUGoalLoc(){
        return CPU_GOAL.getLocation().getLocation();
    }
    public Point getPlayerGoalLoc(){
        return PLAYER_GOAL.getLocation().getLocation();
    }
    public boolean checkForCollisions(Player player, Team team){
        boolean collision;
        if (team.isPlayerTeam()){
            collision = this.cpuTeam.checkForCollisions(player, true);
        }else {
            collision = this.playerTeam.checkForCollisions(player, true);
        }
        return collision;
    }

    public boolean cleanRangeCheck(List<Point> check, Team team) {
        boolean clean;
        if (team.isPlayerTeam()){
            clean = this.cpuTeam.cleanRangeCheck(check);
        }else{
            clean = this.playerTeam.cleanRangeCheck(check);
        }
        return clean;
    }

    public void threatOnGoal(Team team) {
        if (team.isPlayerTeam()){
            this.playerTeamThreat = true;
        }else{
            this.cpuTeamThreat = true;
        }
    }
    public boolean isCpuTeamThreat(){
        return this.cpuTeamThreat;
    }
    public boolean isPlayerTeamThreat(){
        return this.playerTeamThreat;
    }

    public boolean canTackle(FieldPlayer player) {
        boolean result = false;
        if (this.playerTeam != null && this.playerTeam.playersProximity(player)){
            result = true;
        }
        return result;
    }

    public void didFoul(Team team) {
        this.foul = true;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.cpuTeam.paintComponent(g);
        this.playerTeam.paintComponent(g);
        ball.paintComponents(g);
    }

    public void setBallInPossession() {
        this.ball.setInPossession();
    }

    public void setBallLastInPossession(Player player) {
        this.ball.setLastInPosition(player);
    }

    public void ballDelivered(Point goalKick, int ballDelivery) {
        this.ball.ballDelivered(goalKick, ballDelivery);
    }

    public FieldPlayer getEnemyLeader(Team team) {
        FieldPlayer player = null;
        if (team.isPlayerTeam()){
            player = this.cpuTeam.getLeader(null);
        }else {
            player = this.playerTeam.getLeader(null);
        }
        return player;
    }

    public boolean isRunning() {
        return this.cpuTeam.isRunning() && this.playerTeam.isRunning();
    }
}
