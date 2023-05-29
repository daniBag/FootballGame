import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team {
    private String teamName;
    private static int playerId;
    private List<FieldPlayer> fieldPlayers;
    private GoalKeeper goalKeeper;
    private boolean inPossession;
    private final boolean ATTACK_TO_RIGHT;
    private final boolean CONTROLLABLE;
    private Game game;
    private int score;
    private boolean inAttack;
    private boolean running;
    private Map<Integer, Thread> teamThreads;

    public Team(boolean controllable, boolean attackDirectionRight, Game game){
        playerId++;
        this.teamThreads = new HashMap<>();
        this.fieldPlayers = new ArrayList<>();
        this.running = false;
        this.CONTROLLABLE = controllable;
        this.game = game;
        this.ATTACK_TO_RIGHT = attackDirectionRight;
        this.score = 0;
        ArrayList<Point> playersStartLocations = generatePlayersLocations();
        this.goalKeeper = new GoalKeeper(playerId, playersStartLocations.get(Constants.TEAM_SIZE - 1).x,
                playersStartLocations.get(Constants.TEAM_SIZE - 1).y, this, 1);
        playerId++;
        if (this.CONTROLLABLE){
            ControlledPlayer player = new ControlledPlayer(playerId, playersStartLocations.get(Constants.FIRST_PLAYER).x, playersStartLocations.get(Constants.FIRST_PLAYER).y, this);
            playerId++;
            this.fieldPlayers.add(player);
            this.teamThreads.put(Constants.FIRST_PLAYER, new Thread(player));
            this.game.add(player);
        }
        for (int i = 0; i < Constants.TEAM_SIZE - 1; i++){
            if (this.CONTROLLABLE && i == Constants.FIRST_PLAYER){
                continue;
            }
            FieldPlayer fieldPlayer = new FieldPlayer(playerId, playersStartLocations.get(i).x, playersStartLocations.get(i).y, this, (i == Constants.FIRST_PLAYER));
            this.fieldPlayers.add(fieldPlayer);
            this.teamThreads.put(i, new Thread(fieldPlayer));
            this.game.add(fieldPlayer);
            playerId++;

        }
        this.teamThreads.put(this.fieldPlayers.size(), new Thread(this.goalKeeper));
        this.game.add(goalKeeper);
    }
    public void teamStart(){
        for (int i = 0; i < this.teamThreads.size(); i++){
            this.teamThreads.get(i).start();
        }
        this.running = true;
    }
    public boolean isRunning(){
        return this.running;
    }
    public boolean gameRunning(){
        return this.game.isRunning();
    }
    public boolean isPlayerTeam(){
        return this.CONTROLLABLE;
    }
    public boolean attackToRight(){
        return this.ATTACK_TO_RIGHT;
    }
    public void sendForward(){
        for (FieldPlayer player: this.fieldPlayers){
            if (!player.isLeader()){
                Point target = new Point((this.ATTACK_TO_RIGHT? this.game.getCPUGoalLoc().x:this.game.getPlayerGoalLoc().x), player.getRectangle().y);
                player.setRunTarget(target);
            }
        }
    }
    public boolean playersProximity(FieldPlayer player){
        boolean canTackle = false;
        if (this.attacking()){
            canTackle = Utils.playersProximity(player, getEnemyLeader());
        }
        return canTackle;
    }
    public boolean canTackle(FieldPlayer player){
        return this.game.canTackle(player);
    }
    public void switchPlayer(){
        FieldPlayer temp = null;
        int leaderIndex = 0;
        int closest = closestToBall();
        for (FieldPlayer player: this.fieldPlayers) {
            leaderIndex++;
            if (player.isLeader()){
                temp = player;
                break;
            }
        }
        temp = this.fieldPlayers.get(leaderIndex);
        this.fieldPlayers.remove(leaderIndex);
        this.fieldPlayers.add(leaderIndex, new FieldPlayer(temp.getId(), temp.getRectangle().x, temp.getRectangle().y, this, false));
        temp = this.fieldPlayers.get(closest);
        this.fieldPlayers.remove(closest);
        this.fieldPlayers.add(closest, new ControlledPlayer(temp.getId(), temp.getRectangle().x, temp.getRectangle().y, this));
    }
    private int closestToBall(){
        Point ballLoc = this.game.getBallLocation();
        double distance = Constants.PITCH_WIDTH;
        int closestIndex = Constants.INVALID;
        for (int i = 0; i < this.fieldPlayers.size(); i++){
            if (!this.fieldPlayers.get(i).isLeader()){
                double curr = this.fieldPlayers.get(i).getRectangle().getLocation().distance(ballLoc);
                if (curr < distance){
                    distance = curr;
                    closestIndex = i;
                }
            }
        }
        return closestIndex;
    }
    private ArrayList<Point> generatePlayersLocations(){
        ArrayList<Point> locations = new ArrayList<>(Constants.TEAM_SIZE);
        int sideMultiply = 1;
        float sideAddition = Constants.CENTER_LINE / ((float) Constants.TEAM_SIZE);
        sideAddition /= 1.5;
        if(!this.ATTACK_TO_RIGHT){
            sideMultiply *= -1;
        }
        int yLoc;
        int xLoc;
        for (int i = 0; i < Constants.TEAM_SIZE - 1; i++){
            xLoc = (int) (Constants.CENTER_LINE + (sideMultiply * sideAddition * (i+1)));
            if (i % 2 == 0){
                yLoc = Constants.GOAL_UPPER_POST - Constants.PLAYER_HEIGHT;
            }else{
                yLoc = Constants.GOAL_LOWER_POST;
            }
            locations.add(new Point(xLoc, yLoc));
        }
        yLoc = Constants.GOAL_UPPER_POST + ((Constants.GOAL_LOWER_POST - Constants.GOAL_UPPER_POST) / 4);
        if (this.ATTACK_TO_RIGHT){
            xLoc = Constants.PLAYER_WIDTH;
        }else {
            xLoc = Constants.PITCH_WIDTH - (Constants.PLAYER_WIDTH * 2);
        }
        locations.add(new Point(xLoc, yLoc));
        return locations;
    }
    private void attackCheck(){
        boolean fieldAttack = false;
        for (FieldPlayer pl :
                this.fieldPlayers) {
            if (pl.hasTheBall()) {
                fieldAttack = true;
            }
        }
        this.inAttack = this.goalKeeper.hasTheBall() || fieldAttack;
    }
    public boolean attacking(){
        return this.inAttack;
    }
    private void assignLeaderDefense(){
        FieldPlayer leader = findClosestPlayer(this.game.getBallLocation());
        leader.setLeader();
        for (FieldPlayer player: this.fieldPlayers){
            if (!player.equals(leader)){
                player.notLeader();
            }
        }
    }
    public synchronized boolean checkForCollisions(Player player, boolean fromOtherTeam){
        System.out.println(player.getId());
        boolean collision = false;
        for (FieldPlayer fieldPlayer: this.fieldPlayers){
            if (!player.equals(fieldPlayer)){
                if (fieldPlayer.getRectangle().intersects(player.getRectangle())){
                    collision = true;
                }
            }
        }
        if (!player.equals(this.goalKeeper) && player.getRectangle().intersects(this.goalKeeper.getRectangle())){
            collision = true;
        }
        if (!collision && !fromOtherTeam){
            collision = this.game.checkForCollisions(player, this);
        }
        return collision;
    }
    public void update(){
        locationsUpdate();
        attackCheck();
    }

    private void locationsUpdate() {
        for (FieldPlayer fieldPlayer: this.fieldPlayers){
            fieldPlayer.updateLocation();
        }
        this.goalKeeper.updateLocation();
    }

    public void goalScored(){
        this.score++;
    }
    public boolean isInTeam(Player player){
        boolean result = false;
        for (FieldPlayer pl : this.fieldPlayers){
            if (pl.equals(player)){
                result = true;
                break;
            }
        }
        return result || this.goalKeeper.equals(player);
    }
    private FieldPlayer findClosestPlayer(Point targetLocation){
        double min = Constants.PITCH_WIDTH;
        double curr;
        FieldPlayer result = null;
        for (FieldPlayer player: this.fieldPlayers) {
            curr = new Point2D.Double(player.getRectangle().getX(),
                    player.getRectangle().getY()).distance(targetLocation);
            if (min > curr){
                min = curr;
                result = player;
            }
        }
        return result;
    }
    //TODO: soften
    public FieldPlayer getLeader(FieldPlayer player){
        FieldPlayer result = null;
        for (FieldPlayer fieldPlayer: this.fieldPlayers){
            if (fieldPlayer != null){
                if (fieldPlayer.isLeader()){
                    result = fieldPlayer;
                    break;
                }
            }
        }
        if (result == null && player != null){
            if (!player.isLeader()){
                player.setLeader();
            }
            result = player;
        }
        return result;
    }

    public boolean cleanRangeCheck(List<Point> check) {
        boolean clean = true;
        for (FieldPlayer player: this.fieldPlayers){
            if (check.contains(player.getRectangle().getLocation())){
                clean = false;
                break;
            }
        }
        if (clean){
            clean = this.game.cleanRangeCheck(check, this);
        }
        return clean;
    }

    public Point passTarget(FieldPlayer player) {
        FieldPlayer other = null;
        Point result = null;
        for (FieldPlayer fieldPlayer: this.fieldPlayers){
            if (!fieldPlayer.equals(player)){
                other = fieldPlayer;
            }
        }
        List<Point> check = Utils.calculatePointRoute(player.getRectangle().getLocation(), other.getRectangle().getLocation(), Constants.ROUTE_CHECK);
        if (cleanRangeCheck(check)){
            result = other.getRectangle().getLocation();
        }
        return result;
    }

    public void changeLead() {
        if (this.CONTROLLABLE){
            switchPlayer();
        }else {
            int closest = closestToBall();
            this.fieldPlayers.get(closest).setLeader();
            this.fieldPlayers.get(closest).setPassedTo();
        }
    }

    public void threatOnGoal() {
        this.game.threatOnGoal(this);
    }
    public boolean isThreat(){
        boolean threat;
        if (this.isPlayerTeam()){
            threat = this.game.isCpuTeamThreat();
        }else {
            threat = this.game.isPlayerTeamThreat();
        }
        return threat;
    }

    public void didFoul() {
        this.game.didFoul(this);
    }
    public void paintComponent(Graphics g){
        g.setColor(Color.gray.darker());
        for (FieldPlayer player: this.fieldPlayers){
            player.paintComponent(g);
        }
        this.goalKeeper.paintComponent(g);
    }

    public Point getBallLocation() {
        return this.game.getBallLocation();
    }

    public void setBallInPossession() {
        this.game.setBallInPossession();
    }

    public void setBallLastInPossession(Player player) {
        this.game.setBallLastInPossession(player);
    }

    public void ballDelivered(Point goalKick, int ballDelivery) {
        this.game.ballDelivered(goalKick, ballDelivery);
    }

    public FieldPlayer getEnemyLeader() {
        return this.game.getEnemyLeader(this);
    }

    public void updateBallLoc(Point ballLoc) {
        for (FieldPlayer fieldPlayer: this.fieldPlayers){
            fieldPlayer.updateBallLoc(ballLoc);
        }
        this.goalKeeper.updateBallLoc(ballLoc);
    }
}
