package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png",
            "src/main/resources/images/duck.png", "src/main/resources/images/duck.png"
    };
    private Framework framework;

    private int currentPage = 0; // 현재 페이지

    public ShopWindow(Framework framework) {
        this.framework = framework;
        setTitle("상점");
        setSize(840, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상점 나가기 버튼 추가
        JButton exitButton = new JButton("상점 나가기");
        exitButton.addActionListener(e -> {
            dispose();
        });

        // 오른쪽 상단에 위치시키기 위한 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(exitButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 아이템 패널
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(2, 3));
        itemPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // 아이템 추가
        addItemsToPanel(itemPanel);
        add(itemPanel, BorderLayout.CENTER);

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

        // 버튼 사이에 공백 추가
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
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        //itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setPreferredSize(new Dimension(120, 180));

        // 아이템 이미지
        JLabel itemImageLabel = new JLabel(new ImageIcon(imagePath));
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
        itemPanel.add(nameAndPricePanel);

        itemPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 공백 추가


        // 구매 버튼
        ImageIcon buyIcon = new ImageIcon("src/main/resources/images/btn_buy.png");
        ImageIcon buyPressIcon = new ImageIcon("src/main/resources/images/btn_buy_press.png");
        Image scaledBuyImage = buyIcon.getImage().getScaledInstance(100, 43, Image.SCALE_SMOOTH); // 원하는 크기로 조정
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
            framework.InventoryAdder_Gun(name);
            framework.buySomeThing(price);
            framework.getMoney();
            System.out.println(name + " 구매!");
        });
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
