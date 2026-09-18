package gui;

import data.HighScoreManager;
import data.PlayerScore;
import engine.GameEngine;
import exception.HighScoreException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import model.HoleOccupant;

/**
 * Main game GUI using Java Swing. Implements thread-safe communication with
 * GameEngine via callback interface.
 */
public class WhackAMole extends JFrame implements GameEngine.GameCallback {

    // UI Components
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JLabel timeLabel;
    private JButton[] holeButtons;
    private JButton startButton;
    private JButton exitButton;
    private JPanel gameOverPanel;
    private JLabel finalScoreValue;
    private JLabel highScoreValue;

    // Game components
    private GameEngine gameEngine;
    private Thread gameThread;
    private HighScoreManager highScoreManager;
    private int currentHighScore;

    // Constants
    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 3;
    private ImageIcon emptyHoleIcon;

    /**
     * Main constructor - initializes all components.
     */
    public WhackAMole() {
        highScoreManager = new HighScoreManager();
        loadHighScores();
        createEmptyHoleIcon();
        initializeGUI();
        setupWindowListener();
    }

    /**
     * Loads high scores with proper exception handling. Demonstrates handling
     * of custom checked exception.
     */
    private void loadHighScores() {
        try {
            currentHighScore = highScoreManager.getHighScore();
        } catch (HighScoreException e) {
            // Handle gracefully - show alert and start with empty scores
            JOptionPane.showMessageDialog(this,
                    "Could not load high scores: " + e.getMessage()
                    + "\nStarting with empty high score list.",
                    "High Score Load Error",
                    JOptionPane.WARNING_MESSAGE);
            currentHighScore = 0;
        }
    }

    /**
     * Creates the empty hole graphic programmatically.
     */
    private void createEmptyHoleIcon() {
        BufferedImage img = new BufferedImage(70, 70, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dark hole interior
        g.setColor(new Color(30, 30, 30));
        g.fillOval(5, 15, 60, 45);

        // Hole rim/shadow
        g.setColor(new Color(60, 60, 60));
        g.setStroke(new BasicStroke(3));
        g.drawOval(5, 15, 60, 45);

        g.dispose();
        emptyHoleIcon = new ImageIcon(img);
    }

    /**
     * Initializes the complete GUI layout.
     */
    private void initializeGUI() {
        setTitle("Whack-A-Mole");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Main panel with wood-like border
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(139, 90, 43)); // Wood brown
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add components
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createGamePanel(), BorderLayout.CENTER);
        mainPanel.add(createControlPanel(), BorderLayout.SOUTH);

        // Game over overlay (initially hidden)
        gameOverPanel = createGameOverPanel();
        gameOverPanel.setVisible(false);

