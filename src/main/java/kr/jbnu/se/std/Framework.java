package kr.jbnu.se.std;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
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
    public static final long SECINNANOSEC = 1000000000L;

    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long MILISECINNANOSEC = 1000000L;

    /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = SECINNANOSEC / GAME_FPS;

    /**
     * Possible states of the game
     */
    public enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING,LOGIN,MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, MAINPAGE, ROUND, PAUSE, ENDING, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;

    /**
     * Elapsed game time in nanoseconds.
     */
    @SuppressWarnings("squid:S1948")
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @SuppressWarnings("squid:S1948")
    private final ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
    @SuppressWarnings("squid:S1948")
    private final ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
    @SuppressWarnings("squid:S1948")
    private final ScheduledExecutorService scheduler3 = Executors.newScheduledThreadPool(1);
    private final Set<String> existingFriendsinvite = new HashSet<>(); // 중복 방지를 위한 Set

    private long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;



    // The actual game
    @SuppressWarnings("squid:S1948")
    private Game game;
    @SuppressWarnings("squid:S1948")
    //TODO: 자꾸 final을 달았는데도 버그가 안 없어짐. 개빡침.
    private final Window window;
    private static final String DATABASE_URL = "https://shootthedock-default-rtdb.firebaseio.com";
    @SuppressWarnings("squid:S1948")
    private String email;
    private String nickname;
    private String idToken;
    private String password;
    private String realemail;
    @SuppressWarnings("squid:S1948")
    private final MainClient mainV2;
    private int money;
    private InviteFriends inviteFriends;
    private String whatgun;
    @SuppressWarnings("squid:S1948")
    //TODO: 자꾸 final을 달았는데도 버그가 안 없어짐. 개빡침.
    public final FirebaseClient firebaseClient;
    public transient FriendManager friendManager;
    private transient Clip clip;
    private static final String NICKNAME_KEY = "nickname";

    private static final String APPLICATION_JSON = "application/json; charset=utf-8";
    private static final String FIREBASE_SIGNIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";
    private static final String FIREBASE_API_KEY = "AIzaSyCJDgbBXWSRoRUg3xVqsQrSEz1W5AFiE_Y";

    private static final String FIREBASE_BASE_URL = "https://shootthedock-default-rtdb.firebaseio.com/users/";
    private static final String USER_INFO_SUFFIX = "/userinfo.json?auth=";
    private static final String MONEY_KEY = "money";
    private static final String SCORE_SAVE_FAILURE_MESSAGE = "사용자 정보에 점수 저장 실패: ";
    private static final String ERROR_MESSAGE = "데이터 가져오기 실패...";

    private static final Logger logger = Logger.getLogger(Framework.class.getName());


    private static Framework instance;
    @SuppressWarnings("squid:S1948")
    public MessageManager messageManager;
    @SuppressWarnings("squid:S1948")
    public MessageReceiver messageReceiver;
    @SuppressWarnings("squid:S1948")
    public MessageReceiver friendmessageReceiver;
    @SuppressWarnings("squid:S1948")
    public InventoryManager inventoryManager;

    private boolean running = true; // 루프 제어 변수 추가

    /**
     * Image for menu.
     */
    @SuppressWarnings("squid:S1948")
    private BufferedImage shootTheDuckMenuImg;


    public Framework (Window window)
    {
        super();
        initializeFirebase();
        this.window = window;
        gameState = GameState.LOGIN;
        OkHttpClient clientInstance = new OkHttpClient();
        loginClient = new LoginClient(this);
        loginClient.setVisible(true);
        mainV2 = new MainClient(this);
        whatgun = "기본권총";
        mainV2.setVisible(false);
        this.setVisible(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseClient = new FirebaseClient(email);
    }
    public void Invitewindow(){
            inviteFriends = new InviteFriends(this);
            startRecevingFriendInvite();
            if(inviteFriends == null){
                stopReceivingFriendInvite();
            }
    }

    public void inventoryWindow(){
        InventoryWindow inventoryWindow = new InventoryWindow(this);
        inventoryManager= new InventoryManager(email,idToken,inventoryWindow,money);
        inventoryManager.startReceivingInventory();
        if(inventoryWindow == null){
            inventoryManager.stopReceivingInventory();
        }
    }
    public void stoploginClinet(){
        loginClient = null;
    }


    public void rankWindow(){
        RankWindow rankWindow = new RankWindow();
        rankWindow.setVisible(true);
    }

    public void Shopwindowopen(){
        ShopWindow shopWindow = new ShopWindow(this);
        inventoryManager= new InventoryManager(email,idToken,shopWindow,money);

    }
    public void ChatFriendswindow(String nickname){
        ChatwithFriends chatwithFriends = new ChatwithFriends(this);
        chatwithFriends.setFriends(nickname);
        String selectnickname = chatwithFriends.getFriends();
        friendmessageReceiver = new MessageReceiver(idToken,chatwithFriends,this.nickname,selectnickname);
        friendmessageReceiver.startReceivingFriendMessages();

        if(chatwithFriends == null){
            friendmessageReceiver.stopReceivingFriendMessages();
        }
    }


    private void playBackgroundMusic() {
        try {
            String filePath = "src/main/resources/sounds/backgroundonMain.wav"; // 파일 경로 하드코딩
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 무한 반복
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.log(Level.WARNING, "An error occurred: ", e); //스택트레이스도 함께 기록
        }
    }

    public void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }



    public void frendsAddwindows(){
        AddFriends addFriends  = new AddFriends(this);
    }


    public void stopReceivingFriendschat() {
        scheduler1.shutdownNow();
    }

    public void startRecevingFriendInvite(){
        scheduler2.scheduleAtFixedRate(this::receiveFriendsInvite, 0, 1, TimeUnit.SECONDS);
    }

    public void stopReceivingFriendInvite() {
        scheduler2.shutdownNow();
    }
    public void receiveFriendsInvite() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/friend/" + nickname + "/userinfo/friendswant.json?auth=" + idToken;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() ->
                    logger.warning("친구 목록 가져오기 실패: " + e.getMessage())
                );
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        // JSON 객체가 비어있는지 확인
                        if (jsonObject.isEmpty()) {
                            SwingUtilities.invokeLater(() ->
                                logger.warning("친구 신청이 없습니다.")
                            );
                            return;
                        }
                        // 친구 신청 목록 출력
                        for (String key : jsonObject.keySet()) {
                            JSONObject inviteObject = jsonObject.getJSONObject(key); // 친구 신청 객체
                            String friendNickname = inviteObject.getString(NICKNAME_KEY); // 친구의 닉네임
                            // 중복된 친구 신청이 아닌 경우에만 추가
                            if (!existingFriendsinvite.contains(friendNickname)) {
                                existingFriendsinvite.add(friendNickname); // 새로운 친구 신청 추가
                                SwingUtilities.invokeLater(() ->
                                    inviteFriends.setFriends(friendNickname + "\n") // 친구 목록에 추가
                                );
                            }
                        }
                    } catch (JSONException e) {
                        logger.log(Level.WARNING, "An error occurred: ", e); //스택트레이스도 함께 기록
                        SwingUtilities.invokeLater(() -> {
                            logger.warning("친구 신청 목록 처리 중 오류 발생: " + e.getMessage());
                            stopReceivingFriendInvite();
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() ->
                        logger.warning("친구 목록 가져오기 실패: " + response.message())
                    );
                }
            }
        });
    }
    public void deleteFriendInvite(String nicknameToDelete) {
        OkHttpClient client = new OkHttpClient();
        // "nickname" 키를 사용하여 friendswant 밑의 데이터를 삭제하는 URL
        String url = "https://shootthedock-default-rtdb.firebaseio.com/friend/" + nickname + "/userinfo/friendswant/" + nicknameToDelete + "/nickname.json?auth=" + idToken;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() ->
                    logger.warning("친구 삭제 실패: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response){
                if (response.isSuccessful()) {
                    SwingUtilities.invokeLater(() ->
                        logger.info("친구 삭제 성공: " + nicknameToDelete)
                        // 여기서 UI 업데이트 등 추가 작업 가능
                    );
                } else {
                    SwingUtilities.invokeLater(() ->
                        logger.warning("친구 삭제 실패: " + response.message())
                    );
                }
            }
        });
    }
    public void setGun(String gun){
        whatgun = gun;
    }
    public String getGun(){
        return whatgun;
    }
    private void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/shootthedock-firebase-adminsdk-304qc-09167d3967.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred: ", e); //스택트레이스도 함께 기록
            JOptionPane.showMessageDialog(this, "Firebase 초기화 실패: " + e.getMessage());
        }
    }
    private void loginWithFirebase(String email, String password) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        json.put("returnSecureToken", true);
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse(APPLICATION_JSON));
        Request request = new Request.Builder()
                .url(FIREBASE_SIGNIN_URL + FIREBASE_API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "로그인 실패: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    SwingUtilities.invokeLater(() -> {
                        String responseBody;
                        try {
                            responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            // ID 토큰 가져오기
                            idToken = jsonResponse.getString("idToken");
                            logger.info("ID 토큰: " + idToken);
                            // 사용자의 닉네임을 가져옵니다.
                            getNickname(idToken);
                            getMoney();
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

    public void getMoney() {
        OkHttpClient client = new OkHttpClient();
        String url = FIREBASE_BASE_URL + email + USER_INFO_SUFFIX + idToken;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() ->
                    logger.warning(ERROR_MESSAGE + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.has(MONEY_KEY)) {
                        money = jsonResponse.getInt(MONEY_KEY);
                        logger.info("money: " + money);
                        mainV2.setMoney(money);
                    } else {
                        logger.warning("사용자 정보가 존재하지 않습니다.");
                    }
                } else {
                    SwingUtilities.invokeLater(() ->
                        logger.warning(ERROR_MESSAGE + response.message())
                    );
                }
            }
        });
    }


    public void saveScore(int score) {
        OkHttpClient client = new OkHttpClient();

        // Step 1: 사용자 정보에 점수 저장
        String uniqueKey = String.valueOf(System.currentTimeMillis()); // 시간 기반의 고유 키 생성
        JSONObject userJson = new JSONObject();
        try {
            userJson.put(uniqueKey, score); // 시간 기반의 고유 키 아래에 점수만 저장
        } catch (JSONException e) {
            logger.warning("JSON 생성 오류: " + e.getMessage());
            return;
        }

        RequestBody userBody = RequestBody.create(userJson.toString(), MediaType.parse(APPLICATION_JSON));
        Request userRequest = new Request.Builder()
                .url(FIREBASE_BASE_URL + email + "/userinfo/scores.json?auth=" + idToken)
                .patch(userBody) // 데이터를 추가할 때는 PATCH를 사용하여 기존 데이터를 유지
                .build();

        client.newCall(userRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.warning(SCORE_SAVE_FAILURE_MESSAGE + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    logger.warning("사용자 정보에 점수 저장 성공");

                    // Step 2: 최고 점수 확인 및 리더보드 업데이트
                    checkAndSaveLeaderboard(score);
                } else {
                    logger.warning(SCORE_SAVE_FAILURE_MESSAGE + response.code());
                }
            }
        });
    }


    public void saveMoney(int money) {
        OkHttpClient client = new OkHttpClient();
        money = this.money + money;
        // Step 1: 사용자 정보에 점수 저장
        JSONObject userJson = new JSONObject();
        userJson.put(MONEY_KEY, money);

        RequestBody userBody = RequestBody.create(userJson.toString(), MediaType.parse(APPLICATION_JSON));
        Request userRequest = new Request.Builder()
                .url(FIREBASE_BASE_URL + email + USER_INFO_SUFFIX + idToken)
                .patch(userBody)
                .build();

        client.newCall(userRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.warning(SCORE_SAVE_FAILURE_MESSAGE + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    logger.info("사용자 정보에 점수 저장 성공");
                } else {
                    logger.warning(SCORE_SAVE_FAILURE_MESSAGE + response.code());
                }
            }
        });
    }

    private void checkAndSaveLeaderboard(int latestScore) {
        OkHttpClient client = new OkHttpClient();

        // 사용자 정보에서 모든 점수를 가져옴
        String userScoresUrl = FIREBASE_BASE_URL + email + "/userinfo/scores.json?auth=" + idToken;
        Request request = new Request.Builder()
                .url(userScoresUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.warning("점수 목록 가져오기 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    // 응답이 비어 있거나 유효하지 않은 경우 처리
                    if (responseBody.trim().isEmpty()) {
                        logger.warning("응답이 비어있거나 잘못되었습니다.");
                        return;
                    }

                    try {
                        int highestScore = latestScore;

                        // 응답이 JSON 객체인지 확인
                        JSONObject scoresObject = new JSONObject(responseBody);

                        // 객체에서 모든 점수 탐색 (타임스탬프를 키로 사용)
                        Iterator<String> keys = scoresObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            int score = scoresObject.getInt(key);
                            if (score > highestScore) {
                                highestScore = score;
                            }
                        }

                        // 리더보드에 최고 점수가 있는지 확인 후 없으면 저장
                        saveToLeaderboardIfHighest(highestScore);

                    } catch (JSONException e) {
                        logger.warning("JSON 파싱 오류: " + e.getMessage());
                    }
                } else {
                    logger.warning("점수 목록 가져오기 실패: " + response.code());
                }
            }
        });
    }




    private void saveToLeaderboardIfHighest(int highestUserScore) {
        OkHttpClient client = new OkHttpClient();

        // 리더보드 URL 정의
        String leaderboardUrl = "https://shootthedock-default-rtdb.firebaseio.com/leaderboard.json?auth=" + idToken;

        // 리더보드 정보를 GET 요청으로 가져옴
        Request getLeaderboardRequest = new Request.Builder()
                .url(leaderboardUrl)
                .get()
                .build();

        client.newCall(getLeaderboardRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.warning("리더보드 점수 가져오기 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        boolean isNewHighScore = true;

                        // 리더보드가 비어 있지 않다면 현재 최고 점수 확인
                        if (!responseBody.trim().isEmpty() && !responseBody.equals("{}")) {
                            JSONObject leaderboardObject = new JSONObject(responseBody);
                            if (leaderboardObject.has(nickname)) {
                                // 자신의 점수를 찾았으면, 기존 점수와 비교
                                int existingScore = leaderboardObject.getJSONObject(nickname).getInt("score");
                                if (existingScore >= highestUserScore) {
                                    isNewHighScore = false;
                                }
                            }
                        }

                        // 새로운 최고 점수라면 리더보드에 추가 또는 갱신
                        if (isNewHighScore) {
                            addToLeaderboard(highestUserScore);
                        }
                    } catch (JSONException e) {
                        logger.warning("JSON 파싱 오류: " + e.getMessage());
                    }
                } else {
                    logger.warning("리더보드 점수 가져오기 실패: " + response.code());
                }
            }
        });
    }

    private void addToLeaderboard(int highestUserScore) {
        OkHttpClient client = new OkHttpClient();

        // 리더보드 URL 정의, 닉네임을 키로 사용
        String leaderboardUrl = "https://shootthedock-default-rtdb.firebaseio.com/leaderboard/" + nickname + ".json?auth=" + idToken;

        // 리더보드에 저장할 JSON 객체 생성
        JSONObject newEntry = new JSONObject();
        try {
            newEntry.put(NICKNAME_KEY, nickname);
            newEntry.put("score", highestUserScore);
        } catch (JSONException e) {
            logger.warning("JSON 생성 오류: " + e.getMessage());
            return;
        }

        // 새로운 점수 추가를 위한 PUT 요청
        RequestBody body = RequestBody.create(newEntry.toString(), MediaType.parse(APPLICATION_JSON));
        Request updateRequest = new Request.Builder()
                .url(leaderboardUrl) // 닉네임을 키로 사용해 저장
                .put(body)
                .build();

        client.newCall(updateRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.warning("리더보드 업데이트 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    logger.warning("리더보드 업데이트 성공: 최고 점수 " + highestUserScore);
                } else {
                    logger.warning("리더보드 업데이트 실패: " + response.code());
                }
            }
        });
    }







    public void getNickname(String idToken) {
        OkHttpClient client = new OkHttpClient();
        String url = FIREBASE_BASE_URL + email + USER_INFO_SUFFIX + idToken;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> 
                    logger.warning(ERROR_MESSAGE + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.has(NICKNAME_KEY)) {
                        nickname = jsonResponse.getString(NICKNAME_KEY);
                        logger.info("Nickname: " + nickname);
                        mainV2.setNickname(nickname);
                        friendManager = new FriendManager(email,nickname);
                        messageManager = new MessageManager(nickname);
                        messageReceiver = new MessageReceiver(idToken,mainV2,email);
                        messageReceiver.startReceivingMessages();
                    } else {
                        logger.warning("사용자 정보가 존재하지 않습니다.");
                    }
                } else {
                    SwingUtilities.invokeLater(() ->
                        logger.warning(ERROR_MESSAGE + response.message())
                    );
                }
            }
        });
    }

    public void onLoginSuccess() {
        isLoginSuccessful = true;
        loginWithFirebase(realemail, password);
        mainV2.setVisible(true);
        stoploginClinet();
        playBackgroundMusic();
    }

    public void onGameStart(){
        mainV2.dispose();
        stopBackgroundMusic();
        window.onLoginSuccess();
        gameState = GameState.VISUALIZING;
        this.setVisible(true);
        Thread gameThread = new Thread(this::GameLoop); // 메서드 참조
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
        while(running)
        {
            beginTime = System.nanoTime();
            switch (gameState)
            {
                case ENDING:
                    gameTime += System.nanoTime() - lastTime;
                    game.UpdateGame(gameTime, mousePosition());
                    lastTime = System.nanoTime();
                    break;
                case PAUSE:
                    gameTime += System.nanoTime() - lastTime;
                    game.UpdateGame(gameTime, mousePosition());
                    lastTime = System.nanoTime();
                    break;
                case MAINPAGE:
                    gameState = GameState.STARTING;
                    break;
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;
                    game.UpdateGame(gameTime, mousePosition());
                    lastTime = System.nanoTime();
                    break;
                case GAMEOVER:
                    gameTime += System.nanoTime() - lastTime;
                    lastTime = System.nanoTime();
                    running = false;
                    logger.info("게임이 종료되었습니다.");
                    break;
                case LOGIN:
                    if (isLoginSuccessful) {
                        gameState = GameState.MAINPAGE;
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
                    if(this.getWidth() > 1 && visualizingTime > SECINNANOSEC)
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
                default:
                    logger.info("Unhandled GameState: " + gameState);
                    break;
            }

            // Repaint the screen.
            repaint();

            // Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / MILISECINNANOSEC; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10)
                timeLeft = 10; //set a minimum
            try {
                //Provides the necessary delay and also yields control so that other thread can do work.
                Thread.sleep(timeLeft);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 상태 복원
                logger.warning("Thread interrupted: " + e.getMessage());
            }
        }
    }

    @Override
    public void Draw(Graphics2D g2d) {
            switch (gameState) {
                case ENDING:
                    game.DrawEnding(g2d, mousePosition(),gameTime);
                    break;
                case PAUSE:
                    game.Draw(g2d, mousePosition());
                    break;
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
                    break;
                case OPTIONS:
                    //...
                    break;
                case GAME_CONTENT_LOADING:
                    g2d.setColor(Color.white);
                    g2d.drawString("GAME is LOADING", frameWidth / 2 - 50, frameHeight / 2);
                    break;
                default:
                    logger.info("Unhandled GameState: " + gameState);
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
        game = new Game(this);
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

    private void nextRoundGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();


        game.NextRound();

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
            case ENDING:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    System.exit(0);
                }
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    game.ed++;
                }
                break;
            case PAUSE:
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    nextRoundGame();
                }
                break;
                //TODO: GAMEOVER하면 restartGame() 수정하기.
            case GAMEOVER:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    restartGame(); // 게임 재시작
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gameState = GameState.MAIN_MENU;  // 메인 메뉴로 돌아감
                }
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
    public void mouseClicked(MouseEvent e) {
        if (gameState == GameState.MAIN_MENU) { // 게임 상태가 MAIN_MENU일 때만 처리
            if (e.getButton() == MouseEvent.BUTTON1) { // 마우스 왼쪽 버튼 클릭인지 확인
                newGame(); // 새 게임을 시작하는 메서드 호출
            }
        } else {
            // MAIN_MENU가 아닌 상태에서는 특별히 처리할 동작이 없음
            logger.info("Unhandled game state: " + gameState);
        }
    }

}