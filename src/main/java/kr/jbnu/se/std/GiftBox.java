package kr.jbnu.se.std;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GiftBox {
    int x, y;
    int width, height;
    int fallSpeed;
    int type; // 선물 상자의 종류 (1, 2, 3)
    BufferedImage giftBoxImg;

    public GiftBox(int x, int y, int width, int height, int fallSpeed, int type, BufferedImage giftBoxImg) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fallSpeed = fallSpeed;
        this.type = type;
        this.giftBoxImg = giftBoxImg;
    }

    public void update() {
        // 상자가 아래로 떨어지도록 y 좌표를 fallSpeed만큼 증가시킴
        y += fallSpeed;
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(giftBoxImg, x, y, width, height, null);
    }

    public boolean isCollidingWithPlayer(Rectangle playerHitBox) {
        Rectangle giftBoxRect = new Rectangle(x, y, width, height);
        return giftBoxRect.intersects(playerHitBox);
    }
}