        // Use layered pane for overlay effect
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(620, 500));

        mainPanel.setBounds(0, 0, 620, 500);
        gameOverPanel.setBounds(0, 0, 620, 500);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(gameOverPanel, JLayeredPane.POPUP_LAYER);

        add(layeredPane);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Creates the header panel with score and time display.
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 60, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Title on left
        JLabel titleLabel = new JLabel("Whack-A-Mole");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        // Score panel in center
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        scorePanel.setOpaque(false);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(Color.WHITE);

        highScoreLabel = new JLabel("High Score: " + currentHighScore);
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        highScoreLabel.setForeground(Color.WHITE);

        scorePanel.add(scoreLabel);
        scorePanel.add(highScoreLabel);
        panel.add(scorePanel, BorderLayout.CENTER);

        // Time on right
        timeLabel = new JLabel("Time: 30s");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setForeground(Color.WHITE);
        panel.add(timeLabel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the game grid panel with clickable holes.
     */
    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLS, 15, 15));
        panel.setBackground(new Color(100, 180, 180)); // Teal background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        holeButtons = new JButton[GRID_ROWS * GRID_COLS];

        for (int i = 0; i < holeButtons.length; i++) {
            final int index = i; // Final for lambda

            JButton button = new JButton(emptyHoleIcon);
            button.setPreferredSize(new Dimension(80, 80));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // EVENT HANDLING using lambda expression
            // When clicked, trigger whack on the GameEngine
            button.addActionListener(event -> {
                if (gameEngine != null && gameEngine.isRunning()) {
                    gameEngine.whackHole(index);
                }
            });

            holeButtons[i] = button;
            panel.add(button);
        }

        return panel;
    }

    /**
     * Creates the control panel with Start and Exit buttons.
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(0, 40, 60));

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(0, 150, 100));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(120, 35));
        startButton.addActionListener(e -> startGame());

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setBackground(new Color(100, 100, 100));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(80, 35));
        exitButton.addActionListener(e -> exitGame());

        panel.add(startButton);
        panel.add(exitButton);

        return panel;
    }

    /**
     * Creates the semi-transparent game over overlay panel.
     */
    private JPanel createGameOverPanel() {
        // Semi-transparent overlay
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // "GAME OVER" title
        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 52));
        gameOverLabel.setForeground(new Color(255, 50, 50));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Final score display
        finalScoreValue = new JLabel("Your Score: 0");
        finalScoreValue.setFont(new Font("Arial", Font.BOLD, 22));
        finalScoreValue.setForeground(Color.YELLOW);
        finalScoreValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        // High score display
        highScoreValue = new JLabel("High Score: 0");
        highScoreValue.setFont(new Font("Arial", Font.BOLD, 22));
        highScoreValue.setForeground(Color.WHITE);
        highScoreValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton playAgainBtn = new JButton("Play Again");
        playAgainBtn.setFont(new Font("Arial", Font.BOLD, 14));
        playAgainBtn.setBackground(new Color(0, 150, 100));
        playAgainBtn.setForeground(Color.WHITE);
        playAgainBtn.setFocusPainted(false);
        playAgainBtn.addActionListener(e -> {
            gameOverPanel.setVisible(false);
            startGame();
        });

        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exitBtn.setBackground(new Color(100, 100, 100));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.addActionListener(e -> exitGame());

        buttonPanel.add(playAgainBtn);
        buttonPanel.add(exitBtn);

        // Add spacing and components
        contentPanel.add(gameOverLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(finalScoreValue);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(highScoreValue);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(buttonPanel);

        panel.add(contentPanel);
        return panel;
    }

    /**
     * Sets up the window close listener for graceful shutdown. CRITICAL: Must
     * interrupt the game thread to trigger clean exit.
     */
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });
    }

    /**
     * Starts a new game by creating GameEngine and starting its thread.
     */
    private void startGame() {
        // Stop existing game if running
        if (gameThread != null && gameThread.isAlive()) {
            gameEngine.stopGame();
            gameThread.interrupt();
            try {
                gameThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Hide game over panel if visible
        gameOverPanel.setVisible(false);

        // Clear all holes
        for (JButton button : holeButtons) {
            button.setIcon(emptyHoleIcon);
        }

        // Create new game engine with this as callback
        gameEngine = new GameEngine(this);
        gameEngine.setHighScore(currentHighScore);

        // Create and start game thread (REQUIRED per Module 2)
        gameThread = new Thread(gameEngine);
        gameThread.start();

        // Update button state
        startButton.setText("Restart");
    }

    /**
     * Exits the game with proper thread cleanup.
     */
    private void exitGame() {
        if (gameThread != null && gameThread.isAlive()) {
            gameEngine.stopGame();
            // CRITICAL: Call interrupt() to trigger InterruptedException
            // This is the graceful shutdown signal
            gameThread.interrupt();
            try {
                gameThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        dispose();
        System.exit(0);
    }

    // ========== GameCallback Interface Implementation ==========
    // These methods are called from GameEngine via SwingUtilities.invokeLater()
    // so they run on the EDT (Event Dispatch Thread)
    @Override
    public void onScoreUpdate(int score) {
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void onTimeUpdate(int time) {
        timeLabel.setText("Time: " + time + "s");
    }

    @Override
    public void onHoleUpdate(int index, HoleOccupant occupant) {
        if (occupant != null && occupant.isVisible()) {
            holeButtons[index].setIcon(occupant.getImage());
        } else {
            holeButtons[index].setIcon(emptyHoleIcon);
        }
    }

    @Override
    public void onGameOver(int finalScore, int highScore) {
        // Always ask for name after game
        String name = JOptionPane.showInputDialog(this,
                "Game Over! Your score: " + finalScore + "\nEnter your name:",
                "Game Over",
                JOptionPane.PLAIN_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            // Save score to file
            try {
                highScoreManager.addScore(new PlayerScore(name.trim(), finalScore));

                // Reload high score
                currentHighScore = highScoreManager.getHighScore();
                highScoreLabel.setText("High Score: " + currentHighScore);

            } catch (HighScoreException e) {
                JOptionPane.showMessageDialog(this,
                        "Could not save high score: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        // Show leaderboard
        showHighScoreLeaderboard();

        // Update game over panel
        finalScoreValue.setText("Your Score: " + finalScore);
        highScoreValue.setText("High Score: " + currentHighScore);

        // Show game over overlay
        gameOverPanel.setVisible(true);

        // Reset start button
        startButton.setText("Start Game");
    }

    @Override
    public void onHighScoreUpdate(int highScore) {
        currentHighScore = highScore;
        highScoreLabel.setText("High Score: " + highScore);
    }

    /**
     * Shows the high score leaderboard in a dialog.
     */
    private void showHighScoreLeaderboard() {
        try {
            java.util.List<PlayerScore> topScores = highScoreManager.getTopScores();

            StringBuilder leaderboard = new StringBuilder();
            leaderboard.append("═══════════ HIGH SCORES ═══════════\n\n");

            if (topScores.isEmpty()) {
                leaderboard.append("No scores yet. Be the first!\n");
            } else {
                int rank = 1;
                for (PlayerScore ps : topScores) {
                    leaderboard.append(String.format("%2d. %-20s %6d\n",
                            rank++, ps.getPlayerName(), ps.getScore()));
                    if (rank > 10) {
                        break; // Show top 10

                                    }}
            }

            leaderboard.append("\n═══════════════════════════════════");

            JTextArea textArea = new JTextArea(leaderboard.toString());
            textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
            textArea.setEditable(false);
            textArea.setBackground(new Color(240, 240, 240));

            JOptionPane.showMessageDialog(this,
                    textArea,
                    "Leaderboard",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (HighScoreException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load high scores: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Main entry point - launches the game on the EDT.
     */
    public static void main(String[] args) {
        // MUST launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            WhackAMole game = new WhackAMole();
            game.setVisible(true);
        });
    }
}