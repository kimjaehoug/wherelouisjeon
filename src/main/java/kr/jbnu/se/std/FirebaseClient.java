package kr.jbnu.se.std;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;



public class FirebaseClient {
    public String email;
    OkHttpClient client;
    JSONObject json;

    public FirebaseClient(String email) {
        this.email = email;
        client = new OkHttpClient();
    }

    public void senderDatabase(String Dataname,String DataPath, String Data){
        client = new OkHttpClient();
        json = new JSONObject();
        json.put(Dataname, Data);
        // 사용자 ID를 키로 사용하여 닉네임 저장
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(DataPath+Data+".json")
                .patch(body)// POST 메소드 사용
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println(Dataname+"저장 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println(Dataname+"저장 성공: " + response.code());
                }else{
                }
            }
        });
    }

    // Firebase에 메시지 전송
    public void sendMessage(String message, String Data,String nickname) {
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

    public void receiverDatabase(String DataPath){

    }
}
