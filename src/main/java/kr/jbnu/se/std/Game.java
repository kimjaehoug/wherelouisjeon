package kr.jbnu.se.std;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Actual game.
 * 
 * @author www.gametutorial.net
 */

public class Game {

    /**
     * We use this to generate a random number.
     */
    private Random random;
    private boolean isPause = false;
    private int Round;
    private boolean isBossAlive;
    private Duck selectedDuck1 = null;
    private Duck selectedDuck2 = null;


    /**
     * Font that we will use to write statistic to the screen.
     */
    private Font font;

    private BufferedImage bossImg;

    /**
     * Array list of the ducks.
     */
    private ArrayList<Duck> ducks;
    private ArrayList<boss1> boss;

    /**
     * How many ducks leave the screen alive?
     */
    private int runawayDucks;
    private Framework framework;

    /**
     * How many ducks the player killed?
     */
    private int killedDucks;

    /**
     * For each killed duck, the player gets points.
     */
    private int score;
    private int money;

    /**
     * How many times a player is shot?
     */
    private int shoots;
    private boolean leaderboardSaved;
    /**
     * Last time of the shoot.
     */
    private long lastTimeShoot;
    /**
     * The time which must elapse between shots.
     */
    private long timeBetweenShots;

    /**
     * kr.jbnu.se.std.Game background image.
     */
    private BufferedImage backgroundImg;

    /**
     * Bottom grass.
     */
    private BufferedImage grassImg;

    /**
     * kr.jbnu.se.std.Duck image.
     */
    private BufferedImage duckImg;

    /**
     * Shotgun sight image.
     */
    private BufferedImage sightImg;

    /**
     * Middle width of the sight image.
     */
    private int sightImgMiddleWidth;
    /**
     * Middle height of the sight image.
     */
    private int sightImgMiddleHeight;
    private String gun;


    public Game(Framework framework) {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        this.framework = framework;
        Thread threadForInitGame = new Thread() {
            @Override
            public void run() {
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();

                Framework.gameState = Framework.GameState.PLAYING;
                framework.startReceivingInventory();
            }
        };
        threadForInitGame.start();
    }


