package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class ShopWindow extends JFrame {
    private static final int ITEMS_PER_PAGE = 6; // 페이지당 아이템 수
    private String[] itemNames = {
            "더블배럴샷건", "AK-47", "M4A1", "SMG", "스나이퍼", "핸드건", "유탄발사기", "톱",
            "기타1", "기타2", "기타3", "기타4", "기타5", "기타6", "기타7", "기타8"
    };
    private int[] itemPrices = {
            1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
            1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000
    };
    private String[] itemImages = {
            "src/main/resources/images/gun_01.png", "src/main/resources/images/gun_02.png",
            "src/main/resources/images/gun_03.png", "src/main/resources/images/gun_04.png",
            "src/main/resources/images/gun_05.png", "src/main/resources/images/gun_06.png",
            "src/main/resources/images/gun_07.png", "src/main/resources/images/gun_08.png",
            "src/main/resources/images/gun_09.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png"
    };
    private String nickname;
    private JLabel nameLabel;
    private Framework framework;

    private int currentPage = 0; // 현재 페이지
    private Image backgroundImage;

    public ShopWindow(Framework framework) {
        this.framework = framework;
        setTitle("SHOP");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Outer Panel 추가
        OuterPanel outerPanel = new OuterPanel();
        outerPanel.setLayout(new BorderLayout());
        add(outerPanel, BorderLayout.CENTER);

        // Top Panel 설정
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        outerPanel.add(topPanel, BorderLayout.NORTH);

        // 간판 추가
        ImageIcon signboardIcon = new ImageIcon("src/main/resources/images/signboard.png");
        Image signboardImage = signboardIcon.getImage().getScaledInstance(286, 135, Image.SCALE_SMOOTH);
        JLabel signboardLabel = new JLabel(new ImageIcon(signboardImage));
        signboardLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel shopLabel = new JLabel("SHOP");
        shopLabel.setFont(new Font("ADLaM Display", Font.BOLD, 45));
        shopLabel.setForeground(Color.DARK_GRAY);
        shopLabel.setHorizontalAlignment(JLabel.CENTER);
        shopLabel.setVerticalAlignment(JLabel.CENTER);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(286, 135));

        signboardLabel.setBounds(0, 0, 286, 135);
        shopLabel.setBounds(0, 0, 286, 135);

        layeredPane.add(signboardLabel, Integer.valueOf(0));
        layeredPane.add(shopLabel, Integer.valueOf(1));

        // 상점 나가기 버튼
        JButton exitButton = new JButton();
        ImageIcon exitIcon = new ImageIcon("src/main/resources/images/btn_X2.png");
        ImageIcon exitPressIcon = new ImageIcon("src/main/resources/images/btn_X2_press.png");

        int buttonWidth = 70;
        int buttonHeight = (int) (70 * (117.0 / 120)); // 비율 맞춤
        exitButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        Image scaledExitImage = exitIcon.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
        Image scaledExitPressImage = exitPressIcon.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);

        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setMargin(new Insets(0, 0, 0, 0));

        exitButton.setIcon(new ImageIcon(scaledExitImage));
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                exitButton.setIcon(new ImageIcon(scaledExitPressImage));
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                exitButton.setIcon(new ImageIcon(scaledExitImage));
            }
        });

        exitButton.addActionListener(e -> {
            dispose();
        });

        // Top Panel에 간판과 버튼 추가
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(layeredPane, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        topPanel.add(exitButton, gbc);

        // Background Panel 추가
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // 아이템 패널 추가
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(2, 3));
        itemPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        backgroundPanel.add(itemPanel, BorderLayout.CENTER);

        // 프로필 패널
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createTitledBorder("프로필"));
        profilePanel.setPreferredSize(new Dimension(800, 50));
        backgroundPanel.add(profilePanel, BorderLayout.NORTH);

        // 재화 표시
        JLabel moneyLabel = new JLabel("재화: " + " 원");
        profilePanel.add(moneyLabel);
        addItemsToPanel(itemPanel);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        ImageIcon prevIcon = new ImageIcon("src/main/resources/images/btn_left2.png");
        ImageIcon nextIcon = new ImageIcon("src/main/resources/images/btn_right2.png");
        ImageIcon prevPressIcon = new ImageIcon("src/main/resources/images/btn_left2_press.png");
        ImageIcon nextPressIcon = new ImageIcon("src/main/resources/images/btn_right2_press.png");

        Image scaledPrevImage = prevIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        Image scaledNextImage = nextIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        JButton prevButton = new JButton(new ImageIcon(scaledPrevImage));
        JButton nextButton = new JButton(new ImageIcon(scaledNextImage));

        prevButton.setPreferredSize(new Dimension(50, 50));
        nextButton.setPreferredSize(new Dimension(50, 50));

        setButtonProperties(prevButton, new ImageIcon(scaledPrevImage), new ImageIcon(prevPressIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        setButtonProperties(nextButton, new ImageIcon(scaledNextImage), new ImageIcon(nextPressIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));

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

        // 버튼 사이에 공백 추가
        buttonPanel.add(prevButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(nextButton);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
        outerPanel.add(backgroundPanel, BorderLayout.CENTER);

        backgroundPanel.revalidate();
        backgroundPanel.repaint();

        setVisible(true);
    }

    private class OuterPanel extends JPanel {
        public OuterPanel() {
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    private class BackgroundPanel extends JPanel {
        public BackgroundPanel() {
            setBackground(Color.BLUE); // 예: 연한 회색
            setOpaque(true); // 패널을 불투명하게 설정
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    private void setButtonProperties(JButton button, ImageIcon defaultIcon, ImageIcon pressedIcon) {
        button.setIcon(defaultIcon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));

        // 마우스 리스너 추가
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setIcon(pressedIcon); // 눌렸을 때 아이콘 변경
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setIcon(defaultIcon); // 놓았을 때 원래 아이콘으로 복구
            }
        });
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        nameLabel.setText("이름: " + nickname);
    }

    private void updateButtonState(JButton prevButton, JButton nextButton) {
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled((currentPage + 1) * ITEMS_PER_PAGE < itemNames.length);
    }

    private void addItemsToPanel(JPanel panel) {
        panel.removeAll();
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = currentPage * ITEMS_PER_PAGE + i;
            if (index < itemNames.length) {
                JPanel item = createItemPanel(itemNames[index], itemImages[index], itemPrices[index]);
                panel.add(item);
            } else {
                JPanel emptySlot = new JPanel();
                emptySlot.setBackground(panel.getBackground());
                panel.add(emptySlot);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private void updateItemPanel(JPanel panel) {
        panel.removeAll();
        addItemsToPanel(panel);
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createItemPanel(String name, String imagePath, int price) {
        JPanel itemPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImage = new ImageIcon("src/main/resources/images/paper_rectangle.png").getImage();
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int originalWidth = backgroundImage.getWidth(null);
                int originalHeight = backgroundImage.getHeight(null);
                double aspectRatio = (double) originalWidth / originalHeight;
                int newWidth = (int) (panelHeight * aspectRatio);
                int x = (panelWidth - newWidth) / 2;

                g.drawImage(backgroundImage, x + 15, 15, newWidth - 30, panelHeight - 30, this);
            }
        };

        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setPreferredSize(new Dimension(120, 180));
        itemPanel.setOpaque(false);

        // 위쪽 공백 추가
        itemPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 아이템 이미지
        ImageIcon itemImageIcon = new ImageIcon(imagePath);
        Image itemImage = itemImageIcon.getImage();
        int originalWidth = itemImage.getWidth(null);
        int originalHeight = itemImage.getHeight(null);

        // 비율 계산
        int newHeight = 100;
        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth = (int) (newHeight * aspectRatio);

        JLabel itemImageLabel = new JLabel(new ImageIcon(itemImage.getScaledInstance(150, newHeight, Image.SCALE_SMOOTH)));
        itemImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        itemPanel.add(itemImageLabel);
        itemPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 공백 추가

        // 아이템 이름 및 가격
        JPanel nameAndPricePanel = new JPanel();
        nameAndPricePanel.setLayout(new BoxLayout(nameAndPricePanel, BoxLayout.Y_AXIS));
        JLabel itemNameLabel = new JLabel(name);
        JLabel itemPriceLabel = new JLabel(price + " 원");
        itemNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameAndPricePanel.add(itemNameLabel);
        nameAndPricePanel.add(itemPriceLabel);
        nameAndPricePanel.setOpaque(false);

        itemPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 공백 추가
        itemPanel.add(nameAndPricePanel);

        // 구매 버튼
        ImageIcon buyIcon = new ImageIcon("src/main/resources/images/btn_buy.png");
        ImageIcon buyPressIcon = new ImageIcon("src/main/resources/images/btn_buy_press.png");
        Image scaledBuyImage = buyIcon.getImage().getScaledInstance(100, 43, Image.SCALE_SMOOTH);
        JButton buyButton = new JButton(new ImageIcon(scaledBuyImage));

        buyButton.setContentAreaFilled(false);
        buyButton.setBorderPainted(false);
        buyButton.setFocusPainted(false);
        buyButton.setMargin(new Insets(0, 0, 0, 0));
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buyButton.setIcon(new ImageIcon(buyPressIcon.getImage().getScaledInstance(100, 43, Image.SCALE_SMOOTH)));
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                buyButton.setIcon(new ImageIcon(scaledBuyImage));
            }
        });

        buyButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    name + " 구매하시겠습니까?", "구매 확인",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                framework.InventoryAdder_Gun(name);
                framework.buySomeThing(price);
                framework.getMoney();

                // 구매 완료 알림
                JOptionPane.showMessageDialog(this, name + " 구매가 완료되었습니다!", "구매 완료", JOptionPane.INFORMATION_MESSAGE);
                System.out.println(name + " 구매!");
            }
        });

        itemPanel.add(Box.createVerticalGlue());
        itemPanel.add(buyButton);

        return itemPanel;
    }

    // 가격 변경 메소드 추가
    public void setItemPrice(int index, int price) {
        if (index >= 0 && index < itemPrices.length) {
            itemPrices[index] = price;
        }
    }
}

