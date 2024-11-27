import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TicTacToeServer {
    private static Map<String, RoomThread> rooms = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started. Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String input;
                while ((input = in.readLine()) != null) {
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
                if (joinedRoom != null) {
                    joinedRoom.removeClient(out);
                }
                System.out.println("Client disconnected");

                //e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private static int roomCounter = 1;  // 방 번호를 추적하는 정적 변수

        private void createRoom(String roomName) {
            String roomId = String.valueOf(roomCounter++);
            RoomThread room = new RoomThread(roomId);
            rooms.put(roomId, room);

            // 디버깅 출력
            System.out.println("Room created with ID: " + roomId);

            new Thread(room).start();

            out.println("ROOM_CREATED " + roomId);
            joinRoom(roomId); // 방 생성 후 자동 참가
        }


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


        private void handleMove(String move) {
            if (joinedRoom != null) {
                joinedRoom.processMove(out, move);
            } else {
                out.println("ERROR You are not in a room.");
            }
        }
    }

    static class RoomThread implements Runnable {
        private String roomId;
        private List<Socket> clients = new ArrayList<>();
        private List<PrintWriter> outputs = new ArrayList<>();
        private GameLogic gameLogic = new GameLogic();  // GameLogic 객체로 교체
        private int currentPlayer = 0;

        public RoomThread(String roomId) {
            this.roomId = roomId;
        }

        public synchronized boolean addClient(Socket clientSocket, PrintWriter out) {
            if (clients.size() < 2) {
                clients.add(clientSocket);
                outputs.add(out);
                out.println("GAME_START " + (clients.size() == 1 ? "X" : "O"));
                if (clients.size() == 2) {
                    broadcast("TURN X");
                }
                return true;
            }
            return false;
        }

        public synchronized void removeClient(PrintWriter out) {
            int index = outputs.indexOf(out);
            if (index != -1) {
                clients.remove(index);
                outputs.remove(index);
                broadcast("RESULT Opponent left the game.");
            }
        }

        public synchronized void processMove(PrintWriter out, String move) {
            String[] parts = move.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            if (!gameLogic.makeMove(x, y)) {
                out.println("ERROR Invalid move. Cell already occupied.");
                return;
            }

            String player = currentPlayer == 0 ? "X" : "O";
            broadcast("MOVE " + x + "," + y + " " + player);

            if (gameLogic.checkWin(x, y)) {
                broadcast("RESULT " + player + " wins!");
                resetGame();
                return;
            }

            if (gameLogic.isBoardFull()) {
                broadcast("RESULT Draw!");
                resetGame();
                return;
            }

            currentPlayer = 1 - currentPlayer;
            broadcast("TURN " + (currentPlayer == 0 ? "X" : "O"));
        }

        private void broadcast(String message) {
            for (PrintWriter out : outputs) {
                out.println(message);
            }
        }

        private void resetGame() {
            gameLogic.resetBoard();
            currentPlayer = 0;
            broadcast("RESET");
        }

        @Override
        public void run() {
            System.out.println("Room " + roomId + " is running...");
        }
    }
}
