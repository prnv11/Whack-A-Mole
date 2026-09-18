package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Bomb - gives negative points when whacked. Extends HoleOccupant to
 * demonstrate polymorphism.
 */
public class Bomb extends HoleOccupant {

    private static final int SCORE_VALUE = -500;
    private ImageIcon image;

    public Bomb() {
        super();
        this.timeRemaining = 4; // Bombs stay longer than moles
        createImage();
    }

    private void createImage() {
        BufferedImage img = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Bomb body (black sphere)
        g.setColor(Color.BLACK);
        g.fillOval(10, 15, 40, 40);

        // Shine highlight
        g.setColor(new Color(60, 60, 60));
        g.fillOval(15, 20, 12, 12);

        // Fuse
        g.setColor(new Color(139, 69, 19));
        g.fillRect(28, 5, 4, 15);

        // Spark/flame
        g.setColor(Color.ORANGE);
        g.fillOval(24, 0, 12, 12);
        g.setColor(Color.YELLOW);
        g.fillOval(26, 2, 8, 8);
        g.setColor(Color.RED);
        g.fillOval(28, 4, 4, 4);

        // Skull face on bomb
        g.setColor(Color.WHITE);
        g.fillOval(20, 25, 8, 8);  // Left eye
        g.fillOval(32, 25, 8, 8);  // Right eye
        g.fillOval(25, 40, 10, 6); // Mouth area

        // Eye sockets (black)
        g.setColor(Color.BLACK);
        g.fillOval(22, 27, 4, 4);
        g.fillOval(34, 27, 4, 4);

        g.dispose();
        image = new ImageIcon(img);
    }

    @Override
    public int whack() {
        hide();
        return SCORE_VALUE;
    }

    @Override
    public ImageIcon getImage() {
        return image;
    }

    @Override
    public String getTypeName() {
        return "Bomb";
    }
}