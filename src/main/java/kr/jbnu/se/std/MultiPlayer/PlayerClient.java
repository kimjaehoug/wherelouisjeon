package kr.jbnu.se.std.MultiPlayer;

import kr.jbnu.se.std.*;
import kr.jbnu.se.std.Canvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerClient {

    private Random random;
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
    private Duck duck;
    private ScheduledExecutorService hunterExecutor;

    /**
     * Font that we will use to write statistic to the screen.
     */
    private Font font;
    boolean hunterTrigger = true;

    private BufferedImage bossImg;
    private BufferedImage[] hpImages = new BufferedImage[12]; // HP 이미지를 저장할 배열

    /**
     * Array list of the ducks.
     */
    private ArrayList<Duck> ducks;
    private ArrayList<boss1> boss;
    private ArrayList<Buttonbuy> buttonbuy;
    private ArrayList<kr.jbnu.se.std.Hunter1> Hunters;

    /**
     * How many ducks leave the screen alive?
     */
    private int runawayDucks;

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
    private GameClient multiplayerClient;

    private GameServer server;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Framework framework;
    public PlayerClient(Framework framework,String host, int port) throws IOException {
        connectToServer(host, port);
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
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void UpdateGame(long gameTime, Point mousePosition){
        if(System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks){
            receiveDuckUpdates();

            Duck.nextDuckLines++;
            if(Duck.nextDuckLines >= Duck.duckLines.length){
                Duck.nextDuckLines = 0;
            }
            Duck.lastDuckTime = System.nanoTime();
        }

        for(int i = 0; i < ducks.size(); i++){
            ducks.get(i).Update();

            if(ducks.get(i).x < 0 - duckImg.getWidth()){
                ducks.remove(i);
                runawayDucks++;
            }
        }
        if(Canvas.mouseButtonState(MouseEvent.BUTTON1)){
            if(System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks){
                for(int i = 0; i < ducks.size(); i++){
                    if(new Rectangle(ducks.get(i).x + 18, ducks.get(i).y     , 27, 30).contains(mousePosition) ||
                            new Rectangle(ducks.get(i).x + 30, ducks.get(i).y + 30, 88, 25).contains(mousePosition)){
                        killedDucks++;
                        server.plusscore(score);
                        server.deleteDuck(ducks.get(i));

                        break;
                    }
                }
                lastTimeShoot = System.nanoTime();
            }
        }
        if(runawayDucks >= 200){
            Framework.gameState = Framework.GameState.GAMEOVER;
        }
    }

    public void Draw(Graphics2D g2d, Point mousePosition){
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth,Framework.frameHeight,null);
        for(int i = 0; i < ducks.size(); i++){
            ducks.get(i).Draw(g2d);
        }

        g2d.drawImage(grassImg, 0, Framework.frameHeight - grassImg.getHeight(), Framework.frameWidth, grassImg.getHeight(), null);

        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);

        g2d.setFont(font);
        g2d.setColor(Color.darkGray);

        g2d.drawString("RUNAWAY: " + runawayDucks, 10, 21);
        g2d.drawString("KILLS: " + killedDucks, 160, 21);
        g2d.drawString("SHOOTS: " + shoots, 299, 21);
        g2d.drawString("SCORE: " + score, 440, 21);
    }
    public void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        // 오리 정보 수신 스레드
        new Thread(this::receiveDuckUpdates).start();
    }

    private void receiveDuckUpdates() {
        try {
            while (true) {
                duck = (Duck) inputStream.readObject(); // 서버로부터 오리 정보를 수신
                updateClientDucks((ArrayList<Duck>) Collections.singletonList(duck)); // 클라이언트의 오리 정보를 업데이트
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateClientDucks(ArrayList<Duck> ducks) {
        this.ducks = ducks;
    }

    // 나머지 코드...
}
