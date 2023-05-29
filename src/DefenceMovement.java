import java.awt.event.KeyEvent;

public class DefenceMovement extends Movement{
    public DefenceMovement(ControlledPlayer player, Team team) {
        super(player, team);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
    }

    @Override
    protected void action(KeyEvent e) {

    }
}
