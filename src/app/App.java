package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Board board = new Board();
            frame.setContentPane(board);

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setMinimumSize(frame.getSize());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}