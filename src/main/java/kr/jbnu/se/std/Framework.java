package kr.jbnu.se.std;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * kr.jbnu.se.std.Framework that controls the game (kr.jbnu.se.std.Game.java) that created it, update it and draw it on the screen.
 *
 * @author www.gametutorial.net
 */

public class Framework extends Canvas {

    //login 변수
    private JPanel loginPanel;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton passButton;
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds.
     * 1 second = 1 000 000 000 nanoseconds
     */
    public static final long secInNanosec = 1000000000L;

    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;

    /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;

    /**
     * Possible states of the game
     */
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING,LOGIN,MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;

    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;

    // The actual game
    private Game game;


    /**
     * Image for menu.
     */
    private BufferedImage shootTheDuckMenuImg;


    public Framework ()
    {
        super();

        gameState = GameState.VISUALIZING;

        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                loginPanel = new JPanel();
                GameLoop();
            }
        };
        gameThread.start();
    }


    /**
     * Set variables and objects.
     * This method is intended to set the variables and objects for this class, variables and objects for the actual game can be set in kr.jbnu.se.std.Game.java.
     */
    private void Initialize()
    {
        // 화면 크기 정보를 가져옵니다.
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle screenSize = gd.getDefaultConfiguration().getBounds();
        frameWidth = (int) screenSize.getWidth();
        frameHeight = (int) screenSize.getHeight();
        this.setSize(frameWidth, frameHeight);
    }


    /**
     * Load files - images, sounds, ...
     * This method is intended to load files for this class, files for the actual game can be loaded in kr.jbnu.se.std.Game.java.
     */
    private void LoadContent()
    {
        try
        {
            URL shootTheDuckMenuImgUrl = this.getClass().getResource("/images/menu.jpg");
            shootTheDuckMenuImg = ImageIO.read(shootTheDuckMenuImgUrl);

            // 이미지가 화면 크기에 맞게 조정됩니다.
            shootTheDuckMenuImg = resizeImage(shootTheDuckMenuImg, frameWidth, frameHeight);
        }
        catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 이미지의 가로와 세로 비율을 계산합니다.
        float aspectRatio = (float) originalWidth / originalHeight;

        int newWidth;
        int newHeight;

        // 화면의 비율에 맞게 이미지를 조정합니다.
        if (targetWidth / (float) targetHeight > aspectRatio) {
            newWidth = (int) (targetHeight * aspectRatio);
            newHeight = targetHeight;
        } else {
            newWidth = targetWidth;
            newHeight = (int) (targetWidth / aspectRatio);
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }



    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is updated and then the game is drawn on the screen.
     */
    private void GameLoop()
    {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();

        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;

        while(true)
        {
            beginTime = System.nanoTime();

            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;

                    game.UpdateGame(gameTime, mousePosition());

                    lastTime = System.nanoTime();
                    break;
                case GAMEOVER:
                    //...
                    break;
                case LOGIN:
                    createLoginPanel();
                    gameState = GameState.MAIN_MENU;
                    break;

                case MAIN_MENU:
                    //...
                    break;
                case OPTIONS:
                    //...
                    break;
                case GAME_CONTENT_LOADING:
                    //...
                    break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();

                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.LOGIN;
                    break;
                case VISUALIZING:
                    // On Ubuntu OS (when I tested on my old computer) this.getWidth() method doesn't return the correct value immediately (eg. for frame that should be 800px width, returns 0 than 790 and at last 798px).
                    // So we wait one second for the window/frame to be set to its correct size. Just in case we
                    // also insert 'this.getWidth() > 1' condition in case when the window/frame size wasn't set in time,
                    // so that we although get approximately size.
                    if(this.getWidth() > 1 && visualizingTime > secInNanosec)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();

                        // When we get size of frame we change status.
                        gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                    break;
            }

            // Repaint the screen.
            repaint();

            // Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10)
                timeLeft = 10; //set a minimum
            try {
                //Provides the necessary delay and also yields control so that other thread can do work.
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }

    private void createLoginPanel() {
        if (loginPanel != null) {
            return; // 이미 로그인 패널이 생성된 경우
        }

        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weighty = 500.0;

        // ID 라벨 및 입력 필드
        JLabel idLabel = new JLabel("ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(idLabel, gbc);

        idField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(idField, gbc);

        // 비밀번호 라벨 및 입력 필드
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(passwordField, gbc);

        // 로그인 버튼
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        // 회원가입 버튼
        signupButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(signupButton, gbc);

        // 로그인 버튼 클릭 시 이벤트 처리
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                // 로그인 확인 로직 (임시로 id와 password가 비어있지 않으면 로그인 성공으로 간주)
                if (!id.isEmpty() && !password.isEmpty()) {
                    gameState = GameState.MAIN_MENU; // 게임 상태 변경
                    loginPanel.setVisible(false); // 로그인 패널 비활성화
                    repaint(); // 화면 갱신
                } else {
                    JOptionPane.showMessageDialog(null, "ID와 비밀번호를 입력하세요.");
                }
            }
        });

        // 회원가입 버튼 클릭 시 이벤트 처리
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 회원가입 로직 추가 가능
                JOptionPane.showMessageDialog(null, "회원가입 기능은 아직 구현되지 않았습니다.");
            }
        });

        // 로그인 패널을 프레임에 추가
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.setMinimumSize(new Dimension(frameWidth, frameHeight));
            topFrame.getContentPane().add(loginPanel, BorderLayout.SOUTH);

            topFrame.pack();
        }
    }

    @Override
    public void Draw(Graphics2D g2d) {
        // 로그인 패널이 보이는 경우
        if (loginPanel.isVisible()) {
            // 로그인 패널을 중앙에 배치합니다.
            int x = (frameWidth - shootTheDuckMenuImg.getWidth()) / 2;
            int y = (frameHeight - shootTheDuckMenuImg.getHeight()) / 2;
            g2d.drawImage(shootTheDuckMenuImg, x, y, null);
        }else {
            switch (gameState) {
                case PLAYING:
                    game.Draw(g2d, mousePosition());
                    break;
                case GAMEOVER:
                    game.DrawGameOver(g2d, mousePosition());
                    break;
                case MAIN_MENU:
                    // 중앙에 이미지를 배치합니다.
                    int x1 = (frameWidth - shootTheDuckMenuImg.getWidth()) / 2;
                    int y1 = (frameHeight - shootTheDuckMenuImg.getHeight()) / 2;
                    g2d.drawImage(shootTheDuckMenuImg, x1, y1, null);

                    // 나머지 텍스트는 화면 중앙에 맞게 배치합니다.
                    g2d.drawString("Use left mouse button to shot the duck.", frameWidth / 2 - 83, (int) (frameHeight * 0.65));
                    g2d.drawString("Click with left mouse button to start the game.", frameWidth / 2 - 100, (int) (frameHeight * 0.67));
                    g2d.drawString("Press ESC any time to exit the game.", frameWidth / 2 - 75, (int) (frameHeight * 0.70));
                    g2d.setColor(Color.white);
                    g2d.drawString("WWW.GAMETUTORIAL.NET", 7, frameHeight - 5);
                    break;
                case OPTIONS:
                    //...
                    break;
                case GAME_CONTENT_LOADING:
                    g2d.setColor(Color.white);
                    g2d.drawString("GAME is LOADING", frameWidth / 2 - 50, frameHeight / 2);
                    break;
            }
        }
    }
    /**
     * Starts new game.
     */
    private void newGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();

        game = new Game();
    }

    /**
     *  Restart game - reset game time and call RestartGame() method of game object so that reset some variables.
     */
    private void restartGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();

        game.RestartGame();

        // We change game status so that the game can start.
        gameState = GameState.PLAYING;
    }

    /**
     * Returns the position of the mouse pointer in game frame/window.
     * If mouse position is null than this method return 0,0 coordinate.
     *
     * @return Point of mouse coordinates.
     */
    private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();

            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }

    /**
     * This method is called when keyboard key is released.
     *
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        switch (gameState)
        {
            case GAMEOVER:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    System.exit(0);
                else if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
                    restartGame();
                break;
            case PLAYING:
            case MAIN_MENU:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    System.exit(0);
                break;
        }
    }

    /**
     * This method is called when mouse button is clicked.
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        switch (gameState)
        {
            case MAIN_MENU:
                if(e.getButton() == MouseEvent.BUTTON1)
                    newGame();
                break;
        }
    }
}