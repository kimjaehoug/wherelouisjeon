package kr.jbnu.se.std;

import javax.swing.*;
import java.awt.*;

public class TextField extends JTextField {
    private Color backgroundColor = Color.BLACK;
    private Color textColor = Color.WHITE;

    public TextField(int columns) {
        super(columns);
        setOpaque(false); // 텍스트 필드를 투명하게 만듭니다.
        setForeground(textColor);
        setBackground(backgroundColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 배경 그리기
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setTextColor(Color color) {
        this.textColor = color;
        setForeground(color);
    }
}
