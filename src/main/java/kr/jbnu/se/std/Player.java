package kr.jbnu.se.std;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

class Player {
    private String name;
    private int score;
    private Socket socket;

    public Player(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        this.score = 0;
    }

    public void increaseScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 기타 필요한 메서드
}
