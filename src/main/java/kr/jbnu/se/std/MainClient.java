package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainClient extends JFrame {
    private String nickname;
    private JTextArea chatArea;
    private JTextField messageField;
    private Framework framework;
    private JLabel nameLabel;
    private DefaultListModel<String> friendsModel;
    private int money;
    private JLabel moneyLabel;
    // 원본 이미지 아이콘 생성
    ImageIcon originalIcon = new ImageIcon("src/main/resources/images/start_btn.png");

    // 눌릴 때 이미지 아이콘 생성
    ImageIcon pressedIcon = new ImageIcon("src/main/resources/images/press_start_btn.png");

    public MainClient(Framework framework) {
        // 기본 프레임 설정
        this.framework = framework;
        setTitle("Shoot the Dock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // 상단 패널
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 게임 시작 버튼
        JButton startButton = new JButton();
        startButton.setPreferredSize(new Dimension(300, 100)); // 버튼 크기 설정
        startButton.setFocusable(false); // 포커스 비활성화
        startButton.setBackground(Color.LIGHT_GRAY); // 배경색 설정 (이미지 외의 영역에 적용)
        startButton.setBorderPainted(false); // 테두리 제거
        startButton.setContentAreaFilled(false); // 버튼 내용 영역 비움 (이미지만 표시)
        // 원본 이미지 크기
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // 버튼 크기
        int buttonWidth = startButton.getPreferredSize().width;
        int buttonHeight = startButton.getPreferredSize().height;

        // 이미지 비율을 유지하면서 버튼 크기에 맞게 조정
        double widthRatio = (double) buttonWidth / originalWidth;
        double heightRatio = (double) buttonHeight / originalHeight;
        double scaleFactor = Math.min(widthRatio, heightRatio); // 비율 유지하며 맞춤

        int scaledWidth = (int) (originalWidth * scaleFactor);
        int scaledHeight = (int) (originalHeight * scaleFactor);

        // 이미지 크기 조정
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                scaledWidth,
                scaledHeight,
                Image.SCALE_SMOOTH
        );
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // 버튼에 조정된 이미지 아이콘 설정
        startButton.setIcon(scaledIcon);

        // 버튼에 액션 리스너 추가 (버튼을 눌렀을 때 동작 수행)
        startButton.addActionListener(e -> {
            // 버튼 클릭 시 원하는 동작을 정의
            System.out.println("게임이 시작됩니다!");
        });

// 버튼 눌림 효과를 위한 마우스 리스너 추가
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startButton.setIcon(new ImageIcon(pressedIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)));
                startButton.setBackground(Color.GRAY); // 눌릴 때 색상 변경

                // 1초 후에 원래 아이콘으로 복원
                Timer timer = new Timer(500, event -> {
                    startButton.setIcon(scaledIcon); // 원래 아이콘으로 복원
                    startButton.setBackground(Color.LIGHT_GRAY); // 원래 색상으로 복원
                });
                timer.setRepeats(false); // 1회만 실행
                timer.start();
                framework.onGameStart();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 마우스 릴리즈 시에는 아무 동작도 하지 않도록 설정
            }
        });

