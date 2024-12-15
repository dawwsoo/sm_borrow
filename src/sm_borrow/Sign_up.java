package sm_borrow;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Sign_up {
    private Connection connection;
    private Scanner scanner;

    public Sign_up(Connection connection) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
    }

    public boolean signUp() {
        try {
            System.out.println("===== ȸ������ =====");
            
            // �̸��� �Է� �� ����
            String username;
            while (true) {
                System.out.print("���� �̸����� �Է��ϼ��� (��: example@sookmyung.ac.kr): ");
                username = scanner.nextLine();
                
                // ���� �̸��� ���� ����
                if (!isValidSookmyungEmail(username)) {
                    System.out.println("�߸��� �̸��� �����Դϴ�. ���� �̸����� �Է����ּ���.");
                    continue;
                }
                
                // �̸��� �ߺ� Ȯ��
                if (isUsernameTaken(username)) {
                    System.out.println("�̹� �����ϴ� �̸����Դϴ�.");
                    continue;
                }
                
                break;
            }

            // ��й�ȣ �Է�
            String password;
            while (true) {
                System.out.print("��й�ȣ�� �Է��ϼ���: ");
                password = scanner.nextLine();
                System.out.print("��й�ȣ�� �ٽ� �Է��ϼ���: ");
                String confirmPassword = scanner.nextLine();
                
                if (password.equals(confirmPassword)) {
                    break;
                } else {
                    System.out.println("��й�ȣ�� ��ġ���� �ʽ��ϴ�. �ٽ� �Է����ּ���.");
                }
            }

            // �����ͺ��̽��� ����� �߰�
            String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password); // ���� � �� ��й�ȣ�� ��ȣȭ�ؾ� ��
                pstmt.executeUpdate();
            }

            System.out.println("ȸ�������� �Ϸ�Ǿ����ϴ�.");
            return true;

        } catch (SQLException e) {
            System.out.println("ȸ������ �� ������ �߻��߽��ϴ�: " + e.getMessage());
            return false;
        }
    }

    // ���� �̸��� ���� ����
    private boolean isValidSookmyungEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@sookmyung\\.ac\\.kr$", email);
    }

    // �̸��� �ߺ� Ȯ��
    private boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}