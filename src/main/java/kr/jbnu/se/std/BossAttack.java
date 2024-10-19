package kr.jbnu.se.std;

import java.awt.*;

public class BossAttack {
    private double deltaTime;
    private double gravity;
    private double vy;
    private double vx;
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

    public BossAttack(int x, int y, double vx, double vy, double gravity, double deltaTime) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.gravity = gravity;
        this.deltaTime = deltaTime;
    }

    // 공격을 업데이트 (위치를 이동)
    public void update() {
        x += (int) (speed * Math.cos(Math.toRadians(angle)));
        y += (int) (speed * Math.sin(Math.toRadians(angle)));
    }

    public void updatewithgravity(){
        // Update position based on velocity
        // 수평 위치 업데이트
        x += vx * deltaTime;

        // 중력을 수직 속도에 적용 (아래로 떨어지게 하기 위해)
        vy += gravity * deltaTime;  // 중력을 더해 아래로 가속

        // 수직 위치 업데이트
        y += vy * deltaTime;
    }

    // 피격 범위 확인
    public boolean isHit(Point mousePosition) {
        Rectangle hitArea = new Rectangle(x - HIT_RADIUS, y - HIT_RADIUS, HIT_RADIUS * 2, HIT_RADIUS * 2);
        return hitArea.contains(mousePosition);
    }
}
