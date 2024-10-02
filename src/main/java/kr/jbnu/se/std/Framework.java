package kr.jbnu.se.std;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.*;
import com.google.firebase.database.core.AuthTokenProvider;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;


/**
 * kr.jbnu.se.std.Framework that controls the game (kr.jbnu.se.std.Game.java) that created it, update it and draw it on the screen.
 *
 * @author www.gametutorial.net
 */

public class Framework extends Canvas {

    private boolean isLoginSuccessful = false; // 로그인 성공 여부를 관리
    private LoginClient loginClient;
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds.
     * 1 second = 1 000 000 000 nanoseconds
     */
    public static final long secInNanosec = 1000000000L;

    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;

    /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;

    /**
     * Possible states of the game
     */
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING,LOGIN,MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;

    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;

    // The actual game
    private Game game;
    private Thread gameThread;
    private Window window;
    private String userid;
    private static final String DATABASE_URL = "https://shootthedock-default-rtdb.firebaseio.com";
    private OkHttpClient client;
    private String email;
    private String nickname;
    private String idToken;
    private String password;
    private String realemail;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;


    /**
     * Image for menu.
     */
    private BufferedImage shootTheDuckMenuImg;


    public Framework (Window window)
    {
        super();
        initializeFirebase();
        this.window = window;
        gameState = GameState.LOGIN;
        client = new OkHttpClient();
        loginClient = new LoginClient(this);
        loginClient.setVisible(true);
        this.setVisible(false);


        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void refreshIdToken(String email) {
        try {
            // 사용자의 ID 토큰을 새로 고치기 위한 로직
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            String uid = userRecord.getUid();

            // Custom token을 생성합니다.
            String customToken = FirebaseAuth.getInstance().createCustomToken(uid);
            this.idToken = customToken; // 새로 생성된 ID 토큰
            System.out.println("New ID Token: " + idToken);
        } catch (FirebaseAuthException e) {
            System.err.println("Error refreshing ID Token: " + e.getMessage());
        }
    }



    private void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/shootthedock-firebase-adminsdk-304qc-09167d3967.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://shootthedock-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Firebase 초기화 실패: " + e.getMessage());
        }
    }
    private void loginWithFirebase(String email, String password) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        json.put("returnSecureToken", true);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyCJDgbBXWSRoRUg3xVqsQrSEz1W5AFiE_Y")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "로그인 실패: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    SwingUtilities.invokeLater(() -> {
                        String responseBody;
                        try {
                            responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            // ID 토큰 가져오기
                            idToken = jsonResponse.getString("idToken");
                            System.out.println("ID 토큰: " + idToken);
                            // 사용자의 닉네임을 가져옵니다.
                            getNickname(idToken);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "로그인 실패: 잘못된 자격 증명"));
                }
            }
        });
    }


    public void getNickname(String idToken) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/userinfo.json?auth=" + idToken;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("데이터 가져오기 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.has("nickname")) {
                        nickname = jsonResponse.getString("nickname");
                        System.out.println("Nickname: " + nickname);
                    } else {
                        System.err.println("사용자 정보가 존재하지 않습니다.");
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("데이터 가져오기 실패: " + response.message());
                    });
                }
            }
        });
    }



    public void onLoginSuccess() {
        isLoginSuccessful = true;
        gameState = GameState.VISUALIZING;
        this.setVisible(true);
        window.onLoginSuccess();
        loginWithFirebase(realemail, password);
        gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };// 게임 창 표시
        gameThread.start();
    }
    /**
     * Set variables and objects.
     * This method is intended to set the variables and objects for this class, variables and objects for the actual game can be set in kr.jbnu.se.std.Game.java.
     */
    private void Initialize()
    {
        // 화면 크기 정보를 가져옵니다.
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle screenSize = gd.getDefaultConfiguration().getBounds();
        frameWidth = (int) screenSize.getWidth();
        frameHeight = (int) screenSize.getHeight();
        this.setSize(frameWidth, frameHeight);
    }


    /**
     * Load files - images, sounds, ...
     * This method is intended to load files for this class, files for the actual game can be loaded in kr.jbnu.se.std.Game.java.
     */
    private void LoadContent()
    {
        try
        {
            URL shootTheDuckMenuImgUrl = this.getClass().getResource("/images/menu.jpg");
            shootTheDuckMenuImg = ImageIO.read(shootTheDuckMenuImgUrl);

            // 이미지가 화면 크기에 맞게 조정됩니다.
            shootTheDuckMenuImg = resizeImage(shootTheDuckMenuImg, frameWidth, frameHeight);
        }
        catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void getEmail(String email){
        realemail = email;
    }
    public void getPassword(String password){
        this.password = password;
    }
    public void getIdtoken(String idToken){
        this.idToken = idToken;
    }

    public void getUserId(String userId){
        email = userId;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 이미지의 가로와 세로 비율을 계산합니다.
        float aspectRatio = (float) originalWidth / originalHeight;

        int newWidth;
        int newHeight;

        // 화면의 비율에 맞게 이미지를 조정합니다.
        if (targetWidth / (float) targetHeight > aspectRatio) {
            newWidth = (int) (targetHeight * aspectRatio);
            newHeight = targetHeight;
        } else {
            newWidth = targetWidth;
            newHeight = (int) (targetWidth / aspectRatio);
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }



    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is updated and then the game is drawn on the screen.
     */
    private void GameLoop()
    {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();

        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;

        while(true)
        {
            beginTime = System.nanoTime();

            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;

                    game.UpdateGame(gameTime, mousePosition());

                    lastTime = System.nanoTime();
                    break;
                case GAMEOVER:
                    //...
                    break;
                case LOGIN:
                    if (isLoginSuccessful) {
                        gameState = GameState.VISUALIZING;
                    }
                    break;
                case MAIN_MENU:
                    //...
                    break;
                case OPTIONS:
                    //...
                    break;
                case GAME_CONTENT_LOADING:
                    //...
                    break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();

                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                    break;
                case VISUALIZING:
                    // On Ubuntu OS (when I tested on my old computer) this.getWidth() method doesn't return the correct value immediately (eg. for frame that should be 800px width, returns 0 than 790 and at last 798px).
                    // So we wait one second for the window/frame to be set to its correct size. Just in case we
                    // also insert 'this.getWidth() > 1' condition in case when the window/frame size wasn't set in time,
                    // so that we although get approximately size.
                    if(this.getWidth() > 1 && visualizingTime > secInNanosec)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();

                        // When we get size of frame we change status.
                        gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                    break;
            }

            // Repaint the screen.
            repaint();

            // Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10)
                timeLeft = 10; //set a minimum
            try {
                //Provides the necessary delay and also yields control so that other thread can do work.
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }

    @Override
    public void Draw(Graphics2D g2d) {
            switch (gameState) {
                case PLAYING:
                    game.Draw(g2d, mousePosition());
                    break;
                case GAMEOVER:
                    game.DrawGameOver(g2d, mousePosition());
                    break;
                case MAIN_MENU:
                    // 중앙에 이미지를 배치합니다.
                    int x1 = (frameWidth - shootTheDuckMenuImg.getWidth()) / 2;
                    int y1 = (frameHeight - shootTheDuckMenuImg.getHeight()) / 2;
                    g2d.drawImage(shootTheDuckMenuImg, x1, y1, null);
                    g2d.drawString("Nickname :" + nickname, frameWidth / 2 - 83, (int) (frameHeight * 0.61));
                    // 나머지 텍스트는 화면 중앙에 맞게 배치합니다.
                    g2d.drawString("Use left mouse button to shot the duck.", frameWidth / 2 - 83, (int) (frameHeight * 0.65));
                    g2d.drawString("Click with left mouse button to start the game.", frameWidth / 2 - 100, (int) (frameHeight * 0.67));
                    g2d.drawString("Press ESC any time to exit the game.", frameWidth / 2 - 75, (int) (frameHeight * 0.70));
                    g2d.setColor(Color.white);
                    g2d.drawString("WWW.GAMETUTORIAL.NET", 7, frameHeight - 5);
                    break;
                case OPTIONS:
                    //...
                    break;
                case GAME_CONTENT_LOADING:
                    g2d.setColor(Color.white);
                    g2d.drawString("GAME is LOADING", frameWidth / 2 - 50, frameHeight / 2);
                    break;
            }
    }
    /**
     * Starts new game.
     */
    private void newGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();

        game = new Game();
    }

    /**
     *  Restart game - reset game time and call RestartGame() method of game object so that reset some variables.
     */
    private void restartGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();

        game.RestartGame();

        // We change game status so that the game can start.
        gameState = GameState.PLAYING;
    }

    /**
     * Returns the position of the mouse pointer in game frame/window.
     * If mouse position is null than this method return 0,0 coordinate.
     *
     * @return Point of mouse coordinates.
     */
    private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();

            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }

    /**
     * This method is called when keyboard key is released.
     *
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        switch (gameState)
        {
            case GAMEOVER:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    System.exit(0);
                else if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
                    restartGame();
                break;
            case PLAYING:
            case MAIN_MENU:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    System.exit(0);
                break;
        }
    }

    /**
     * This method is called when mouse button is clicked.
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        switch (gameState)
        {
            case MAIN_MENU:
                if(e.getButton() == MouseEvent.BUTTON1)
                    newGame();
                break;
        }
    }
}