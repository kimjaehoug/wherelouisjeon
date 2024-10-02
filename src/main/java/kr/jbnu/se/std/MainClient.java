package kr.jbnu.se.std;

import javax.swing.*;

public class MainClient extends JFrame {
    private JPanel mainpanel;
    private JButton button1;

    public MainClient() {
        setTitle("Shoot the Dock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280,720);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setContentPane(mainpanel);
    }
}
