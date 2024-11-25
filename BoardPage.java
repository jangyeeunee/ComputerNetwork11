import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPage extends JFrame {
    private GameLogic gameLogic;            // GameLogic 객체
    private JLabel statusLabel;             // 게임 상태 표시
    private JLabel scoreLabel;              // 점수 표시
    private BoardPanel boardPanel;          // 보드 패널

    public BoardPage() {
        setTitle("Tic Tac Toe");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gameLogic = new GameLogic();

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(800, 50));

        // 상태 표시창
        statusLabel = new JLabel("Player X's Turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(statusLabel, BorderLayout.CENTER);

        // 다시 시작 버튼
        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 14));
        restartButton.setPreferredSize(new Dimension(80, 30));
        restartButton.addActionListener(e -> resetGame());
        topPanel.add(restartButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 점수 표시창
        scoreLabel = new JLabel("Player 1 (X): 0 | Player 2 (O): 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(scoreLabel, BorderLayout.SOUTH);

        // 보드 패널
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);
    }

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
                        repaint();

                        // 승리 조건 체크
                        if (gameLogic.checkWin(row, col)) {
                            gameLogic.updateScore();
                            updateScoreAndShowResult(gameLogic.getCurrentPlayer());
                            disableBoard();
                        } else if (gameLogic.isBoardFull()) {
                            JOptionPane.showMessageDialog(BoardPage.this, "It's a draw!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
                            resetGame();
                        } else {
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

            g2.setStroke(new BasicStroke(5));
            g2.setColor(Color.BLACK);

            // 보드 그리기
            int cellWidth = getWidth() / 3;
            int cellHeight = getHeight() / 3;

            for (int i = 1; i < 3; i++) {
                g2.drawLine(0, i * cellHeight, getWidth(), i * cellHeight);
                g2.drawLine(i * cellWidth, 0, i * cellWidth, getHeight());
            }

            // 마크 그리기
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

    private void disableBoard() {
        boardPanel.removeMouseListener(boardPanel.getMouseListeners()[0]);
    }

    private void resetGame() {
        gameLogic.resetBoard();
        statusLabel.setText("Player X's Turn");
        scoreLabel.setText("Player 1 (X): " + gameLogic.getPlayer1Score() + " | Player 2 (O): " + gameLogic.getPlayer2Score());
        boardPanel.repaint();
    }

    private void updateScoreAndShowResult(char winner) {
        JOptionPane.showMessageDialog(this, "Player " + (winner == 'X' ? "1 (X)" : "2 (O)") + " wins!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
        resetGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BoardPage().setVisible(true));
    }
}
