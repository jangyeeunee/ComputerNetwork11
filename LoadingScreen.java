import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 로딩 화면 클래스
 * 로딩 중 상태를 사용자에게 표시
 */
public class LoadingScreen extends JFrame {

    private PrintWriter out;
    private BufferedReader in;
    private Thread waitThread; // 대기 스레드 참조

    public LoadingScreen(PrintWriter out, BufferedReader in, String message) {
        this.out = out;
        this.in = in;

        // 프레임 설정
        setTitle("Tic Tac Toe - Loading");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 로딩 메시지 라벨
        JLabel loadingLabel = new JLabel(message, SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(loadingLabel, BorderLayout.NORTH);

        // 진행 표시 바
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.CENTER);

        // 프레임 설정 마무리
        setSize(400, 200);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setVisible(true);

        // 서버 메세지 대기
        WaitForGameStart();
    }

    // 서버 메시지에 따라 BoardPage로 전환
    private void WaitForGameStart() {
        waitThread = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("GAME_START")) {
                        SwingUtilities.invokeLater(() -> {
                            new BoardPage(out, in).setVisible(true); // 보드 페이지로 이동
                            dispose(); // 로딩 화면 닫기
                        });
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Connection lost.");
                dispose();
            }
        });
        waitThread.start();
    }

    // 창 닫을 때 스레드 종료
    @Override
    public void dispose() {
        if (waitThread != null) {
            waitThread.interrupt(); // 스레드 중지
            try {
                waitThread.join(); // 스레드 종료를 기다림
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.dispose(); // 부모 클래스의 dispose 호출
    }

}
