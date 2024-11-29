package kr.jbnu.se.std;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The duck class.
 *
 * @author www.gametutorial.net
 */

public class Buttonbuy {

    /**
     * How much time must pass in order to create a new duck?
     */
    public static long timeBetweenButton = Framework.SECINNANOSEC / 2;
    /**
     * Last time when the duck was created.
     */
    public static long lastButtonTime = 0;

    /**
     * kr.jbnu.se.std.Duck lines.
     * Where is starting location for the duck?
     * Speed of the duck?
     * How many points is a duck worth?
     */
    public static int[][] ButtonLines = {
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.60), -2, 20},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.65), -3, 30},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.70), -4, 40},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.78), -5, 50}
    };
    /**
     * Indicate which is next duck line.
     */
    public static int nextbossLines = 0;


    /**
     * X coordinate of the duck.
     */
    public int x;
    /**
     * Y coordinate of the duck.
     */
    public int y;
    public int width;
    public int height;

    /**
     * How fast the duck should move? And to which direction?
     */
    private double speed;

    /**
     * How many points this duck is worth?
     */

    /**
     * kr.jbnu.se.std.Duck image.
     */
    BufferedImage ButtonImg;


    /**
     * Creates new duck.
     *
     * @param x Starting x coordinate.
     * @param y Starting y coordinate.
     */
    public Buttonbuy(int x, int y, BufferedImage ButtonImg)
    {
        this.x = x;
        this.y = y;

        this.ButtonImg = ButtonImg;
    }


    /**
     * Move the duck.
     */
    public void update()
    {
        x -= speed;
    }

    /**
     * Draw the duck to the screen.
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    {
        g2d.drawImage(ButtonImg, x, y, null);
    }
}
