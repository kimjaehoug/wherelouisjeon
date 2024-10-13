package kr.jbnu.se.std;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;


public class FirebaseClient {
    public String email;
    public FirebaseClient(String email) {
        this.email = email;
    }
    public void senderDatabase(String Dataname,String DataPath, String Data){
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
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
    public void receiverDatabase(String DataPath){

    }
}
