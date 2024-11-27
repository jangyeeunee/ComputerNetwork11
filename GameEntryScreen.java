import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * 게임 입장 화면 클래스
 * 사용자에게 방 생성 또는 방 참가 옵션 제공
 */
public class GameEntryScreen extends JFrame {
    private PrintWriter out; // 서버로 메세지를 전송하는 객체
    private BufferedReader in; // 서버로부터 메세지를 수신하는 객체

    private JTextField roomIdField; // 방 ID 입력 필드

    public GameEntryScreen(PrintWriter out, BufferedReader in) {

        // 서버 스트림 객체를 클래스 변수에 저장
        this.out = out;
        this.in = in;

        // 프레임 설정
        setTitle("Tic Tac Toe - Game Entry");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        new LoadingScreen(out,in); // 로딩 화면으로 이동
        dispose(); // 현재 창 닫기
    }

    /**
     * "방 참가" 버튼 클릭 시 호출되는 메서드
     */
    private void onJoinRoom() {
        String roomId = roomIdField.getText().trim();
        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Room ID.");
            return;
        }
        out.println("JOIN " + roomId); // 서버에 방 참가 요청
        new LoadingScreen(out,in); // 로딩 화면으로 이동
        dispose(); // 현재 창 닫기
    }

    /**
     * 로딩 화면을 표시하는 메서드
     * @param message 로딩 화면에 표시할 메시지
     */
    private void showLoadingScreen(String message) {
        new LoadingScreen(message);
        this.dispose(); // 입장 화면 닫기
    }

    public static void main(String[] args) {
        // 이벤트 디스패치 스레드에서 GUI 생성
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameEntryScreen();
            }
        });
    }
}