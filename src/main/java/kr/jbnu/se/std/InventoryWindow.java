package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

public class InventoryWindow extends JFrame {
    private int ITEMS_PER_PAGE;
    private int currentPage;
    private String[] itemNames;
    private String[] itemImages;
    private JPanel panel;
    private JButton buyButton;

    public InventoryWindow(Framework framework) {
        setTitle("Inventory");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 840);

        ITEMS_PER_PAGE = 9;
        itemNames = new String[]{"더블배럴샷건", "AK-47", "M4A1", "SMG", "스나이퍼", "핸드건"}; // 예시 아이템
        itemImages = new String[]{
                "src/main/resources/images/gun_01.png", "src/main/resources/images/gun_02.png",
                "src/main/resources/images/gun_03.png", "src/main/resources/images/gun_04.png",
                "src/main/resources/images/gun_05.png", "src/main/resources/images/gun_06.png",
        };

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setPreferredSize(new Dimension(500, 840)); // 너비 500px로 설정

        addItemsToPanel(centerPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel itemPanel = new JPanel(new GridBagLayout()); // GridBagLayout 사용
        GridBagConstraints gbc = new GridBagConstraints(); // GridBagConstraints 생성
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5); // 여백 설정
        addItemsToPanel(itemPanel);
        add(itemPanel, BorderLayout.CENTER);

        // 간판 추가
        ImageIcon signboardIcon = new ImageIcon("src/main/resources/images/signboard.png");
        Image signboardImage = signboardIcon.getImage().getScaledInstance(286, 135, Image.SCALE_SMOOTH);
        JLabel signboardLabel = new JLabel(new ImageIcon(signboardImage));
        signboardLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(new Font("ADLaM Display", Font.BOLD, 35));
        inventoryLabel.setForeground(Color.DARK_GRAY);
        inventoryLabel.setHorizontalAlignment(JLabel.CENTER);
        inventoryLabel.setVerticalAlignment(JLabel.CENTER);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(286, 135));

        signboardLabel.setBounds(0, 0, 286, 135);
        inventoryLabel.setBounds(0, 0, 286, 135);

        layeredPane.add(signboardLabel, Integer.valueOf(0));
        layeredPane.add(inventoryLabel, Integer.valueOf(1));

        // 인벤토리 나가기 버튼
        JButton exitButton = new JButton();

        exitButton.setContentAreaFilled(false); // 버튼 배경을 투명하게 설정
        exitButton.setBorderPainted(false); // 버튼 테두리를 없앰
        exitButton.setFocusPainted(false);

        ImageIcon exitIcon = new ImageIcon("src/main/resources/images/btn_X.png");
        ImageIcon exitPressIcon = new ImageIcon("src/main/resources/images/btn_X_press.png");

        int buttonWidth = 70;
        int buttonHeight = (int) (70 * (117.0 / 120)); // 비율 맞춤
        exitButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        Image scaledExitImage = exitIcon.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
        Image scaledPressImage = exitPressIcon.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);

        exitButton.setIcon(new ImageIcon(scaledExitImage));
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                exitButton.setIcon(new ImageIcon(scaledPressImage));
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                exitButton.setIcon(new ImageIcon(scaledExitImage));
            }
        });

        exitButton.addActionListener(e -> {
            dispose();
        });

        // Top Panel 설정
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints topgbc = new GridBagConstraints();
        topgbc.insets = new Insets(5, 5, 5, 5);

        // 간판
        topgbc.gridx = 0;
        topgbc.gridy = 0;
        topgbc.weightx = 1.0;
        topgbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(layeredPane, topgbc);

