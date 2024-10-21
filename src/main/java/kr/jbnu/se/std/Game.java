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
    List<GiftBox> giftBoxes = new ArrayList<>();
    long lastGiftBoxTime = 0;
    int minInterval = 9000; // 최소 간격 3초
    int maxInterval = 30000; // 최대 간격 10초
    int giftBoxInterval = getRandomInterval(minInterval, maxInterval); // 랜덤 간격
    private Clip clip;
    private Clip clipbg;
    private boolean isPause = false;
    private int Round;
    private boolean isBossAlive;
    private Duck[] hunterSelectedDucks;
    private Duck[] FireSelectedDucks;
    private Duck[] playerSelectedDucks;
    private int ammo;          // 현재 사용 가능한 총알
    private int maxAmmo;       // 한 번에 장전할 수 있는 최대 탄약 수
    private boolean isReloading; // 장전 중인지 여부
    private long reloadStartTime; // 장전이 시작된 시간
    private long reloadDuration;  // 장전 시간 (예: 2초)
    private URL hpUrl;
    private int selectduck;
    private ScheduledExecutorService hunterExecutor;
    private ScheduledExecutorService FireExecutor;
    private int PlayerHp;
    private BufferedImage sightImg_Fire;

    /**
     * Font that we will use to write statistic to the screen.
     */
    private Font font;
    boolean hunterTrigger = true;
    private int damage;
    private BufferedImage bossImg;
    private BufferedImage boss2Img;
    private BufferedImage boss3Img;
    private BufferedImage boss4Img;
    private BufferedImage boss5Img;
    private BufferedImage bossAttack;
    private BufferedImage hunter111Img;
    private BufferedImage bossAttack2;
    private BufferedImage bossAttack3;
    private BufferedImage bossAttack4;
    private BufferedImage bossAttack5;
    private BufferedImage warningImg;
    private BufferedImage[] hpImages = new BufferedImage[12];
    private BufferedImage[] shopImages = new BufferedImage[4];// HP 이미지를 저장할 배열
    private int duckspeed;
        /**
     * Array list of the ducks.
     */
    private ArrayList<Duck> ducks;
    private ArrayList<boss1> boss;
    private ArrayList<Buttonbuy> buttonbuy;
    private long lastBossAttackTime = 0;  // 마지막 공격 시간
    private final long bossAttackInterval = 3000;
    long lastBottomAttackTime = 0;
    long bottomAttackInterval = 4000; // 공격 간격 (3초)
    private ArrayList<Hunter1> Hunters;
    private boolean Bosswith3delay;

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
    private boolean fire = false;
    /**
     * The time which must elapse between shots.
     */
    private long timeBetweenShots;
    private BufferedImage giftBoxImg1;
    private BufferedImage giftBoxImg2;
    private BufferedImage giftBoxImg3;
    /**
     * kr.jbnu.se.std.Game background image.
     */
    private BufferedImage backgroundImg;
    private BufferedImage backgroundImg2;
    private BufferedImage backgroundImg3;
    private BufferedImage backgroundImg4;
    private BufferedImage backgroundImg5;
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
    private List<BossAttack> bossAttacks2 = new ArrayList<>();
    private List<BossAttack> bossAttacks3 = new ArrayList<>();
    private List<BossAttack> bossAttacks4 = new ArrayList<>();
    private List<BossAttack> bossAttacks5 = new ArrayList<>();
    private int roundPass;



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

                playBackgroundMusic("src/main/resources/sounds/MoonlightShadow.wav");

                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    // 랜덤한 시간 간격 생성 메서드
    public int getRandomInterval(int min, int max) {
        return min + (int) (Math.random() * (max - min));
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
        money = 0;
        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        roundPass = 60;
        shoots = 0;
        PlayerHp = 100;
        Round = 1;
        damage = 20;
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

            for (int i = 0; i < 3; i++) { // 0부터 11까지 반복
                try {
                    // 이미지 경로를 생성
                    URL hpUrl = this.getClass().getResource("/images/shop" + i + ".png");

                    // URL이 null이 아닐 경우에만 이미지 읽기
                    if (hpUrl != null) {
                        shopImages[i] = ImageIO.read(hpUrl);
                    } else {
                        System.out.println("Image not found: /images/shop" + i + ".png");
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // IOException 처리
                }
            }



            URL hunterimg = this.getClass().getResource("/images/hunterrrrr.png");
            hunter111Img = ImageIO.read(hunterimg);


            URL Buttonimg = this.getClass().getResource("/images/btn_buy.png");
            buttonImg = ImageIO.read(Buttonimg);

            URL Bossimg2 = this.getClass().getResource("/images/boss_crocs.png");
            boss2Img = ImageIO.read(Bossimg2);

            URL Bossimg3 = this.getClass().getResource("/images/boss_hippo.png");
            boss3Img = ImageIO.read(Bossimg3);

            URL Bossimg4 = this.getClass().getResource("/images/boss_dugong.png");
            boss4Img = ImageIO.read(Bossimg4);

            URL Bossimg5 = this.getClass().getResource("/images/duck_boss1.png");
            boss5Img = ImageIO.read(Bossimg5);

            URL backgroundImgUrl = this.getClass().getResource("/images/background.png");
            backgroundImg = ImageIO.read(backgroundImgUrl);

            URL backgroundImgUrl3 = this.getClass().getResource("/images/background_SAFARI.png");
            backgroundImg3 = ImageIO.read(backgroundImgUrl3);

            URL backgroundImgUrl4 = this.getClass().getResource("/images/bossbackground4.png");
            backgroundImg4 = ImageIO.read(backgroundImgUrl4);

            URL backgroundImgUrl5 = this.getClass().getResource("/images/boss_lv5.png");
            backgroundImg5 = ImageIO.read(backgroundImgUrl5);

            URL backgroundImgUrl2 = this.getClass().getResource("/images/background_mud.png");
            backgroundImg2 = ImageIO.read(backgroundImgUrl2);

            URL WarningURL = this.getClass().getResource("/images/warning.png");
            warningImg = ImageIO.read(WarningURL);

            URL bossImgUrl = this.getClass().getResource("/images/boss.png");
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

            URL bossAttackImage2 = this.getClass().getResource("/images/crocs_mud.png");
            bossAttack2 = ImageIO.read(bossAttackImage2);

            URL bossAttackImage3 = this.getClass().getResource("/images/crocs_mud.png");
            bossAttack3 = ImageIO.read(bossAttackImage3);

            URL bossAttackImage4 = this.getClass().getResource("/images/waterball.png");
            bossAttack4 = ImageIO.read(bossAttackImage4);

            URL bossAttackImage5 = this.getClass().getResource("/images/skull.png");
            bossAttack5 = ImageIO.read(bossAttackImage5);

            URL bossAttackImage = this.getClass().getResource("/images/attack1.png");
            bossAttack = ImageIO.read(bossAttackImage);

            URL giftImage1 = this.getClass().getResource("/images/giftbox.png");
            giftBoxImg1 = ImageIO.read(giftImage1);

            URL giftImage2 = this.getClass().getResource("/images/giftbox.png");
            giftBoxImg2 = ImageIO.read(giftImage2);

            URL giftImage3 = this.getClass().getResource("/images/giftbox.png");
            giftBoxImg3 = ImageIO.read(giftImage3);

            URL Fire1 = this.getClass().getResource("/images/fire.png");
            sightImg_Fire = ImageIO.read(Fire1);
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


    // N마리 오리를 선택하는 메소드 (Hunter용)
    private void selectFireDucks(int numberOfDucks) {
        if (ducks.size() >= numberOfDucks) {
            FireSelectedDucks = new Duck[numberOfDucks]; // Hunter 선택된 오리 배열 초기화
            Random random = new Random();

            for (int i = 0; i < numberOfDucks; i++) {
                Duck selectedDuck;
                int index;

                // 중복되지 않는 오리를 선택
                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(hunterSelectedDucks).contains(selectedDuck) || Arrays.asList(playerSelectedDucks).contains(selectedDuck) || Arrays.asList(FireSelectedDucks).contains(selectedDuck)); // 중복 방지

                FireSelectedDucks[i] = selectedDuck;
                System.out.println("Selected Fire Ducks: " + Arrays.toString(FireSelectedDucks));

            }
        }
    }

    // Hunter가 자동으로 오리를 제거하는 메소드
    private void startFireAutoKill(int interval) {
        if (FireExecutor == null || FireExecutor.isShutdown()) {
            FireExecutor = Executors.newScheduledThreadPool(1); // 새로 생성
        }
        FireExecutor.scheduleAtFixedRate(() -> {
            if (FireSelectedDucks == null || Arrays.stream(FireSelectedDucks).allMatch(Objects::isNull)) {
                // Hunter가 선택한 오리가 없으면 새롭게 선택
                selectFireDucks(1);
            }
            if (FireSelectedDucks != null) {
                for (Duck duck : FireSelectedDucks) {
                    if (duck != null
                            && !Arrays.asList(playerSelectedDucks).contains(duck)
                            && !Arrays.asList(hunterSelectedDucks).contains(duck)) {
                        // Hunter가 선택한 오리가 Player가 선택한 오리와 중복되지 않도록 확인
                        ducks.remove(duck);
                        killedDucks++;
                        money += 10;
                        score += duck.score;
                        System.out.println("Hunter가 오리를 죽였습니다: " + duck);
                        break; // 한 마리씩 죽이고 나가도록

                    }
                }
                updateFireSelectedDucks();
            }
        }, 0, interval, TimeUnit.MILLISECONDS); // interval 시간마다 실행
    }



    // 게임이 끝나면 Hunter의 자동조준 타이머를 중지하는 코드
    private void stopFireAutoKill() {
        if (FireExecutor != null && !FireExecutor.isShutdown()) {
            FireExecutor.shutdown(); // Hunter의 자동 조준 종료
        }
    }


    private void updateFireSelectedDucks() {
        Random random = new Random();

        for (int i = 0; i < FireSelectedDucks.length; i++) {
            if (hunterSelectedDucks[i] == null || !ducks.contains(hunterSelectedDucks[i]) || !ducks.contains(playerSelectedDucks[i])) {
                // 새로운 오리를 선택하여 중복되지 않게 추가
                Duck selectedDuck;
                int index;

                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(hunterSelectedDucks).contains(selectedDuck) ||
                        Arrays.asList(playerSelectedDucks).contains(selectedDuck) ||
                Arrays.asList(FireSelectedDucks).contains(selectedDuck)); // 중복 방지

                FireSelectedDucks[i] = selectedDuck;
            }
        }
    }


    // Hunter가 자동으로 오리를 제거하는 메소드
    private void startHunterAutoKill(int interval) {
        if (hunterExecutor == null || hunterExecutor.isShutdown()) {
            hunterExecutor = Executors.newScheduledThreadPool(1); // 새로 생성
        }
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

    // 더블배럴샷건 모드에서 Hunter 선택된 오리들에게 sightImg를 그리기
    private void drawSightOnFireSelectedDucks(Graphics2D g2d) {
        if (FireSelectedDucks != null) {
            for (Duck duck : FireSelectedDucks) {
                if (duck != null) {
                    g2d.drawImage(sightImg_Fire, duck.x, duck.y,100,100,null);
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
        stopBackgroundMusic();
    }


    public void NextRound() {
        stopBackgroundMusic();
        isPause = false;
        Framework.gameState = Framework.GameState.PLAYING;
        Duck.lastDuckTime = 0; // 오리 타이머 초기화
        killedDucks = 0; // 죽인 오리 수 초기화
        runawayDucks = 0; // 도망간 오리 수 초기화
        Round += 1;
        isBossAlive = false;
        roundPass += 30;
        if(Round == 2){
            playBackgroundMusic("src/main/resources/sounds/JungleBook.wav");
        }else if(Round == 3){
            playBackgroundMusic("src/main/resources/sounds/FantasticThemePark.wav");
        }else if(Round == 4){
            playBackgroundMusic("src/main/resources/sounds/WhaleBelly.wav");
        }else if(Round == 5){
            playBackgroundMusic("src/main/resources/sounds/TempleOfDarkness.wav");
        }

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
    public void stopBackgroundMusic() {
        if (clipbg != null && clipbg.isRunning()) {
            clipbg.stop();
        }
    }

    public void playBackgroundMusic(String filePath){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clipbg = AudioSystem.getClip();
            clipbg.open(audioStream);
            clipbg.start();
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
                if (Round == 1) {
                    // 일정 시간마다 공격 발사 (보스 공격 간격 체크)
                    if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 1_000_000) {
                        double angle = 150 + Math.random() * 70;
                        double angle2 = 150 + Math.random() * 70;
                        double angle3 = 150 + Math.random() * 70;// 0에서 360도 사이의 랜덤 각도
                        bossAttacks.add(new BossAttack(boss.get(i).x, boss.get(i).y, angle, 15));
                        bossAttacks.add(new BossAttack(boss.get(i).x, boss.get(i).y, angle2, 15));
                        bossAttacks.add(new BossAttack(boss.get(i).x, boss.get(i).y, angle3, 15));// 속도 10으로 설정
                        playActiveSound("src/main/resources/sounds/bossattck.wav");
                        lastBossAttackTime = System.nanoTime(); // 마지막 공격 시간 갱신
                    }
                }

                if (Round == 2) {
                    if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
                        double angle1 = Math.toRadians(150 + Math.random() * 70);
                        double angle2 = Math.toRadians(150 + Math.random() * 70);
                        double angle3 = Math.toRadians(150 + Math.random() * 70);
                        double angle4 = Math.toRadians(150 + Math.random() * 70);
                        double gravity = 6;
                        double speed = 150; // Initial speed of the projectile
                        double deltaTime = 0.1;
                        // 각도에 따른 수평 및 수직 속도 계산 (vy는 음수)
                        double vx1 = speed * Math.cos(angle1);
                        double vy1 = speed * Math.sin(angle1); // vy를 음수로 하지 않음 (초기 방향이 위로 향함)

                        double vx2 = speed * Math.cos(angle2);
                        double vy2 = speed * Math.sin(angle2);

                        double vx3 = speed * Math.cos(angle3);
                        double vy3 = speed * Math.sin(angle3);

                        double vx4 = speed * Math.cos(angle3);
                        double vy4 = speed * Math.sin(angle3);

                        // Add the BossAttacks with initial velocities and positions
                        bossAttacks2.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx1, vy1, gravity, deltaTime));
                        bossAttacks2.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx2, vy2, gravity, deltaTime));
                        bossAttacks2.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx3, vy3, gravity, deltaTime));
                        bossAttacks2.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx4, vy4, gravity, deltaTime));
                        playActiveSound("src/main/resources/sounds/bossattck.wav");
                        // Update the time of the last attack
                        lastBossAttackTime = System.nanoTime();
                    }
                }
                if (Round == 3) {
                    if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
                        double angle1 = Math.toRadians(150 + Math.random() * 70);
                        double angle2 = Math.toRadians(150 + Math.random() * 70);
                        double angle3 = Math.toRadians(150 + Math.random() * 70);
                        double angle4 = Math.toRadians(150 + Math.random() * 70);
                        double gravity = 6;
                        double speed = 150; // 초기 속도
                        double deltaTime = 0.1;

                        // 기존 공격 유도탄
                        double vx1 = speed * Math.cos(angle1);
                        double vy1 = speed * Math.sin(angle1);
                        double vx2 = speed * Math.cos(angle2);
                        double vy2 = speed * Math.sin(angle2);
                        double vx3 = speed * Math.cos(angle3);
                        double vy3 = speed * Math.sin(angle3);
                        double vx4 = speed * Math.cos(angle4);
                        double vy4 = speed * Math.sin(angle4);

                        // 기존 공격 유도탄을 추가
                        bossAttacks3.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx1, vy1, gravity, deltaTime));
                        bossAttacks3.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx2, vy2, gravity, deltaTime));
                        bossAttacks3.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx3, vy3, gravity, deltaTime));
                        bossAttacks3.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx4, vy4, gravity, deltaTime));

                        // 마우스 위치를 향한 유도탄의 각도 계산
                        double mouseX = mousePosition.getX(); // 마우스 X 좌표를 가져오는 메서드 구현 또는 사용
                        double mouseY = mousePosition.getY(); // 마우스 Y 좌표를 가져오는 메서드 구현 또는 사용

                        double dx = mouseX - boss.get(i).x;
                        double dy = mouseY - boss.get(i).y;
                        double homingAngle = Math.atan2(dy, dx);

                        double vxHoming = speed * Math.cos(homingAngle);
                        double vyHoming = speed * Math.sin(homingAngle);

                        // 유도탄을 추가
                        bossAttacks3.add(new BossAttack(boss.get(i).x, boss.get(i).y, vxHoming, vyHoming, gravity, deltaTime));
                        playActiveSound("src/main/resources/sounds/bossattck.wav");

                        // 마지막 공격 시간 업데이트
                        lastBossAttackTime = System.nanoTime();
                    }
                }if (Round == 4) {
                    // 일반 유도탄 공격 처리
                    if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
                        double angle1 = Math.toRadians(150 + Math.random() * 70);
                        double angle2 = Math.toRadians(150 + Math.random() * 70);
                        double angle3 = Math.toRadians(150 + Math.random() * 70);
                        double angle4 = Math.toRadians(150 + Math.random() * 70);
                        double gravity = 6;
                        double speed = 150; // 초기 속도
                        double deltaTime = 0.1;

                        // 기존 유도탄
                        double vx1 = speed * Math.cos(angle1);
                        double vy1 = speed * Math.sin(angle1);
                        double vx2 = speed * Math.cos(angle2);
                        double vy2 = speed * Math.sin(angle2);
                        double vx3 = speed * Math.cos(angle3);
                        double vy3 = speed * Math.sin(angle3);
                        double vx4 = speed * Math.cos(angle4);
                        double vy4 = speed * Math.sin(angle4);

                        // 기존 유도탄 추가
                        bossAttacks4.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx1, vy1, gravity, deltaTime));
                        bossAttacks4.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx2, vy2, gravity, deltaTime));
                        bossAttacks4.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx3, vy3, gravity, deltaTime));
                        bossAttacks4.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx4, vy4, gravity, deltaTime));

                        // 마우스 위치를 향한 유도탄의 각도 계산
                        double mouseX = mousePosition.getX(); // 마우스 X 좌표를 가져오는 메서드 구현 또는 사용
                        double mouseY = mousePosition.getY(); // 마우스 Y 좌표를 가져오는 메서드 구현 또는 사용

                        double dx = mouseX - boss.get(i).x;
                        double dy = mouseY - boss.get(i).y;
                        double homingAngle = Math.atan2(dy, dx);

                        double vxHoming = speed * Math.cos(homingAngle);
                        double vyHoming = speed * Math.sin(homingAngle);

                        // 유도탄을 추가
                        bossAttacks4.add(new BossAttack(boss.get(i).x, boss.get(i).y, vxHoming, vyHoming, gravity, deltaTime));
                        bossAttacks4.add(new BossAttack(boss.get(i).x, boss.get(i).y, vxHoming, vyHoming, gravity, deltaTime));
                        playActiveSound("src/main/resources/sounds/bossattck.wav");

                        // 마지막 공격 시간 업데이트
                        lastBossAttackTime = System.nanoTime();
                    }

                    // 밑에서 올라오는 탄환 처리
                    if (System.nanoTime() - lastBottomAttackTime >= bottomAttackInterval * 900_000) {
                        double gravity = 6;
                        double speed = 150; // 초기 속도
                        double deltaTime = 0.1;

                        // 화면 아래쪽에서 시작하도록 위치 설정 (예: 아래 중앙)
                        double screenBottomX1 = framework.getWidth() / 2.0; // 화면 중앙 X 좌표
                        double screenBottomX2 = framework.getWidth() * (0.11 + Math.random() * (0.33 - 0.11)); // 왼쪽에서 나오는 탄환
                        double screenBottomX3 = framework.getWidth() * (0.11 + Math.random() * (0.33 - 0.11)); // 오른쪽에서 나오는 탄환
                        double screenBottomY = framework.getHeight(); // 화면의 아래 Y 좌표

                        // 위쪽으로 일정한 각도로 발사되는 일반 탄환
                        double upwardAngle = Math.toRadians(80 + Math.random()*(90 - 80)); // 정확히 위쪽으로

                        // 각 탄환의 속도 계산
                        double vxUp1 = speed * Math.cos(upwardAngle);
                        double vyUp1 = -speed * Math.sin(upwardAngle);
                        System.out.println(screenBottomY+"aaaaaa"+screenBottomX3+"aaaa"+vyUp1);
                        // 일반 탄환을 추가
                        bossAttacks4.add(new BossAttack((int)screenBottomX1, (int)screenBottomY, vxUp1, vyUp1, gravity, deltaTime));
                        bossAttacks4.add(new BossAttack((int)screenBottomX2, (int)screenBottomY, vxUp1, vyUp1, gravity, deltaTime));
                        bossAttacks4.add(new BossAttack((int)screenBottomX3, (int)screenBottomY, vxUp1, vyUp1, gravity, deltaTime));

                        // 마지막 밑에서 올라오는 공격 시간 업데이트
                        lastBottomAttackTime = System.nanoTime();
                    }
                }if (Round == 5) {
                    // 일반 유도탄 공격 처리
                    if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
                        double angle1 = Math.toRadians(150 + Math.random() * 70);
                        double angle2 = Math.toRadians(150 + Math.random() * 70);
                        double angle3 = Math.toRadians(150 + Math.random() * 70);
                        double angle4 = Math.toRadians(150 + Math.random() * 70);
                        double gravity = 6;
                        double speed = 150; // 초기 속도
                        double deltaTime = 0.1;

                        // 기존 유도탄
                        double vx1 = speed * Math.cos(angle1);
                        double vy1 = speed * Math.sin(angle1);
                        double vx2 = speed * Math.cos(angle2);
                        double vy2 = speed * Math.sin(angle2);
                        double vx3 = speed * Math.cos(angle3);
                        double vy3 = speed * Math.sin(angle3);
                        double vx4 = speed * Math.cos(angle4);
                        double vy4 = speed * Math.sin(angle4);

                        // 기존 유도탄 추가
                        bossAttacks5.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx1, vy1, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx2, vy2, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx3, vy3, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack(boss.get(i).x, boss.get(i).y, vx4, vy4, gravity, deltaTime));

                        // 마우스 위치를 향한 유도탄의 각도 계산
                        double mouseX = mousePosition.getX(); // 마우스 X 좌표를 가져오는 메서드 구현 또는 사용
                        double mouseY = mousePosition.getY(); // 마우스 Y 좌표를 가져오는 메서드 구현 또는 사용

                        double dx = mouseX - boss.get(i).x;
                        double dy = mouseY - boss.get(i).y;
                        double homingAngle = Math.atan2(dy, dx);

                        double vxHoming = speed * Math.cos(homingAngle);
                        double vyHoming = speed * Math.sin(homingAngle);

                        // 유도탄을 추가
                        bossAttacks5.add(new BossAttack(boss.get(i).x, boss.get(i).y, vxHoming, vyHoming, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack(boss.get(i).x, boss.get(i).y, vxHoming, vyHoming, gravity, deltaTime));
                        playActiveSound("src/main/resources/sounds/bossattck.wav");

                        // 마지막 공격 시간 업데이트
                        lastBossAttackTime = System.nanoTime();
                    }

                    // 밑에서 올라오는 탄환 처리
                    if (System.nanoTime() - lastBottomAttackTime >= bottomAttackInterval * 900_000) {
                        double gravity = 6;
                        double speed = 150; // 초기 속도
                        double deltaTime = 0.1;

                        // 화면 아래쪽에서 시작하도록 위치 설정 (예: 아래 중앙)
                        double screenBottomX1 = framework.getWidth() / 2.0; // 화면 중앙 X 좌표
                        double screenBottomX2 = framework.getWidth() * (0.11 + Math.random() * (0.33 - 0.11)); // 왼쪽에서 나오는 탄환
                        double screenBottomX3 = framework.getWidth() * (0.11 + Math.random() * (0.33 - 0.11)); // 오른쪽에서 나오는 탄환
                        double screenBottomY = framework.getHeight(); // 화면의 아래 Y 좌표
                        double screenTopY = 0;

                        // 위쪽으로 일정한 각도로 발사되는 일반 탄환
                        double upwardAngle = Math.toRadians(80 + Math.random()*(90 - 80)); // 정확히 위쪽으로

                        // 각 탄환의 속도 계산
                        double vxUp1 = speed * Math.cos(upwardAngle);
                        double vyUp1 = -speed * Math.sin(upwardAngle);
                        double vyDown1 = speed * Math.sin(upwardAngle);
                        System.out.println(screenBottomY+"aaaaaa"+screenBottomX3+"aaaa"+vyUp1);
                        // 일반 탄환을 추가
                        bossAttacks5.add(new BossAttack((int)screenBottomX1, (int)screenBottomY, vxUp1, vyUp1, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack((int)screenBottomX2, (int)screenBottomY, vxUp1, vyUp1, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack((int)screenBottomX3, (int)screenBottomY, vxUp1, vyUp1, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack((int)screenBottomX3, (int)screenTopY, vxUp1, vyDown1, gravity, deltaTime));
                        bossAttacks5.add(new BossAttack((int)screenBottomX2, (int)screenTopY, vxUp1, vyDown1, gravity, deltaTime));

                        // 마지막 밑에서 올라오는 공격 시간 업데이트
                        lastBottomAttackTime = System.nanoTime();
                    }
                }
            }

            // 보스 공격 업데이트 및 피격 체크
            for (int i = 0; i < bossAttacks.size(); i++) {
                if (Round == 1) {
                    BossAttack attack = bossAttacks.get(i);
                    attack.update(); // 공격 위치 업데이트

                    // 피격 범위 확인
                    if (attack.isHit(mousePosition)) {
                        System.out.println("Player hit! Remaining health: ");
                        bossAttacks.remove(i);// 공격이 맞았으므로 제거
                        PlayerHp -= 10;
                        playActiveSound("src/main/resources/sounds/hit.wav");
                        i--; // 인덱스 조정
                    }

                    // 화면 밖으로 나간 공격은 제거
                    if (attack.x < 0 || attack.x > framework.getWidth() || attack.y < 0 || attack.y > framework.getHeight()) {
                        bossAttacks.remove(i);
                        i--; // 인덱스 조정
                    }
                }
            }
            for(int i = 0; i < bossAttacks2.size(); i++){
                if(Round==2){
                    BossAttack attack2 = bossAttacks2.get(i);
                    attack2.updatewithgravity();

                    if (attack2.isHit(mousePosition)) {
                        System.out.println("Player hit! Remaining health: ");
                        bossAttacks2.remove(i);// 공격이 맞았으므로 제거
                        PlayerHp -= 10;
                        playActiveSound("src/main/resources/sounds/hit.wav");
                        i--; // 인덱스 조정
                    }

                    // 화면 밖으로 나간 공격은 제거
                    if (attack2.x < 0 || attack2.x > framework.getWidth() || attack2.y < 0 || attack2.y > framework.getHeight()) {
                        bossAttacks2.remove(i);
                        i--; // 인덱스 조정
                    }
                }
            }  for(int i = 0; i < bossAttacks3.size(); i++){
                if(Round==3){
                    BossAttack attack2 = bossAttacks3.get(i);
                    attack2.updatewithgravity();

                    if (attack2.isHit(mousePosition)) {
                        System.out.println("Player hit! Remaining health: ");
                        bossAttacks3.remove(i);// 공격이 맞았으므로 제거
                        PlayerHp -= 10;
                        playActiveSound("src/main/resources/sounds/hit.wav");
                        i--; // 인덱스 조정
                    }

                    // 화면 밖으로 나간 공격은 제거
                    if (attack2.x < 0 || attack2.x > framework.getWidth() || attack2.y < 0 || attack2.y > framework.getHeight()) {
                        bossAttacks3.remove(i);
                        i--; // 인덱스 조정
                    }
                }
            }for(int i = 0; i < bossAttacks4.size(); i++){
                if(Round==4){
                    BossAttack attack2 = bossAttacks4.get(i);
                    attack2.updatewithgravity();
                    attack2.update();
                    if (attack2.isHit(mousePosition)) {
                        System.out.println("Player hit! Remaining health: ");
                        bossAttacks4.remove(i);// 공격이 맞았으므로 제거
                        PlayerHp -= 10;
                        playActiveSound("src/main/resources/sounds/hit.wav");
                        i--; // 인덱스 조정
                    }

                    // 화면 밖으로 나간 공격은 제거
                    if (attack2.x < 0 || attack2.x > framework.getWidth() || attack2.y < 0 || attack2.y > framework.getHeight()) {
                        bossAttacks4.remove(i);
                        i--; // 인덱스 조정
                    }
                }
            }for(int i = 0; i < bossAttacks5.size(); i++){
                if(Round==5){
                    BossAttack attack2 = bossAttacks5.get(i);
                    attack2.updatewithgravity();

                    if (attack2.isHit(mousePosition)) {
                        System.out.println("Player hit! Remaining health: ");
                        bossAttacks5.remove(i);// 공격이 맞았으므로 제거
                        PlayerHp -= 10;
                        playActiveSound("src/main/resources/sounds/hit.wav");
                        i--; // 인덱스 조정
                    }

                    // 화면 밖으로 나간 공격은 제거
                    if (attack2.x < 0 || attack2.x > framework.getWidth() || attack2.y < 0 || attack2.y > framework.getHeight()) {
                        bossAttacks5.remove(i);
                        i--; // 인덱스 조정
                    }
                }
            }
        }


        if(Hunter1&& hunterTrigger && !isPause){
            startHunterAutoKill(2500);
            hunterTrigger = false;
        }else if(!Hunter1){
        }else if(!hunterTrigger){

        }
        if(!isPause) {
            int maxGiftBoxes = 1;
            int Random = 1+(int)(Math.random()*100);
        // Creates a new duck, if it's the time, and add it to the array list.
            // 랜덤한 간격으로 선물 상자를 생성
            if (System.nanoTime() - lastGiftBoxTime >= giftBoxInterval * 1_000_000) {
                if ((giftBoxes.size() < maxGiftBoxes) && Random == 50) {
                    int randomX = (int) (Math.random() * (framework.getWidth() - 50)); // 랜덤 X 좌표 (상자 크기를 고려)
                    int fallSpeed = 5 + (int) (Math.random() * 5); // 5~10 사이의 낙하 속도

                    // 랜덤한 타입 선택 (1, 2, 3)
                    int giftBoxType = 1 + (int) (Math.random() * 3);

                    // 타입에 따라 이미지 선택
                    BufferedImage selectedImg = giftBoxImg1;
                    if (giftBoxType == 2) {
                        selectedImg = giftBoxImg2;
                    } else if (giftBoxType == 3) {
                        selectedImg = giftBoxImg3;
                    }

                    // 새로운 선물 상자 추가
                    giftBoxes.add(new GiftBox(randomX, 0, 150, 150, fallSpeed, giftBoxType, selectedImg));

                    // 마지막 생성 시간 갱신 및 다음 생성 간격을 랜덤하게 설정
                    lastGiftBoxTime = System.nanoTime();
                    giftBoxInterval = getRandomInterval(minInterval, maxInterval);
                }
            }

            // 상자 업데이트 및 충돌 체크
            for (int i = 0; i < giftBoxes.size(); i++) {
                giftBoxes.get(i).update();

                // 플레이어와 충돌 체크
                if (new Rectangle(giftBoxes.get(i).x + 18, giftBoxes.get(i).y, 150, 150).contains(mousePosition)
                        && Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
                    System.out.println("Player collected a gift!");

                    // 선물 상자의 타입에 따라 다른 보상 제공
                    if (giftBoxes.get(i).type == 1) {
                        startFireAutoKill(1500);
                        System.out.println("Fire");
                        fire = true;
                        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                        scheduler.schedule(() -> {
                           stopFireAutoKill();
                           fire = false;
                        }, 9000, TimeUnit.SECONDS);// 3초 후 실행/ 1번 상자: 돈 증가
                    } else if (giftBoxes.get(i).type == 2) {
                        // 모든 오리의 속도를 증가시키기
                        System.out.println("speed");
                        for (Duck duck : ducks) {
                            int currentSpeed = duck.getSpeed();
                            System.out.println(currentSpeed);
                            if(currentSpeed > -3 ) {
                                duck.setSpeed(currentSpeed + 1);
                            }// 기존 속도보다 1씩 증가
                        }
                        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                        scheduler.schedule(() -> {
                            for (Duck duck : ducks) {
                                int currentSpeed = duck.getSpeed();
                                if(currentSpeed > -4) {
                                    duck.setSpeed(currentSpeed - 1);
                                }// 기존 속도보다 1씩 증가
                            }
                        }, 3, TimeUnit.SECONDS);// 3초 후 실행
                    } else if (giftBoxes.get(i).type == 3) {
                        PlayerHp += 10; // 3번 상자: 체력 증가
                    }

                    // 충돌한 상자는 제거
                    giftBoxes.remove(i);
                    i--; // 인덱스 조정
                }

                // 화면 밖으로 벗어난 상자 제거
                if(giftBoxes.size() > 0) {
                    if (giftBoxes.get(i).y > framework.getHeight()) {
                        giftBoxes.remove(i);
                        i--; // 인덱스 조정
                    }
                }
            }

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
            ducks.add(new Duck(Duck.duckLines[Duck.nextDuckLines][0] + random.nextInt(200), Duck.duckLines[Duck.nextDuckLines][1], Duck.duckLines[Duck.nextDuckLines][2], Duck.duckLines[Duck.nextDuckLines][3],duckImg));

            // Here we increase nextDuckLines so that next duck will be created in next line.
            Duck.nextDuckLines++;
            if (Duck.nextDuckLines >= Duck.duckLines.length)
                Duck.nextDuckLines = 0;

            if (killedDucks >= roundPass && !isBossAlive) {
                // 보스 생성
                stopBackgroundMusic();
                playBackgroundMusic("src/main/resources/sounds/warning.wav");
                spawnBossWithDelay();
                Bosswith3delay = true;
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
                                boss.get(i).health -= damage; // Reduce boss health by 20 on each hit.
                                System.out.println("attack boss");
                                System.out.println(boss.get(i).health);
                                // If the boss is dead, update score, money, etc.
                                if (boss.get(i).health <= 0) {
                                    money += 100; // Bosses give more money
                                    score += boss.get(i).score; // Boss-specific score
                                    boss.remove(i);
                                    if (Round == 5) {
                                        Framework.gameState = Framework.GameState.ENDING;
                                        stopBackgroundMusic();
                                        framework.saveScore(score);
                                        framework.saveScore(score);
                                        leaderboardSaved = true;
                                        playBackgroundMusic("src/main/resources/sounds/NewBeginningNotTheEnd.wav"); // 엔딩 테마곡 재생
                                    } else {
                                        Pause(); // 다른 라운드의 경우 일시 정지 상태로 전환
                                    }
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
            if (runawayDucks >= 10 || PlayerHp < 0 )
                Framework.gameState = Framework.GameState.GAMEOVER;
            if (Framework.gameState == Framework.GameState.GAMEOVER && !leaderboardSaved) {
                framework.saveScore(score);
                framework.saveMoney(score);
                leaderboardSaved = true;  // 리더보드 저장 완료
            }
        }
        if(isPause) {
            hunterTrigger = true;
            stopHunterAutoKill();
            buttonbuy.add(new Buttonbuy(framework.getWidth()/2 - 350, framework.getHeight()/2+50, buttonImg));
            buttonbuy.add(new Buttonbuy(framework.getWidth()/2 -50, framework.getHeight()/2+50, buttonImg));
            buttonbuy.add(new Buttonbuy(framework.getWidth()/2 + 250, framework.getHeight()/2+50, buttonImg));
            // 마우스 포지션 및 버튼 클릭 상태 확인
            boolean mouseClicked = false;
            if (Canvas.mouseButtonState(MouseEvent.BUTTON1) && !mouseClicked) {
                mouseClicked = true;
                if(!Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
                    mouseClicked = false;
                }
                // 버튼 1 (헌터 구매)
                if (money > 200 && Hunters.size() < 1) {
                    Rectangle buttonArea1 = new Rectangle(buttonbuy.get(0).x, buttonbuy.get(0).y, 367, 257);
                    if (buttonArea1.contains(mousePosition) && mouseClicked) {
                        System.out.println("buybutton");
                        Hunters.add(new Hunter1(220, 290, 0, 100, hunter111Img));
                        Hunter1 = true;
                        money -= 200;
                        System.out.println("Mouse Position: " + mousePosition);
                        System.out.println("Button Position: " + buttonbuy.get(0).x + ", " + buttonbuy.get(0).y);
                    }
                }

                // 버튼 2 (데미지 증가)
                if (money > 100) {
                    Rectangle buttonArea2 = new Rectangle(buttonbuy.get(1).x, buttonbuy.get(1).y, 367, 257);
                    if (buttonArea2.contains(mousePosition)&& mouseClicked) {
                        System.out.println("buybutton");
                        damage += 10;
                        money -= 100;
                        System.out.println("Mouse Position: " + mousePosition);
                        System.out.println("Button Position: " + buttonbuy.get(1).x + ", " + buttonbuy.get(1).y);
                    }
                }

                // 버튼 3 (최대 탄약 증가)
                if (money > 100) {
                    Rectangle buttonArea3 = new Rectangle(buttonbuy.get(2).x, buttonbuy.get(2).y, 367, 257);
                    if (buttonArea3.contains(mousePosition) && mouseClicked) {
                        System.out.println("buybutton");
                        maxAmmo += 2;
                        money -= 100;
                        System.out.println("Mouse Position: " + mousePosition);
                        System.out.println("Button Position: " + buttonbuy.get(2).x + ", " + buttonbuy.get(2).y);
                    }
                }
            }
        }
        if(Hunters.size() > 0) {
            Hunter1 = true;
        }
}

    public void drawBossAttack(Graphics2D g2d){
        for(int i = 0; i < bossAttacks.size(); i++) {
            g2d.drawImage(bossAttack, bossAttacks.get(i).x,bossAttacks.get(i).y, null );
        }
    }
    private void spawnBossWithDelay() {
        // 3초 후 보스 생성
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            if(Round == 1){
                boss.add(new boss1(1200, 400, 0, 1000,200,bossImg));
            }else if(Round == 2){
                boss.add(new boss1(1200,400,0,1500, 400,boss2Img));
            }else if(Round == 3){
                boss.add(new boss1(1200,400, 0, 2000, 800,boss3Img));
            }else if(Round == 4){
                boss.add(new boss1(1200,400,0,3000, 1600, boss4Img));
            }else if(Round == 5){
                boss.add(new boss1(1200,400,0,4000, 3200, boss5Img));
            }
            stopBackgroundMusic();
            if(Round == 1) {
                playBackgroundMusic("src/main/resources/sounds/AquaCave.wav");
            }else if(Round == 2){
                playBackgroundMusic("src/main/resources/sounds/RuinCastle.wav");
            }else if(Round == 3){
                playBackgroundMusic("src/main/resources/sounds/ArabPirate.wav");
            }else if(Round == 4){
                playBackgroundMusic("src/main/resources/sounds/SinkingThings.wav");
            }else if(Round == 5){
                playBackgroundMusic("src/main/resources/sounds/WorldHorizon.wav");
            }
            isBossAlive = true; // 보스가 등장했음을 표시
            System.out.println("Boss activity");
            ducks.clear();
            Bosswith3delay = false;
        }, 3, TimeUnit.SECONDS); // 3초 후 실행
    }

    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        if (Round == 1){
            g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }

        if(Round == 2){
            g2d.drawImage(backgroundImg2, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }

        if(Round == 3){
            g2d.drawImage(backgroundImg3, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }

        if(Round == 4){
            g2d.drawImage(backgroundImg4, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }

        if(Round == 5){
            g2d.drawImage(backgroundImg5, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }
        
        // Here we draw all the ducks.
        for(int i = 0; i < ducks.size(); i++)
        {
            ducks.get(i).Draw(g2d);
        }
        if(Bosswith3delay){
            g2d.drawImage(warningImg,Framework.frameWidth/2-275,Framework.frameHeight/2-250, null);
        }
        if(Hunter1){
            g2d.drawImage(hunter111Img,Hunters.get(0).x,Hunters.get(0).y,null);
            drawSightOnHunterSelectedDucks(g2d);

        }

        if(bossAttacks.size() > 0){
            for(int i = 0; i < bossAttacks.size(); i++) {
                g2d.drawImage(bossAttack, bossAttacks.get(i).x,bossAttacks.get(i).y, null );
            }
        }

        if(bossAttacks2.size() >0){
            for(int i = 0; i < bossAttacks2.size(); i++) {
                g2d.drawImage(bossAttack2, bossAttacks2.get(i).x,bossAttacks2.get(i).y,100,100,null);
            }
        }

        if(bossAttacks3.size() > 0){
            for(int i = 0; i < bossAttacks3.size(); i++) {
                g2d.drawImage(bossAttack3, bossAttacks3.get(i).x,bossAttacks3.get(i).y,100,100,null);
            }
        }

        if(bossAttacks4.size() > 0){
            for(int i = 0; i < bossAttacks4.size(); i++) {
                g2d.drawImage(bossAttack4, bossAttacks4.get(i).x,bossAttacks4.get(i).y,100,100,null);
            }
        }

        if(bossAttacks5.size() > 0){
            for(int i = 0; i < bossAttacks5.size(); i++) {
                g2d.drawImage(bossAttack5, bossAttacks5.get(i).x,bossAttacks5.get(i).y,100,100,null);
            }
        }

        if(isPause){
            int buyWidth = buttonImg.getWidth(null) / 2; // 너비 50%
            int buyHeight = buttonImg.getHeight(null) / 2; // 높이 50%
            for(int i = 0; i < buttonbuy.size(); i++) {
                g2d.drawImage(buttonImg, buttonbuy.get(i).x, buttonbuy.get(i).y,buyWidth,buyHeight,null);
            }
            for(int i = 0; i < shopImages.length; i++) {
                g2d.drawImage(shopImages[i],buttonbuy.get(i).x-60, buttonbuy.get(i).y - 250, 300,300,null);
            }
        }
        // 보스 그리기
        if (!boss.isEmpty()) {
            for (int i = 0; i < boss.size(); i++) {
                // 보스 이미지 그리기
                if(Round == 1) {
                    g2d.drawImage(bossImg, boss.get(i).x - 90, boss.get(i).y - 20, 378/2, 268/2, null);
                }else if(Round == 2) {
                    g2d.drawImage(boss2Img, boss.get(i).x - 90, boss.get(i).y - 20, 378, 268, null);
                }else if(Round == 3) {
                    g2d.drawImage(boss3Img, boss.get(i).x - 90, boss.get(i).y - 20, 378, 268, null);
                }else if(Round == 4) {
                    g2d.drawImage(boss4Img, boss.get(i).x - 90, boss.get(i).y - 20, 378, 268, null);
                }else if(Round == 5){
                    g2d.drawImage(boss5Img, boss.get(i).x - 90, boss.get(i).y - 20, 378, 268, null);
                }
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
        if(fire){
            drawSightOnFireSelectedDucks(g2d);
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

        if(giftBoxes != null){
            for (GiftBox giftBox : giftBoxes) {
                // 타입에 맞는 이미지를 그리도록 설정
                BufferedImage selectedImg = null;
                if (giftBox.type == 1) {
                    selectedImg = giftBoxImg1;
                } else if (giftBox.type == 2) {
                    selectedImg = giftBoxImg2;
                } else if (giftBox.type == 3) {
                    selectedImg = giftBoxImg3;
                }

                // 이미지가 null이 아닐 때만 그리기
                if (selectedImg != null) {
                    g2d.drawImage(selectedImg, giftBox.x, giftBox.y, giftBox.width, giftBox.height, null);
                }
            }
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

        g2d.drawString("PlayerHP: " + PlayerHp, 10, 80);

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
    public void DrawEnding(Graphics2D g2d, Point mousePosition) {
        Draw(g2d, mousePosition);
        // 배경 화면 설정 (엔딩 전용 배경 이미지가 있다면 해당 이미지로 설정)
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }, 1500, TimeUnit.SECONDS);
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
