package kr.jbnu.se.std;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import okhttp3.*;
import org.json.JSONObject;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String idToken;

    private FirebaseManager(String serviceAccountPath, String databaseUrl) {
        try {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();
            FirebaseApp.initializeApp(options);

            this.auth = FirebaseAuth.getInstance();
            this.databaseReference = FirebaseDatabase.getInstance().getReference();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Firebase 초기화 실패: " + e.getMessage());
        }
    }

    public static FirebaseManager getInstance(String serviceAccountPath, String databaseUrl) {
        if (instance == null) {
            instance = new FirebaseManager(serviceAccountPath, databaseUrl);
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void loginWithFirebase(String email, String password, Callback callback) {
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

        client.newCall(request).enqueue(callback);
    }

    public void sendToFirebase(String url, JSONObject json, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body) // PUT 메서드 사용
                .build();

        client.newCall(request).enqueue(callback);
    }
}
