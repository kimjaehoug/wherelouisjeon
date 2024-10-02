package kr.jbnu.se.std;

public class ChatMessage {
    private String message;
    private String nickname;
    public ChatMessage() {
    }
    public ChatMessage(String message, String nickname) {
        this.message = message;
        this.nickname = nickname;
    }
    public String getMessage() {
        return message;
    }
    public String getNickname() {
        return nickname;
    }
}
