package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatwithFriends extends JFrame {
    private JTextArea chatArea3;
    private JTextField inputField;
    private JButton sendButton;
    private String selectnickname;
    private Framework framework;

    public ChatwithFriends(Framework framework) {
        // 프레임 설정
        this.framework = framework;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setLayout(new BorderLayout());

        // 채팅 영역
        chatArea3 = new JTextArea();
        chatArea3.setEditable(false); // 채팅 기록은 수정 불가
        chatArea3.setLineWrap(true); // 자동 줄 바꿈
        JScrollPane chatScroll = new JScrollPane(chatArea3);
        add(chatScroll, BorderLayout.CENTER);

        // 하단 패널 (메시지 입력 필드 + 전송 버튼)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputField = new JTextField();
        inputPanel.add(inputField, BorderLayout.CENTER);

        sendButton = new JButton("전송");
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // 전송 버튼 클릭 이벤트
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 입력 필드에서 엔터키로도 메시지 전송
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 프레임 표시
        setVisible(true);
    }

    // 메시지 전송 처리
    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            framework.sendMessageFriend(selectnickname, inputField.getText());
            inputField.setText(""); // 입력 필드 초기화
        }
    }
    public void setFriends(String nickname){
        this.selectnickname = nickname;
        setTitle(selectnickname + "와의 채팅");
    }
    public String getFriends(){
        return selectnickname;
    }
    public void setChat(String message) {
        // 입력 필드 초기화
        this.chatArea3.append(message);
    }
}
