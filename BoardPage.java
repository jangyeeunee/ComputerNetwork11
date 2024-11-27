import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPage extends JFrame {
    private GameLogic gameLogic = new GameLogic();
    private JLabel statusLabel;

    public BoardPage() {
        setTitle("Tic Tac Toe");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        statusLabel = new JLabel("Player X's Turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, BorderLayout.NORTH);

        add(new BoardPanel(), BorderLayout.CENTER);
    }

    private class BoardPanel extends JPanel {
        public BoardPanel() {
            setPreferredSize(new Dimension(400, 400));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int cellWidth = getWidth() / 3;
                    int cellHeight = getHeight() / 3;
                    int col = e.getX() / cellWidth;
                    int row = e.getY() / cellHeight;

                    if (gameLogic.makeMove(row, col)) {
                        repaint();
                        if (gameLogic.checkWin(row, col)) {
                            JOptionPane.showMessageDialog(BoardPage.this, "Player " + gameLogic.getCurrentPlayer() + " wins!");
                        } else if (gameLogic.isBoardFull()) {
                            JOptionPane.showMessageDialog(BoardPage.this, "It's a draw!");
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

            int cellWidth = getWidth() / 3;
            int cellHeight = getHeight() / 3;

            for (int i = 1; i < 3; i++) {
                g2.drawLine(0, i * cellHeight, getWidth(), i * cellHeight);
                g2.drawLine(i * cellWidth, 0, i * cellWidth, getHeight());
            }

            char[][] board = gameLogic.getBoard();
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (board[row][col] == 'X') {
                        g2.drawLine(col * cellWidth, row * cellHeight,
                                (col + 1) * cellWidth, (row + 1) * cellHeight);
                        g2.drawLine((col + 1) * cellWidth, row * cellHeight,
                                col * cellWidth, (row + 1) * cellHeight);
                    } else if (board[row][col] == 'O') {
                        g2.drawOval(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BoardPage().setVisible(true));
    }
}
