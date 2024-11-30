package kr.jbnu.se.std;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

public class MessageManager {
    private String nickname;
    private OkHttpClient client;
    private static final Logger logger = Logger.getLogger(MessageManager.class.getName());

    public MessageManager(String nickname) {
        this.nickname = nickname;
        this.client = new OkHttpClient();
    }

    public void sendMessageToFriend(String selectedNick, String message) {
        sendMessageToUrl("https://shootthedock-default-rtdb.firebaseio.com/chatfriend/" + selectedNick + nickname + "/", message);
    }

    public void sendMessage(String message) {
        sendMessageToUrl("https://shootthedock-default-rtdb.firebaseio.com/chat/", message);
    }

    private void sendMessageToUrl(String baseUrl, String message) {
        try {
            JSONObject json = new JSONObject();
            json.put("message", message);
            json.put("nickname", nickname);
            String timestamp = String.valueOf(System.currentTimeMillis());

            String url = baseUrl + timestamp + ".json";

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            sendRequest(request, "메시지 저장");
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    private void sendRequest(Request request, String action) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println(action + " 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println(action + " 실패: " + response.code());
                } else {
                    System.out.println(action + " 성공: " + response.body().string());
                }
            }
        });
    }
}
