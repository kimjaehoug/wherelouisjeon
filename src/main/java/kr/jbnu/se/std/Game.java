package kr.jbnu.se.std;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;

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
    private Clip clip;
    private boolean isPause = false;
    private int Round;
    private boolean isBossAlive;
    private Duck[] hunterSelectedDucks;
    private Duck[] playerSelectedDucks;
    private int ammo;          // 현재 사용 가능한 총알
    private int maxAmmo;       // 한 번에 장전할 수 있는 최대 탄약 수
    private boolean isReloading; // 장전 중인지 여부
    private long reloadStartTime; // 장전이 시작된 시간
    private long reloadDuration;  // 장전 시간 (예: 2초)
    private URL hpUrl;
    private int selectduck;
    private ScheduledExecutorService hunterExecutor;
    private int PlayerHp;

    /**
     * Font that we will use to write statistic to the screen.
     */
    private Font font;
    boolean hunterTrigger = true;

    private BufferedImage bossImg;
    private BufferedImage bossAttack;
    private BufferedImage[] hpImages = new BufferedImage[12]; // HP 이미지를 저장할 배열

    /**
     * Array list of the ducks.
     */
    private ArrayList<Duck> ducks;
    private ArrayList<boss1> boss;
    private ArrayList<Buttonbuy> buttonbuy;
    private long lastBossAttackTime = 0;  // 마지막 공격 시간
    private final long bossAttackInterval = 3000;  // 공격 간격 (3초)
    private ArrayList<Hunter1> Hunters;

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
    private BufferedImage buttonImg;
    private BufferedImage sightImg_hunter;

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
    private boolean Hunter1 = false;

    /**
     * Middle width of the sight image.
     */
    private int sightImgMiddleWidth;
    /**
     * Middle height of the sight image.
     */
    private int sightImgMiddleHeight;
    private String gun;
    private List<BossAttack> bossAttacks = new ArrayList<>();




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
        buttonbuy = new ArrayList<Buttonbuy>();
        Hunters = new ArrayList<Hunter1>();

        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;
        PlayerHp = 100;
        Round = 1;
        isBossAlive = false;
        Hunter1 = false;
        lastTimeShoot = 0;
        timeBetweenShots = 500_000_000L;

        ammo = 6;              // 기본 탄약 수
        maxAmmo = 6;           // 최대 장전할 수 있는 탄약 수
        isReloading = false;   // 초기에는 장전 중이 아님
        reloadDuration = 2000000000L; // 장전 시간 2초 (나노초 단위)
    }

    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent() {
        try {
            for (int i = 0; i < 12; i++) { // 0부터 11까지 반복
                try {
                    // 이미지 경로를 생성
                    URL hpUrl = this.getClass().getResource("/images/hp_" + i + ".png");

                    // URL이 null이 아닐 경우에만 이미지 읽기
                    if (hpUrl != null) {
                        hpImages[i] = ImageIO.read(hpUrl);
                    } else {
                        System.out.println("Image not found: /images/hp_" + i + ".png");
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // IOException 처리
                }
            }

            URL Buttonimg = this.getClass().getResource("/images/btn_buy.png");
            buttonImg = ImageIO.read(Buttonimg);

            URL backgroundImgUrl = this.getClass().getResource("/images/background.png");
            backgroundImg = ImageIO.read(backgroundImgUrl);

            URL bossImgUrl = this.getClass().getResource("/images/duck_boss1.png");
            bossImg = ImageIO.read(bossImgUrl);

            URL grassImgUrl = this.getClass().getResource("/images/grass.png");
            grassImg = ImageIO.read(grassImgUrl);

            URL sight_hunterURL = this.getClass().getResource("/images/sight_hunter.png");
            sightImg_hunter = ImageIO.read(sight_hunterURL);

            URL duckImgUrl = this.getClass().getResource("/images/duck.png");
            duckImg = ImageIO.read(duckImgUrl);

            URL sightImgUrl = this.getClass().getResource("/images/sight.png");
            sightImg = ImageIO.read(sightImgUrl);
            sightImgMiddleWidth = sightImg.getWidth() / 2;
            sightImgMiddleHeight = sightImg.getHeight() / 2;

            URL bossAttackImage = this.getClass().getResource("/images/skull.png");
            bossAttack = ImageIO.read(bossAttackImage);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectPlayerDucks(int numberOfDucks) {
        // 선택된 오리들이 이미 있으면 리턴
        if (playerSelectedDucks != null) {
            return;
        }

        // 오리들이 충분히 있을 때 N마리 오리를 무작위로 선택
        if (ducks.size() >= numberOfDucks) {
            playerSelectedDucks = new Duck[numberOfDucks]; // Player 선택된 오리 배열 초기화
            Random random = new Random();

            for (int i = 0; i < numberOfDucks; i++) {
                Duck selectedDuck;
                int index;

                // 중복되지 않는 오리를 선택
                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(playerSelectedDucks).contains(selectedDuck) ||
                        (hunterSelectedDucks != null && Arrays.asList(hunterSelectedDucks).contains(selectedDuck))); // hunterSelectedDucks가 null일 경우 중복 방지 생략

                playerSelectedDucks[i] = selectedDuck;
            }
        }
    }



    // N마리 오리를 선택하는 메소드 (Hunter용)
    private void selectHunterDucks(int numberOfDucks) {
        if (ducks.size() >= numberOfDucks) {
            hunterSelectedDucks = new Duck[numberOfDucks]; // Hunter 선택된 오리 배열 초기화
            Random random = new Random();

            for (int i = 0; i < numberOfDucks; i++) {
                Duck selectedDuck;
                int index;

                // 중복되지 않는 오리를 선택
                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(hunterSelectedDucks).contains(selectedDuck) || Arrays.asList(playerSelectedDucks).contains(selectedDuck)); // 중복 방지

                hunterSelectedDucks[i] = selectedDuck;
            }
        }
    }

    // Hunter가 자동으로 오리를 제거하는 메소드
    private void startHunterAutoKill(int interval) {
        hunterExecutor = Executors.newScheduledThreadPool(1); // 스레드 풀 생성
        hunterExecutor.scheduleAtFixedRate(() -> {
            if (hunterSelectedDucks == null || Arrays.stream(hunterSelectedDucks).allMatch(Objects::isNull)) {
                // Hunter가 선택한 오리가 없으면 새롭게 선택
                selectHunterDucks(1);
            }
            if (hunterSelectedDucks != null) {
                for (Duck duck : hunterSelectedDucks) {
                    if (duck != null && !Arrays.asList(playerSelectedDucks).contains(duck)) {
                        // Hunter가 선택한 오리가 Player가 선택한 오리와 중복되지 않도록 확인
                        ducks.remove(duck);
                        killedDucks++;
                        money += 10;
                        score += duck.score;
                        System.out.println("Hunter가 오리를 죽였습니다: " + duck);
                        break; // 한 마리씩 죽이고 나가도록

                    }
                }
                updateHunterSelectedDucks();
            }
        }, 0, interval, TimeUnit.MILLISECONDS); // interval 시간마다 실행
    }


    // 게임이 끝나면 Hunter의 자동조준 타이머를 중지하는 코드
    private void stopHunterAutoKill() {
        if (hunterExecutor != null && !hunterExecutor.isShutdown()) {
            hunterExecutor.shutdown(); // Hunter의 자동 조준 종료
        }
    }


    private void updateAndReselectPlayerDucks(int numberOfDucks) {
        // playerSelectedDucks가 null일 때만 새로 선택
        playerSelectedDucks = null;
        if (playerSelectedDucks == null) {
            selectPlayerDucks(numberOfDucks);
        }
    }

    // 오리들이 죽으면 Hunter 선택된 오리를 null로 설정
    private void updateHunterSelectedDucks() {
        Random random = new Random();

        for (int i = 0; i < hunterSelectedDucks.length; i++) {
            if (hunterSelectedDucks[i] == null || !ducks.contains(hunterSelectedDucks[i])) {
                // 새로운 오리를 선택하여 중복되지 않게 추가
                Duck selectedDuck;
                int index;

                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(hunterSelectedDucks).contains(selectedDuck) ||
                        Arrays.asList(playerSelectedDucks).contains(selectedDuck)); // 중복 방지

                hunterSelectedDucks[i] = selectedDuck;
            }
        }
    }

    // 더블배럴샷건 모드에서 Player 선택된 오리들에게 sightImg를 그리기
    private void drawSightOnPlayerSelectedDucks(Graphics2D g2d) {
        if (playerSelectedDucks != null) {
            for (Duck duck : playerSelectedDucks) {
                if (duck != null) {
                    g2d.drawImage(sightImg, duck.x, duck.y, null);
                }
            }
        }else{
            return;
        }
    }

    // 더블배럴샷건 모드에서 Hunter 선택된 오리들에게 sightImg를 그리기
    private void drawSightOnHunterSelectedDucks(Graphics2D g2d) {
        if (hunterSelectedDucks != null) {
            for (Duck duck : hunterSelectedDucks) {
                if (duck != null) {
                    g2d.drawImage(sightImg_hunter, duck.x, duck.y,28,28,null);
                }else{
                    return;
                }
            }
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
        System.out.println("buttonbuyadd");
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

    public void playActiveSound(String filePath){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }catch(UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    /**
     * Update game logic.
     *
     * @param gameTime      gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition) {
        if (isBossAlive) {
            for (int i = 0; i < boss.size(); i++) {
                boss.get(i).update(); // 보스 위치 업데이트

                // 일정 시간마다 공격 발사 (보스 공격 간격 체크)
                if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 1_000_000) {
                    double angle = 150 + Math.random() * 70;
                    double angle2 = 150 + Math.random() * 70;
                    double angle3 = 150 + Math.random() * 70;// 0에서 360도 사이의 랜덤 각도
                    bossAttacks.add(new BossAttack(boss.get(i).x, boss.get(i).y, angle, 15));
                    bossAttacks.add(new BossAttack(boss.get(i).x, boss.get(i).y, angle2, 15));
                    bossAttacks.add(new BossAttack(boss.get(i).x, boss.get(i).y, angle3, 15));// 속도 10으로 설정
                    lastBossAttackTime = System.nanoTime(); // 마지막 공격 시간 갱신
                }
            }

            // 보스 공격 업데이트 및 피격 체크
            for (int i = 0; i < bossAttacks.size(); i++) {
                BossAttack attack = bossAttacks.get(i);
                attack.update(); // 공격 위치 업데이트

                // 피격 범위 확인
                if (attack.isHit(mousePosition)) {
                    System.out.println("Player hit! Remaining health: ");
                    bossAttacks.remove(i);// 공격이 맞았으므로 제거
                    PlayerHp -= 10;
                    i--; // 인덱스 조정
                }

                // 화면 밖으로 나간 공격은 제거
                if (attack.x < 0 || attack.x > framework.getWidth() || attack.y < 0 || attack.y > framework.getHeight()) {
                    bossAttacks.remove(i);
                    i--; // 인덱스 조정
                }
            }
        }


        if(Hunter1&& hunterTrigger){
            startHunterAutoKill(2500);
            hunterTrigger = false;
        }else if(!Hunter1){
            stopHunterAutoKill();
        }else if(!hunterTrigger){

        }
        if(!isPause) {
        // Creates a new duck, if it's the time, and add it to the array list.
        if (System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks) {

            if (isReloading) {
                if (System.nanoTime() - reloadStartTime >= reloadDuration) {
                    ammo = maxAmmo;   // 탄약을 최대치로 채움
                    isReloading = false; // 장전 상태 해제
                }
            }
            if(ammo<=0 && !isReloading){
                Reload();
            }

            if (framework.getGun().equals("더블배럴샷건")) {
                selectPlayerDucks(1);
                // 선택된 오리들이 죽었는지 확인하고, 죽으면 다시 선택
            }else if(framework.getGun().equals("기본권총")){

            }
            // Here we create new duck and add it to the array list.
            ducks.add(new Duck(Duck.duckLines[Duck.nextDuckLines][0] + random.nextInt(200), Duck.duckLines[Duck.nextDuckLines][1], Duck.duckLines[Duck.nextDuckLines][2], Duck.duckLines[Duck.nextDuckLines][3], duckImg));

            // Here we increase nextDuckLines so that next duck will be created in next line.
            Duck.nextDuckLines++;
            if (Duck.nextDuckLines >= Duck.duckLines.length)
                Duck.nextDuckLines = 0;

            if (killedDucks >= 20 && !isBossAlive) {
                // 보스 생성
                boss.add(new boss1(1300, 500,0,3000, bossImg));
                isBossAlive = true; // 보스가 등장했음을 표시
                System.out.println("boss activity");
                ducks.clear();
            }
            Duck.lastDuckTime = System.nanoTime();
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
            if (Canvas.mouseButtonState(MouseEvent.BUTTON1) && !isReloading) {
                if (System.nanoTime() - lastTimeShoot >= timeBetweenShots) {
                    shoots++;
                    playActiveSound("src/main/resources/sounds/gun.wav");
                    ammo--;
                    if (playerSelectedDucks != null) {
                        for (int i = 0; i < playerSelectedDucks.length; i++) {
                            if (playerSelectedDucks[i] != null) {
                                playActiveSound("src/main/resources/sounds/quack.wav");
                                killedDucks++; // 죽인 오리 수 증가
                                money += 10; // 돈 증가
                                score += playerSelectedDucks[i].score; // 점수 증가
                                // 오리 리스트에서 제거
                                ducks.remove(playerSelectedDucks[i]);

                                // 선택된 오리를 null로 설정하여 초기화
                                playerSelectedDucks[i] = null;
                                updateAndReselectPlayerDucks(1);
                            }
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
                            playActiveSound("src/main/resources/sounds/quack.wav");

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

            if (Canvas.mouseButtonState(MouseEvent.BUTTON1) && !isReloading) {
                if (System.nanoTime() - lastTimeShoot >= timeBetweenShots) {
                    shoots++;
                    ammo--;
                    if (isBossAlive) {
                        for (int i = 0; i < boss.size(); i++) {
                            // Define the boss hitbox (for exampl, a larger area for the boss).
                            // 보스가 랜덤 각도로 공격 발사
                            if (new Rectangle(boss.get(i).x, boss.get(i).y, 378, 268).contains(mousePosition)) {
                                // Reduce boss health
                                playActiveSound("src/main/resources/sounds/gun.wav");
                                boss.get(i).health -= 20; // Reduce boss health by 20 on each hit.
                                System.out.println("attack boss");
                                System.out.println(boss.get(i).health);
                                // If the boss is dead, update score, money, etc.
                                if (boss.get(i).health <= 0) {
                                    money += 100; // Bosses give more money
                                    score += boss.get(i).score; // Boss-specific score
                                    boss.remove(i);
                                    Pause();
                                    // Remove the boss from the array list.
                                }
                                break;
                                // Since a boss was hit, we can leave the loop.
                            }
                        }
                    }
                }
                lastTimeShoot = System.nanoTime();
            }
        }

        // When 200 ducks runaway, the game ends.
            if (runawayDucks >= 10)
                Framework.gameState = Framework.GameState.GAMEOVER;
            if (Framework.gameState == Framework.GameState.GAMEOVER && !leaderboardSaved) {
                framework.saveScore(score);
                leaderboardSaved = true;  // 리더보드 저장 완료
            }
        }
        if(isPause) {
            System.out.println("isPause");
            buttonbuy.add(new Buttonbuy(framework.getWidth()/2 - 300, framework.getHeight()/2+50, buttonImg));
                for (int i = 0; i < buttonbuy.size(); i++) {
                    if (Canvas.mouseButtonState(MouseEvent.BUTTON1) && money > 200) {
                        if (new Rectangle(buttonbuy.get(i).x, buttonbuy.get(i).y, 367, 257).contains(mousePosition)) {
                            System.out.println("buybutton");
                            System.out.println(mousePosition);
                            System.out.println(buttonbuy.get(i).x + " " + buttonbuy.get(i).y);
                            Hunters.add(new Hunter1(220, 110, 0, 100, duckImg));
                            Hunter1 = true;
                            money -= 200;
                    }
                }
            }
        }
}

    public void drawBossAttack(Graphics2D g2d){
        for(int i = 0; i < bossAttacks.size(); i++) {
            g2d.drawImage(bossAttack, bossAttacks.get(i).x,bossAttacks.get(i).y, null );
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

        if(Hunter1){
            g2d.drawImage(bossImg,Hunters.get(0).x,Hunters.get(0).y,null);
            drawSightOnHunterSelectedDucks(g2d);

        }

        if(bossAttacks.size() > 0){
            for(int i = 0; i < bossAttacks.size(); i++) {
                g2d.drawImage(bossAttack, bossAttacks.get(i).x,bossAttacks.get(i).y, null );
            }
        }

        if(isPause){
            int buyWidth = buttonImg.getWidth(null) / 2; // 너비 50%
            int buyHeight = buttonImg.getHeight(null) / 2; // 높이 50%
            for(int i = 0; i < buttonbuy.size(); i++) {
                g2d.drawImage(buttonImg, buttonbuy.get(i).x, buttonbuy.get(i).y,buyWidth,buyHeight,null);
            }
        }
        // 보스 그리기
        if (!boss.isEmpty()) {
            for (int i = 0; i < boss.size(); i++) {
                // 보스 이미지 그리기
                g2d.drawImage(bossImg, boss.get(i).x - 90, boss.get(i).y - 20,378,268,null);

                // 보스의 체력 상태를 기반으로 HP 이미지를 선택
                int currentHealth = boss.get(i).health;
                int maxHealth = boss.get(i).maxHealth; // 보스의 최대 체력

                // 체력에 따른 HP 이미지를 표시
                int hpIndex = (int) ((currentHealth / (double) maxHealth) * 11); // 0에서 11까지의 인덱스를 계산
                hpIndex = Math.max(0, Math.min(11, hpIndex)); // 범위를 0 ~ 11로 제한
                // HP 바 크기 조정 (예: 50% 크기)
                int hpBarWidth = hpImages[hpIndex].getWidth(null) / 8; // 너비 50%
                int hpBarHeight = hpImages[hpIndex].getHeight(null) / 8; // 높이 50%

                // HP 바 그리기 (크기 조정 후)
                g2d.drawImage(hpImages[hpIndex], boss.get(i).x - 20, boss.get(i).y - 60, hpBarWidth, hpBarHeight, null);
            }
        }

        if(isReloading){
            g2d.drawString("Reloading", Framework.frameWidth/2, Framework.frameHeight/2);
        }

        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);
        // 더블배럴샷건일 때 랜덤 오리 2마리 지정
        if (framework.getGun().equals("더블배럴샷건")) {
            drawSightOnPlayerSelectedDucks(g2d);
            reloadDuration = 2500000000L;
        }
        if(framework.getGun().equals("AK-47")){
            maxAmmo = 30;
            reloadDuration = 3000000000L;
            timeBetweenShots = 100_000_000L;
        }

        g2d.setFont(font);
        g2d.setColor(Color.darkGray);

        g2d.drawString("Ammo: " + ammo + "/" + maxAmmo, 10, 50);
        g2d.drawString("RUNAWAY: " + runawayDucks, 10, 21);
        g2d.drawString("KILLS: " + killedDucks, 160, 21);
        g2d.drawString("SHOOTS: " + shoots, 299, 21);
        g2d.drawString("SCORE: " + score, 440, 21);
        g2d.drawString("Round: " + Round, 570, 21);
        g2d.drawString("Money: " + money, 700, 21);

        g2d.drawString("PlayerHP" + PlayerHp, 10, 70);

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

    private void Reload() {
        isReloading = true;
        playActiveSound("src/main/resources/sounds/reloading.wav");
        reloadStartTime = System.nanoTime();
    }
}
