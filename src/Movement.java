import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public abstract class Movement implements KeyListener {
    private List<KeyEvent> movementPressedKeys;
    private ControlledPlayer player;
    private Team team;
    public Movement(ControlledPlayer player, Team team){
        this.movementPressedKeys = new ArrayList<>();
        this.player = player;
        this.team = team;
    }
    public void changePlayer(ControlledPlayer player){
        this.player = player;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        keyAction(e);
        System.out.println("typed");
    }
    private boolean findInKeyListRemoveOptional(int keyCode, boolean toRemove){
        boolean result = false;
        for (KeyEvent key: this.movementPressedKeys){
            if (key.getKeyCode() == keyCode){
                result = true;
                if (toRemove){
                    this.movementPressedKeys.remove(key);
                }
                break;
            }
        }
        return result;
    }
    protected Team getTeam(){
        return this.team;
    }
    protected ControlledPlayer getPlayer(){
        return this.player;
    }
    protected List<KeyEvent> getMovementPressedKeys(){
        return this.movementPressedKeys;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("pressed");
        keyAction(e);
        System.out.println("done pressing");
    }
    private void keyAction(KeyEvent e){
        int keyCode = e.getKeyCode();
        boolean toAddMove = false;
        boolean toAddAction = false;
        if (keyCode == KeyEvent.VK_LEFT){
            toAddMove = true;
            this.findInKeyListRemoveOptional(KeyEvent.VK_RIGHT, true);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            toAddMove = true;
            this.findInKeyListRemoveOptional(KeyEvent.VK_LEFT, true);
        }
        if (keyCode == KeyEvent.VK_UP) {
            toAddMove = true;
            this.findInKeyListRemoveOptional(KeyEvent.VK_DOWN,true);
        }
        if (keyCode == KeyEvent.VK_DOWN){
            toAddMove = true;
            this.findInKeyListRemoveOptional(KeyEvent.VK_UP, true);
        }
        if (keyCode == KeyEvent.VK_SHIFT) {
            toAddMove = true;
        }
        if (toAddMove){
            if (!this.movementPressedKeys.contains(e)){
                movementPressedKeys.add(e);
            }
            move();
        }
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
            action(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_UP
                || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_SHIFT){
            movementPressedKeys.remove(e);
        }
    }
    protected abstract void action(KeyEvent e);
    private void move(){
        int speed = this.player.getSpeed();
        Point currLoc = this.player.getLocation();
        int newX = currLoc.x;
        int newY = currLoc.y;
        if (this.findInKeyListRemoveOptional(KeyEvent.VK_SHIFT, false)){
            speed = Constants.RUN_SPEED;
        }
        if (this.findInKeyListRemoveOptional(KeyEvent.VK_RIGHT, false)){
            newX += speed;
        }
        if (this.findInKeyListRemoveOptional(KeyEvent.VK_LEFT, false)){
            newX -= speed;
        }
        if (this.findInKeyListRemoveOptional(KeyEvent.VK_DOWN, false)){
            newY += speed;
        }
        if (this.findInKeyListRemoveOptional(KeyEvent.VK_UP, false)){
            newY -= speed;
        }
        Point moveTo = new Point(newX, newY);
        this.player.setRunTarget(moveTo);
        this.player.setRoute();
        this.player.move(false);
        System.out.println("controlled move initiated");
    }
}
