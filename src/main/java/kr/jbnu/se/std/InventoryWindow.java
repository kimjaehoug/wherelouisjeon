package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;

public class InventoryWindow extends JFrame {
    private int ITEMS_PER_PAGE;
    private int currentPage;
    private String[] itemNames;
    private String[] itemImages;
    private JPanel panel;

    public InventoryWindow(Framework framework) {
        setTitle("Inventory");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400,600);
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(4, 2));

        addItemsToPanel(itemPanel);
        add(itemPanel, BorderLayout.CENTER);
        setVisible(true);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("이전");
        JButton nextButton = new JButton("다음");

        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateItemPanel(itemPanel);
            }
        });

        // 다음 버튼 클릭 이벤트
        nextButton.addActionListener(e -> {
            if ((currentPage + 1) * ITEMS_PER_PAGE < itemNames.length) {
                currentPage++;
                updateItemPanel(itemPanel);
            }
        });

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH); // 하단에 추가

        setVisible(true);

    }
    public void addPanel(String gun, String imagepath){
        JPanel itemPanel = createItemPanel(gun,imagepath);
        add(itemPanel, BorderLayout.CENTER);
        repaint();
    }
    private void addItemsToPanel(JPanel panel) {
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = currentPage * ITEMS_PER_PAGE + i;
            if (index < itemNames.length) {
                JPanel item = createItemPanel(itemNames[index], itemImages[index]);
                panel.add(item);
            }
        }
    }

    public JPanel createItemPanel(String name, String imagePath) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());
        itemNames = new String[]{name};
        itemImages = new String[]{imagePath};
        // 아이템 이미지
        JLabel itemImageLabel = new JLabel(new ImageIcon(imagePath));
        itemPanel.add(itemImageLabel, BorderLayout.WEST); // 왼쪽에 이미지 추가

        // 아이템 이름 및 가격
        JPanel nameAndPricePanel = new JPanel();
        nameAndPricePanel.setLayout(new BorderLayout());
        JLabel itemNameLabel = new JLabel(name);
        nameAndPricePanel.add(itemNameLabel, BorderLayout.CENTER); // 중앙에 이름 추가
        itemPanel.add(nameAndPricePanel, BorderLayout.CENTER); // 중앙에 이름 및 가격 추가

        // 구매 버튼
        JButton buyButton = new JButton("장착");
        buyButton.addActionListener(e -> {
            System.out.println(name + " 장착!");

        });
        itemPanel.add(buyButton, BorderLayout.EAST); // 오른쪽에 버튼 추가

        return itemPanel;
    }


    public void updateItemPanel(JPanel panel) {
        panel.removeAll(); // 기존 아이템 제거
        addItemsToPanel(panel); // 새로운 아이템 추가
        panel.revalidate(); // 패널 재검증
        panel.repaint(); // 패널 재페인팅
    }

}
