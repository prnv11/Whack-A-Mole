
import gui.WhackAMole;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Whack-A-Mole game. Launches the GUI on the Event
 * Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // Launch GUI on the Event Dispatch Thread (required for Swing)
        SwingUtilities.invokeLater(() -> {
            WhackAMole game = new WhackAMole();
            game.setVisible(true);
        });
    }
}