package kr.jbnu.se.std;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.sql.Time;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MessageReceiver {
    private final OkHttpClient client;
    private final String idToken;
    private final Set<String> receivedMessageKeysF;
    private final Set<String> existingFriends;
    private final Set<String> receivedMessageKeysM;
    private ChatwithFriends chatwithFriends;// 채팅을 표시하는 UI 컴포넌트
    private MainClient mainClient;
    private final ScheduledExecutorService schedulerM = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService schedulerF = Executors.newScheduledThreadPool(1);
    private String nickname;
    private String selectnickname;
    private String email;
    private static final Logger logger = Logger.getLogger(MessageReceiver.class.getName());


    public MessageReceiver(String idToken, MainClient MainClient,String email){
        this.idToken = idToken;
        this.email = email;
        this.client = new OkHttpClient();
        this.receivedMessageKeysF = new HashSet<>();
        this.receivedMessageKeysM = new HashSet<>();
        this.existingFriends = new HashSet<>();
        this.mainClient = MainClient;
    }
    public MessageReceiver(String idToken, ChatwithFriends chatwithFriends,String nickname,String selectnickname) {
        this.client = new OkHttpClient();
        this.idToken = idToken;
        this.nickname = nickname;
        this.selectnickname = selectnickname;
        this.receivedMessageKeysF = new HashSet<>();
        this.receivedMessageKeysM = new HashSet<>();
        this.existingFriends = new HashSet<>();
        this.chatwithFriends = chatwithFriends;
    }

    public void receiveMessagesFriends() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/chatfriend/"
                + nickname + selectnickname + ".json?auth=" + idToken;
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> System.err.println("채팅 메시지 가져오기 실패: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.isEmpty()) {
                        SwingUtilities.invokeLater(() -> System.out.println("채팅 내역이 존재하지 않습니다."));
                        return;
                    }

                    for (String key : jsonResponse.keySet()) {
                        JSONObject messageData = jsonResponse.getJSONObject(key);
                        String message = messageData.getString("message");
                        String senderNickname = messageData.getString("nickname");

                        if (!receivedMessageKeysF.contains(key)) {
                            receivedMessageKeysF.add(key);
                            String uniqueMessage = senderNickname + ": " + message;
                            SwingUtilities.invokeLater(() -> chatwithFriends.setChat(uniqueMessage + "\n"));
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(() -> System.err.println("채팅 메시지 가져오기 실패: " + response.message()));
                }
            }
        });
    }

    public void receiveMessagesFriends2() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/chatfriend/"+ selectnickname+nickname+".json?auth=" + idToken;

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> System.err.println("채팅 메시지 가져오기 실패: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.isEmpty()) {
                        SwingUtilities.invokeLater(() -> System.out.println("채팅 내역이 존재하지 않습니다."));
                        return;
                    }

                    for (String key : jsonResponse.keySet()) {
                        JSONObject messageData = jsonResponse.getJSONObject(key);
                        String message = messageData.getString("message");
                        String senderNickname = messageData.getString("nickname");

                        if (!receivedMessageKeysF.contains(key)) {
                            receivedMessageKeysF.add(key);
                            String uniqueMessage = senderNickname + ": " + message;
                            SwingUtilities.invokeLater(() -> chatwithFriends.setChat(uniqueMessage + "\n"));
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(() -> System.err.println("채팅 메시지 가져오기 실패: " + response.message()));
                }
            }
        });
    }

    public void receiveMessages() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://shootthedock-default-rtdb.firebaseio.com/chat.json?auth=" + idToken;

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> System.err.println("채팅 메시지 가져오기 실패: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.isEmpty()) {
                        SwingUtilities.invokeLater(() -> System.out.println("채팅 내역이 존재하지 않습니다."));
                        return;
                    }

                    for (String key : jsonResponse.keySet()) {
                        JSONObject messageData = jsonResponse.getJSONObject(key);
                        String message = messageData.getString("message");
                        String senderNickname = messageData.getString("nickname");

                        if (!receivedMessageKeysM.contains(key)) {
                            receivedMessageKeysM.add(key);
                            String uniqueMessage = senderNickname + ": " + message;
                            SwingUtilities.invokeLater(() -> mainClient.setChat(uniqueMessage + "\n"));
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(() -> System.err.println("채팅 메시지 가져오기 실패: " + response.message()));
                }
            }
        });
    }

    public void receiveFriends() {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(
                "https://shootthedock-default-rtdb.firebaseio.com/users/%s/userinfo/friends.json?auth=%s",
                email, idToken
        );

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() ->
                        System.err.println("친구 목록 가져오기 실패: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    processFriendsResponse(response.body().string());
                } else {
                    SwingUtilities.invokeLater(() ->
                            System.err.println("친구 목록 가져오기 실패: " + response.message())
                    );
                }
            }
        });
    }

    // 친구 목록 응답 처리
    private void processFriendsResponse(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);

            if (jsonObject.isEmpty()) {
                SwingUtilities.invokeLater(() ->
                        System.out.println("친구가 없습니다.")
                );
                return;
            }

            addNewFriends(jsonObject);

        } catch (JSONException e) {
            handleFriendsError(e);
        }
    }

    // 새로운 친구 추가
    private void addNewFriends(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            try {
                JSONObject friendObject = jsonObject.getJSONObject(key);
                String nickname = friendObject.getString("nickname");

                if (!existingFriends.contains(nickname)) {
                    existingFriends.add(nickname);
                    SwingUtilities.invokeLater(() ->
                            mainClient.setFriends(nickname + "\n")
                    );
                }
            } catch (JSONException e) {
                logger.warning("친구 처리 중 오류 발생: " + e.getMessage());
            }
        }
    }

    // 친구 목록 처리 중 오류
    private void handleFriendsError(JSONException e) {
        logger.warning(e.getMessage());
        SwingUtilities.invokeLater(() ->
                System.err.println("친구 목록 처리 중 오류 발생: " + e.getMessage())
        );
    }


    public void startReceivingMessages() {
        // 0초 후에 시작하고, 5초마다 receiveMessages 메소드를 호출
        schedulerM.scheduleAtFixedRate(this::receiveMessages, 0, 1, TimeUnit.SECONDS);
        schedulerM.scheduleAtFixedRate(this::receiveFriends, 0, 1, TimeUnit.SECONDS);
    }

    public void stopReceivingMessages() {
        // 0초 후에 시작하고, 5초마다 receiveMessages 메소드를 호출
        schedulerM.shutdown();
    }

    public void startReceivingFriendMessages(){
        schedulerF.scheduleAtFixedRate(this::receiveMessagesFriends, 0, 1, TimeUnit.SECONDS);
        schedulerF.scheduleAtFixedRate(this::receiveMessagesFriends2, 0, 1, TimeUnit.SECONDS);
    }

    public void stopReceivingFriendMessages() {
        // 0초 후에 시작하고, 5초마다 receiveMessages 메소드를 호출
        schedulerF.shutdown();
    }
}
