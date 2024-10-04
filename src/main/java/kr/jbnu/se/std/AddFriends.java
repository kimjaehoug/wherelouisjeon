package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddFriends extends JFrame {
    private JTextField nicknameField;
    private JButton addFriendButton;

    public AddFriends(Framework framework) {
        // 기본 프레임 설정
        setTitle("친구 추가");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // 닉네임 입력 필드
        nicknameField = new JTextField(15);
        add(new JLabel("닉네임:"));
        add(nicknameField);

        // 친구 추가 버튼
        addFriendButton = new JButton("친구 추가");
        add(addFriendButton);

        // 버튼 클릭 리스너
        addFriendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameField.getText();
                // 여기서 닉네임 처리 로직 추가 가능
                framework.friendsAdder(nickname);
                framework.friendsAdderother(nickname);
                System.out.println("추가할 친구: " + nickname);
            }
        });

        // 프레임을 화면에 표시
        setVisible(true);
    }
}