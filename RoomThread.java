import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RoomThread implements Runnable {
    private String roomId; // 방 ID
    private List<PrintWriter> clients = new ArrayList<>(); // 방에 연결된 클라이언트 목록
    private GameLogic gameLogic = new GameLogic(); // 게임 로직 관리 객체

    public RoomThread(String roomId) {
        this.roomId = roomId;
    }

    public synchronized boolean addClient(Socket socket, PrintWriter out) {
        if (clients.size() >= 2) {
            return false; // 방이 가득 찼을 경우
        }
        clients.add(out);
        out.println("JOINED " + roomId);

        if (clients.size() == 2) {
            broadcast("GAME_START X"); // 두 명이 모두 참여하면 게임 시작
        }
        return true;
    }

    public synchronized void removeClient(PrintWriter out) {
        clients.remove(out); // 클라이언트를 방에서 제거
        if (clients.isEmpty()) {
            // 모든 클라이언트가 방을 떠난 경우
            broadcast("ROOM_CLOSED");
        }
    }

    public synchronized void processMove(PrintWriter playerOut, String move) {
        String[] parts = move.split(",");
        if (parts.length != 2) {
            playerOut.println("ERROR Invalid move format.");
            return;
        }

        int x, y;
        try {
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            playerOut.println("ERROR Invalid move coordinates.");
            return;
        }

        if (!gameLogic.makeMove(x, y)) {
            playerOut.println("ERROR Invalid move.");
            return;
        }

        broadcast("MOVE " + x + "," + y + " " + gameLogic.getCurrentPlayer());

        if (gameLogic.checkWin(x, y)) {
            broadcast("RESULT " + gameLogic.getCurrentPlayer() + " wins!");
            resetRoom();
        } else if (gameLogic.isBoardFull()) {
            broadcast("RESULT Draw!");
            resetRoom();
        } else {
            gameLogic.switchPlayer();
            broadcast("TURN " + gameLogic.getCurrentPlayer());
        }
    }

    private void broadcast(String message) {
        for (PrintWriter client : clients) {
            client.println(message);
        }
    }

    private void resetRoom() {
        gameLogic.resetBoard();
        broadcast("RESET");
    }

    @Override
    public void run() {
        // 방 스레드가 지속적으로 동작할 필요가 없으므로 추가 동작 없음
    }
}
