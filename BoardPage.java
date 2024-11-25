import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPage extends JFrame {
    private char[][] board = new char[3][3]; // 3x3 틱택토 보드
    private char currentPlayer = 'X';       // 현재 플레이어 (X 또는 O)
    private JLabel statusLabel;             // 게임 상태 표시

    public BoardPage() {
        setTitle("Tic Tac Toe");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상태 표시창
        statusLabel = new JLabel("X's Turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(statusLabel, BorderLayout.NORTH);

        // 보드 패널
        BoardPanel boardPanel = new BoardPanel();
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
                            statusLabel.setText(currentPlayer + " WIN!");
                            disableBoard(); // 게임 종료
                        } else if (isBoardFull()) {
                            statusLabel.setText("DRAW!");
                            disableBoard(); // 무승부
                        } else {
                            // 다음 턴으로 변경
                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                            statusLabel.setText(currentPlayer + " 's Turn");
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
        if (getMouseListeners().length > 0) {
            removeMouseListener(getMouseListeners()[0]);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BoardPage().setVisible(true);
        });
    }
}
