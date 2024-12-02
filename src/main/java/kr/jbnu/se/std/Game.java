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


/**
 * Actual game.
 *
 * @author www.gametutorial.net
 */

@SuppressWarnings("ALL")
public class Game {

    /**
     * We use this to generate a random number.
     */
    private Random random = new Random();
    List<GiftBox> giftBoxes = new ArrayList<>();
    long lastGiftBoxTime = 0;
    int minInterval = 9000; // 최소 간격 3초
    int maxInterval = 30000; // 최대 간격 10초
    int giftBoxInterval = getRandomInterval(minInterval, maxInterval); // 랜덤 간격
    private Clip clipbg;
    public int ed = 0;
    private boolean isPause = false;
    private int round;
    private boolean isBossAlive;
    private Duck[] hunterSelectedDucks;
    private Duck[] fireSelectedDucks;
    private Duck[] playerSelectedDucks;
    private int ammo;// 현재 사용 가능한 총알
    private int maxAmmo;       // 한 번에 장전할 수 있는 최대 탄약 수
    private BufferedImage gameoverfImg;
    private boolean isReloading; // 장전 중인지 여부
    private long reloadStartTime; // 장전이 시작된 시간
    private long reloadDuration;  // 장전 시간 (예: 2초)
    private ScheduledExecutorService hunterExecutor;
    private ScheduledExecutorService fireExecutor;
    private int playerHp;
    private BufferedImage sightImgFire;
    private final BufferedImage[] giftBoxImages = new BufferedImage[3];

    /**
     * Font that we will use to write statistic to the screen.
     */
    private Font font;
    private BufferedImage gameoverImg;
    boolean hunterTrigger = true;
    private int damage;
    //private long lastClickTime = 0; // 마지막으로 클릭한 시간
    //private final long clickDelay = 1000; // 클릭 사이의 최소 간격 (밀리초)
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
    private final BufferedImage[] hpImages = new BufferedImage[12];
    private final BufferedImage[] shopImages = new BufferedImage[4];// HP 이미지를 저장할 배열
    private final BufferedImage[] endingImages = new BufferedImage[4];

    /**
     * Array list of the ducks.
     */
    private ArrayList<Duck> ducks;
    private ArrayList<boss1> boss;
    private ArrayList<Buttonbuy> buttonbuy;
    private long lastBossAttackTime = 0;  // 마지막 공격 시간
    long lastBottomAttackTime = 0;
    long bottomAttackInterval = 4000; // 공격 간격 (3초)
    private ArrayList<Hunter1> hunters;
    private boolean bosswith3delay;

    /**
     * How many ducks leave the screen alive?
     */
    private int runawayDucks;
    private final Framework framework;

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
    private BufferedImage sightImgHunter;

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
    private final List<BossAttack> bossAttacks = new ArrayList<>();
    private final List<BossAttack> bossAttacks2 = new ArrayList<>();
    private final List<BossAttack> bossAttacks3 = new ArrayList<>();
    private final List<BossAttack> bossAttacks4 = new ArrayList<>();
    private final List<BossAttack> bossAttacks5 = new ArrayList<>();
    private int roundPass;

    private static final String HIT_SOUND_PATH = "src/main/resources/sounds/hit.wav";
    private static final String HIT_MESSAGE = "Player hit! Remaining health: ";
    private static final String BOSSATTCK_SOUND_PATH = "src/main/resources/sounds/bossattck.wav";

    private static final String BUY_BUTTON_MESSAGE = "buybutton";
    private static final String MOUSE_POSITION_MESSAGE = "Mouse Position: ";
    private static final String BUTTON_POSITION_MESSAGE = "Button Position: ";
    private final long bossAttackInterval = 3000;



    public Game(Framework framework) {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        this.framework = framework;
        Thread threadForInitGame = new Thread() {
            @Override
            public void run() {
                // Sets variables and objects for the game.
                initialize();
                // Load game files (images, sounds, ...)
                loadContent();

                playBackgroundMusic("src/main/resources/sounds/MoonlightShadow.wav");

                updateGameState();
            }
        };
        threadForInitGame.start();
    }

    // 게임 상태를 안전하게 업데이트하는 동기화된 메서드
    private synchronized void updateGameState() {
        Framework.gameState = Framework.GameState.PLAYING;
    }

