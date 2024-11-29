import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class BoardPage extends JFrame {
    private GameLogic gameLogic = new GameLogic(); // 게임 로직 객체
    private PrintWriter out;
    private BufferedReader in;

    private JLabel statusLabel; // 현재 상태 표시 라벨
    private JLabel scoreLabel;  // 점수 표시 라벨
    private JPanel boardPanel;  // 게임 보드 패널

    public BoardPage(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;

        setTitle("Tic Tac Toe");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 상태 표시
        statusLabel = new JLabel("Player X's Turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(statusLabel, BorderLayout.NORTH);

        // 중앙 보드 패널
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard((Graphics2D) g);
            }
        };

        boardPanel.setPreferredSize(new Dimension(400, 400));
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        add(boardPanel, BorderLayout.CENTER);

        // 하단 점수 표시
        scoreLabel = new JLabel("Player 1 (X): 0 | Player 2 (O): 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(scoreLabel, BorderLayout.SOUTH);

        startListeningForMoves(); // 서버 메시지 처리
    }

    private void handleMouseClick(MouseEvent e) {
        int cellWidth = boardPanel.getWidth() / 3;
        int cellHeight = boardPanel.getHeight() / 3;
        int col = e.getX() / cellWidth;
        int row = e.getY() / cellHeight;

        out.println("MOVE " + row + "," + col); // 서버로 MOVE 전송
    }

    private void drawBoard(Graphics2D g2) {
        int cellWidth = boardPanel.getWidth() / 3;
        int cellHeight = boardPanel.getHeight() / 3;

        // 보드 그리기
        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.BLACK);
        for (int i = 1; i < 3; i++) {
            g2.drawLine(0, i * cellHeight, boardPanel.getWidth(), i * cellHeight);
            g2.drawLine(i * cellWidth, 0, i * cellWidth, boardPanel.getHeight());
        }

        // X와 O 표시
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

    private void startListeningForMoves() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String finalMessage = message; // 람다 내에서 사용할 임시 변수 생성

                    // MOVE : 특정 위치에 플레이어의 기호(X 또는 O)를 그림
                    // 메세지 형식 : "MOVE row,col player"
                    if (finalMessage.startsWith("MOVE")) {
                        String[] parts = finalMessage.split(" ");
                        String[] coords = parts[1].split(",");
                        int row = Integer.parseInt(coords[0]);  // 행 좌표
                        int col = Integer.parseInt(coords[1]);  // 열 좌표
                        char player = parts[2].charAt(0);

                        gameLogic.getBoard()[row][col] = player; // 게임 보드 상태  업데이트
                        SwingUtilities.invokeLater(() -> boardPanel.repaint());
                    }
                    // TURN : 다음 플레이어의 차례를 업데이트
                    // 메세지 형식 : "TURN player"
                    else if (finalMessage.startsWith("TURN")) {
                        String nextPlayer = finalMessage.split(" ")[1];  // 다음 플레이어 정보
                        // UI를 갱신하여 다음 플레이어의 턴 표시
                        SwingUtilities.invokeLater(() ->
                                statusLabel.setText("Player " + nextPlayer + "'s Turn")
                        );
                    }
                    // RESULT : 게임 결과 ( 승리 또는 무승부) 표시
                    // 메세지 형식: "RESULT message"
                    else if (finalMessage.startsWith("RESULT")) {
                        SwingUtilities.invokeLater(() ->

                        {
                            JOptionPane.showMessageDialog(this, finalMessage.substring(7));
                            dispose();
                        }
                        );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start(); // 스레드 시작
    }




    private void updateScoreDisplay() {
        scoreLabel.setText("Player 1 (X): " + gameLogic.getPlayer1Score() + " | Player 2 (O): " + gameLogic.getPlayer2Score());
    }

    private void resetGame() {
        gameLogic.resetBoard();
        statusLabel.setText("Player X's Turn");
        boardPanel.repaint();
    }
}
