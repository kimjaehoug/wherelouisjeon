package kr.jbnu.se.std;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class FriendManager {
    private String email;
    private String nickname;

    public FriendManager(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public void addFriend(String nickname) {
        JSONObject json = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        json.put("nickname", nickname);
        String othernickname;
        othernickname = nickname;
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/userinfo/friends/" + nickname + ".json")
                .patch(body)
                .build();

        sendRequest(request, "닉네임 저장",client);
    }

    public void addFriendToOther(String otherNickname) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("nickname", nickname);
        System.out.println("JSON 데이터: " + json.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/friend/" + otherNickname + "/userinfo/friendswant/" + nickname + ".json")
                .patch(body)
                .build();

        sendRequest(request, "친구추가 저장",client);

    }

    private void sendRequest(Request request, String action,OkHttpClient client) {
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
                    System.out.println(action + " 성공"+response.body().string());
                }
                response.close();
            }
        });
    }
}
