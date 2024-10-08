package kr.jbnu.se.std;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The duck class.
 *
 * @author www.gametutorial.net
 */

public class boss1 {

    /**
     * How much time must pass in order to create a new duck?
     */
    public static long timeBetweenboss = Framework.secInNanosec / 2;
    /**
     * Last time when the duck was created.
     */
    public static long lastbossTime = 0;

    /**
     * kr.jbnu.se.std.Duck lines.
     * Where is starting location for the duck?
     * Speed of the duck?
     * How many points is a duck worth?
     */
    public static int[][] bossLines = {
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
    private int speed;

    /**
     * How many points this duck is worth?
     */
    public int score;

    /**
     * kr.jbnu.se.std.Duck image.
     */
    BufferedImage bossImg;
    public int health;


    /**
     * Creates new duck.
     *
     * @param x Starting x coordinate.
     * @param y Starting y coordinate.
     * @param speed The speed of this duck.
     * @param score How many points this duck is worth?
     * @param bossImg Image of the boss.
     */
    public boss1(int x, int y, int speed, int score, BufferedImage bossImg)
    {
        this.x = x;
        this.y = y;

        this.speed = speed;

        this.score = score;

        this.bossImg = bossImg;
        this.health = 500;
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
        g2d.drawImage(bossImg, x, y, null);
    }

    // 보스가 피해를 입었을 때 체력을 감소시킴
    public void takeDamage(int damage) {
        this.health -= damage;
    }
    // 보스가 죽었는지 확인
    public boolean isDead() {
        return health <= 0;
    }
}
