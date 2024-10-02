package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainClient extends JFrame {
    private String nickname;
    private JTextArea chatArea;
    private JTextField messageField;
    private Framework framework;
    private JLabel nameLabel;

    public MainClient(Framework framework) {
        // 기본 프레임 설정
        this.framework = framework;
        setTitle("Shoot the Dock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        framework.receiveMessages();

        // 상단 패널
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 게임 시작 버튼
        JButton startButton = new JButton("게임 시작");
        startButton.setPreferredSize(new Dimension(200, 50)); // 버튼 크기 설정
        startButton.setFont(new Font("Arial", Font.BOLD, 18)); // 폰트 설정
        startButton.setFocusable(false); // 포커스 비활성화
        startButton.setBackground(Color.LIGHT_GRAY); // 배경색 설정

        // 프로필 패널
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 프로필 사진
        JLabel profileImage = new JLabel(new ImageIcon("src/main/resources/images/duck.png")); // 프로필 이미지
        profilePanel.add(profileImage); // 프로필 사진 추가

        // 이름과 자기소개 패널
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS)); // 세로로 정렬
        nameLabel = new JLabel("이름: " + nickname); // 이름 레이블
        JLabel introLabel = new JLabel("자기소개 한줄");
        namePanel.add(nameLabel);
        namePanel.add(introLabel);
        profilePanel.add(namePanel); // 프로필 패널에 추가

        topPanel.add(startButton, BorderLayout.CENTER); // 중앙에 추가
        topPanel.add(profilePanel, BorderLayout.EAST); // 오른쪽에 추가
        add(topPanel, BorderLayout.NORTH); // 상단에 추가

        // 중앙 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // 채팅 메시지를 표시할 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false); // 읽기 전용
        chatArea.setLineWrap(true); // 줄 바꿈 설정
        JScrollPane chatScroll = new JScrollPane(chatArea); // 스크롤 패널 추가
        centerPanel.add(chatScroll, BorderLayout.CENTER); // 중앙에 추가

        // 친구 목록
        DefaultListModel<String> friendsModel = new DefaultListModel<>();
        friendsModel.addElement("친구1");
        friendsModel.addElement("친구2");
        friendsModel.addElement("친구3");
        JList<String> friendsList = new JList<>(friendsModel);
        JScrollPane friendsScroll = new JScrollPane(friendsList); // 스크롤 패널 추가
        friendsScroll.setPreferredSize(new Dimension(200, 0)); // 폭 설정
        centerPanel.add(friendsScroll, BorderLayout.EAST); // 오른쪽에 추가

        add(centerPanel, BorderLayout.CENTER); // 중앙 패널 추가

        // 입력 필드와 전송 버튼이 들어갈 패널
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        // 입력 필드
        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER); // 중앙에 추가

        // 전송 버튼
        JButton sendButton = new JButton("전송");
        inputPanel.add(sendButton, BorderLayout.EAST); // 동쪽에 추가

        // 입력 패널을 프레임 하단에 추가
        add(inputPanel, BorderLayout.SOUTH);

        // 전송 버튼 클릭 이벤트
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    framework.sendMessage(message);
                }
            }
        });

        // 프레임을 화면에 표시
        setVisible(true);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        nameLabel.setText("이름: " + nickname);
    }

    public void setChat(String message) {
        messageField.setText(""); // 입력 필드 초기화
        this.chatArea.append(message);
    }
}
