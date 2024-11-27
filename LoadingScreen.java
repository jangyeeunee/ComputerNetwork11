import javax.swing.*;
import java.awt.*;

/**
 * 로딩 화면 클래스
 * 로딩 중 상태를 사용자에게 표시
 */
public class LoadingScreen extends JFrame {

    public LoadingScreen(String message) {
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

        // 추가 안내 메시지
        JLabel tipLabel = new JLabel("Please wait while we prepare the game...", SwingConstants.CENTER);
        tipLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(tipLabel, BorderLayout.SOUTH);

        // 프레임 설정 마무리
        setSize(400, 200);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setVisible(true);

        // 로딩 프로세스 시뮬레이션
        simulateLoading();
    }

    /**
     * 로딩 과정을 시뮬레이션하는 메서드
     */
    private void simulateLoading() {
        // 백그라운드 스레드에서 실행하여 GUI 프리징 방지
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 로딩 시간 시뮬레이션 (ex 서버 연결 대기)
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 로딩 완료 후 다음 화면으로 전환
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // 로딩 화면 닫기
                        dispose();
                        // 게임 화면 또는 다음 단계로 이동
                        showGameScreen();
                    }
                });
            }
        }).start();
    }

    /**
     * 로딩 완료 후 게임 화면으로 전환하는 메서드 (현재는 메시지 표시)
     */
    private void showGameScreen() {
        JOptionPane.showMessageDialog(null, "Loading complete! Game will start now.");
        // 여기서 게임 화면을 초기화

    }
}