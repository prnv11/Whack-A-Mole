package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Standard mole - gives positive points when whacked. Extends HoleOccupant to
 * demonstrate polymorphism.
 */
public class Mole extends HoleOccupant {

    private static final int SCORE_VALUE = 100;
    private ImageIcon image;

    public Mole() {
        super();
        createImage();
    }

    private void createImage() {
        BufferedImage img = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Brown mole body
        g.setColor(new Color(139, 90, 43));
        g.fillOval(5, 10, 50, 45);

        // Face (lighter brown)
        g.setColor(new Color(210, 180, 140));
        g.fillOval(15, 20, 30, 25);

        // Eyes
        g.setColor(Color.BLACK);
        g.fillOval(20, 25, 8, 8);
        g.fillOval(32, 25, 8, 8);

        // Eye highlights
        g.setColor(Color.WHITE);
        g.fillOval(22, 26, 3, 3);
        g.fillOval(34, 26, 3, 3);

        // Nose
        g.setColor(new Color(255, 150, 150));
        g.fillOval(26, 35, 8, 6);

        // Hat (green cap)
        g.setColor(new Color(34, 139, 34));
        g.fillArc(10, 5, 40, 30, 0, 180);

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
        return "Mole";
    }
}