// 버튼을 패널에 추가
        topPanel.add(startButton, BorderLayout.CENTER);
        JButton inventoryButton = new JButton("inventory");
        inventoryButton.setPreferredSize(new Dimension(300, 100));
        inventoryButton.setFocusable(false);
        inventoryButton.setBackground(Color.LIGHT_GRAY);
        inventoryButton.setBorderPainted(false);
        inventoryButton.setContentAreaFilled(false);

        inventoryButton.addActionListener(e -> {
            framework.inventoryWindow();
        });
        // 상점 버튼 생성
        JButton shopButton = new JButton("상점");
        shopButton.setPreferredSize(new Dimension(300, 100)); // 버튼 크기 설정
        shopButton.setFocusable(false); // 포커스 비활성화
        shopButton.setBackground(Color.LIGHT_GRAY); // 배경색 설정
        shopButton.setBorderPainted(false); // 테두리 제거
        shopButton.setContentAreaFilled(false); // 버튼 내용 영역 비움 (이미지만 표시)

        // 상점 버튼 클릭 시 동작 정의
        shopButton.addActionListener(e -> {
            // 상점 창 열기
            framework.Shopwindowopen();
            System.out.println("상점이 열립니다!");
            // framework.openShopWindow(); // 예시: 상점 창 열기
        });

        // 버튼을 상단 패널에 추가
        topPanel.add(shopButton, BorderLayout.SOUTH);
        topPanel.add(inventoryButton, BorderLayout.NORTH);// 상단 패널의 남쪽에 추가


        // 프로필 패널
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 프로필 사진
        JLabel profileImage = new JLabel(new ImageIcon("src/main/resources/images/profile.png")); // 프로필 이미지
        profilePanel.add(profileImage); // 프로필 사진 추가

        // 이름과 자기소개 패널
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS)); // 세로로 정렬
        nameLabel = new JLabel("이름: " + nickname); // 이름 레이블
        JLabel introLabel = new JLabel("자기소개 한줄");
        moneyLabel = new JLabel("DP : ");
        namePanel.add(nameLabel);
        namePanel.add(introLabel);
        namePanel.add(moneyLabel);
        profilePanel.add(namePanel); // 프로필 패널에 추가

        topPanel.add(profilePanel, BorderLayout.EAST); // 오른쪽에 추가
        add(topPanel, BorderLayout.NORTH); // 상단에 추가

        // 중앙 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // 채팅 메시지를 표시할 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false); // 읽기 전용
        chatArea.setLineWrap(true); // 줄 바꿈 설정
        chatArea.setOpaque(false); // 배경을 투명하게 설정
        chatArea.setBackground(new Color(0, 0, 0, 0)); // 배경색을 투명으로 설정
        chatArea.setForeground(Color.BLACK); // 글자색을 흰색으로 설정 (배경과 대비되게)
        chatArea.setFont(new Font("Gothic", Font.PLAIN, 20));// 폰트 설정
        // 채팅 영역을 스크롤 패널에 추가
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(1047, 468)); // 채팅 영역 크기 설정
        chatScroll.setOpaque(false); // 스크롤 패널 배경을 투명하게 설정
        chatScroll.getViewport().setOpaque(false); // 뷰포트 배경을 투명하게 설정

        // 채팅창 배경 이미지 설정
        ImageIcon chatBackgroundIcon = new ImageIcon("src/main/resources/images/background_chat.png");
        JLabel chatBackgroundLabel = new JLabel(chatBackgroundIcon);
        chatBackgroundLabel.setLayout(new BorderLayout());
        chatBackgroundLabel.setPreferredSize(new Dimension(1047, 468));
        chatBackgroundLabel.add(chatScroll, BorderLayout.CENTER); // 중앙에 추가

        centerPanel.add(chatBackgroundLabel, BorderLayout.CENTER); // 중앙에 추가

        // 친구 목록
        friendsModel = new DefaultListModel<>();
        JList<String> friendsList = new JList<>(friendsModel);
        JScrollPane friendsScroll = new JScrollPane(friendsList); // 스크롤 패널 추가
        friendsScroll.setPreferredSize(new Dimension(200, 0)); // 폭 설정
        JButton friendRequestsButton = new JButton("친구 신청 목록");
        friendRequestsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.Invitewindow(); // 친구 신청 목록 창 호출
            }
        });

        // 친구 목록 투명하게 설정
        friendsList.setOpaque(false);  // 리스트 배경 투명하게 설정
        friendsList.setBackground(new Color(0, 0, 0, 0));  // 투명 배경
        friendsList.setForeground(Color.BLACK);  // 글자 색상 흰색으로 설정 (배경 대비)
        friendsList.setFont(new Font("Gothic", Font.PLAIN, 20)); // 폰트 설정

        // 친구 목록 배경 이미지 설정
        ImageIcon friendBackgroundIcon = new ImageIcon("src/main/resources/images/background_friend.png");
        JLabel friendsBackgroundLabel = new JLabel(friendBackgroundIcon);
        friendsBackgroundLabel.setLayout(new BorderLayout());
        friendsBackgroundLabel.setPreferredSize(new Dimension(195, 468));

        // 친구 목록 스크롤 패널
        friendsScroll.setOpaque(false);  // 스크롤 패널도 투명하게
        friendsScroll.getViewport().setOpaque(false);  // 뷰포트도 투명하게
        friendsScroll.setPreferredSize(new Dimension(195, 468));  // 친구 목록 크기 설정

        // 친구 목록 패널과 버튼을 함께 추가
        JPanel friendsPanel = new JPanel(new BorderLayout());
        friendsPanel.add(friendsScroll, BorderLayout.CENTER); // 친구 목록 추가
        friendsBackgroundLabel.add(friendRequestsButton, BorderLayout.SOUTH); // 친구 신청 목록 버튼 추가
        // 친구 목록 스크롤 패널을 배경 레이블에 추가
        friendsBackgroundLabel.add(friendsScroll, BorderLayout.CENTER);
        centerPanel.add(friendsPanel, BorderLayout.EAST); // 오른쪽에 추가
        centerPanel.add(friendsBackgroundLabel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER); // 중앙 패널 추가

        // 입력 필드와 전송 버튼이 들어갈 패널
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        // 입력 필드
        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER); // 중앙에 추가
        messageField.setFont(new Font("Gothic", Font.PLAIN, 20)); // 폰트 설정
        // 버튼 패널 (전송 버튼과 친구 추가 버튼을 포함)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // 전송 버튼
        JButton sendButton = new JButton();
        sendButton.setPreferredSize(new Dimension(100, 50)); // 버튼 크기 설정
        sendButton.setFocusable(false); // 포커스 비활성화
        sendButton.setBackground(Color.LIGHT_GRAY); // 배경색 설정 (이미지 외의 영역에 적용)
        sendButton.setBorderPainted(false); // 테두리 제거
        sendButton.setContentAreaFilled(false); // 버튼 내용 영역 비움 (이미지만 표시)
        buttonPanel.add(sendButton); // 버튼 패널에 전송 버튼 추가
        // 원본 이미지 아이콘 생성
        ImageIcon sendIcon = new ImageIcon("src/main/resources/images/send_btn.png");
        ImageIcon sendPressedIcon = new ImageIcon("src/main/resources/images/send_btn_press.png");

        // 원본 이미지 크기
        int sendOriginalWidth = sendIcon.getIconWidth();
        int sendOriginalHeight = sendIcon.getIconHeight();

        // 버튼 크기
        int sendButtonWidth = sendButton.getPreferredSize().width;
        int sendButtonHeight = sendButton.getPreferredSize().height;

        // 이미지 비율을 유지하면서 버튼 크기에 맞게 조정
        double sendWidthRatio = (double) sendButtonWidth / sendOriginalWidth;
        double sendHeightRatio = (double) sendButtonHeight / sendOriginalHeight;
        double sendScaleFactor = Math.min(sendWidthRatio, sendHeightRatio); // 비율 유지하며 맞춤

        int sendScaledWidth = (int) (sendOriginalWidth * sendScaleFactor);
        int sendScaledHeight = (int) (sendOriginalHeight * sendScaleFactor);

        // 이미지 크기 조정
        Image sendScaledImage = sendIcon.getImage().getScaledInstance(
                sendScaledWidth,
                sendScaledHeight,
                Image.SCALE_SMOOTH
        );
        ImageIcon sendScaledIcon = new ImageIcon(sendScaledImage);

        // 버튼에 조정된 이미지 아이콘 설정
        sendButton.setIcon(sendScaledIcon);
        // 친구 추가 버튼
        JButton addFriendButton = new JButton("친구 추가");
        buttonPanel.add(addFriendButton); // 버튼 패널에 친구 추가 버튼 추가



        // 버튼 눌림 효과를 위한 마우스 리스너 추가
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sendButton.setIcon(new ImageIcon(sendPressedIcon.getImage().getScaledInstance(sendScaledWidth, sendScaledHeight, Image.SCALE_SMOOTH)));
                sendButton.setBackground(Color.GRAY); // 눌릴 때 색상 변경
                String message = messageField.getText();
                // 1초 후에 원래 아이콘으로 복원
                Timer timer = new Timer(500, event -> {
                    sendButton.setIcon(sendScaledIcon); // 원래 아이콘으로 복원
                    sendButton.setBackground(Color.LIGHT_GRAY); // 원래 색상으로 복원
                });
                timer.setRepeats(false); // 1회만 실행
                timer.start();
                if (!message.isEmpty()) {
                    framework.sendMessage(message);
                    messageField.setText(""); // 입력 필드 초기화
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 마우스 릴리즈 시에는 아무 동작도 하지 않도록 설정
            }
        });

        // 버튼 패널을 입력 패널의 동쪽에 추가
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        // 입력 패널을 프레임 하단에 추가
        add(inputPanel, BorderLayout.SOUTH);

        // 친구 추가 버튼 클릭 이벤트
        addFriendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framework.frendsAddwindows();
            }
        });

        // 친구 목록에서 클릭하면 새로운 채팅 창 띄우기
        friendsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블 클릭 이벤트 처리
                    String selectedFriend = friendsList.getSelectedValue();
                    if (selectedFriend != null) {
                        framework.ChatFriendswindow(selectedFriend);
                    }
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
        // 입력 필드 초기화
        this.chatArea.append(message);
    }
    public void setFriends(String friends) {
        this.friendsModel.addElement(friends);
    }

    public void setMoney(int money) {
        this.money = money;
        moneyLabel.setText("DP: " + money);
    }
}
