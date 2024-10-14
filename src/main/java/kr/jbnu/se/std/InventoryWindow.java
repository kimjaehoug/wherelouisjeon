package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;

// InventoryWindow 클래스 수정
public class InventoryWindow extends JFrame {
    private int ITEMS_PER_PAGE = 8;
    private int currentPage = 0;
    private String[] itemNames;
    private String[] itemImages;
    private JPanel itemPanel;
    private Framework framework;

    public InventoryWindow(Framework framework) {
        this.framework = framework;
        setTitle("Inventory");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400, 600);

        itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(4, 2));

        // 빈 상태에서 시작
        add(itemPanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("이전");
        JButton nextButton = new JButton("다음");

        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateItemPanel();
            }
        });

        nextButton.addActionListener(e -> {
            if ((currentPage + 1) * ITEMS_PER_PAGE < itemNames.length) {
                currentPage++;
                updateItemPanel();
            }
        });

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    // 패널에 아이템 추가 메소드
    public void addPanel(String itemName, String itemImagePath) {
        JPanel item = createItemPanel(itemName, itemImagePath);
        itemPanel.add(item);
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    // 개별 아이템 패널 생성
    public JPanel createItemPanel(String name, String imagePath) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());

        // 아이템 이미지
        JLabel itemImageLabel = new JLabel(new ImageIcon(imagePath));
        itemPanel.add(itemImageLabel, BorderLayout.WEST);

        // 아이템 이름
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        JLabel itemNameLabel = new JLabel(name);
        namePanel.add(itemNameLabel, BorderLayout.CENTER);
        itemPanel.add(namePanel, BorderLayout.CENTER);

        // 장착 버튼
        JButton equipButton = new JButton("장착");
        equipButton.addActionListener(e -> {
            System.out.println(name + " 장착!");
            framework.setGun(name);
        });
        itemPanel.add(equipButton, BorderLayout.EAST);

        return itemPanel;
    }

    public void updateItemPanel() {
        itemPanel.removeAll(); // 기존 아이템 제거
        // 새로 추가된 아이템 표시
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = currentPage * ITEMS_PER_PAGE + i;
            if (index < itemNames.length) {
                JPanel item = createItemPanel(itemNames[index], itemImages[index]);
                itemPanel.add(item);
            }
        }
        itemPanel.revalidate(); // 패널 갱신
        itemPanel.repaint(); // 패널 재페인팅
    }
}

