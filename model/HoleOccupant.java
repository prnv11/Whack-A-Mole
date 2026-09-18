package model;

import javax.swing.ImageIcon;

/**
 * Abstract base class for all objects that can appear from a hole. Demonstrates
 * abstraction by defining common state and behavior while forcing subclasses to
 * implement specific functionality.
 */
public abstract class HoleOccupant {

    protected boolean visible;
    protected int timeRemaining;
    protected static final int DEFAULT_LIFETIME = 3;

    public HoleOccupant() {
        this.visible = true;
        this.timeRemaining = DEFAULT_LIFETIME;
    }

    /**
     * Abstract method - returns score change when whacked. Positive for moles,
     * negative for bombs.
     */
    public abstract int whack();

    /**
     * Abstract method - returns the visual representation.
     */
    public abstract ImageIcon getImage();

    /**
     * Abstract method - returns the occupant type name.
     */
    public abstract String getTypeName();

    /**
     * Concrete method - hides the occupant.
     */
    public void hide() {
        this.visible = false;
    }

    /**
     * Concrete method - decrements lifetime, returns true if expired.
     */
    public boolean tick() {
        timeRemaining--;
        return timeRemaining <= 0;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }
}