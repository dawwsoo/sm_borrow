package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Mypage_list {
    private Connection connection;
    private Scanner scanner;
    private String currentUser;

    public Mypage_list(Connection connection, String currentUser) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.currentUser = currentUser;
    }

    public void showBorrowedItems() {
        try {
            // ����� ID ��ȸ
            int userId = getUserId(currentUser);

            // ���� ���� ��ǰ ��� ��ȸ
            String itemsSql = "SELECT bi.borrowed_item_pk, i.item_name, bi.message " +
                              "FROM BorrowedItems bi " +
                              "JOIN Items i ON bi.item_fk = i.item_pk " +
                              "WHERE bi.user_fk = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(itemsSql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== ���� �ø� �뿩 ��û ��� =====");
                    
                    boolean hasItems = false;
                    while (rs.next()) {
                        hasItems = true;
                        int itemPk = rs.getInt("borrowed_item_pk");
                        String itemName = rs.getString("item_name");
                        String message = rs.getString("message");
                        
                        System.out.println("��ǰ ID: " + itemPk);
                        System.out.println("��ǰ��: " + itemName);
                        System.out.println("�޽���: " + message);
                        System.out.println("-------------------");
                    }
                    
                    if (!hasItems) {
                        System.out.println("�뿩 ��û�� ��ǰ�� �����ϴ�.");
                        return;
                    }
                    
                    // ���� �ɼ�
                    System.out.println("��ǰ�� �����Ͻðڽ��ϱ�? (Y/N)");
                    String deleteChoice = scanner.nextLine().toUpperCase();
                    
                    if (deleteChoice.equals("Y")) {
                        System.out.print("������ ��ǰ�� ID�� �Է��ϼ���: ");
                        int deleteItemId = scanner.nextInt();
                        deleteItem(deleteItemId, userId);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("��ǰ ��� ��ȸ �� ������ �߻��߽��ϴ�: " + e.getMessage());
        }
    }

    private void deleteItem(int itemId, int userId) throws SQLException {
        String deleteSql = "DELETE FROM BorrowedItems WHERE borrowed_item_pk = ? AND user_fk = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("��ǰ�� ���������� �����Ǿ����ϴ�.");
            } else {
                System.out.println("������ �� ���� ��ǰ�Դϴ�.");
            }
        }
    }

    private int getUserId(String username) throws SQLException {
        String sql = "SELECT user_pk FROM Users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_pk");
                }
            }
        }
        throw new SQLException("����ڸ� ã�� �� �����ϴ�.");
    }
}