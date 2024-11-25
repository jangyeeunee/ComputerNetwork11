import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BoardPage extends JFrame {
    private char[][] board = new char[3][3]; // 3x3 틱택토 보드
    private char currentPlayer = 'X';       // 현재 플레이어 (X 또는 O)
    private JLabel statusLabel;             // 게임 상태 표시
    private JLabel scoreLabel;              // 점수 표시
    private int player1Score = 0;           // Player 1 (X) 점수
    private int player2Score = 0;           // Player 2 (O) 점수
    private BoardPanel boardPanel;          // 보드 패널

    public BoardPage() {
        setTitle("Tic Tac Toe");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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
        restartButton.setPreferredSize(new Dimension(80, 30)); // 작게 설정
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
                    if (board[row][col] == '\0') {
                        board[row][col] = currentPlayer; // 현재 플레이어의 마크
                        repaint(); // 보드 다시 그리기

                        // 승리 조건 체크
                        if (checkWin(row, col)) {
                            updateScoreAndShowResult(currentPlayer); // 점수 업데이트 및 결과 표시
                            disableBoard(); // 게임 종료
                        } else if (isBoardFull()) {
                            JOptionPane.showMessageDialog(BoardPage.this, "It's a draw!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
                            resetGame();
                        } else {
                            // 다음 턴으로 변경
                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                            statusLabel.setText("Player " + currentPlayer + "'s Turn");
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
                g2.drawLine(0, i * cellHeight, getWidth(), i * cellHeight); // 가로선
                g2.drawLine(i * cellWidth, 0, i * cellWidth, getHeight()); // 세로선
            }

            // 마크 그리기 (X 또는 O)
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

    private boolean checkWin(int row, int col) {
        // 현재 플레이어가 승리했는지 확인 (가로, 세로, 대각선)
        return (board[row][0] == currentPlayer && board[row][1] == currentPlayer && board[row][2] == currentPlayer) ||
                (board[0][col] == currentPlayer && board[1][col] == currentPlayer && board[2][col] == currentPlayer) ||
                (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) ||
                (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer);
    }

    private boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private void disableBoard() {
        // 보드 비활성화
        for (MouseListener listener : boardPanel.getMouseListeners()) {
            boardPanel.removeMouseListener(listener);
        }
    }

    private void resetGame() {
        // 보드와 상태 초기화
        board = new char[3][3];
        currentPlayer = 'X';
        statusLabel.setText("Player X's Turn");
        scoreLabel.setText("Player 1 (X): " + player1Score + " | Player 2 (O): " + player2Score);
        boardPanel.repaint();

        // 기존 마우스 리스너 제거
        for (MouseListener listener : boardPanel.getMouseListeners()) {
            boardPanel.removeMouseListener(listener);
        }

        // 새로운 마우스 리스너 추가
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cellWidth = boardPanel.getWidth() / 3;
                int cellHeight = boardPanel.getHeight() / 3;
                int col = e.getX() / cellWidth;
                int row = e.getY() / cellHeight;

                if (board[row][col] == '\0') {
                    board[row][col] = currentPlayer;
                    boardPanel.repaint();

                    if (checkWin(row, col)) {
                        updateScoreAndShowResult(currentPlayer);
                        disableBoard();
                    } else if (isBoardFull()) {
                        JOptionPane.showMessageDialog(BoardPage.this, "It's a draw!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
                        resetGame();
                    } else {
                        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                        statusLabel.setText("Player " + currentPlayer + "'s Turn");
                    }
                }
            }
        });
    }

    private void updateScoreAndShowResult(char winner) {
        if (winner == 'X') {
            player1Score++;
            JOptionPane.showMessageDialog(this, "Player 1 (X) wins!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            player2Score++;
            JOptionPane.showMessageDialog(this, "Player 2 (O) wins!", "Game Result", JOptionPane.INFORMATION_MESSAGE);
        }
        resetGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BoardPage().setVisible(true);
        });
    }
}
