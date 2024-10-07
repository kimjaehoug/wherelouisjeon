package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.firebase.auth.FirebaseAuth;
import okhttp3.*;
import org.json.JSONObject;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginClient extends JFrame {

    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private Framework framework;
    private String id, password;
    private FirebaseAuth auth;
    private String realUserId;
    private String email;
    private String encodeemail1;
    private String encodeemail2;

    public LoginClient(Framework framework) {
        this.framework = framework;
        setTitle("Login");
        setSize(840, 630);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        auth = FirebaseAuth.getInstance();

        JPanel panel = new JPanel(new GridBagLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 오른쪽 화면
        ImageIcon imageIcon = new ImageIcon("src/main/resources/images/login_duck.png");
        Image backgroundImage = imageIcon.getImage();
        JPanel rightPanel = new ImagePanel(backgroundImage);
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(560, 630));
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // 왼쪽 화면
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(280, 630));
        mainPanel.add(leftPanel, BorderLayout.WEST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        gbc.weighty = 0.7;
        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(new JLabel(), gbc);

        // "로그인" 텍스트 추가
        JLabel loginLabel = new JLabel("LOGIN");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 20));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 30, 0);
        leftPanel.add(loginLabel, gbc);

        // ID 라벨 및 입력 필드
        JPanel idPanel = new JPanel();
        idPanel.setBackground(new Color(233, 233, 233));
        idPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 여백 추가
        idPanel.setPreferredSize(new Dimension(230, 50)); // 너비 250, 높이 50
        idPanel.setLayout(new BorderLayout());

        // 텍스트 필드 생성
        JTextField idField = new JTextField("ID", 30); // 기본 텍스트로 "ID" 설정
        idField.setForeground(Color.GRAY);
        idField.setOpaque(false);
        idField.setBorder(BorderFactory.createEmptyBorder());

        idField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("ID".equals(idField.getText())) {
                    idField.setText("");
                    idField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (idField.getText().isEmpty()) {
                    idField.setText("ID");
                    idField.setForeground(Color.GRAY);
                }
            }
        });

        idPanel.add(idField, BorderLayout.CENTER);
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        leftPanel.add(idPanel, gbc);

        // 비밀번호 라벨 및 입력 필드
        JPanel passwordPanel = new JPanel();
        passwordPanel.setBackground(new Color(233, 233, 233));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        passwordPanel.setPreferredSize(new Dimension(230, 50));
        passwordPanel.setLayout(new BorderLayout());

        // 비밀번호 텍스트 필드 생성
        JPasswordField passwordField = new JPasswordField("Password", 15);
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        passwordField.setOpaque(false);
        passwordField.setBorder(BorderFactory.createEmptyBorder());

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Password".equals(passwordField.getText())) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getText().isEmpty()) {
                    passwordField.setText("Password");
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });

        passwordPanel.add(passwordField, BorderLayout.CENTER);
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 5, 0);
        leftPanel.add(passwordPanel, gbc);

        // 로그인 버튼
        ImageIcon originalIcon = new ImageIcon("src/main/resources/images/grass.png");
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(230, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        loginButton = new JButton(scaledIcon);
        loginButton.setPreferredSize(new Dimension(230, 50));
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        loginButton.setBorderPainted(false);

        loginButton.setText("Login");
        loginButton.setHorizontalTextPosition(SwingConstants.CENTER);
        loginButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        leftPanel.add(loginButton, gbc);

        gbc.weighty = 0.8;
        gbc.gridx = 0;
        gbc.gridy = 5;
        leftPanel.add(new JLabel(), gbc);

        // 회원가입 버튼
        signupButton = new JButton("Sign Up");
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        signupButton.setBorderPainted(false);
        signupButton.setContentAreaFilled(false);
        leftPanel.add(signupButton, gbc);

        add(mainPanel);
        pack();
        setVisible(true);

        leftPanel.requestFocusInWindow();

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

    public class ImagePanel extends JPanel{
        private Image backgroundImage;
        public ImagePanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    //파이어베이스 초기화
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
                        String responseBody = null;
                        try {
                            responseBody = response.body().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        dispose(); // 로그인 창 닫기
                        // ID 토큰 가져오기
                        String idToken = jsonResponse.getString("idToken");
                        System.out.println("ID 토큰: " + idToken);
                        framework.getEmail(email);
                        framework.getPassword(password);
                        encodeemail1 = email.split("@")[0];
                        framework.getIdtoken(idToken);
                        framework.onLoginSuccess();
                        encodeemail2 = email.replace(".", "%2E");
                        framework.getUserId(encodeemail1);
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
        registerFrame.setSize(350, 250);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 이메일 라벨 및 입력 필드
        /*JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        registerPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        registerPanel.add(emailField, gbc);*/

        // new ID 라벨 및 입력 필드
        JPanel newidpanel = new JPanel();
        newidpanel.setBackground(new Color(233, 233, 233));
        newidpanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        newidpanel.setLayout(new BorderLayout());

        // new ID 텍스트 필드 생성
        JTextField newidField = new JTextField("email", 30);
        newidField.setForeground(Color.GRAY);
        newidField.setOpaque(false);
        newidField.setBorder(BorderFactory.createEmptyBorder());

        newidField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("ID".equals(newidField.getText())) {
                    newidField.setText("");
                    newidField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (newidField.getText().isEmpty()) {
                    newidField.setText("email");
                    newidField.setForeground(Color.GRAY);
                }
            }
        });

        newidpanel.add(newidField, BorderLayout.CENTER);
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 15, 5, 15);
        registerPanel.add(newidpanel, gbc);

        // 비밀번호 라벨 및 입력 필드
        /*JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        registerPanel.add(passwordLabel, gbc);

        JPasswordField registerPasswordField = new JPasswordField(15);
        gbc.gridx = 0;
        gbc.gridy = 2;
        registerPanel.add(registerPasswordField, gbc);*/

        // new password 라벨 및 입력 필드
        JPanel newpasswordpanel = new JPanel();
        newpasswordpanel.setBackground(new Color(233, 233, 233)); //색을 바꾸든 이미지를 설정하든 바꿀것!!!
        newpasswordpanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        newpasswordpanel.setLayout(new BorderLayout());

        // new password 텍스트 필드
        JPasswordField newpasswordField = new JPasswordField("password", 30);
        newpasswordField.setForeground(Color.GRAY);
        newpasswordField.setOpaque(false);
        newpasswordField.setBorder(BorderFactory.createEmptyBorder());
        newpasswordField.setEchoChar((char) 0);

        newpasswordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("password".equals(new String(newpasswordField.getPassword()))) {
                    newpasswordField.setText("");
                    newpasswordField.setForeground(Color.BLACK);
                    newpasswordField.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (newpasswordField.getPassword().length == 0) {
                    newpasswordField.setText("password");
                    newpasswordField.setForeground(Color.GRAY);
                    newpasswordField.setEchoChar((char) 0);
                }
            }
        });

        newpasswordpanel.add(newpasswordField, BorderLayout.CENTER);
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 15, 5, 15);

        registerPanel.add(newpasswordpanel, gbc);


        /*JLabel nicknameLabel = new JLabel("Nickname:");
        gbc.gridx = 0;
        gbc.gridy = 3; // 비밀번호 아래에 배치
        registerPanel.add(nicknameLabel, gbc);

        JTextField nicknameField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 4; // 비밀번호 아래에 배치
        registerPanel.add(nicknameField, gbc);*/

        // Nickname 라벨 및 입력 필드
        JPanel nicknamepanel = new JPanel();
        nicknamepanel.setBackground(new Color(233, 233, 233)); //색을 바꾸든 이미지를 설정하든 바꿀것!!!
        nicknamepanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nicknamepanel.setLayout(new BorderLayout());

        // Nickname 텍스트 필드 생성
        JTextField nicknameField = new JTextField("nickname", 30);
        nicknameField.setForeground(Color.GRAY);
        nicknameField.setOpaque(false);
        nicknameField.setBorder(BorderFactory.createEmptyBorder());

        nicknameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("nickname".equals(nicknameField.getText())) {
                    nicknameField.setText("");
                    nicknameField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nicknameField.getText().isEmpty()) {
                    nicknameField.setText("nickname");
                    nicknameField.setForeground(Color.GRAY);
                }
            }
        });

        nicknamepanel.add(nicknameField, BorderLayout.CENTER);
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 15, 5, 15);
        registerPanel.add(nicknamepanel, gbc);

        // 회원가입 버튼
        JButton submitButton = new JButton("Sign Up");
        submitButton.setBackground(Color.GRAY);//색을 바꾸든 이미지를 설정하든 바꿀것!!!
        submitButton.setForeground(Color.BLACK);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);

        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 15, 5, 15);
        registerPanel.add(submitButton, gbc);

        // 회원가입 버튼 클릭 시 Firebase에 사용자 등록
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                email = newidField.getText();
                String password = new String(newpasswordField.getPassword());
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
    public static String encodeEmail(String email) {
        try {
            System.err.println("이메일 변환성공");
            return URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println("이메일변환실패");
            return email; // 인코딩 실패 시 원본 이메일을 그대로 반환
        }
    }

    // 닉네임 저장
    private void saveUserNickname(String userId, String nickname) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject(userId);
        realUserId = json.getString("localId");
        encodeemail1 = email.split("@")[0];
        System.err.println(encodeemail1);
        json.put("nickname", nickname);
        // 사용자 ID를 키로 사용하여 닉네임 저장
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("https://shootthedock-default-rtdb.firebaseio.com/users/" + encodeemail1 + "/"+ "userinfo"+".json")
                .put(body) // POST 메소드 사용
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
                }else{
                    System.err.println(userId);
                }
            }
        });
    }



}