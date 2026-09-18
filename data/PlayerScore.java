package data;

import java.io.Serializable;

/**
 * POJO class for storing player score data.
 * Implements Serializable for file persistence using ObjectOutputStream/ObjectInputStream.
 */
public class PlayerScore implements Serializable {
    
    // Required for serialization version control
    private static final long serialVersionUID = 1L;
    
    private String playerName;
    private int score;
    private long timestamp;
    
    /**
     * Constructs a new PlayerScore with the given name and score.
     * Timestamp is automatically set to current time.
     * @param playerName the name of the player
     * @param score the player's final score
     */
    public PlayerScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Gets the player's name.
     * @return the player name
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Gets the player's score.
     * @return the score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Gets the timestamp when this score was recorded.
     * @return the timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return playerName + ": " + score;
    }
}