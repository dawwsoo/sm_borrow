package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Mypage_Cancel_Membership {
    private Connection connection;
    private Scanner scanner;
    private String currentUser;

    public Mypage_Cancel_Membership(Connection connection, String currentUser) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.currentUser = currentUser;
    }

    public boolean cancelMembership() {
        try {
            System.out.println("===== ȸ�� Ż�� =====");
            System.out.print("��й�ȣ�� �Է��ϼ���: ");
            String password = scanner.nextLine();

            // ��й�ȣ Ȯ��
            if (verifyPassword(password)) {
                // ����� ���� (���õ� ��� �����͵� ����)
                deleteUserData();
                System.out.println("ȸ�� Ż�� �Ϸ�Ǿ����ϴ�.");
                return true;
            } else {
                System.out.println("��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("ȸ�� Ż�� �� ������ �߻��߽��ϴ�: " + e.getMessage());
            return false;
        }
    }

    private boolean verifyPassword(String password) throws SQLException {
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

    private void deleteUserData() throws SQLException {
        // Ʈ����� ����
        connection.setAutoCommit(false);
        
        try {
            // ���õ� ��� ���̺��� ����� ������ ����
            String[] deleteTables = {
                "Alerts", "BorrowedItems", "LendItems", 
                "ChatMessages", "ChatRooms", "CompletedItems", 
                "Users"
            };

            for (String table : deleteTables) {
                String deleteSql = "DELETE FROM " + table + " WHERE " + 
                    (table.equals("Users") ? "username" : "user_fk") + " = ?";
                
                try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                    pstmt.setString(1, currentUser);
                    pstmt.executeUpdate();
                }
            }

            // Ʈ����� Ŀ��
            connection.commit();
        } catch (SQLException e) {
            // �ѹ�
            connection.rollback();
            throw e;
        } finally {
            // �ٽ� �ڵ� Ŀ�� ����
            connection.setAutoCommit(true);
        }
    }
}