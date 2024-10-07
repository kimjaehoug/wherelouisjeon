package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShopWindow extends JFrame {
    private static final int ITEMS_PER_PAGE = 8; // 페이지당 아이템 수
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
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 아이템 패널
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(4, 2)); // 4행 2열로 배치

        // 아이템 추가
        addItemsToPanel(itemPanel);
        add(itemPanel, BorderLayout.CENTER); // 중앙에 추가
        setVisible(true);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("이전");
        JButton nextButton = new JButton("다음");

        // 이전 버튼 클릭 이벤트
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

    private void addItemsToPanel(JPanel panel) {
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = currentPage * ITEMS_PER_PAGE + i;
            if (index < itemNames.length) {
                JPanel item = createItemPanel(itemNames[index], itemImages[index], itemPrices[index]);
                panel.add(item);
            }
        }
    }

    private void updateItemPanel(JPanel panel) {
        panel.removeAll(); // 기존 아이템 제거
        addItemsToPanel(panel); // 새로운 아이템 추가
        panel.revalidate(); // 패널 재검증
        panel.repaint(); // 패널 재페인팅
    }

    private JPanel createItemPanel(String name, String imagePath, int price) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());

        // 아이템 이미지
        JLabel itemImageLabel = new JLabel(new ImageIcon(imagePath));
        itemPanel.add(itemImageLabel, BorderLayout.WEST); // 왼쪽에 이미지 추가

        // 아이템 이름 및 가격
        JPanel nameAndPricePanel = new JPanel();
        nameAndPricePanel.setLayout(new BorderLayout());
        JLabel itemNameLabel = new JLabel(name);
        JLabel itemPriceLabel = new JLabel(price + " 원");
        nameAndPricePanel.add(itemNameLabel, BorderLayout.CENTER); // 중앙에 이름 추가
        nameAndPricePanel.add(itemPriceLabel, BorderLayout.SOUTH); // 하단에 가격 추가

        itemPanel.add(nameAndPricePanel, BorderLayout.CENTER); // 중앙에 이름 및 가격 추가

        // 구매 버튼
        JButton buyButton = new JButton("구매");
        buyButton.addActionListener(e -> {
            framework.InventoryAdder_Gun(name);
            framework.buySomeThing(price);
            framework.getMoney();
            System.out.println(name + " 구매!");

        });
        itemPanel.add(buyButton, BorderLayout.EAST); // 오른쪽에 버튼 추가

        return itemPanel;
    }

    // 가격 변경 메소드 추가
    public void setItemPrice(int index, int price) {
        if (index >= 0 && index < itemPrices.length) {
            itemPrices[index] = price;
        }
    }
}
