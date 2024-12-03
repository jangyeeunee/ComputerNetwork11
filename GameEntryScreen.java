import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 게임 입장 화면 클래스
 * 사용자에게 방 생성 또는 방 참가 옵션 제공
 */
public class GameEntryScreen extends JFrame {

    private PrintWriter out; // 서버로 메세지를 전송하는 객체
    private BufferedReader in; // 서버로부터 메세지를 수신하는 객체
    private Socket socket;
    private JTextField roomIdField; // 방 ID 입력 필드

    public GameEntryScreen(PrintWriter out, BufferedReader in, Socket socket) {

        // 서버 스트림 객체를 클래스 변수에 저장
        this.out = out;
        this.in = in;
        this.socket = socket;

        // 프레임 설정
        setTitle("Tic Tac Toe - Game Entry");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // 종료 시 직접 처리하도록 설정
        setLayout(new BorderLayout());

        // 제목 라벨 설정
        JLabel titleLabel = new JLabel("Welcome to Tic Tac Toe!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 중앙 패널 생성
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // 방 ID 입력 필드 생성
        roomIdField = new JTextField();
        roomIdField.setBorder(BorderFactory.createTitledBorder("Enter Room ID (for Join):"));
        centerPanel.add(roomIdField);

        // 방 생성 버튼 생성
        JButton createButton = new JButton("Create Room");
        centerPanel.add(createButton);

        // 방 참가 버튼 생성
        JButton joinButton = new JButton("Join Room");
        centerPanel.add(joinButton);

        add(centerPanel, BorderLayout.CENTER);

        // 버튼에 대한 이벤트 리스너 추가
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateRoom();
            }
        });

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onJoinRoom();
            }
        });

        // 창 닫을 때 연결 종료 및 클라이언트 종료
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 종료 작업을 별도의 스레드에서 처리
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            out.println("LEAVE");
                            out.flush(); // 메시지가 즉시 전송되도록 보장
        
                            // 서버로의 출력 스트림 닫기
                            out.close();
                            // 서버로부터의 입력 스트림 닫기
                            in.close();
                            // 소켓 연결 종료
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } finally {
                            dispose();  // 창을 닫고
                            System.exit(0);  // 프로그램 종료
                        }
                    }
                }).start();  // 새로운 스레드 시작
            }
        });
        
        // 프레임 설정 마무리
        setSize(400, 300);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setVisible(true);
    }

    /**
     * "방 생성" 버튼 클릭 시 호출되는 메서드
     */
    private void onCreateRoom() {
        out.println("CREATE"); // 서버에 방 생성 요청
        System.out.println("Sent CREATE to server");
        new LoadingScreen(out, in, "Creating room... Please wait.").setVisible(true); // 메시지 추가
        dispose(); // 현재 창 닫기
    }

    /**
     * "방 참가" 버튼 클릭 시 호출되는 메서드
     */
    private void onJoinRoom() {
        String roomId = roomIdField.getText().trim();
        if (!roomId.isEmpty()) {
            out.println("JOIN " + roomId); // 서버에 방 참가 요청
            System.out.println("Sent JOIN " + roomId + " to server");
            new LoadingScreen(out, in, "Joining room " + roomId + "... Please wait.").setVisible(true); // 메시지 추가
            dispose(); // 현재 창 닫기
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid room ID.");
        }
    }

    public static void main(String[] args) {
        try {
            // 서버와 연결 초기화
            Socket socket = new Socket("localhost", 1234);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // GUI 생성
            SwingUtilities.invokeLater(() -> new GameEntryScreen(out, in, socket));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");
        }
    }
}