    /**
     * Set variables and objects for the game.
     */
    private void Initialize() {
        random = new Random();
        font = new Font("monospaced", Font.BOLD, 18);

        ducks = new ArrayList<Duck>();
        boss = new ArrayList<boss1>();

        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;
        Round = 0;
        isBossAlive = false;

        lastTimeShoot = 0;
        timeBetweenShots = Framework.secInNanosec / 3;
    }

    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent() {
        try {
            URL backgroundImgUrl = this.getClass().getResource("/images/background.png");
            backgroundImg = ImageIO.read(backgroundImgUrl);

            URL bossImgUrl = this.getClass().getResource("/images/boss.png");
            bossImg = ImageIO.read(bossImgUrl);

            URL grassImgUrl = this.getClass().getResource("/images/grass.png");
            grassImg = ImageIO.read(grassImgUrl);

            URL duckImgUrl = this.getClass().getResource("/images/duck.png");
            duckImg = ImageIO.read(duckImgUrl);

            URL sightImgUrl = this.getClass().getResource("/images/sight.png");
            sightImg = ImageIO.read(sightImgUrl);
            sightImgMiddleWidth = sightImg.getWidth() / 2;
            sightImgMiddleHeight = sightImg.getHeight() / 2;
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // 더블배럴샷건 모드에서 두 마리 오리를 선택하는 메소드
    private void selectTwoDucks() {
        // 선택된 오리들이 이미 있으면 리턴
        if (selectedDuck1 != null) {
            return;
        }

        // 오리들이 충분히 있을 때 두 마리 오리를 무작위로 선택
        if (ducks.size() >= 1) {
            Random random = new Random();
            int index1 = random.nextInt(ducks.size());


            selectedDuck1 = ducks.get(index1);
        }
    }

    // 오리들이 죽으면 선택된 오리를 null로 설정
    private void updateSelectedDucks() {
        if (selectedDuck1 != null && !ducks.contains(selectedDuck1)) {
            selectedDuck1 = null;
        }
    }

    // 더블배럴샷건 모드에서 두 마리 오리에게 sightImg를 그리기
    private void drawSightOnSelectedDucks(Graphics2D g2d) {
        if (selectedDuck1 != null) {
            g2d.drawImage(sightImg, selectedDuck1.x, selectedDuck1.y, null);
        }
    }

    /**
     * Restart game - reset some variables.
     */
    public void RestartGame() {
        // Removes all of the ducks from this list.
        ducks.clear();

        // We set last duckt time to zero.
        Duck.lastDuckTime = 0;

        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;

        lastTimeShoot = 0;
    }

    public void Pause() {
        ducks.clear();
        isPause = true;
        Framework.gameState = Framework.GameState.Pause;
    }


    public void NextRound() {
        isPause = false;
        Framework.gameState = Framework.GameState.PLAYING;
        Duck.lastDuckTime = 0; // 오리 타이머 초기화
        killedDucks = 0; // 죽인 오리 수 초기화
        runawayDucks = 0; // 도망간 오리 수 초기화
        Round += 1;
        isBossAlive = false;

    }


    /**
     * Update game logic.
     *
     * @param gameTime      gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition) {
        if(!isPause) {
        // Creates a new duck, if it's the time, and add it to the array list.
        if (System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks) {

            if (framework.getGun().equals("더블배럴샷건")) {
                selectTwoDucks();
            }

            // 선택된 오리들이 죽었는지 확인하고, 죽으면 다시 선택
            updateSelectedDucks();
            // Here we create new duck and add it to the array list.
            ducks.add(new Duck(Duck.duckLines[Duck.nextDuckLines][0] + random.nextInt(200), Duck.duckLines[Duck.nextDuckLines][1], Duck.duckLines[Duck.nextDuckLines][2], Duck.duckLines[Duck.nextDuckLines][3], duckImg));

            // Here we increase nextDuckLines so that next duck will be created in next line.
            Duck.nextDuckLines++;
            if (Duck.nextDuckLines >= Duck.duckLines.length)
                Duck.nextDuckLines = 0;

            Duck.lastDuckTime = System.nanoTime();
            if (killedDucks >= 20 && !isBossAlive) {
                // 보스 생성
                boss.add(new boss1(1100, 500,0,3000, bossImg));
                isBossAlive = true; // 보스가 등장했음을 표시
                System.out.println("boss activity");
                ducks.clear();
            }
        }

        if(!isBossAlive) {
            // Update all of the ducks.
            for (int i = 0; i < ducks.size(); i++) {
                // Move the duck.
                ducks.get(i).Update();

                // Checks if the duck leaves the screen and remove it if it does.
                if (ducks.get(i).x < 0 - duckImg.getWidth()) {
                    ducks.remove(i);
                    runawayDucks++;
                }
            }
            // Does player shoots?
            if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
                if (System.nanoTime() - lastTimeShoot >= timeBetweenShots) {
                    shoots++;

                    // 선택된 두 마리 오리를 제거하는 로직 추가
                    if (selectedDuck1 != null) {

                        // 선택된 오리 제거 및 점수 업데이트
                        if (selectedDuck1 != null) {
                            killedDucks++;
                            money += 10;
                            score += selectedDuck1.score;
                            ducks.remove(selectedDuck1);
                            selectedDuck1 = null; // 선택된 오리 초기화
                        }

                    }
                    // We go over all the ducks and we look if any of them was shoot.
                    for (int i = 0; i < ducks.size(); i++) {
                        // We check, if the mouse was over ducks head or body, when player has shot.
                        if (new Rectangle(ducks.get(i).x + 18, ducks.get(i).y, 27, 30).contains(mousePosition) ||
                                new Rectangle(ducks.get(i).x + 30, ducks.get(i).y + 30, 88, 25).contains(mousePosition)) {
                            killedDucks++;
                            money += 10;
                            score += ducks.get(i).score;

                            // Remove the duck from the array list.
                            ducks.remove(i);

                            // We found the duck that player shoot so we can leave the for loop.
                            break;
                        }
                    }
                    // We go over all the bosses and we look if any of them was shoot.
                    // We go over all the bosses and we look if any of them was shoot.


                    lastTimeShoot = System.nanoTime();
                }
            }
        }else {
                if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
                    if (isBossAlive) {
                        for (int i = 0; i < boss.size(); i++) {
                            // Define the boss hitbox (for exampl, a larger area for the boss).
                            if (new Rectangle(boss.get(i).x, boss.get(i).y, 100, 100).contains(mousePosition)) {
                                // Reduce boss health
                                boss.get(i).health -= 20; // Reduce boss health by 20 on each hit.
                                System.out.println("attack boss");
                                // If the boss is dead, update score, money, etc.
                                if (boss.get(i).health <= 0) {
                                    money += 100; // Bosses give more money
                                    score += boss.get(i).score; // Boss-specific score
                                    Pause();
                                    // Remove the boss from the array list.
                                    boss.remove(i);
                                }

                                // Since a boss was hit, we can leave the loop.
                                break;
                            }
                        }
                    }
                }
            }


        // When 200 ducks runaway, the game ends.
        if (runawayDucks >= 10)
            Framework.gameState = Framework.GameState.GAMEOVER;
        if (Framework.gameState == Framework.GameState.GAMEOVER && !leaderboardSaved) {
            framework.saveScore(score);
            leaderboardSaved = true;  // 리더보드 저장 완료
        }
    }else{
            return;
        }
}
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        
        // Here we draw all the ducks.
        for(int i = 0; i < ducks.size(); i++)
        {
            ducks.get(i).Draw(g2d);
        }

        // 보스 그리기
        if (!boss.isEmpty()) {
            for (int i = 0; i < boss.size(); i++) {
                boss.get(i).Draw(g2d);
            }
        }

        
        g2d.drawImage(grassImg, 0, Framework.frameHeight - grassImg.getHeight(), Framework.frameWidth, grassImg.getHeight(), null);
        
        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);
        // 더블배럴샷건일 때 랜덤 오리 2마리 지정
        if (framework.getGun().equals("더블배럴샷건")) {
            drawSightOnSelectedDucks(g2d);
        }

        g2d.setFont(font);
        g2d.setColor(Color.darkGray);
        
        g2d.drawString("RUNAWAY: " + runawayDucks, 10, 21);
        g2d.drawString("KILLS: " + killedDucks, 160, 21);
        g2d.drawString("SHOOTS: " + shoots, 299, 21);
        g2d.drawString("SCORE: " + score, 440, 21);
        g2d.drawString("Round: " + Round, 570, 21);
        g2d.drawString("Money: " + money, 700, 21);

    }
    
    
    /**
     * Draw the game over screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition Current mouse position.
     */
    public void DrawGameOver(Graphics2D g2d, Point mousePosition)
    {
        Draw(g2d, mousePosition);
        
        // The first text is used for shade.
        g2d.setColor(Color.black);
        g2d.drawString("kr.jbnu.se.std.Game Over", Framework.frameWidth / 2 - 39, (int)(Framework.frameHeight * 0.65) + 1);
        g2d.drawString("Press space or enter to restart.", Framework.frameWidth / 2 - 149, (int)(Framework.frameHeight * 0.70) + 1);
        g2d.setColor(Color.red);
        g2d.drawString("kr.jbnu.se.std.Game Over", Framework.frameWidth / 2 - 40, (int)(Framework.frameHeight * 0.65));
        g2d.drawString("Press space or enter to restart.", Framework.frameWidth / 2 - 150, (int)(Framework.frameHeight * 0.70));
    }

    public int getScore(){
        return score;
    }

    public void setgun(String gun){
        this.gun = gun;
    }
}