    // 랜덤한 시간 간격 생성 메서드
    public int getRandomInterval(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    /**
     * Set variables and objects for the game.
     */
    private void initialize() {
        random = new Random();
        font = new Font("monospaced", Font.BOLD, 18);

        ducks = new ArrayList<Duck>();
        boss = new ArrayList<boss1>();
        buttonbuy = new ArrayList<Buttonbuy>();
        hunters = new ArrayList<Hunter1>();
        money = 0;
        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        roundPass = 60;
        shoots = 0;
        playerHp = 100;
        round = 1;
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
    private void loadContent() {
        try {
            loadImages("/images/hp_", 12, hpImages);
            loadImages("/images/shop", 3, shopImages);
            loadImages("/images/ending_", 3, endingImages);

            hunter111Img = loadImage("/images/hunterrrrr.png");
            gameoverImg = loadImage("/images/diegame.png");
            buttonImg = loadImage("/images/btn_buy.png");

            boss2Img = loadImage("/images/boss_crocs.png");
            boss3Img = loadImage("/images/boss_hippo.png");
            boss4Img = loadImage("/images/boss_dugong.png");
            boss5Img = loadImage("/images/duck_boss1.png");

            backgroundImg = loadImage("/images/background.png");
            backgroundImg3 = loadImage("/images/background_SAFARI.png");
            backgroundImg4 = loadImage("/images/bossbackground4.png");
            backgroundImg5 = loadImage("/images/boss_lv5.png");
            backgroundImg2 = loadImage("/images/background_mud.png");

            warningImg = loadImage("/images/warning.png");
            bossImg = loadImage("/images/boss.png");

            BufferedImage grassImg = loadImage("/images/grass.png");
            sightImgHunter = loadImage("/images/sight_hunter.png");
            duckImg = loadImage("/images/duck.png");
            gameoverfImg = loadImage("/images/duck.png");
            sightImg = loadImage("/images/sight.png");

            sightImgMiddleWidth = sightImg.getWidth() / 2;
            sightImgMiddleHeight = sightImg.getHeight() / 2;

            bossAttack2 = loadImage("/images/crocs_mud.png");
            bossAttack3 = loadImage("/images/crocs_mud.png");
            bossAttack4 = loadImage("/images/waterball.png");
            bossAttack5 = loadImage("/images/skull.png");
            bossAttack = loadImage("/images/attack1.png");

            // giftBoxImages 배열에 이미지 로드
            for (int i = 0; i < giftBoxImages.length; i++) {
                giftBoxImages[i] = loadImage("/images/giftbox.png");
            }

            sightImgFire = loadImage("/images/fire.png");
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // 이미지 로드를 위한 공통 메서드
    private BufferedImage loadImage(String path) throws IOException {
        URL imgUrl = this.getClass().getResource(path);
        if (imgUrl != null) {
            return ImageIO.read(imgUrl);
        } else {
            System.out.println("Image not found: " + path);
            return null;
        }
    }

    // 여러 이미지를 로드할 수 있는 메서드
    private void loadImages(String basePath, int count, BufferedImage[] imageArray) {
        for (int i = 0; i < count; i++) {
            String path = basePath + i + ".png";
            try {
                imageArray[i] = loadImage(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

            Set<Duck> selectedDucksSet = new HashSet<>(Arrays.asList(hunterSelectedDucks));

            for (int i = 0; i < numberOfDucks; i++) {
                Duck selectedDuck;
                int index;

                // 중복되지 않는 오리를 선택
                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (selectedDucksSet.contains(selectedDuck));

                playerSelectedDucks[i] = selectedDuck;
                selectedDucksSet.add(selectedDuck);
            }
        }
    }


    // N마리 오리를 선택하는 메소드 (Hunter용)
    private void selectHunterDucks(int numberOfDucks) {
        if (ducks.size() >= numberOfDucks) {
            hunterSelectedDucks = new Duck[numberOfDucks]; // Hunter 선택된 오리 배열 초기화
            Set<Duck> selectedDucksSet = new HashSet<>();

            for (int i = 0; i < numberOfDucks; i++) {
                Duck selectedDuck;
                int index;

                // 중복되지 않는 오리를 선택
                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(hunterSelectedDucks).contains(selectedDuck) || (playerSelectedDucks != null && Arrays.asList(playerSelectedDucks).contains(selectedDuck))); // 중복 방지

                hunterSelectedDucks[i] = selectedDuck;
                selectedDucksSet.add(selectedDuck);
            }
        }
    }


    // N마리 오리를 선택하는 메소드 (Hunter용)
    private void selectFireDucks(int numberOfDucks) {
        if (ducks.size() >= numberOfDucks) {
            fireSelectedDucks = new Duck[numberOfDucks]; // Hunter 선택된 오리 배열 초기화
            Set<Duck> selectedDucksSet = new HashSet<>();

            for (int i = 0; i < 1; i++) {
                Duck selectedDuck;
                int index;

                // 중복되지 않는 오리를 선택
                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(hunterSelectedDucks).contains(selectedDuck) || Arrays.asList(hunterSelectedDucks).contains(selectedDuck) || playerSelectedDucks != null && Arrays.asList(playerSelectedDucks).contains(selectedDuck)); // 중복 방지

                fireSelectedDucks[i] = selectedDuck;
                selectedDucksSet.add(selectedDuck);

                System.out.println("Selected Fire Ducks: " + Arrays.toString(fireSelectedDucks));

            }
        }
    }

    // Hunter가 자동으로 오리를 제거하는 메소드
    private void startFireAutoKill() {
        if (fireExecutor == null || fireExecutor.isShutdown()) {
            fireExecutor = Executors.newScheduledThreadPool(1); // 새로 생성
        }
        fireExecutor.scheduleAtFixedRate(() -> {
            if (fireSelectedDucks == null || Arrays.stream(fireSelectedDucks).allMatch(Objects::isNull)) {
                // Hunter가 선택한 오리가 없으면 새롭게 선택
                selectFireDucks(1);
            }
            if (fireSelectedDucks != null) {
                for (Duck duck : fireSelectedDucks) {
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
        }, 0, 1500, TimeUnit.MILLISECONDS); // interval 시간마다 실행
    }


    // 게임이 끝나면 Hunter의 자동조준 타이머를 중지하는 코드
    private void stopFireAutoKill() {
        if (fireExecutor != null && !fireExecutor.isShutdown()) {
            fireExecutor.shutdown(); // Hunter의 자동 조준 종료
        }
    }


    private void updateFireSelectedDucks() {
        for (int i = 0; i < fireSelectedDucks.length; i++) {
            if (hunterSelectedDucks[i] == null || !ducks.contains(hunterSelectedDucks[i]) || !ducks.contains(playerSelectedDucks[i])) {
                // 새로운 오리를 선택하여 중복되지 않게 추가
                Duck selectedDuck;
                int index;

                Set<Duck> selectedDucksSet = new HashSet<>();
                selectedDucksSet.addAll(Arrays.asList(hunterSelectedDucks));
                selectedDucksSet.addAll(Arrays.asList(playerSelectedDucks));
                selectedDucksSet.addAll(Arrays.asList(fireSelectedDucks));

                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (Arrays.asList(hunterSelectedDucks).contains(selectedDuck) ||
                        Arrays.asList(playerSelectedDucks).contains(selectedDuck) ||
                        Arrays.asList(fireSelectedDucks).contains(selectedDuck)); // 중복 방지

                fireSelectedDucks[i] = selectedDuck;
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
        }, 0, 2500, TimeUnit.MILLISECONDS); // interval 시간마다 실행
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
        Set<Duck> selectedDucksSet = new HashSet<>();
        selectedDucksSet.addAll(Arrays.asList(hunterSelectedDucks));
        selectedDucksSet.addAll(Arrays.asList(playerSelectedDucks));

        for (int i = 0; i < hunterSelectedDucks.length; i++) {
            if (hunterSelectedDucks[i] == null || !ducks.contains(hunterSelectedDucks[i])) {
                // 새로운 오리를 선택하여 중복되지 않게 추가
                Duck selectedDuck;
                int index;

                do {
                    index = random.nextInt(ducks.size());
                    selectedDuck = ducks.get(index);
                } while (selectedDucksSet.contains(selectedDuck)); // 중복 방지

                hunterSelectedDucks[i] = selectedDuck;
                selectedDucksSet.add(selectedDuck);  // 선택된 오리 추가
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
                    g2d.drawImage(sightImgHunter, duck.x, duck.y,28,28,null);
                }else{
                    return;
                }
            }
        }
    }

    // 더블배럴샷건 모드에서 Hunter 선택된 오리들에게 sightImg를 그리기
    private void drawSightOnFireSelectedDucks(Graphics2D g2d) {
        if (fireSelectedDucks != null) {
            for (Duck duck : fireSelectedDucks) {
                if (duck != null) {
                    g2d.drawImage(sightImgFire, duck.x, duck.y,100,100,null);
                }else{
                    return;
                }
            }
        }
    }

    /**
     * Restart game - reset some variables.
     */
    public void restartGame() {
        // Removes all of the ducks from this list.
        ducks.clear();

        // We set last duckt time to zero.
        Duck.setLastDuckTime(0);

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
        Framework.setGameState(Framework.GameState.PAUSE);
        stopBackgroundMusic();
    }

    public void NextRound() {
        stopBackgroundMusic();
        isPause = false;
        Framework.setGameState(Framework.GameState.PLAYING);
        //Duck.lastDuckTime = 0; // 오리 타이머 초기화
        killedDucks = 0; // 죽인 오리 수 초기화
        runawayDucks = 0; // 도망간 오리 수 초기화
        round += 1;
        isBossAlive = false;
        roundPass += 30;
        if(round == 2){
            playBackgroundMusic("src/main/resources/sounds/JungleBook.wav");
        }else if(round == 3){
            playBackgroundMusic("src/main/resources/sounds/FantasticThemePark.wav");
        }else if(round == 4){
            playBackgroundMusic("src/main/resources/sounds/WhaleBelly.wav");
        }else if(round == 5){
            playBackgroundMusic("src/main/resources/sounds/TempleOfDarkness.wav");
        }

    }

    public void playActiveSound(String filePath){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
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

        if (framework.getGun().equals("더블배럴샷건")) {
            if (hunterSelectedDucks == null) {
                hunterSelectedDucks = new Duck[0]; // 빈 배열로 초기화
            }
            selectPlayerDucks(1);
        }else if(framework.getGun().equals("기본권총")){
        }
        if (isBossAlive) {
            for (int i = 0; i < boss.size(); i++) {
                boss1 currentBoss = boss.get(i);
                currentBoss.update(); // 보스 위치 업데이트

                // 라운드에 따라 공격 생성
                switch (round) {
                    case 1:
                        handleRound1BossAttack(currentBoss);
                        break;
                    case 2:
                        handleRound2BossAttack(currentBoss);
                        break;
                    case 3:
                        handleRound3BossAttack(currentBoss, mousePosition);
                        break;
                    case 4:
                        handleRound4BossAttack(currentBoss, mousePosition);
                        break;
                    case 5:
                        handleRound5BossAttack(currentBoss, mousePosition);
                        break;
                    default:
                        break;
                }

                // 공격 업데이트 및 충돌 처리
                updateBossAttacks(mousePosition);
            }
        }

        if(Hunter1&& hunterTrigger && !isPause){
            startHunterAutoKill(2500);
            hunterTrigger = false;
        }else if(!Hunter1){
        }else if(!hunterTrigger){

        }
        if (!isPause) {
            handleGiftBoxUpdates(mousePosition); // 선물 상자 업데이트 및 충돌 처리
            handleDuckUpdates(mousePosition);   // 오리 업데이트 및 생성 처리
            handlePlayerShooting(mousePosition); // 플레이어 공격 처리
            handleBossStateUpdates(mousePosition); // 보스 상태 업데이트 및 처리

            // 게임 오버 조건 확인
            if (runawayDucks >= 10 || playerHp <= 0) {
                handleGameOver();
            }
        }

        if (isPause) {
            hunterTrigger = true;
            stopHunterAutoKill();

            // 버튼 생성
            int baseX = framework.getWidth() / 2 - 350;
            int baseY = framework.getHeight() / 2 + 50;
            int buttonGap = 300;

            for (int i = 0; i < 3; i++) {
                buttonbuy.add(new Buttonbuy(baseX + buttonGap * i, baseY, buttonImg));
            }

            // 마우스 클릭 및 버튼 동작 처리
            if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
                handleButtonClick(mousePosition);
            }
        }
        if(hunters.size() > 0) {
            Hunter1 = true;
        }
    }
    // 버튼 클릭 처리 함수
    private void handleButtonClick(Point mousePosition) {
        // 헌터 구매
        if (checkButton(0,mousePosition) && money >= 200 && hunters.size() < 1) {
            hunters.add(new Hunter1(220, 290, 0, 100, hunter111Img));
            Hunter1 = true;
            money -= 200;
        }

        // 데미지 증가
        if (checkButton(1,mousePosition) && money >= 100) {
            damage += 10;
            money -= 100;
        }

        // 최대 탄약 증가
        if (checkButton(2,mousePosition) && money >= 100) {
            maxAmmo += 2;
            money -= 100;
        }
    }

    // 버튼 클릭 여부 확인 함수
    private boolean checkButton(int index,Point mousePosition) {
        Rectangle buttonArea = new Rectangle(buttonbuy.get(index).x, buttonbuy.get(index).y, 367, 257);
        return buttonArea.contains(mousePosition);
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
            if(round == 1){
                boss.add(new boss1(1200, 400, 0, 1000,200,bossImg));
            }else if(round == 2){
                boss.add(new boss1(1200,400,0,1500, 400,boss2Img));
            }else if(round == 3){
                boss.add(new boss1(1200,400, 0, 2000, 2500,boss3Img));
            }else if(round == 4){
                boss.add(new boss1(1200,400,0,3000, 6400, boss4Img));
            }else if(round == 5){
                boss.add(new boss1(1200,400,0,4000, 12000, boss5Img));
            }
            stopBackgroundMusic();
            if(round == 1) {
                playBackgroundMusic("src/main/resources/sounds/AquaCave.wav");
            }else if(round == 2){
                playBackgroundMusic("src/main/resources/sounds/RuinCastle.wav");
            }else if(round == 3){
                playBackgroundMusic("src/main/resources/sounds/ArabPirate.wav");
            }else if(round == 4){
                playBackgroundMusic("src/main/resources/sounds/SinkingThings.wav");
            }else if(round == 5){
                playBackgroundMusic("src/main/resources/sounds/WorldHorizon.wav");
            }
            isBossAlive = true; // 보스가 등장했음을 표시
            System.out.println("Boss activity");
            ducks.clear();
            bosswith3delay = false;
        }, 3, TimeUnit.SECONDS); // 3초 후 실행
    }

    private void handleRound1BossAttack(boss1 currentBoss) {
        if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 1_000_000) {
            for (int j = 0; j < 3; j++) {
                double angle = 150 + Math.random() * 70;
                bossAttacks.add(new BossAttack(currentBoss.x, currentBoss.y, angle, 15));
            }
            playActiveSound(BOSSATTCK_SOUND_PATH);
            lastBossAttackTime = System.nanoTime();
        }
    }

    private void handleRound2BossAttack(boss1 currentBoss) {
        if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
            double gravity = 6, speed = 150, deltaTime = 0.1;

            for (int j = 0; j < 4; j++) {
                double angle = Math.toRadians(150 + Math.random() * 70);
                double vx = speed * Math.cos(angle);
                double vy = speed * Math.sin(angle);
                bossAttacks2.add(new BossAttack(currentBoss.x, currentBoss.y, vx, vy, gravity, deltaTime));
            }

            playActiveSound(BOSSATTCK_SOUND_PATH);
            lastBossAttackTime = System.nanoTime();
        }
    }

