package kr.jbnu.se.std;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InviteFriends extends JFrame {
    private DefaultListModel<String> friendRequestsModel; // 친구 신청 목록을 관리하는 모델
    private Framework framework;
    private String DataPath;
    public InviteFriends(Framework framework) {
        this.framework = framework;
        DataPath = "https://shootthedock-default-rtdb.firebaseio.com/users/" + framework.firebaseClient.email + "/"+ "userinfo/friends/";
        setTitle("친구 신청 목록");
        setVisible(true);
        setSize(400, 300);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창을 닫을 때 현재 창만 종료

        // 친구 신청 목록 패널 생성
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BorderLayout());

        // 친구 신청 목록을 표시할 리스트
        friendRequestsModel = new DefaultListModel<>();
        JList<String> friendRequestsList = new JList<>(friendRequestsModel);
        friendRequestsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 리스트를 스크롤 가능한 패널에 추가
        JScrollPane scrollPane = new JScrollPane(friendRequestsList);
        requestPanel.add(scrollPane, BorderLayout.CENTER);

        // 수락/거절 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        // 수락 버튼
        JButton acceptButton = new JButton("수락");
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRequest = friendRequestsList.getSelectedValue();
                if (selectedRequest != null) {
                    acceptFriendRequest(selectedRequest);
                    friendRequestsModel.removeElement(selectedRequest);
                    //framework.friendsAdder(selectedRequest);
                    framework.firebaseClient.senderDatabase("nickname",DataPath,selectedRequest);
                    framework.deleteFriendInvite(selectedRequest);// 수락 후 신청 목록에서 제거
                }
            }
        });
        buttonPanel.add(acceptButton);

        // 거절 버튼
        JButton rejectButton = new JButton("거절");
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRequest = friendRequestsList.getSelectedValue();
                if (selectedRequest != null) {
                    friendRequestsModel.removeElement(selectedRequest);
                    framework.deleteFriendInvite(selectedRequest);
                }
            }
        });
        buttonPanel.add(rejectButton);

        // 버튼 패널을 하단에 추가
        requestPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 프레임에 패널 추가
        add(requestPanel);
    }

    public void setFriends(String friends) {
        this.friendRequestsModel.addElement(friends);
    }

    // 친구 신청을 수락하는 메소드
    private void acceptFriendRequest(String friendNickname) {
        // 친구 신청 수락 처리 로직 (예: Firebase에서 친구 추가 등)
        System.out.println(friendNickname + "님의 친구 신청을 수락했습니다.");
    }

    // 친구 신청을 거절하는 메소드
    private void rejectFriendRequest(String friendNickname) {
        // 친구 신청 거절 처리 로직 (예: Firebase에서 친구 신청 제거 등)
        System.out.println(friendNickname + "님의 친구 신청을 거절했습니다.");
    }

    // 친구 신청 목록에 항목을 추가하는 메소드
    public void addFriendRequest(String friendNickname) {
        friendRequestsModel.addElement(friendNickname);
    }
}
