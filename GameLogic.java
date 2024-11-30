public class GameLogic {
    private char[][] board;       // 3x3 틱택토 보드
    private char currentPlayer;   // 현재 플레이어 (X 또는 O)
    private int player1Score;     // Player 1 (X) 점수
    private int player2Score;     // Player 2 (O) 점수

    public GameLogic() {
        board = new char[3][3];
        currentPlayer = 'X';
        player1Score = 0;
        player2Score = 0;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public char[][] getBoard() {
        return board;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public void resetBoard() {
        board = new char[3][3];
        currentPlayer = 'X'; // 초기 플레이어
    }

    public boolean makeMove(int row, int col) {
        if (board[row][col] == '\0') {
            board[row][col] = currentPlayer;
            return true;
        }
        return false;
    }

    public boolean checkWin(int row, int col) {
        // 현재 플레이어가 승리했는지 확인 (가로, 세로, 대각선)
        return (board[row][0] == currentPlayer && board[row][1] == currentPlayer && board[row][2] == currentPlayer) ||
                (board[0][col] == currentPlayer && board[1][col] == currentPlayer && board[2][col] == currentPlayer) ||
                (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) ||
                (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer);
    }

    public boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    public void incrementPlayer1Score() {
        player1Score++;
    }

    public void incrementPlayer2Score() {
        player2Score++;
    }

    public String updateScoreAndReturnWinner() {
        if (currentPlayer == 'X') {
            player1Score++;
            return "Player 1 (X)";
        } else {
            player2Score++;
            return "Player 2 (O)";
        }
    }
}