package kr.jbnu.se.std;

import java.awt.*;

public class BossAttack {
    public int x, y;      // 현재 위치
    public double angle;   // 공격 각도
    public int speed;      // 공격 속도
    public static final int HIT_RADIUS = 80; // 피격 범위

    public BossAttack(int startX, int startY, double angle, int speed) {
        this.x = startX;
        this.y = startY;
        this.angle = angle;
        this.speed = speed;
    }

    // 공격을 업데이트 (위치를 이동)
    public void update() {
        x += (int) (speed * Math.cos(Math.toRadians(angle)));
        y += (int) (speed * Math.sin(Math.toRadians(angle)));
    }

    // 피격 범위 확인
    public boolean isHit(Point mousePosition) {
        Rectangle hitArea = new Rectangle(x - HIT_RADIUS, y - HIT_RADIUS, HIT_RADIUS * 2, HIT_RADIUS * 2);
        return hitArea.contains(mousePosition);
    }
}