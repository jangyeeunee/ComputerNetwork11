import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 게임 입장 화면 클래스
 * 사용자에게 방 생성 또는 방 참가 옵션 제공
 */
public class GameEntryScreen extends JFrame {
    private JTextField roomIdField; // 방 ID 입력 필드

    public GameEntryScreen() {
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
        // 현재는 서버 통신 없이 로딩 화면으로 이동
        System.out.println("Create Room clicked!");
        showLoadingScreen("Creating Room...");
    }

    /**
     * "방 참가" 버튼 클릭 시 호출되는 메서드
     */
    private void onJoinRoom() {
        String roomId = roomIdField.getText().trim();
        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Room ID to join.");
            return;
        }
        // 현재는 서버 통신 없이 로딩 화면으로 이동
        System.out.println("Join Room clicked! Room ID: " + roomId);
        showLoadingScreen("Joining Room...");
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
