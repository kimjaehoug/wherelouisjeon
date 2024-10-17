package kr.jbnu.se.std.MultiPlayer;

import kr.jbnu.se.std.Duck;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class GameServer {
    private List<Duck> ducks; // 오리 목록
    private List<ClientHandler> clients;// 클라이언트 목록
    private int score;

    public GameServer() {
        ducks = new ArrayList<>();
        clients = new ArrayList<ClientHandler>();
        score = 0;
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void updatePlayerPosition(String playerName, int mouseX, int mouseY) {
        // 모든 클라이언트에게 위치 업데이트 전송
        for (ClientHandler client : clients) {
            if (!client.getPlayerName().equals(playerName)) {
                try {
                    client.output.writeUTF("PLAYER_MOVED");
                    client.output.writeUTF(playerName);
                    client.output.writeInt(mouseX);
                    client.output.writeInt(mouseY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 클라이언트에게 오리 위치 정보 전송
    public void broadcastDuck(Duck duck) {
        for (ClientHandler client : clients) {
        }
    }

    // 오리 위치 업로드 메소드
    public void uploadDuck(Duck duck) {
        updateDuck(duck);
        broadcastDuck(duck);
    }

    // 오리 위치 업데이트
    private void updateDuck(Duck duck) {
        ducks.add(duck); // 새로운 오리 추가
    }

    public void deleteDuck(Duck duck) {
        ducks.remove(duck);
    }

    public void plusscore(int score) {
        this.score += score;
    }

    public void sendMouseposition(Point mousePosition){

    }
}
