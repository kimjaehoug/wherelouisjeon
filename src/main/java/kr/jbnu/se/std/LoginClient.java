package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.*;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.json.JSONObject;


public class LoginClient extends JFrame {

    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private Framework framework;
    private String id, password;
    private FirebaseAuth auth;

    public LoginClient(Framework framework) {
        this.framework = framework;
        setTitle("Login");
        initializeFirebase();
        setSize(300, 150);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        auth = FirebaseAuth.getInstance();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID 라벨 및 입력 필드
        JLabel idLabel = new JLabel("ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);

        idField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(idField, gbc);

        // 비밀번호 라벨 및 입력 필드
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        // 로그인 버튼
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        // 회원가입 버튼
        signupButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(signupButton, gbc);


        // 로그인 버튼 클릭 시 이벤트 처리
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = idField.getText();
                password = new String(passwordField.getPassword());
                loginWithFirebase(id, password);

            }
        });


        // 회원가입 버튼 클릭 시 이벤트 처리
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterWindow();
            }
        });

        add(panel);
    }
    //파이어베이스 초기화
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
    // Firebase를 통한 로그인 처리 메소드
    private void loginWithFirebase(String email, String password) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put ("email", email);
        json.put ("password", password);
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
                        dispose(); // 로그인 창 닫기
                        framework.onLoginSuccess();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "로그인 실패: 잘못된 자격 증명"));
                }
            }
        });

    }
    // 회원가입 창 표시
    private void showRegisterWindow() {
        JFrame registerFrame = new JFrame("Sign Up");
        registerFrame.setSize(300, 200);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 이메일 라벨 및 입력 필드
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        registerPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        registerPanel.add(emailField, gbc);

        // 비밀번호 라벨 및 입력 필드
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        registerPanel.add(passwordLabel, gbc);

        JPasswordField registerPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        registerPanel.add(registerPasswordField, gbc);

        JLabel nicknameLabel = new JLabel("Nickname:");
        gbc.gridx = 0;
        gbc.gridy = 2; // 비밀번호 아래에 배치
        registerPanel.add(nicknameLabel, gbc);

        JTextField nicknameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2; // 비밀번호 아래에 배치
        registerPanel.add(nicknameField, gbc);

        // 회원가입 버튼
        JButton submitButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 3;
        registerPanel.add(submitButton, gbc);

        // 회원가입 버튼 클릭 시 Firebase에 사용자 등록
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(registerPasswordField.getPassword());
                String nickname = nicknameField.getText();
                signUpWithFirebase(email,password,nickname,registerFrame);
            }
        });

        registerFrame.add(registerPanel);
        registerFrame.setVisible(true);
    }

    // Firebase 회원가입 처리
    private void signUpWithFirebase(String email, String password,String nickname,JFrame Frame) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        JFrame frame = Frame;
        json.put("email", email);
        json.put("password", password);
        json.put("returnSecureToken", true);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyCJDgbBXWSRoRUg3xVqsQrSEz1W5AFiE_Y")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "회원가입 실패: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String userId = response.body().string();
                    saveUserNickname(userId, nickname);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "회원가입 성공!");
                        frame.dispose();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "회원가입 실패: 잘못된 정보"));
                }
            }
        });
    }
    // 닉네임 저장
    private void saveUserNickname(String userId, String nickname) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject(userId);
        String realUserId = json.getString("localId");
        json.put("nickname", nickname);
        // 사용자 ID를 키로 사용하여 닉네임 저장
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + realUserId + ".json")
                .post(body) // POST 메소드 사용
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("닉네임 저장 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("닉네임 저장 실패: " + response.code());
                    System.err.println(userId);
                }
            }
        });
    }

}
