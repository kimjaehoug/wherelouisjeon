//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package kr.jbnu.se.std;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainClient extends JFrame {
    private String nickname;
    private JTextArea chatArea;
    private JTextField messageField;
    private Framework framework;
    private JLabel nameLabel;

    public MainClient(final Framework framework) {
        this.framework = framework;
        this.setTitle("Shoot the Dock");
        this.setDefaultCloseOperation(3);
        this.setSize(1280, 720);
        this.setLocationRelativeTo((Component)null);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        framework.receiveMessages();
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JButton startButton = new JButton("게임 시작");
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setFont(new Font("Arial", 1, 18));
        startButton.setFocusable(false);
        startButton.setBackground(Color.LIGHT_GRAY);
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new FlowLayout(0));
        JLabel profileImage = new JLabel(new ImageIcon("src/main/resources/images/duck.png"));
        profilePanel.add(profileImage);
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, 1));
        this.nameLabel = new JLabel("이름: " + this.nickname);
        JLabel introLabel = new JLabel("자기소개 한줄");
        namePanel.add(this.nameLabel);
        namePanel.add(introLabel);
        profilePanel.add(namePanel);
        topPanel.add(startButton, "Center");
        topPanel.add(profilePanel, "East");
        this.add(topPanel, "North");
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        this.chatArea = new JTextArea();
        this.chatArea.setEditable(false);
        this.chatArea.setLineWrap(true);
        JScrollPane chatScroll = new JScrollPane(this.chatArea);
        centerPanel.add(chatScroll, "Center");
        DefaultListModel<String> friendsModel = new DefaultListModel();
        friendsModel.addElement("친구1");
        friendsModel.addElement("친구2");
        friendsModel.addElement("친구3");
        JList<String> friendsList = new JList(friendsModel);
        JScrollPane friendsScroll = new JScrollPane(friendsList);
        friendsScroll.setPreferredSize(new Dimension(200, 0));
        centerPanel.add(friendsScroll, "East");
        this.add(centerPanel, "Center");
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        this.messageField = new JTextField();
        inputPanel.add(this.messageField, "Center");
        JButton sendButton = new JButton("전송");
        inputPanel.add(sendButton, "East");
        this.add(inputPanel, "South");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = MainClient.this.messageField.getText();
                if (!message.isEmpty()) {
                    framework.sendMessage(message);
                }

            }
        });
        this.setVisible(true);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.nameLabel.setText("이름: " + nickname);
    }

    public void setChat(String message) {
        this.messageField.setText("");
        this.chatArea.append(message);
    }
}
