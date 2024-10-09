package kr.jbnu.se.std;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.*;
import com.google.firebase.database.core.AuthTokenProvider;
import com.google.gson.JsonObject;
import jdk.jfr.internal.tool.Main;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import com.google.firebase.database.FirebaseDatabase;

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
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING,LOGIN,MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, MainPage, Round, Pause, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;

    /**
     * Elapsed game time in nanoseconds.
     */

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService scheduler3 = Executors.newScheduledThreadPool(1);
    private final Set<String> existingFriends = new HashSet<>(); // 중복 방지를 위한 Set
    private final Set<String> existingFriendsinvite = new HashSet<>(); // 중복 방지를 위한 Set

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
    private MainClient MainV2;
    private AddFriends addFriends;
    private DatabaseReference chatRef;
    private final Set<String> receivedMessageKeys = new HashSet<>();
    private final Set<String> receivedMessageKeysF = new HashSet<>(); // 이미 받은 메시지의 키를 저장할 Set
    private ChatwithFriends chatwithFriends;
    private String selectnickname;
    private int money;
    private InviteFriends inviteFriends;
    private ShopWindow shopWindow;
    private InventoryWindow inventoryWindow;
    private String inventoryimage;
    private String whatgun;


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
        MainV2 = new MainClient(this);

        MainV2.setVisible(false);
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
    public void Invitewindow(){
            inviteFriends = new InviteFriends(this);
            startRecevingFriendInvite();
            if(inviteFriends == null){
                stopReceivingFriendInvite();
            }
    }

    public void inventoryWindow(){
        inventoryWindow = new InventoryWindow(this);
        startReceivingInventory();

    }
    public void stopfriendadd(){
        addFriends = null;
    }

    public void stopfriends(){
        inviteFriends = null;
    }
    public void stoploginClinet(){
        loginClient = null;
    }

    public void stopshop(){
        shopWindow = null;
    }

    public void stopmain(){
        MainV2 = null;
    }


    public void Shopwindowopen(){
        shopWindow = new ShopWindow(this);
    }
    public void ChatFriendswindow(String nickname){
        chatwithFriends = new ChatwithFriends(this);
        chatwithFriends.setFriends(nickname);
        selectnickname = chatwithFriends.getFriends();
        startRecevingFriendschat();
        if(chatwithFriends == null){
            stopReceivingFriendschat();
        }
    }



    public void frendsAddwindows(){
        addFriends = new AddFriends(this);
    }

    public void buySomeThing(int sell) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        if(sell < money) {
            int money = this.money - sell;
            json.put("money", money);
            // 사용자 ID를 키로 사용하여 닉네임 저장
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request = new Request.Builder()
                    .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/" + "userinfo.json")
                    .patch(body)// POST 메소드 사용
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("인벤토리 저장 실패: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        System.err.println("인벤토리 저장 성공: " + response.code());
                    } else {
                    }
                }
            });
        }else{

        }
    }
    public void InventoryAdder_Gun(String item) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("item", item);
        // 사용자 ID를 키로 사용하여 닉네임 저장
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/"+ "userinfo/inventory/Gun/"+item+".json")
                .patch(body)// POST 메소드 사용
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("인벤토리 저장 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("인벤토리 저장 성공: " + response.code());
                }else{
                }
            }
        });
    }


    public void friendsAdder(String nickname) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        System.err.println(nickname);
        json.put("nickname", nickname);
        // 사용자 ID를 키로 사용하여 닉네임 저장
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/"+ "userinfo/friends/"+nickname+".json")
                .patch(body)// POST 메소드 사용
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("닉네임 저장 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("닉네임 저장 성공: " + response.code());
                }else{
                }
            }
        });
    }

    public void friendsAdderother(String nickname) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        System.err.println(nickname);
        json.put("nickname", this.nickname);
        // 사용자 ID를 키로 사용하여 닉네임 저장
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/friend/" + nickname + "/"+ "userinfo/friendswant/"+this.nickname+".json")
                .patch(body) // POST 메소드 사용
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("친구추가 저장 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("친구추가 저장 실패: " + response.code());
                }else{
                }
            }
        });
    }
    // Firebase에 메시지 전송
    public void sendMessageFriend(String selectednick,String message) {
        try {
            // 메시지를 JSON 형식으로 변환
            JSONObject json = new JSONObject();
            json.put("message", message);
            json.put("nickname", nickname);
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Firebase에 저장할 URL
            String url = "https://shootthedock-default-rtdb.firebaseio.com/chatfriend/"+ selectednick+nickname+"/"+timestamp+".json"; // 채팅 메시지를 저장하는 경로

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url(url)
                    .put(body) // POST 메소드 사용
                    .build();

            // 비동기 호출
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("메시지 저장 실패: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        System.err.println("메시지 저장 실패: " + response.code());
                    } else {
                        System.out.println("메시지 저장 성공: " + response.body().string());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Firebase에 메시지 전송
    public void sendMessage(String message) {
        try {
            // 메시지를 JSON 형식으로 변환
            JSONObject json = new JSONObject();
            json.put("message", message);
            json.put("nickname", nickname);
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Firebase에 저장할 URL
            String url = "https://shootthedock-default-rtdb.firebaseio.com/chat/"+ timestamp+".json"; // 채팅 메시지를 저장하는 경로

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url(url)
                    .put(body) // POST 메소드 사용
                    .build();

            // 비동기 호출
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("메시지 저장 실패: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        System.err.println("메시지 저장 실패: " + response.code());
                    } else {
                        System.out.println("메시지 저장 성공: " + response.body().string());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Firebase에서 메시지 수신
    public void startReceivingInventory(){
        scheduler3.scheduleAtFixedRate(this::receivedinventory, 0, 1, TimeUnit.MINUTES);

    }
    public void startReceivingMessages() {
        // 0초 후에 시작하고, 5초마다 receiveMessages 메소드를 호출
        scheduler.scheduleAtFixedRate(this::receiveMessages, 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::receiveFriends, 0, 1, TimeUnit.SECONDS);
    }

    public void startRecevingFriendschat() {
        scheduler1.scheduleAtFixedRate(this::receiveMessagesFriends, 0, 1, TimeUnit.SECONDS);
        scheduler1.scheduleAtFixedRate(this::receiveMessagesFriends2, 0, 1, TimeUnit.SECONDS);
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

    public void receivedinventory() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/users/"+ email+"/userinfo/inventory/Gun"+".json?auth=" + idToken; // 채팅 메시지를 가져올 URL

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("채팅 메시지 가져오기 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // JSON 객체가 비어있는지 확인
                    if (jsonResponse.length() == 0) {
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("채팅 내역이 존재하지 않습니다.");
                        });
                        return;
                    }

                    // 채팅 메시지 출력
                    for (String key : jsonResponse.keySet()) {
                        JSONObject inventoryData = jsonResponse.getJSONObject(key);
                        String inventory = inventoryData.getString("item");
                        inventoryimage = "" ;
                        if(inventory.equals("더블배럴샷건")){
                            inventoryimage = "src/main/resources/images/duck.png";
                        }

                        // 인벤토리 적용하기 (타임스탬프 키 사용)
                        if (!receivedMessageKeysF.contains(key)) {
                            receivedMessageKeysF.add(key); // 새 메시지 키 추가
                            String uniqueMessage = inventory; // 고유 메시지 생성
                            SwingUtilities.invokeLater(() -> {
                                inventoryWindow.addPanel(uniqueMessage,inventoryimage);
                            });
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("인벤토리 가져오기 실패: " + response.message());
                    });
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
    public void receiveMessagesFriends2() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/chatfriend/"+ nickname+selectnickname+".json?auth=" + idToken; // 채팅 메시지를 가져올 URL

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("채팅 메시지 가져오기 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // JSON 객체가 비어있는지 확인
                    if (jsonResponse.length() == 0) {
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("채팅 내역이 존재하지 않습니다.");
                        });
                        return;
                    }

                    // 채팅 메시지 출력
                    for (String key : jsonResponse.keySet()) {
                        JSONObject messageData = jsonResponse.getJSONObject(key);
                        String message = messageData.getString("message");
                        String senderNickname = messageData.getString("nickname");

                        // 이미 받은 메시지인지 확인 (타임스탬프 키 사용)
                        if (!receivedMessageKeysF.contains(key)) {
                            receivedMessageKeysF.add(key); // 새 메시지 키 추가
                            String uniqueMessage = senderNickname + ": " + message; // 고유 메시지 생성
                            SwingUtilities.invokeLater(() -> {
                                chatwithFriends.setChat(uniqueMessage + "\n"); // 채팅 영역에 메시지 추가
                            });
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("채팅 메시지 가져오기 실패: " + response.message());
                    });
                }
            }
        });
    }

    public void receiveMessagesFriends() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/chatfriend/"+ selectnickname+nickname+".json?auth=" + idToken; // 채팅 메시지를 가져올 URL

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("채팅 메시지 가져오기 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // JSON 객체가 비어있는지 확인
                    if (jsonResponse.length() == 0) {
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("채팅 내역이 존재하지 않습니다.");
                        });
                        return;
                    }

                    // 채팅 메시지 출력
                    for (String key : jsonResponse.keySet()) {
                        JSONObject messageData = jsonResponse.getJSONObject(key);
                        String message = messageData.getString("message");
                        String senderNickname = messageData.getString("nickname");

                        // 이미 받은 메시지인지 확인 (타임스탬프 키 사용)
                        if (!receivedMessageKeysF.contains(key)) {
                            receivedMessageKeysF.add(key); // 새 메시지 키 추가
                            String uniqueMessage = senderNickname + ": " + message; // 고유 메시지 생성
                            SwingUtilities.invokeLater(() -> {
                                chatwithFriends.setChat(uniqueMessage + "\n"); // 채팅 영역에 메시지 추가
                            });
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("채팅 메시지 가져오기 실패: " + response.message());
                    });
                }
            }
        });
    }

    public void receiveMessages() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/chat.json?auth=" + idToken; // 채팅 메시지를 가져올 URL

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("채팅 메시지 가져오기 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // JSON 객체가 비어있는지 확인
                    if (jsonResponse.length() == 0) {
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("채팅 내역이 존재하지 않습니다.");
                        });
                        return;
                    }

                    // 채팅 메시지 출력
                    for (String key : jsonResponse.keySet()) {
                        JSONObject messageData = jsonResponse.getJSONObject(key);
                        String message = messageData.getString("message");
                        String senderNickname = messageData.getString("nickname");

                        // 이미 받은 메시지인지 확인 (타임스탬프 키 사용)
                        if (!receivedMessageKeys.contains(key)) {
                            receivedMessageKeys.add(key); // 새 메시지 키 추가
                            String uniqueMessage = senderNickname + ": " + message; // 고유 메시지 생성
                            SwingUtilities.invokeLater(() -> {
                                MainV2.setChat(uniqueMessage + "\n"); // 채팅 영역에 메시지 추가
                            });
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("채팅 메시지 가져오기 실패: " + response.message());
                    });
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
                SwingUtilities.invokeLater(() -> {
                    System.err.println("친구 삭제 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("친구 삭제 성공: " + nicknameToDelete);
                        // 여기서 UI 업데이트 등 추가 작업 가능
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("친구 삭제 실패: " + response.message());
                    });
                }
            }
        });
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
                SwingUtilities.invokeLater(() -> {
                    System.err.println("친구 목록 가져오기 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);

                        // JSON 객체가 비어있는지 확인
                        if (jsonObject.length() == 0) {
                            SwingUtilities.invokeLater(() -> {
                                System.out.println("친구 신청이 없습니다.");
                            });
                            return;
                        }

                        // 친구 신청 목록 출력
                        for (String key : jsonObject.keySet()) {
                            JSONObject inviteObject = jsonObject.getJSONObject(key); // 친구 신청 객체
                            String friendNickname = inviteObject.getString("nickname"); // 친구의 닉네임

                            // 중복된 친구 신청이 아닌 경우에만 추가
                            if (!existingFriendsinvite.contains(friendNickname)) {
                                existingFriendsinvite.add(friendNickname); // 새로운 친구 신청 추가
                                SwingUtilities.invokeLater(() -> {
                                    inviteFriends.setFriends(friendNickname + "\n"); // 친구 목록에 추가
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            System.err.println("친구 신청 목록 처리 중 오류 발생: " + e.getMessage());
                            stopReceivingFriendInvite();
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("친구 목록 가져오기 실패: " + response.message());
                    });
                }
            }
        });
    }


    public void receiveFriends() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/userinfo/friends.json?auth=" + idToken;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("친구 목록 가져오기 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);

                        // JSON 객체가 비어있는지 확인
                        if (jsonObject.length() == 0) {
                            SwingUtilities.invokeLater(() -> {
                                System.out.println("친구가 없습니다.");
                            });
                            return;
                        }

                        // 친구 목록 출력
                        for (String key : jsonObject.keySet()) {
                            JSONObject friendObject = jsonObject.getJSONObject(key); // 친구 객체
                            String nickname = friendObject.getString("nickname"); // 친구의 닉네임

                            // 중복된 친구가 아닌 경우에만 추가
                            if (!existingFriends.contains(nickname)) {
                                existingFriends.add(nickname); // 새로운 친구 추가
                                SwingUtilities.invokeLater(() -> {
                                    MainV2.setFriends(nickname + "\n"); // 친구 목록에 추가
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            System.err.println("친구 목록 처리 중 오류 발생: " + e.getMessage());
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        System.err.println("친구 목록 가져오기 실패: " + response.message());
                    });
                }
            }
        });
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

                    if (jsonResponse.has("money")) {
                        money = jsonResponse.getInt("money");
                        System.out.println("money: " + money);
                        MainV2.setMoney(money);
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

    public void saveScore(int score) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("nickname", this.nickname);
        json.put("score", score);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/leaderboard/"+nickname+".json")
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("점수 저장 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("점수 저장 성공: " + response.body().string());
                } else {
                    System.err.println("점수 저장 실패: " + response.code());
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
                        MainV2.setNickname(nickname);
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
        loginWithFirebase(realemail, password);
        MainV2.setVisible(true);
        stoploginClinet();
        startReceivingMessages();
    }

    public void onGameStart(){
        MainV2.dispose();
        window.onLoginSuccess();
        stopshop();
        stopfriendadd();
        stopfriends();
        stopReceivingFriendschat();
        stopReceivingFriendInvite();
        gameState = GameState.VISUALIZING;
        this.setVisible(true);
        gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
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
                case Pause:

                    break;
                case MainPage:
                    gameState = GameState.STARTING;
                    break;
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;

                    game.UpdateGame(gameTime, mousePosition());

                    lastTime = System.nanoTime();
                    break;
                case GAMEOVER:
                    break;
                case LOGIN:
                    if (isLoginSuccessful) {
                        gameState = GameState.MainPage;
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
                case Pause:
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
            case Pause:
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    nextRoundGame();
                }
                break;
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