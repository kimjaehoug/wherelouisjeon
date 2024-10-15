package kr.jbnu.se.std.MultiPlayer;

import kr.jbnu.se.std.Duck;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    public DataInputStream input;
    public DataOutputStream output;
    private GameServer server; // 서버 참조
    private String playerName;

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;

        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // 클라이언트로부터 플레이어 이름 수신
            playerName = input.readUTF();
            server.addClient(this);

            // 클라이언트의 위치 업데이트
            while (true) {
                String command = input.readUTF();
                if (command.equals("UPDATE_POSITION")) {
                    int mouseX = input.readInt();
                    int mouseY = input.readInt();
                    server.updatePlayerPosition(playerName, mouseX, mouseY);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDucks(List<Duck> ducks) throws IOException {
        output.writeInt(ducks.size());
        for (Duck duck : ducks) {
            output.writeInt(duck.getX());
            output.writeInt(duck.getY());
            // 오리에 대한 추가 정보가 있으면 여기에 추가
        }
    }

    public String getPlayerName() {
        return playerName;
    }
}
