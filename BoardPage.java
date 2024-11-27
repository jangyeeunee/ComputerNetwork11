import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPage extends JFrame {
    private GameLogic gameLogic = new GameLogic(); // 게임 로직 객체
    private JLabel statusLabel; // 현재 상태 표시 라벨
    private JLabel scoreLabel;  // 점수 표시 라벨
    private JPanel boardPanel;  // 게임 보드 패널

    public BoardPage() {
        setTitle("Tic Tac Toe");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 상태 표시
        statusLabel = new JLabel("Player X's Turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(statusLabel, BorderLayout.NORTH);

        // 중앙 보드 패널
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        // 하단 점수 표시
        scoreLabel = new JLabel("Player 1 (X): 0 | Player 2 (O): 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(scoreLabel, BorderLayout.SOUTH);


    }

    // 보드 패널 클래스
    private class BoardPanel extends JPanel {
        public BoardPanel() {
            setPreferredSize(new Dimension(400, 400));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // 클릭한 셀 계산
                    int cellWidth = getWidth() / 3;
                    int cellHeight = getHeight() / 3;
                    int col = e.getX() / cellWidth;
                    int row = e.getY() / cellHeight;

                    // 빈 칸 클릭 시만 동작
                    if (gameLogic.makeMove(row, col)) {
                        repaint(); // 보드 다시 그리기
                        if (gameLogic.checkWin(row, col)) {
                            // 승리 시 점수 업데이트 및 결과 표시
                            String winner = gameLogic.updateScoreAndReturnWinner();
                            JOptionPane.showMessageDialog(BoardPage.this, winner + " wins!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
                            updateScoreDisplay();
                            resetGame();
                        } else if (gameLogic.isBoardFull()) {
                            // 무승부 처리
                            JOptionPane.showMessageDialog(BoardPage.this, "It's a draw!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
                            resetGame();
                        } else {
                            // 턴 교대
                            gameLogic.switchPlayer();
                            statusLabel.setText("Player " + gameLogic.getCurrentPlayer() + "'s Turn");
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int cellWidth = getWidth() / 3;
            int cellHeight = getHeight() / 3;

            // 보드 그리기
            g2.setStroke(new BasicStroke(5));
            g2.setColor(Color.BLACK);
            for (int i = 1; i < 3; i++) {
                g2.drawLine(0, i * cellHeight, getWidth(), i * cellHeight);
                g2.drawLine(i * cellWidth, 0, i * cellWidth, getHeight());
            }

            // 플레이어 마크 (X, O) 그리기
            char[][] board = gameLogic.getBoard();
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (board[row][col] == 'X') {
                        g2.setColor(Color.RED);
                        int padding = 20;
                        g2.drawLine(col * cellWidth + padding, row * cellHeight + padding,
                                (col + 1) * cellWidth - padding, (row + 1) * cellHeight - padding);
                        g2.drawLine((col + 1) * cellWidth - padding, row * cellHeight + padding,
                                col * cellWidth + padding, (row + 1) * cellHeight - padding);
                    } else if (board[row][col] == 'O') {
                        g2.setColor(Color.BLUE);
                        int padding = 20;
                        g2.drawOval(col * cellWidth + padding, row * cellHeight + padding,
                                cellWidth - 2 * padding, cellHeight - 2 * padding);
                    }
                }
            }
        }
    }

    // 점수 및 상태 표시 업데이트
    private void updateScoreDisplay() {
        scoreLabel.setText("Player 1 (X): " + gameLogic.getPlayer1Score() + " | Player 2 (O): " + gameLogic.getPlayer2Score());
    }

    // 게임 초기화
    private void resetGame() {
        gameLogic.resetBoard();
        statusLabel.setText("Player X's Turn");
        boardPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BoardPage game = new BoardPage();
            game.setVisible(true);
        });
    }
}
