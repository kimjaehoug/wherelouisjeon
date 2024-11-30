package kr.jbnu.se.std;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * The duck class.
 *
 * @author www.gametutorial.net
 */

public class Duck implements Serializable {

    /**
     * How much time must pass in order to create a new duck?
     */
    public final static long timeBetweenDucks = Framework.SECINNANOSEC / 2;

    private static final long serialVersionUID = 1L;
    /**
     * Last time when the duck was created.
     */
    public static long lastDuckTime = 0;

    public synchronized static void setLastDuckTime(long time) {
        lastDuckTime = time;
    }

    public synchronized static long getLastDuckTime() {
        return lastDuckTime;
    }

    /**
     * kr.jbnu.se.std.Duck lines.
     * Where is starting location for the duck?
     * Speed of the duck?
     * How many points is a duck worth?
     */
    public final static int[][] duckLines = {
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.60), -2, 20},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.65), -3, 30},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.70), -4, 40},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.78), -5, 50}
    };
    /**
     * Indicate which is next duck line.
     */
    public static int nextDuckLines = 0;

    public synchronized static void incrementNextDuckLines() {
        nextDuckLines++;
    }

    public synchronized static void resetNextDuckLines() {
        nextDuckLines = 0;
    }

    public synchronized static int getNextDuckLines() {
        return nextDuckLines;
    }


    /**
     * X coordinate of the duck.
     */
    public int x;
    /**
     * Y coordinate of the duck.
     */
    public final int y;

    /**
     * How fast the duck should move? And to which direction?
     */
    private int speed;

    /**
     * How many points this duck is worth?
     */
    public final int score;

    /**
     * kr.jbnu.se.std.Duck image.
     */
    private final BufferedImage duckImg;

    private boolean isAlive;




    /**
     * Creates new duck.
     *
     * @param x Starting x coordinate.
     * @param y Starting y coordinate.
     * @param speed The speed of this duck.
     * @param score How many points this duck is worth?
     * @param duckImg Image of the duck.
     */
    public Duck(int x, int y, int speed, int score, BufferedImage duckImg)
    {
        this.x = x;
        this.y = y;

        this.speed = speed;

        this.score = score;

        this.duckImg = duckImg;
        this.isAlive = true;
    }


    /**
     * Move the duck.
     */
    public void Update()
    {
        x += speed;
    }

    /**
     * Draw the duck to the screen.
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    {
        g2d.drawImage(duckImg, x, y, null);
    }

    public Image getImage() {
        return duckImg;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void shoot() {
        this.isAlive = false;
    }

    // 속도를 변경하는 메서드
    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    // 현재 속도를 반환하는 메서드 (필요한 경우)
    public int getSpeed() {
        return this.speed;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
