//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RoomThread implements Runnable {
//    private String roomId; // 방 ID
//    private List<PrintWriter> clients = new ArrayList<>(); // 방에 연결된 클라이언트 목록
//    private GameLogic gameLogic = new GameLogic(); // 게임 로직 관리 객체
//
//    public RoomThread(String roomId) {
//        this.roomId = roomId;
//    }
//
//    public synchronized boolean addClient(Socket socket, PrintWriter out) {
//        if (clients.size() >= 2) {
//            System.out.println("Room ID: " + roomId + ", client size: " + clients.size() + " - Room full");
//            return false;
//        }
//        clients.add(out);
//        System.out.println("Room ID: " + roomId + ", client added, current size: " + clients.size());
//        out.println("JOINED " + roomId);
//
//        if (clients.size() == 2) {
//            broadcast("GAME_START X");
//            System.out.println("Room ID: " + roomId + " - Game started");
//        }
//        return true;
//    }
//
//
//    public synchronized void removeClient(PrintWriter out) {
//        clients.remove(out); // 클라이언트를 방에서 제거
//        if (clients.isEmpty()) {
//            // 모든 클라이언트가 방을 떠난 경우
//            broadcast("ROOM_CLOSED");
//        }
//    }
//
//    public synchronized void processMove(PrintWriter out, String move) {
//        String[] parts = move.split(",");
//        int row = Integer.parseInt(parts[0]);
//        int col = Integer.parseInt(parts[1]);
//
//        if (!gameLogic.makeMove(row, col)) {
//            out.println("ERROR Invalid move.");
//            return;
//        }
//
//        char currentPlayer = gameLogic.getCurrentPlayer();
//        broadcast("MOVE " + row + "," + col + " " + currentPlayer);
//
//        if (gameLogic.checkWin(row, col)) {
//            broadcast("RESULT " + currentPlayer + " wins!");
//            resetRoom();
//        } else if (gameLogic.isBoardFull()) {
//            broadcast("RESULT Draw!");
//            resetRoom();
//        } else {
//            gameLogic.switchPlayer();
//            broadcast("TURN " + gameLogic.getCurrentPlayer());
//        }
//    }
//
//
//    private void broadcast(String message) {
//        for (PrintWriter client : clients) {
//            client.println(message);
//        }
//    }
//
//    private void resetRoom() {
//        gameLogic.resetBoard();
//        broadcast("RESET");
//    }
//
//    @Override
//    public void run() {
//        // 방 스레드가 지속적으로 동작할 필요가 없으므로 추가 동작 없음
//    }
//}
