package engine;

import exception.InvalidGameStateException;
import java.util.Random;
import javax.swing.SwingUtilities;
import model.Bomb;
import model.BonusMole;
import model.HoleOccupant;
import model.Mole;

/**
 * Game engine running on a separate thread. Implements Runnable for concurrent execution separate from the UI thread
 * This class encapsulates the game loop and manages: - Game timer countdown -
 * Spawning of HoleOccupant objects - Lifecycle management of occupants - Score
 * tracking
 */
public class GameEngine implements Runnable {

    // Callback interface for thread-safe UI updates
    private final GameCallback callback;

    // Game state - array of holes (polymorphic collection)
    private final HoleOccupant[] holes;
    private final Random random;

    // Volatile for thread visibility
    private volatile boolean gameIsRunning;
    private int score;
    private int timeRemaining;
    private int highScore;

    // Game constants
    private static final int GRID_SIZE = 12; // 4x3 grid
    private static final int GAME_DURATION = 30; // seconds
    private static final int TICK_INTERVAL = 1000; // milliseconds

    // Callback interface for thread-safe UI updates. all methods will be called using SwingUtilities.invokeLater()
    public interface GameCallback {

        void onScoreUpdate(int score);

        void onTimeUpdate(int time);

        void onHoleUpdate(int index, HoleOccupant occupant);

        void onGameOver(int finalScore, int highScore);

        void onHighScoreUpdate(int highScore);
    }

    // Constructs a new GameEngine with the given callback.
    public GameEngine(GameCallback callback) {
        this.callback = callback;
        this.holes = new HoleOccupant[GRID_SIZE];
        this.random = new Random();
        this.gameIsRunning = false;
        this.score = 0;
        this.timeRemaining = GAME_DURATION;
        this.highScore = 0;
    }

    // Sets the current high score (loaded from file)
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    // Main game loop - runs on separate thread. Contains the core game logic with proper InterruptedException handling
    @Override
    public void run() {
        gameIsRunning = true;
        score = 0;
        timeRemaining = GAME_DURATION;

        // Clear all holes at start
        for (int i = 0; i < GRID_SIZE; i++) {
            holes[i] = null;
            updateHoleOnUI(i, null);
        }

        // Update initial UI state
        updateScoreOnUI(score);
        updateTimeOnUI(timeRemaining);

        while (gameIsRunning && timeRemaining > 0) {
            try {
                // Spawn new occupants in random holes
                spawnOccupants();

                // Update all active occupants (tick down their timers)
                tickAllOccupants();

                // Decrement game timer
                timeRemaining--;
                updateTimeOnUI(timeRemaining);

                // REQUIRED: Thread.sleep for game pacing
                // This forces handling of InterruptedException
                Thread.sleep(TICK_INTERVAL);

            } catch (InterruptedException e) {
                // InterruptedException is a SIGNAL, not an error!
                // This is triggered when the window closes and interrupt() is called
                gameIsRunning = false;

                // Preserve interrupt status for any calling code
                Thread.currentThread().interrupt();

                // Exit cleanly - don't show game over screen on interrupt
                return;
            }
        }

        // Update high score if current score is higher
        if (score > highScore) {
            highScore = score;
        }

        // Notify UI of game over (MUST use SwingUtilities for thread safety)
        SwingUtilities.invokeLater(() -> {
            callback.onGameOver(score, highScore);
        });
    }

    // Spawns new occupants in random empty holes. Called each tick of the game loop
    private void spawnOccupants() {
        // Spawn 1-2 occupants per tick
        int spawnCount = random.nextInt(2) + 1;

        for (int i = 0; i < spawnCount; i++) {
            int holeIndex = random.nextInt(GRID_SIZE);

            // Only spawn in empty or hidden holes
            if (holes[holeIndex] == null || !holes[holeIndex].isVisible()) {
                HoleOccupant occupant = createRandomOccupant();
                holes[holeIndex] = occupant;
                updateHoleOnUI(holeIndex, occupant);
            }
        }
    }

    /**
     * Creates a random HoleOccupant based on weighted probability.
     * @return a new Mole, Bomb, or BonusMole instance
     */
    private HoleOccupant createRandomOccupant() {
        int rand = random.nextInt(100);

        if (rand < 10) {
            return new BonusMole(); // 10% chance - rare, high value
        } else if (rand < 25) {
            return new Bomb();      // 15% chance - penalty
        } else {
            return new Mole();      // 75% chance - standard target
        }
    }

    // Updates all active occupants, hiding expired ones. Called each tick of the game loop
    private void tickAllOccupants() {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (holes[i] != null && holes[i].isVisible()) {
                // tick() returns true if the occupant's time has expired
                boolean expired = holes[i].tick();
                if (expired) {
                    holes[i].hide();
                    updateHoleOnUI(i, null);
                }
            }
        }
    }

    /**
     * Handles a whack at a specific hole index.
     *no instanceof or type checking for score! The
     * whack() method behavior is determined at runtime by the concrete class.
     * @param index the hole index that was clicked
     */
    public synchronized void whackHole(int index) {
        if (!gameIsRunning) {
            return;
        }

        // Validate index - throw unchecked exception for programmer error
        if (index < 0 || index >= GRID_SIZE) {
            throw new InvalidGameStateException(
                    "Invalid hole index: " + index + ". Valid range: 0-" + (GRID_SIZE - 1));
        }

        HoleOccupant occupant = holes[index];

        if (occupant != null && occupant.isVisible()) {
            // The specific behavior of whack() is determined at runtime, by whether occupant is a Mole, Bomb, or BonusMole
            int scoreChange = occupant.whack();
            score += scoreChange;

            // Ensure score doesn't go negative
            if (score < 0) {
                score = 0;
            }

            // Handle bonus time from BonusMole (special case)
            if (occupant instanceof BonusMole) {
                timeRemaining += ((BonusMole) occupant).getTimeBonus();
                updateTimeOnUI(timeRemaining);
            }

            // Update UI (thread-safe)
            updateScoreOnUI(score);
            updateHoleOnUI(index, null);

            // Update high score display if needed
            if (score > highScore) {
                highScore = score;
                SwingUtilities.invokeLater(() -> callback.onHighScoreUpdate(highScore));
            }
        }
    }

    // All UI updates from GameEngine MUST go through SwingUtilities.invokeLater()
    private void updateHoleOnUI(int index, HoleOccupant occupant) {
        SwingUtilities.invokeLater(() -> callback.onHoleUpdate(index, occupant));
    }

    private void updateScoreOnUI(int score) {
        SwingUtilities.invokeLater(() -> callback.onScoreUpdate(score));
    }

    private void updateTimeOnUI(int time) {
        SwingUtilities.invokeLater(() -> callback.onTimeUpdate(time));
    }

    public void stopGame() {
        gameIsRunning = false;
    }

    public boolean isRunning() {
        return gameIsRunning;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }
}