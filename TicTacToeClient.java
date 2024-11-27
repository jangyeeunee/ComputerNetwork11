import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class TicTacToeClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel;
    private String currentPlayer = "";
    private String roomId = "";

    public TicTacToeClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            setupGUI();
            listenForServerMessages();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // 창 닫을 때 직접 처리
        frame.setSize(400, 400);

        statusLabel = new JLabel("Connect to a room or create one.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                final int x = i, y = j;
                buttons[i][j].addActionListener(e -> makeMove(x, y));
                panel.add(buttons[i][j]);
            }
        }

        frame.add(panel, BorderLayout.CENTER);

        // 창 닫기 이벤트 처리
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeConnection();
                System.exit(0); // 프로그램 종료
            }
        });

        JPanel controlPanel = new JPanel();
        JButton createButton = new JButton("Create Room");
        JButton joinButton = new JButton("Join Room");
        createButton.addActionListener(e -> createRoom());
        joinButton.addActionListener(e -> joinRoom());
        controlPanel.add(createButton);
        controlPanel.add(joinButton);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.println("DISCONNECT"); // 서버에 종료 메시지 보내기 (서버에서 처리 필요)
                out.close();
                in.close();
                socket.close();
                System.out.println("Connection closed safely.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createRoom() {
        out.println("CREATE");
    }

    private void joinRoom() {
        String inputRoomId = JOptionPane.showInputDialog(frame, "Enter Room ID:");
        if (inputRoomId != null && !inputRoomId.trim().isEmpty()) {
            out.println("JOIN " + inputRoomId.trim());
        }
    }

    private void makeMove(int x, int y) {
        if (!currentPlayer.equals("X") && !currentPlayer.equals("O")) {
            JOptionPane.showMessageDialog(frame, "Wait for the game to start!");
            return;
        }
        if (!buttons[x][y].getText().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid move! Cell already occupied.");
            return;
        }
        out.println("MOVE " + x + "," + y);
    }

    private void listenForServerMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    processServerMessage(message);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Disconnected from server.");
                e.printStackTrace();
            }
        }).start();
    }

    private void processServerMessage(String message) {
        String[] parts = message.split(" ", 2);
        String command = parts[0];

        switch (command) {
            case "ROOM_CREATED":
                roomId = parts[1];
                statusLabel.setText("Room created. Room ID: " + roomId);
                break;

            case "JOINED":
                roomId = parts[1];
                statusLabel.setText("Joined room: " + roomId);
                break;

            case "GAME_START":
                currentPlayer = parts[1];
                statusLabel.setText("Game started! You are: " + currentPlayer);
                break;

            case "MOVE":
                String[] moveParts = parts[1].split(" ");
                String[] coordinates = moveParts[0].split(",");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                String player = moveParts[1];
                buttons[x][y].setText(player);
                break;

            case "TURN":
                currentPlayer = parts[1];
                statusLabel.setText("Your turn: " + currentPlayer);
                break;

            case "RESULT":
                statusLabel.setText(parts[1]);
                JOptionPane.showMessageDialog(frame, parts[1]);
                disableBoard();
                break;

            case "RESET":
                resetBoard();
                statusLabel.setText("Game reset. Waiting for moves...");
                break;

            case "ERROR":
                JOptionPane.showMessageDialog(frame, parts[1]);
                break;

            default:
                System.out.println("Unknown message from server: " + message);
                break;
        }
    }

    private void disableBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        String serverAddress = JOptionPane.showInputDialog("Enter Server Address:");
        if (serverAddress != null && !serverAddress.trim().isEmpty()) {
            new TicTacToeClient(serverAddress, 1234);
        }
    }
}
