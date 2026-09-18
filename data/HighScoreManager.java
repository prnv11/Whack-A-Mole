package data;

import exception.HighScoreException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages high score persistence using Java serialization. Uses
 * ObjectOutputStream and ObjectInputStream to write/read score data.
 */
public class HighScoreManager {

    private static final String SCORES_FILE = "scores.dat";
    private static final int MAX_SCORES = 10;

    /**
     * Saves the list of scores to file using ObjectOutputStream. Sorts scores
     * in descending order and keeps only top MAX_SCORES.
     *
     * @param scores the list of PlayerScore objects to save
     * @throws HighScoreException if serialization fails (wraps IOException)
     */
    public void saveScores(List<PlayerScore> scores) throws HighScoreException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SCORES_FILE))) {

            // Sort scores in descending order
            List<PlayerScore> topScores = new ArrayList<>(scores);
            topScores.sort(Comparator.comparingInt(PlayerScore::getScore).reversed());

            // Keep only top scores
            if (topScores.size() > MAX_SCORES) {
                topScores = new ArrayList<>(topScores.subList(0, MAX_SCORES));
            }

            // Serialize the entire list
            oos.writeObject(topScores);

        } catch (IOException e) {
            // Wrap IOException in custom checked exception
            throw new HighScoreException("Failed to save scores: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the list of scores from file using ObjectInputStream.
     *
     * @return the list of PlayerScore objects, or empty list if file doesn't
     * exist
     * @throws HighScoreException if deserialization fails (wraps IOException or
     * ClassNotFoundException)
     */
    @SuppressWarnings("unchecked")
    public List<PlayerScore> loadScores() throws HighScoreException {
        File file = new File(SCORES_FILE);

        // Return empty list if file doesn't exist yet
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SCORES_FILE))) {

            // Deserialize the list
            return (ArrayList<PlayerScore>) ois.readObject();

        } catch (IOException e) {
            // Wrap IOException in custom checked exception
            throw new HighScoreException("Failed to load scores (IO error): " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            // Wrap ClassNotFoundException in custom checked exception
            throw new HighScoreException("Failed to load scores (class not found): " + e.getMessage(), e);
        }
    }

    /**
     * Gets the current high score, or 0 if no scores exist.
     *
     * @return the highest score recorded
     * @throws HighScoreException if loading scores fails
     */
    public int getHighScore() throws HighScoreException {
        List<PlayerScore> scores = loadScores();

        if (scores.isEmpty()) {
            return 0;
        }

        return scores.stream()
                .mapToInt(PlayerScore::getScore)
                .max()
                .orElse(0);
    }

    /**
     * Adds a new score to the high score list and saves to file.
     *
     * @param score the PlayerScore to add
     * @throws HighScoreException if saving fails
     */
    public void addScore(PlayerScore score) throws HighScoreException {
        List<PlayerScore> scores = loadScores();
        scores.add(score);
        saveScores(scores);
    }

    /**
     * Gets all scores sorted by score descending.
     *
     * @return sorted list of all scores
     * @throws HighScoreException if loading fails
     */
    public List<PlayerScore> getTopScores() throws HighScoreException {
        List<PlayerScore> scores = loadScores();
        scores.sort(Comparator.comparingInt(PlayerScore::getScore).reversed());
        return scores;
    }
}
