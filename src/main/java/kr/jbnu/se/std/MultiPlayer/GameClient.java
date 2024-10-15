package kr.jbnu.se.std.MultiPlayer;

import kr.jbnu.se.std.Duck;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GameClient {
    private Point otherPlayerMousePosition;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String serverAddress;
    private int serverPort; // 서버 주소
    private ArrayList<Duck> duckList;

    public GameClient(String serverAddress, int serverPort) {
        // 클라이언트 초기화 코드
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.otherPlayerMousePosition = new Point(0, 0);// 초기값 설정
        this.duckList = new ArrayList<>();

        try {
            // 서버에 연결
            socket = new Socket(serverAddress, serverPort);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            // 별도의 스레드에서 서버로부터 데이터를 수신
            new Thread(this::listenForUpdates).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadDuckList() {
        try {
            output.writeInt(duckList.size());
            for (Duck duck : duckList) {
                // 추가적인 속성도 필요에 따라 전송 가능
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendShootData(Point mousePosition) {
        try {
            output.writeInt(mousePosition.x);
            output.writeInt(mousePosition.y);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateOtherPlayerMousePosition(Point position) {
        this.otherPlayerMousePosition = position;
    }

    public Point getOtherPlayerMousePosition() {
        return otherPlayerMousePosition;
    }

    private void listenForUpdates() {
        while (true) {
            try {
                // 다른 플레이어의 마우스 위치를 수신
                int x = input.readInt();
                int y = input.readInt();
                updateOtherPlayerMousePosition(new Point(x, y));
            } catch (IOException e) {
                e.printStackTrace();
                break; // 예외가 발생하면 루프 종료
            }
        }
    }

    public void disconnect() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
