package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Bonus mole - gives high points and bonus time when whacked. Extends
 * HoleOccupant to demonstrate polymorphism.
 */
public class BonusMole extends HoleOccupant {

    private static final int SCORE_VALUE = 500;
    private static final int TIME_BONUS = 5; // seconds added to game clock
    private ImageIcon image;

    public BonusMole() {
        super();
        this.timeRemaining = 6; // 6 ticks = 1.5 seconds (harder to catch)
        createImage();
    }

    private void createImage() {
        BufferedImage img = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Golden mole body
        g.setColor(new Color(255, 215, 0));
        g.fillOval(5, 15, 50, 40);

        // Face (light golden)
        g.setColor(new Color(255, 250, 205));
        g.fillOval(15, 22, 30, 25);

        // Happy eyes
        g.setColor(new Color(255, 100, 100));
        g.fillOval(20, 27, 8, 8);
        g.fillOval(32, 27, 8, 8);

        // Eye sparkles
        g.setColor(Color.WHITE);
        g.fillOval(22, 28, 3, 3);
        g.fillOval(34, 28, 3, 3);

        // Smile
        g.setColor(new Color(200, 100, 100));
        g.drawArc(23, 35, 14, 8, 180, 180);

        // Crown
        g.setColor(new Color(255, 215, 0));
        g.fillRect(15, 5, 30, 12);

        // Crown points
        g.fillPolygon(new int[]{15, 20, 25}, new int[]{5, 0, 5}, 3);
        g.fillPolygon(new int[]{25, 30, 35}, new int[]{5, 0, 5}, 3);
        g.fillPolygon(new int[]{35, 40, 45}, new int[]{5, 0, 5}, 3);

        // Gems on crown
        g.setColor(Color.RED);
        g.fillOval(18, 8, 4, 4);
        g.setColor(Color.BLUE);
        g.fillOval(28, 8, 4, 4);
        g.setColor(Color.GREEN);
        g.fillOval(38, 8, 4, 4);

        g.dispose();
        image = new ImageIcon(img);
    }

    @Override
    public int whack() {
        hide();
        return SCORE_VALUE;
    }

    /**
     * Returns the time bonus awarded when this mole is whacked.
     */
    public int getTimeBonus() {
        return TIME_BONUS;
    }

    @Override
    public ImageIcon getImage() {
        return image;
    }

    @Override
    public String getTypeName() {
        return "BonusMole";
    }
}