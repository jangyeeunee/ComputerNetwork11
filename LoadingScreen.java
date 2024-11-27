import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * 로딩 화면 클래스
 * 로딩 중 상태를 사용자에게 표시
 */
public class LoadingScreen extends JFrame {

    private PrintWriter out;
    private BufferedReader in;

    public LoadingScreen(PrintWriter out,BufferedReader in) {
        this.out=out;
        this.in=in;

        // 프레임 설정
        setTitle("Tic Tac Toe - Loading");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 로딩 메시지 라벨
        JLabel loadingLabel = new JLabel("Loading... Waiting for another player", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(loadingLabel, BorderLayout.NORTH);

        // 진행 표시 바
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.CENTER);

        // 추가 안내 메시지
        JLabel tipLabel = new JLabel("Please wait while we prepare the game...", SwingConstants.CENTER);
        tipLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(tipLabel, BorderLayout.SOUTH);

        // 프레임 설정 마무리
        setSize(400, 200);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setVisible(true);

        // 서버 메세지 대기
        WaitForGameStart();
    }

    private void WaitForGameStart() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("GAME_START")) {
                        SwingUtilities.invokeLater(() -> {
                            new BoardPage(out, in).setVisible(true); // BoardPage로 이동
                            dispose(); // 로딩 화면 닫기
                        });
                        break;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Disconnected from server.");
                e.printStackTrace();
                System.exit(0);
            }
        }).start();
    }

}