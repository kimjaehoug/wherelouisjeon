package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

public class InventoryWindow extends JFrame {
    private int ITEMS_PER_PAGE;
    private int currentPage;
    private String[] itemNames;
    private String[] itemImages;
    private JButton buyButton;
    private String selectedItem = null;
    private JPanel centerPanel;
    private JPanel selectedPanel = null;

    public InventoryWindow(Framework framework) {
        setTitle("Inventory");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 840);

        ITEMS_PER_PAGE = 9;
        itemNames = new String[]{};
        itemImages = new String[]{};

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setPreferredSize(new Dimension(500, 840));
        add(centerPanel, BorderLayout.CENTER);

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
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);

        ImageIcon exitIcon = new ImageIcon("src/main/resources/images/btn_X2.png");
        ImageIcon exitPressIcon = new ImageIcon("src/main/resources/images/btn_X2_press.png");

        int buttonWidth = 70;
        int buttonHeight = (int) (70 * (117.0 / 120));
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
            if (selectedItem != null) {
                System.out.println(selectedItem + " 장착!");
                framework.setGun(selectedItem); // 실제 사용 메서드에 맞게 수정
            } else {
                System.out.println("아이템을 선택하세요!");
            }
        });

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

        setButtonProperties(prevButton, new ImageIcon(scaledPrevImage), new ImageIcon(prevPressIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        setButtonProperties(nextButton, new ImageIcon(scaledNextImage), new ImageIcon(nextPressIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));

        updateButtonState(prevButton, nextButton);

        buttonPanel.add(buyButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setButtonProperties(JButton button, ImageIcon defaultIcon, ImageIcon pressedIcon) {
        button.setIcon(defaultIcon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setIcon(pressedIcon);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setIcon(defaultIcon);
            }
        });
    }

    private void updateButtonState(JButton prevButton, JButton nextButton) {
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled((currentPage + 1) * ITEMS_PER_PAGE < itemNames.length);
    }

    public void addPanel(String gun, String imagepath) {
        itemNames = appendToArray(itemNames, gun);
        itemImages = appendToArray(itemImages, imagepath);
        updateItemPanel(centerPanel);
    }

    private String[] appendToArray(String[] array, String newItem) {
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newItem;
        return newArray;
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
            itemImageLabel.setBounds(0, (150 - newHeight) / 2, newWidth, newHeight);
            itemPanel.add(itemImageLabel);
        }

        JLabel itemNameLabel = new JLabel(name);
        itemNameLabel.setOpaque(false);
        itemNameLabel.setHorizontalAlignment(JLabel.CENTER);
        itemNameLabel.setBounds(0, 150, 150, 30);
        itemPanel.add(itemNameLabel);

        if (name != null && !name.isEmpty()) {
            itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    resetItemSelection(centerPanel);
                    if (selectedPanel != null) {
                        selectedPanel.setBackground(null);
                        selectedPanel.setBorder(null);
                    }

                    selectedPanel = itemPanel;
                    selectedPanel.setOpaque(false);
                    selectedPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

                    itemPanel.setOpaque(false);
                    itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                    selectedItem = name;
                    System.out.println(name + " 선택됨!");
                }
            });
        }

        ImageIcon paperIcon = new ImageIcon("src/main/resources/images/paper_square.png");
        Image scaledPaperImage = paperIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel paperLabel = new JLabel(new ImageIcon(scaledPaperImage));
        paperLabel.setBounds(0, 0, 150, 150);
        itemPanel.add(paperLabel);

        return itemPanel;
    }

    private void resetItemSelection(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(null);
                ((JPanel) comp).setBorder(null);
            }
        }
        selectedItem = null;
    }

    private void addItemsToPanel(JPanel panel) {
        panel.removeAll();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints itemGbc = new GridBagConstraints();
        itemGbc.fill = GridBagConstraints.BOTH;
        int startIndex = currentPage * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            GridBagConstraints itemgbc = new GridBagConstraints();
            itemgbc.fill = GridBagConstraints.BOTH;
            itemgbc.insets = new Insets(5, 5, 5, 5);
            itemgbc.gridx = i % 3;
            itemgbc.gridy = i / 3;

            if (i + startIndex < itemNames.length) {
                JPanel item = createItemPanel(itemNames[i + startIndex], itemImages[i + startIndex]);
                panel.add(item, itemgbc);
            } else {
                JPanel emptyItem = createItemPanel("", null);
                panel.add(emptyItem, itemgbc);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    public void updateItemPanel(JPanel panel) {
        panel.removeAll();
        addItemsToPanel(panel);
        panel.revalidate();
        panel.repaint();
    }
}
