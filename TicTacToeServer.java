import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TicTacToeServer {
    private static Map<String, RoomThread> rooms = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started. Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // 새로운 클라이언트 연결 시 핸들러 스레드 생성
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 클라이언트와의 통신을 처리하는 핸들러 클래스
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private RoomThread joinedRoom;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // 클라이언트와 입출력 스트림 연결
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String input;
                while ((input = in.readLine()) != null) {
                    // 명령과 인자를 분리
                    String[] parts = input.split(" ", 2);
                    String command = parts[0];
                    String argument = parts.length > 1 ? parts[1] : "";

                    switch (command) {
                        case "CREATE":
                            createRoom(argument);
                            break;
                        case "JOIN":
                            joinRoom(argument);
                            break;
                        case "MOVE":
                            handleMove(argument);
                            break;
                        default:
                            out.println("ERROR Unknown command.");
                            break;
                    }
                }
            } catch (IOException e) {
                // 클라이언트가 연결을 끊었을 때 처리
                if (joinedRoom != null) {
                    joinedRoom.removeClient(out);
                }
                System.out.println("Client disconnected");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private static int roomCounter = 1; // 방 번호를 추적하는 정적 변수

        // 방을 생성하는 메서드
        private void createRoom(String roomName) {
            String roomId = String.valueOf(roomCounter++);
            RoomThread room = new RoomThread(roomId);
            rooms.put(roomId, room);

            // 방 생성 디버깅 메시지 출력
            System.out.println("Room created with ID: " + roomId);

            // 방 스레드 실행
            new Thread(room).start();

            out.println("ROOM_CREATED " + roomId);

            // 방 생성 후 자동으로 참가
            joinRoom(roomId);
        }

        // 방에 참가하는 메서드
        private void joinRoom(String roomId) {
            RoomThread room = rooms.get(roomId);
            if (room != null) {
                if (room.addClient(socket, out)) {
                    joinedRoom = room;
                    out.println("JOINED " + roomId);
                    System.out.println("Client joined room: " + roomId);
                } else {
                    out.println("ERROR Room is full or game in progress.");
                }
            } else {
                out.println("ERROR Room not found.");
            }
        }

        // 클라이언트의 MOVE 명령을 처리하는 메서드
        private void handleMove(String move) {
            if (joinedRoom != null) {
                joinedRoom.processMove(out, move);
            } else {
                out.println("ERROR You are not in a room.");
            }
        }
    }

    // 방의 상태를 관리하는 클래스
    static class RoomThread implements Runnable {
        private String roomId; // 방 ID
        private List<PrintWriter> clients = new ArrayList<>(); // 방에 연결된 클라이언트 목록
        private GameLogic gameLogic = new GameLogic(); // 게임 로직 관리 객체
        private String currentPlayer = "X"; // 초기 플레이어는 X로 설정하도록 수정
        private int player1Score = 0;
        private int player2Score = 0;

        public RoomThread(String roomId) {
            this.roomId = roomId;
        }
        // 플레이어를 전환하는 메서드
        public void switchPlayer() {
            currentPlayer = (currentPlayer.equals("X")) ? "O" : "X";
        }

        // 클라이언트를 방에 추가하는 메서드
        public synchronized boolean addClient(Socket socket, PrintWriter out) {
            if (clients.size() >= 2) {
                System.out.println("Room ID: " + roomId + ", client size: " + clients.size() + " - Room full");
                return false;
            }
            clients.add(out);
            System.out.println("Room ID: " + roomId + ", client added, current size: " + clients.size());
            out.println("JOINED " + roomId);

            if (clients.size() == 2) {
                broadcast("GAME_START X");
                System.out.println("Room ID: " + roomId + " - Game started");
                broadcast("TURN " + currentPlayer);
            }
            return true;
        }

        // 클라이언트를 방에서 제거하는 메서드
        public synchronized void removeClient(PrintWriter out) {
            clients.remove(out);
            if (clients.isEmpty()) {
                System.out.println("Room ID: " + roomId + " - All clients disconnected");
            }
        }
        
        // MOVE 명령을 처리하는 메서드
        public synchronized void processMove(PrintWriter out, String move) {
            String[] parts = move.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            if (!gameLogic.makeMove(row, col)) {
                out.println("ERROR Invalid move.");
                return;
            }

            char currentPlayerChar = gameLogic.getCurrentPlayer();
            broadcast("MOVE " + row + "," + col + " " + currentPlayerChar);

            if (gameLogic.checkWin(row, col)) {
                if(currentPlayerChar == 'X'){
                    player1Score++;
                } else{
                    player2Score++;
                }
                broadcast("RESULT " + currentPlayerChar + " wins! Score: X=" + player1Score + ", O=" + player2Score);
                resetRoom();
            } else if (gameLogic.isBoardFull()) {
                broadcast("RESULT Draw! Score: X=" + player1Score + ", O=" + player2Score);
                resetRoom();
            } else {
                gameLogic.switchPlayer();
                broadcast("TURN " + gameLogic.getCurrentPlayer());
            }
        }

        // 방에 메시지를 전송하는 메서드
        private void broadcast(String message) {
            for (PrintWriter client : clients) {
                client.println(message);
            }
        }

        // 게임을 초기화하는 메서드
        private void resetRoom() {
            gameLogic.resetBoard();
            broadcast("RESET");
        }

        @Override
        public void run() {
            System.out.println("Room " + roomId + " is running...");
        }
    }
}