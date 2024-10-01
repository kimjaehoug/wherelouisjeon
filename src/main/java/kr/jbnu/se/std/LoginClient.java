package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SignInResult;
import com.google.firebase.auth.UserRecord;

public class LoginClient extends JFrame {

    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private Framework framework;
    private String id, password;

    public LoginClient(Framework framework) {
        this.framework = framework;
        setTitle("Login");
        setSize(300, 150);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
                JOptionPane.showMessageDialog(null, "회원가입 기능은 아직 구현되지 않았습니다.");
            }
        });

        add(panel);
    }
    private void loginWithFirebase(String id, String password) {
        try {
            // Firebase Authentication으로 로그인
            SignInResult signInResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
            if (signInResult.getUser() != null) {
                dispose(); // 로그인 창 닫기
                framework.onLoginSuccess();
            }
        } catch (FirebaseAuthException ex) {
            JOptionPane.showMessageDialog(null, "로그인 실패: " + ex.getMessage());
        }
    }
    private void registerwindows(){
        setTitle("register");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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



    }
}