// 인벤토리 나가기 버튼
        topgbc.gridx = 1;
        topgbc.gridy = 0;
        topgbc.weightx = 0.0;
        topgbc.anchor = GridBagConstraints.NORTHEAST;
        topPanel.add(exitButton, topgbc);

        add(topPanel, BorderLayout.NORTH);

        // 장착 버튼 초기화
        buyButton = new JButton("장착");
        buyButton.addActionListener(e -> {
            if (currentPage * ITEMS_PER_PAGE < itemNames.length) {
                String selectedItem = itemNames[currentPage * ITEMS_PER_PAGE];
                System.out.println(selectedItem + " 장착!");
            }
        });

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        ImageIcon prevIcon = new ImageIcon("src/main/resources/images/btn_left.png");
        ImageIcon nextIcon = new ImageIcon("src/main/resources/images/btn_right.png");

        Image scaledPrevImage = prevIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        Image scaledNextImage = nextIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        JButton prevButton = new JButton(new ImageIcon(scaledPrevImage));
        JButton nextButton = new JButton(new ImageIcon(scaledNextImage));

        prevButton.setContentAreaFilled(false);
        prevButton.setBorderPainted(false);
        prevButton.setFocusPainted(false);
        prevButton.setMargin(new Insets(0, 0, 0, 0));

        nextButton.setContentAreaFilled(false);
        nextButton.setBorderPainted(false);
        nextButton.setFocusPainted(false);
        nextButton.setMargin(new Insets(0, 0, 0, 0));

        // 이전 버튼 클릭 이벤트
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateItemPanel(itemPanel);
                updateButtonState(prevButton, nextButton);
            }
        });

        // 다음 버튼 클릭 이벤트
        nextButton.addActionListener(e -> {
            if ((currentPage + 1) * ITEMS_PER_PAGE < itemNames.length) {
                currentPage++;
                updateItemPanel(itemPanel);
                updateButtonState(prevButton, nextButton);
            }
        });

        updateButtonState(prevButton, nextButton);

        buttonPanel.add(buyButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

    }

    private void updateButtonState(JButton prevButton, JButton nextButton) {
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled((currentPage + 1) * ITEMS_PER_PAGE < itemNames.length);
    }

    public void addPanel(String gun, String imagepath){
        JPanel itemPanel = createItemPanel(gun,imagepath);
        add(itemPanel, BorderLayout.CENTER);
        repaint();
    }

    public JPanel createItemPanel(String name, String imagePath) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(null);

        itemPanel.setPreferredSize(new Dimension(150, 180));

        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            double aspectRatio = (double) originalIcon.getIconWidth() / originalIcon.getIconHeight();
            int newWidth = 140;
            int newHeight = (int) (newWidth / aspectRatio);

            Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            JLabel itemImageLabel = new JLabel(new ImageIcon(scaledImage));
            itemImageLabel.setOpaque(false);

            itemImageLabel.setBounds(0, (150 - newHeight) / 2, newWidth, newHeight);
            itemPanel.add(itemImageLabel);
        }

        // 아이템 이름
        JLabel itemNameLabel = new JLabel(name);
        itemNameLabel.setOpaque(false);
        itemNameLabel.setHorizontalAlignment(JLabel.CENTER);
        itemNameLabel.setBounds(0, 150, 150, 30);
        itemPanel.add(itemNameLabel);

        // 종이 이미지 추가
        ImageIcon paperIcon = new ImageIcon("src/main/resources/images/paper_square.png");
        Image scaledPaperImage = paperIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel paperLabel = new JLabel(new ImageIcon(scaledPaperImage));
        paperLabel.setBounds(0, 0, 150, 150);
        itemPanel.add(paperLabel);

        return itemPanel;
    }


    private void addItemsToPanel(JPanel panel) {
        panel.removeAll();
        panel.setLayout(new GridBagLayout()); // 레이아웃 설정
        GridBagConstraints itemGbc = new GridBagConstraints(); // 이름 변경
        itemGbc.fill = GridBagConstraints.BOTH;
        int startIndex = currentPage * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            GridBagConstraints itemgbc = new GridBagConstraints(); // 새로운 GridBagConstraints 생성
            itemgbc.fill = GridBagConstraints.BOTH; // 공간을 모두 채우도록 설정
            itemgbc.insets = new Insets(5, 5, 5, 5); // 여백 설정
            itemgbc.gridx = i % 3; // 열 설정 (0, 1, 2)
            itemgbc.gridy = i / 3; // 행 설정 (0, 1, 2)

            if (i + startIndex < itemNames.length) {
                JPanel item = createItemPanel(itemNames[i + startIndex], itemImages[i + startIndex]);
                panel.add(item, itemgbc); // 아이템 추가
            } else {
                JPanel emptyItem = createItemPanel("", null);
                panel.add(emptyItem, itemgbc); // 빈 아이템 추가
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private void addItemToPanel(JPanel itemPanel, String name, String imagePath) {
        // 아이템 이미지 추가
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            double aspectRatio = (double) originalIcon.getIconWidth() / originalIcon.getIconHeight();
            int newWidth = (int) (150 * aspectRatio);
            Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, 150, Image.SCALE_SMOOTH);

            JLabel itemImageLabel = new JLabel(new ImageIcon(scaledImage));
            itemImageLabel.setOpaque(false); // 배경을 투명하게 설정
            itemPanel.add(itemImageLabel); // 종이 위에 이미지 추가
        }

        // 아이템 이름 추가
        JLabel itemNameLabel = new JLabel(name);
        itemNameLabel.setOpaque(false); // 배경을 투명하게 설정
        itemNameLabel.setHorizontalAlignment(JLabel.CENTER); // 중앙 정렬
        itemPanel.add(itemNameLabel); // 이름 추가
    }


    public void updateItemPanel(JPanel panel) {
        panel.removeAll(); // 기존 아이템 제거
        addItemsToPanel(panel); // 새로운 아이템 추가
        panel.revalidate(); // 패널 재검증
        panel.repaint(); // 패널 재페인팅
    }

}
