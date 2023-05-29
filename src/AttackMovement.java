import java.awt.event.KeyEvent;

public class AttackMovement extends Movement{
    public AttackMovement(ControlledPlayer player, Team team) {
        super(player, team);
        System.out.println("attack");
    }
    @Override
    protected void action(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_SPACE -> this.getTeam().sendForward();
            case KeyEvent.VK_A -> this.getPlayer().pass(this.getTeam().passTarget(this.getPlayer()));
            case KeyEvent.VK_D -> {
                this.kickAndPassDirAffect();
                this.getPlayer().kickProcess();
            }
        }
    }
    private void kickAndPassDirAffect(){
        for (KeyEvent e: this.getMovementPressedKeys()){
            switch (e.getKeyCode()){
                case KeyEvent.VK_DOWN -> this.getPlayer().calibrateShotTargetDown();
                case KeyEvent.VK_UP -> this.getPlayer().calibrateShotTargetUp();
            }
        }
    }
}
