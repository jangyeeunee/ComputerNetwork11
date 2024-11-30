public class GameLogic {
    private char[][] board;       // 3x3 틱택토 보드
    private char currentPlayer;   // 현재 플레이어 (X 또는 O)

    public GameLogic() {
        board = new char[3][3];
        currentPlayer = 'X'; // 초기 플레이어를 X로 설정
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public char[][] getBoard() {
        return board;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public void resetBoard() {
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '\0';  // 각 셀을 빈 값으로 초기화
            }
        }
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
    public void setCurrentPlayer(char player) {
        this.currentPlayer = player;
    }
    
}