import javax.swing.*;

public class Window extends JFrame {
    public Window(){
        this.setSize(Constants.PITCH_WIDTH, Constants.PITCH_HEIGHT);
        this.setVisible(true);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("Football Mania!!!!");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(new Game(Constants.PITCH_WIDTH, Constants.PITCH_HEIGHT));
    }
    public static void main(String[] args) {
        new Window();
    }
}
