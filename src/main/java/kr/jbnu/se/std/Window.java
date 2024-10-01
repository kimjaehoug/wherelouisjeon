package kr.jbnu.se.std;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Window extends JFrame {
    Window() {
        // 화면 해상도를 가져옴
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        this.setTitle("Shoot the duck");
        this.setSize(screenWidth, screenHeight);  // 해상도에 맞춰 창 크기를 설정
        this.setLocationRelativeTo((Component)null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new Framework(this));
        this.setVisible(false);
    }

    public void onLoginSuccess(){
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Window();
            }
        });
    }
}
