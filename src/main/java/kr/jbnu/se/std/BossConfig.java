package kr.jbnu.se.std;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

public enum BossConfig {
    FIRST(1200, 400, 0, 1000, 200, "/images/boss.png"),
    SECOND(1200, 400, 0, 1500, 400, "/images/boss_crocs.png"),
    THIRD(1200, 400, 0, 2000, 2500, "/images/boss_hippo.png"),
    FOURTH(1200, 400, 0, 3000, 6400, "/images/boss_dugong.png"),
    FIFTH(1200, 400, 0, 4000, 12000, "/images/duck_boss1.png");
    private static final Logger logger = Logger.getLogger(BossConfig.class.getName());
    private final int x;
    private final int y;
    private final double speed;
    private final int score;
    private final int health;
    private final BufferedImage bossImg;

    BossConfig(int x, int y, double speed, int score, int health, String path) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.score = score;
        this.health = health;
        this.bossImg = loadImage(path);
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResource(path));
        } catch (IOException | IllegalArgumentException e) {
            logger.warning(e.getMessage());
            return null; // 이미지 로드 실패 시 null 반환
        }
    }

    public static boss1 getInstance(BossConfig config) {
        return new boss1(config.x, config.y, config.speed, config.score, config.health, config.bossImg);
    }
}
