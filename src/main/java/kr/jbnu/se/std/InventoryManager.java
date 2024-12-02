package kr.jbnu.se.std;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import javax.swing.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InventoryManager {

    private final OkHttpClient client = new OkHttpClient();
    private final String email;
    private String inventoryImage;
    private final String idToken;
    private final Set<String> receivedMessageKeysF = new HashSet<>();
    private InventoryWindow inventoryWindow;
    private final int currentMoney;
    private final ScheduledExecutorService schedulerI = Executors.newScheduledThreadPool(1);

    private ShopWindow shopWindow;


    public InventoryManager(String email, String idToken, InventoryWindow inventoryWindow,int currentMoney) {
        this.email = email;
        this.idToken = idToken;
        this.inventoryWindow = inventoryWindow;
        this.currentMoney = currentMoney;
    }

    public InventoryManager(String email, String idToken, ShopWindow shopWindow,int currentMoney) {
        this.email = email;
        this.idToken = idToken;
        this.shopWindow = shopWindow;
        this.currentMoney = currentMoney;
    }

    public void buySomething(int sell) {
        if (sell < currentMoney) {
            int money = currentMoney - sell;
            JSONObject json = new JSONObject();
            json.put("money", money);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request = new Request.Builder()
                    .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/userinfo.json")
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("구매 실패: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        System.err.println("구매 성공: " + response.code());
                    }
                }
            });
        }
    }

    public void addGunToInventory(String item) {
        JSONObject json = new JSONObject();
        json.put("item", item);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + email + "/userinfo/inventory/Gun/" + item + ".json")
                .patch(body)
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
                }
            }
        });
    }

    public void retrieveInventory() {
        String url = String.format(
                "https://shootthedock-default-rtdb.firebaseio.com/users/%s/userinfo/inventory/Gun.json?auth=%s",
                email, idToken
        );

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> System.err.println("인벤토리 가져오기 실패: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    handleInventoryResponse(response.body().string());
                } else {
                    SwingUtilities.invokeLater(() -> System.err.println("인벤토리 가져오기 실패: " + response.message()));
                }
            }
        });
    }

    // 인벤토리 응답 처리 함수
    private void handleInventoryResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);

            if (jsonResponse.isEmpty()) {
                SwingUtilities.invokeLater(() -> System.out.println("인벤토리 데이터가 없습니다."));
                return;
            }

            for (String key : jsonResponse.keySet()) {
                JSONObject inventoryData = jsonResponse.getJSONObject(key);
                String inventory = inventoryData.getString("item");
                String inventoryImage = getInventoryImage(inventory);

                if (!receivedMessageKeysF.contains(key)) {
                    receivedMessageKeysF.add(key);
                    SwingUtilities.invokeLater(() -> inventoryWindow.addPanel(inventory, inventoryImage));
                }
            }
        } catch (JSONException e) {
            SwingUtilities.invokeLater(() -> System.err.println("JSON 파싱 오류: " + e.getMessage()));
        }
    }

    // 인벤토리 이미지 경로 반환 함수
    private String getInventoryImage(String inventory) {
        switch (inventory) {
            case "더블배럴샷건":
                return "src/main/resources/images/gun_01.png";
            case "AK-47":
                return "src/main/resources/images/gun_02.png";
            case "핸드건":
                return "src/main/resources/images/gun_03.png";
            default:
                return "src/main/resources/images/default_gun.png"; // 기본 이미지
        }
    }


    public void startReceivingInventory(){
        schedulerI.scheduleAtFixedRate(this::retrieveInventory, 0, 1, TimeUnit.MINUTES);

    }
    public void stopReceivingInventory(){
        schedulerI.shutdown();
    }
    public void closeshopwindow(){
        this.shopWindow.dispose();
    }
}