    private void handleRound3BossAttack(boss1 currentBoss, Point mousePosition) {
        if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
            double gravity = 6, speed = 150, deltaTime = 0.1;

            // 랜덤 각도 공격
            for (int j = 0; j < 4; j++) {
                double angle = Math.toRadians(150 + Math.random() * 70);
                double vx = speed * Math.cos(angle);
                double vy = speed * Math.sin(angle);
                bossAttacks3.add(new BossAttack(currentBoss.x, currentBoss.y, vx, vy, gravity, deltaTime));
            }

            // 유도탄 공격
            addHomingAttack(currentBoss, mousePosition, bossAttacks3, gravity, deltaTime);

            playActiveSound(BOSSATTCK_SOUND_PATH);
            lastBossAttackTime = System.nanoTime();
        }
    }

    private void handleRound4BossAttack(boss1 currentBoss, Point mousePosition) {
        if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
            double gravity = 6, speed = 150, deltaTime = 0.1;

            // 랜덤 각도 공격
            for (int j = 0; j < 4; j++) {
                double angle = Math.toRadians(150 + Math.random() * 70);
                double vx = speed * Math.cos(angle);
                double vy = speed * Math.sin(angle);
                bossAttacks4.add(new BossAttack(currentBoss.x, currentBoss.y, vx, vy, gravity, deltaTime));
            }

            // 유도탄 공격
            addHomingAttack(currentBoss, mousePosition, bossAttacks4, gravity, deltaTime);

            // 밑에서 올라오는 공격
            if (System.nanoTime() - lastBottomAttackTime >= bottomAttackInterval * 900_000) {
                addBottomAttack(currentBoss, bossAttacks4, gravity, deltaTime);
                lastBottomAttackTime = System.nanoTime();
            }

            playActiveSound(BOSSATTCK_SOUND_PATH);
            lastBossAttackTime = System.nanoTime();
        }
    }

    private void handleRound5BossAttack(boss1 currentBoss, Point mousePosition) {
        if (System.nanoTime() - lastBossAttackTime >= bossAttackInterval * 900_000) {
            double gravity = 6, speed = 150, deltaTime = 0.1;

            // 랜덤 각도 공격
            for (int j = 0; j < 4; j++) {
                double angle = Math.toRadians(150 + Math.random() * 70);
                double vx = speed * Math.cos(angle);
                double vy = speed * Math.sin(angle);
                bossAttacks5.add(new BossAttack(currentBoss.x, currentBoss.y, vx, vy, gravity, deltaTime));
            }

            // 유도탄 공격
            addHomingAttack(currentBoss, mousePosition, bossAttacks5, gravity, deltaTime);

            // 밑에서 올라오는 공격
            addBottomAttack(currentBoss, bossAttacks5, gravity, deltaTime);

            playActiveSound(BOSSATTCK_SOUND_PATH);
            lastBossAttackTime = System.nanoTime();
        }
    }

    private void addHomingAttack(boss1 boss, Point mousePosition, List<BossAttack> attacks, double gravity, double deltaTime) {
        double speed = 150;
        double dx = mousePosition.getX() - boss.x;
        double dy = mousePosition.getY() - boss.y;
        double homingAngle = Math.atan2(dy, dx);

        double vx = speed * Math.cos(homingAngle);
        double vy = speed * Math.sin(homingAngle);
        attacks.add(new BossAttack(boss.x, boss.y, vx, vy, gravity, deltaTime));
    }
    private void addBottomAttack(boss1 boss, List<BossAttack> attacks, double gravity, double deltaTime) {
        double speed = 150;
        double screenBottomX1 = framework.getWidth() / 2.0;
        double screenBottomX2 = framework.getWidth() * (0.11 + Math.random() * (0.33 - 0.11));
        double screenBottomX3 = framework.getWidth() * (0.11 + Math.random() * (0.33 - 0.11));
        double screenBottomY = framework.getHeight();
        double upwardAngle = Math.toRadians(80 + Math.random() * 10);

        double vx = speed * Math.cos(upwardAngle);
        double vy = -speed * Math.sin(upwardAngle);

        attacks.add(new BossAttack((int) screenBottomX1, (int) screenBottomY, vx, vy, gravity, deltaTime));
        attacks.add(new BossAttack((int) screenBottomX2, (int) screenBottomY, vx, vy, gravity, deltaTime));
        attacks.add(new BossAttack((int) screenBottomX3, (int) screenBottomY, vx, vy, gravity, deltaTime));
    }

    private void updateBossAttacks(Point mousePosition) {
        List<List<BossAttack>> allAttacks = Arrays.asList(bossAttacks, bossAttacks2, bossAttacks3, bossAttacks4, bossAttacks5);

        for (List<BossAttack> attacks : allAttacks) {
            Iterator<BossAttack> iterator = attacks.iterator();
            while (iterator.hasNext()) {
                BossAttack attack = iterator.next();
                attack.updatewithgravity();

                if (attack.isHit(mousePosition)) {
                    System.out.println(HIT_MESSAGE);
                    playerHp -= 10;
                    playActiveSound(HIT_SOUND_PATH);
                    iterator.remove();
                } else if (attack.x < 0 || attack.x > framework.getWidth() || attack.y < 0 || attack.y > framework.getHeight()) {
                    iterator.remove();
                }
            }
        }
    }
    private void handleGiftBoxUpdates(Point mousePosition) {
        int maxGiftBoxes = 1;
        int random = 1 + (int) (Math.random() * 1000);

        // 선물 상자 생성
        if (System.nanoTime() - lastGiftBoxTime >= giftBoxInterval * 1_000_000) {
            if (giftBoxes.size() < maxGiftBoxes && random == 50) {
                int randomX = (int) (Math.random() * (framework.getWidth() - 50));
                int fallSpeed = 5 + (int) (Math.random() * 5);
                int giftBoxType = 1 + (int) (Math.random() * 3);
                BufferedImage selectedImg = getGiftBoxImage(giftBoxType);

                giftBoxes.add(new GiftBox(randomX, 0, 150, 150, fallSpeed, giftBoxType, selectedImg));
                lastGiftBoxTime = System.nanoTime();
                giftBoxInterval = getRandomInterval(minInterval, maxInterval);
            }
        }

        // 선물 상자 업데이트 및 충돌 처리
        Iterator<GiftBox> iterator = giftBoxes.iterator();
        while (iterator.hasNext()) {
            GiftBox giftBox = iterator.next();
            giftBox.update();

            // 충돌 확인
            if (new Rectangle(giftBox.x + 18, giftBox.y, 150, 150).contains(mousePosition)
                    && Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
                processGiftBoxEffect(giftBox);
                iterator.remove();
            }

            // 화면 밖으로 나간 상자 제거
            if (giftBox.y > framework.getHeight()) {
                iterator.remove();
            }
        }
    }
    private void processGiftBoxEffect(GiftBox giftBox) {
        System.out.println("Player collected a gift!");

        switch (giftBox.type) {
            case 1:
                activateFireEffect();
                break;
            case 2:
                modifyDuckSpeed(1, 3); // 속도 증가 및 복원
                break;
            case 3:
                increasePlayerHp(10); // 체력 증가
                break;
            default:
                System.out.println("Unknown gift box type: " + giftBox.type);
                break;
        }
    }

    // Fire 효과 활성화
    private void activateFireEffect() {
        fire = true;
        startFireAutoKill();

        scheduleTask(() -> {
            stopFireAutoKill();
            fire = false;
        }, 9, TimeUnit.SECONDS);
    }

    // 오리 속도 변경
    private void modifyDuckSpeed(int speedChange, int restoreDelaySeconds) {
        for (Duck duck : ducks) {
            if (duck.getSpeed() > -3) {
                duck.setSpeed(duck.getSpeed() + speedChange);
            }
        }

        scheduleTask(() -> {
            for (Duck duck : ducks) {
                if (duck.getSpeed() > -4) {
                    duck.setSpeed(duck.getSpeed() - speedChange);
                }
            }
        }, restoreDelaySeconds, TimeUnit.SECONDS);
    }

    // 플레이어 체력 증가
    private void increasePlayerHp(int amount) {
        playerHp += amount;
    }

    // 스케줄 작업 실행
    private void scheduleTask(Runnable task, long delay, TimeUnit timeUnit) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(task, delay, timeUnit);
    }

    private void handleDuckUpdates(Point mousePosition) {
        if (isBossAlive) {
            return;
        }
        if (System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks) {
            ducks.add(new Duck(Duck.duckLines[Duck.nextDuckLines][0] + random.nextInt(200),
                    Duck.duckLines[Duck.nextDuckLines][1],
                    Duck.duckLines[Duck.nextDuckLines][2],
                    Duck.duckLines[Duck.nextDuckLines][3], duckImg));

            Duck.incrementNextDuckLines();
            if (Duck.nextDuckLines >= Duck.duckLines.length) {
                Duck.resetNextDuckLines();
            }
            Duck.setLastDuckTime(System.nanoTime());
        }

        // 오리 업데이트
        Iterator<Duck> iterator = ducks.iterator();
        while (iterator.hasNext()) {
            Duck duck = iterator.next();
            duck.Update();

            if (duck.x < 0 - duckImg.getWidth()) {
                iterator.remove();
                runawayDucks++;
            }
        }

        // 보스 생성 조건 확인
        if (killedDucks >= roundPass && !isBossAlive) {
            spawnBossWithDelay();
            stopBackgroundMusic();
            playBackgroundMusic("src/main/resources/sounds/warning.wav");
            isBossAlive = true;
            ducks.clear();
        }
    }
    private void handlePlayerShooting(Point mousePosition) {
        if (!isReadyToShoot()) return; // 발사 조건 충족 확인

        if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
            handleSelectedDucks(); // 선택된 오리 처리
            if (isReloading) {
                System.out.println("Reloading... Please wait!");
                return;
            }

            if (ammo <= 0) {
                System.out.println("Out of ammo! Reloading...");
                Reload(); // 재장전
                return;
            }

            shootDuck(mousePosition); // 일반 오리와의 충돌 처리
            lastTimeShoot = System.nanoTime(); // 마지막 발사 시간 갱신
        }
    }

    // 발사 조건 충족 확인
    private boolean isReadyToShoot() {
        return System.nanoTime() - lastTimeShoot >= timeBetweenShots && !isReloading;
    }

    // 선택된 오리 처리
    private void handleSelectedDucks() {
        if (playerSelectedDucks != null) {
            for (int i = 0; i < playerSelectedDucks.length; i++) {
                if (playerSelectedDucks[i] != null && ducks.contains(playerSelectedDucks[i])) {
                    removeDuck(playerSelectedDucks[i]);
                    playerSelectedDucks[i] = null; // 초기화
                }
            }
            updateAndReselectPlayerDucks(1); // 선택된 오리 갱신
        }
    }

    // 일반 오리 처리
    private void shootDuck(Point mousePosition) {
        shoots++;
        playActiveSound("src/main/resources/sounds/gun.wav");
        ammo--; // 탄약 소모

        Iterator<Duck> iterator = ducks.iterator();
        while (iterator.hasNext()) {
            Duck duck = iterator.next();
            if (isDuckHit(duck, mousePosition)) {
                removeDuck(duck);
                break; // 한 마리만 처리
            }
        }
    }

    // 오리 제거
    private void removeDuck(Duck duck) {
        playActiveSound("src/main/resources/sounds/quack.wav");
        killedDucks++;
        money += 10;
        score += duck.score;
        ducks.remove(duck); // 리스트에서 제거
    }




    private boolean isDuckHit(Duck duck, Point mousePosition) {
        return new Rectangle(duck.x + 18, duck.y, 27, 30).contains(mousePosition) ||
                new Rectangle(duck.x + 30, duck.y + 30, 88, 25).contains(mousePosition);
    }
    private void handleBossStateUpdates(Point mousePosition) {
        if (!isBossAlive || boss.isEmpty()) return; // 보스가 없으면 처리 중단

        Iterator<boss1> bossIterator = boss.iterator();
        while (bossIterator.hasNext()) {
            boss1 currentBoss = bossIterator.next();
            currentBoss.update(); // 보스 위치 및 상태 업데이트

            // 플레이어 공격 처리
            if (Canvas.mouseButtonState(MouseEvent.BUTTON1) &&
                    new Rectangle(currentBoss.x, currentBoss.y, 378, 268).contains(mousePosition)) {
                currentBoss.health -= damage; // 보스 체력 감소
                playActiveSound("src/main/resources/sounds/gun.wav");

                // 보스 체력이 0 이하일 경우
                if (currentBoss.health <= 0) {
                    money += 100;
                    score += currentBoss.score;
                    bossIterator.remove();// 보스를 제거
                    isBossAlive = false;

                    if (round == 5) {
                        Framework.setGameState(Framework.GameState.ENDING); // 엔딩 상태로 전환
                        stopBackgroundMusic();
                        playBackgroundMusic("src/main/resources/sounds/NewBeginningNotTheEnd.wav");
                        System.out.println("Game Ending Triggered");
                    } else {
                        Pause(); // 다음 라운드를 위해 일시 정지
                    }
                }
            }
        }
    }

    private void handleGameOver() {
        Framework.setGameState(Framework.GameState.GAMEOVER);
        if (!leaderboardSaved) {
            framework.saveScore(score);
            framework.saveMoney(score);
            leaderboardSaved = true;
        }
    }

    /**
     * Draw the game to the screen.
     *
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */

    public void Draw(Graphics2D g2d, Point mousePosition) {
        drawBackground(g2d);
        drawDucks(g2d);
        drawBossWarning(g2d);
        drawHunter1(g2d);
        drawBossAttacks(g2d);
        drawBoss(g2d);
        drawButtonsAndShop(g2d);
        drawGiftBoxes(g2d);
        configureGunSettings(g2d);
        drawGunSightAndReloadStatus(g2d, mousePosition);
        drawHUD(g2d);
    }

    // 배경 그리기
    private void drawBackground(Graphics2D g2d) {
        Image[] backgrounds = { backgroundImg, backgroundImg2, backgroundImg3, backgroundImg4, backgroundImg5 };
        if (round >= 1 && round <= 5) {
            g2d.drawImage(backgrounds[round - 1], 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }
    }

    // 모든 오리 그리기
    private void drawDucks(Graphics2D g2d) {
        for (int i = 0; i < ducks.size(); i++) {
            ducks.get(i).Draw(g2d);
        }
    }

    // 보스 공격 경고 그리기
    private void drawBossWarning(Graphics2D g2d) {
        if (bosswith3delay) {
            g2d.drawImage(warningImg, Framework.frameWidth / 2 - 275, Framework.frameHeight / 2 - 250, null);
        }
    }

    // 헌터1 그리기 및 관련 처리
    private void drawHunter1(Graphics2D g2d) {
        if (Hunter1) {
            g2d.drawImage(hunter111Img, hunters.get(0).x, hunters.get(0).y, null);
            drawSightOnHunterSelectedDucks(g2d);
        }
    }

    // 보스 공격 처리 (보스 공격 목록 및 이미지 그리기)
    private void drawBossAttacks(Graphics2D g2d) {
        List<List<BossAttack>> bossAttackLists = Arrays.asList(bossAttacks, bossAttacks2, bossAttacks3, bossAttacks4, bossAttacks5);
        List<Image> bossAttackImages = Arrays.asList(bossAttack, bossAttack2, bossAttack3, bossAttack4, bossAttack5);

        for (int i = 0; i < bossAttackLists.size(); i++) {
            List<BossAttack> currentList = bossAttackLists.get(i);
            Image currentImage = bossAttackImages.get(i);

            if (!currentList.isEmpty()) {
                for (BossAttack attack : currentList) {
                    g2d.drawImage(currentImage, attack.x, attack.y, 100, 100, null);
                }
            }
        }
    }

    // 보스 그리기 및 HP 바 그리기
    private void drawBoss(Graphics2D g2d) {
        if (!boss.isEmpty()) {
            for (int i = 0; i < boss.size(); i++) {
                BufferedImage bossImage = getBossImageForRound();
                if (bossImage != null) {
                    g2d.drawImage(bossImage, boss.get(i).x - 90, boss.get(i).y - 20, 378, 268, null);
                }

                drawBossHealthBar(g2d, i);
            }
        }
    }

    private BufferedImage getBossImageForRound() {
        switch (round) {
            case 1: return bossImg;
            case 2: return boss2Img;
            case 3: return boss3Img;
            case 4: return boss4Img;
            case 5: return boss5Img;
            default: System.err.println("Warning: Unexpected gift box type: " + round); return null;
        }
    }

    private void drawBossHealthBar(Graphics2D g2d, int i) {
        int currentHealth = boss.get(i).health;
        int maxHealth = boss.get(i).maxHealth;
        int hpIndex = (int) ((currentHealth / (double) maxHealth) * 11);
        hpIndex = Math.max(0, Math.min(11, hpIndex));

        int hpBarWidth = hpImages[hpIndex].getWidth(null) / 8;
        int hpBarHeight = hpImages[hpIndex].getHeight(null) / 8;
        g2d.drawImage(hpImages[hpIndex], boss.get(i).x - 20, boss.get(i).y - 60, hpBarWidth, hpBarHeight, null);
    }

    // 버튼과 상점 그리기
    private void drawButtonsAndShop(Graphics2D g2d) {
        if (isPause) {
            int buyWidth = buttonImg.getWidth(null) / 2;
            int buyHeight = buttonImg.getHeight(null) / 2;
            for (int i = 0; i < buttonbuy.size(); i++) {
                g2d.drawImage(buttonImg, buttonbuy.get(i).x, buttonbuy.get(i).y, buyWidth, buyHeight, null);
                if (i < shopImages.length) {
                    g2d.drawImage(shopImages[i], buttonbuy.get(i).x - 60, buttonbuy.get(i).y - 250, 300, 300, null);
                }
            }
        }
    }

    // 선물 상자 그리기
    private void drawGiftBoxes(Graphics2D g2d) {
        if (giftBoxes != null) {
            for (GiftBox giftBox : giftBoxes) {
                BufferedImage selectedImg = getGiftBoxImage(giftBox.type);
                if (selectedImg != null) {
                    g2d.drawImage(selectedImg, giftBox.x, giftBox.y, giftBox.width, giftBox.height, null);
                }
            }
        }
    }

    private BufferedImage getGiftBoxImage(int type) {
        switch (type) {
            case 1: return giftBoxImg1;
            case 2: return giftBoxImg2;
            case 3: return giftBoxImg3;
            default:
                System.err.println("Warning: Unexpected gift box type: " + type);
                return null;
        }
    }

    // 총기 설정
    private void configureGunSettings(Graphics2D g2d) {
        if (framework.getGun().equals("더블배럴샷건")) {
            drawSightOnPlayerSelectedDucks(g2d);
            reloadDuration = 2500000000L;
        } else if (framework.getGun().equals("AK-47")) {
            maxAmmo = 30;
            reloadDuration = 3000000000L;
            timeBetweenShots = 100_000_000L;
        }
    }

    // 총기 조준선과 리로드 상태 그리기
    private void drawGunSightAndReloadStatus(Graphics2D g2d, Point mousePosition) {
        if (fire) {
            drawSightOnFireSelectedDucks(g2d);
        }
        if (isReloading) {
            g2d.drawString("Reloading", Framework.frameWidth / 2, Framework.frameHeight / 2);
        }

        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);
    }

    // HUD (Heads-Up Display) 그리기
    private void drawHUD(Graphics2D g2d) {
        g2d.setFont(font);
        g2d.setColor(Color.darkGray);

        g2d.drawString("Ammo: " + ammo + "/" + maxAmmo, 10, 50);
        g2d.drawString("RUNAWAY: " + runawayDucks, 10, 21);
        g2d.drawString("KILLS: " + killedDucks, 160, 21);
        g2d.drawString("SHOOTS: " + shoots, 299, 21);
        g2d.drawString("SCORE: " + score, 440, 21);
        g2d.drawString("round: " + round, 570, 21);
        g2d.drawString("Money: " + money, 700, 21);
        g2d.drawString("PlayerHP: " + playerHp, 10, 80);
    }

    /**
     * Draw the game over screen.
     *
     * @param g2d Graphics2D
     * @param mousePosition Current mouse position.
     */
    public void drawGameOver(Graphics2D g2d, Point mousePosition)
    {
        Draw(g2d, mousePosition);
        g2d.drawImage(gameoverImg, 0,0,Framework.frameWidth,Framework.frameHeight, null);
        g2d.drawImage(gameoverfImg, Framework.frameWidth/2,Framework.frameHeight/2,400,400, null);
    }
    public void drawEnding(Graphics2D g2d, Point mousePosition, long gameTime) {
        // 배경화면 설정 (엔딩 전용 배경 이미지)
        g2d.drawImage(endingImages[ed], 0, 0, Framework.frameWidth-50, Framework.frameHeight-50, null);
    }

    private void Reload() {
        isReloading = true;
        playActiveSound("src/main/resources/sounds/reloading.wav");
        reloadStartTime = System.nanoTime();
    }
}
