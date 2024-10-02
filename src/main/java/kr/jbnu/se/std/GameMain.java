package kr.jbnu.se.std;

import com.google.firebase.auth.FirebaseAuth;

import javax.swing.*;
import java.awt.*;
import com.google.firebase.auth.*;

public class GameMain extends JFrame {
    private Framework framework;
    private FirebaseAuth auth;

    public GameMain(Framework framework) {
        this.framework = framework;
        setTitle("main");
        setSize(300,150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        auth = FirebaseAuth.getInstance();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,5,5,5);

        JLabel nicknameLabel = new JLabel("Nickname");
        c.gridx = 0;
        c.gridy = 0;
        panel.add(nicknameLabel, c);

        JTextField nicknameField = new JTextField(15);
        c.gridy = 1;
        c.gridx = 1;
        panel.add(nicknameField, c);
    }
}
