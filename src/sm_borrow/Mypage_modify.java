package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Mypage_modify {
    private Connection connection;
    private Scanner scanner;
    private String currentUser;

    public Mypage_modify(Connection connection, String currentUser) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.currentUser = currentUser;
    }

    public boolean modifyPassword() {
        try {
            System.out.println("===== ��й�ȣ ���� =====");
            
            // ���� ��й�ȣ Ȯ��
            System.out.print("���� ��й�ȣ�� �Է��ϼ���: ");
            String currentPassword = scanner.nextLine();
            
            if (!verifyCurrentPassword(currentPassword)) {
                System.out.println("���� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
                return false;
            }
            
            // �� ��й�ȣ �Է�
            String newPassword;
            while (true) {
                System.out.print("�� ��й�ȣ�� �Է��ϼ���: ");
                newPassword = scanner.nextLine();
                
                System.out.print("�� ��й�ȣ�� �ٽ� �Է��ϼ���: ");
                String confirmPassword = scanner.nextLine();
                
                if (newPassword.equals(confirmPassword)) {
                    break;
                } else {
                    System.out.println("�� ��й�ȣ�� ��ġ���� �ʽ��ϴ�. �ٽ� �Է����ּ���.");
                }
            }
            
            // ��й�ȣ ������Ʈ
            updatePassword(newPassword);
            
            System.out.println("��й�ȣ�� ���������� ����Ǿ����ϴ�.");
            return true;
            
        } catch (SQLException e) {
            System.out.println("��й�ȣ ���� �� ������ �߻��߽��ϴ�: " + e.getMessage());
            return false;
        }
    }

    private boolean verifyCurrentPassword(String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, currentUser);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private void updatePassword(String newPassword) throws SQLException {
        String sql = "UPDATE Users SET password = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, currentUser);
            pstmt.executeUpdate();
        }
    }
}