package kr.jbnu.se.std;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class RankWindow extends JFrame {
    private DefaultTableModel tableModel;

    public RankWindow() {
        setTitle("리더보드");
        JTable leaderboardTable;
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 테이블 모델과 테이블 생성
        tableModel = new DefaultTableModel(new String[]{"순위", "닉네임", "점수"}, 0);
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        add(scrollPane, BorderLayout.CENTER);

        fetchLeaderboardData();
    }

    private void fetchLeaderboardData() {
        OkHttpClient client = new OkHttpClient();
        String leaderboardUrl = "https://shootthedock-default-rtdb.firebaseio.com/leaderboard.json"; // 적절한 URL로 수정

        Request request = new Request.Builder()
                .url(leaderboardUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(RankWindow.this, "리더보드 데이터를 가져오지 못했습니다: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        updateLeaderboard(jsonResponse);
                    } catch (JSONException e) {
                        System.err.println("JSON 파싱 오류: " + e.getMessage());
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(RankWindow.this, "리더보드 데이터를 가져오는 데 실패했습니다: " + response.code());
                    });
                }
            }
        });
    }

    private void updateLeaderboard(JSONObject leaderboardData) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // 기존 데이터 지우기
            List<Player> players = new ArrayList<>();

            for (String key : leaderboardData.keySet()) {
                try {
                    JSONObject playerData = leaderboardData.getJSONObject(key);
                    String nickname = playerData.getString("nickname");
                    int score = playerData.getInt("score");
                    players.add(new Player(nickname, score));
                } catch (JSONException e) {
                    System.err.println("JSON 파싱 오류: " + e.getMessage());
                }
            }

            players.sort(Comparator.comparingInt(Player::getScore).reversed());

            int rank = 1;
            for (Player player : players) {
                tableModel.addRow(new Object[]{rank++, player.getNickname(), player.getScore()});
            }
        });
    }

    // Player
    private static class Player {
        private final String nickname;
        private final int score;

        public Player(String nickname, int score) {
            this.nickname = nickname;
            this.score = score;
        }

        public String getNickname() {
            return nickname;
        }

        public int getScore() {
            return score;
        }
    }
}
