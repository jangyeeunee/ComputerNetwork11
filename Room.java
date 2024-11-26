import java.io.PrintWriter;
import java.util.Arrays;

public class Room {
    private PrintWriter[] players = new PrintWriter[2];
    private String[] board = new String[9];
    private int currentPlayerIndex = 0;

    public Room() {
        Arrays.fill(board, null);
    }

    public synchronized void addPlayer(PrintWriter out) {
        if (players[0] == null) {
            players[0] = out;
        } else {
            players[1] = out;
        }
    }

    public synchronized boolean isFull() {
        return players[0] != null && players[1] != null;
    }

    public synchronized void waitForPlayers() {
        while (!isFull()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void removePlayer(PrintWriter out) {
        if (players[0] == out) {
            players[0] = null;
        } else if (players[1] == out) {
            players[1] = null;
        }
    }

    public synchronized String getSymbol(PrintWriter out) {
        return (players[0] == out) ? "X" : "O";
    }

    public synchronized boolean makeMove(String symbol, int position) {
        if (position < 0 || position >= 9 || board[position] != null) {
            return false;
        }
        board[position] = symbol;
        currentPlayerIndex = (currentPlayerIndex + 1) % 2;
        return true;
    }
    

    public synchronized void broadcast(String message) {
        for (PrintWriter player : players) {
            if (player != null) {
                player.println(message);
            }
        }
    }

    public synchronized void broadcastBoard() {
        StringBuilder boardState = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            boardState.append(board[i] == null ? "-" : board[i]);
            if (i % 3 == 2) {
                boardState.append("\n");
            } else {
                boardState.append(" ");
            }
        }
        broadcast(boardState.toString());
    }

    public synchronized void reset() {
        Arrays.fill(board, null);
        currentPlayerIndex = 0;
    }
}