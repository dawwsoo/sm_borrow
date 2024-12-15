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
            // 사용자 ID 조회
            int userId = getUserId(currentUser);

            // 내가 빌린 물품 목록 조회
            String itemsSql = "SELECT bi.borrowed_item_pk, i.item_name, bi.message " +
                              "FROM BorrowedItems bi " +
                              "JOIN Items i ON bi.item_fk = i.item_pk " +
                              "WHERE bi.user_fk = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(itemsSql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== 내가 올린 대여 요청 목록 =====");
                    
                    boolean hasItems = false;
                    while (rs.next()) {
                        hasItems = true;
                        int itemPk = rs.getInt("borrowed_item_pk");
                        String itemName = rs.getString("item_name");
                        String message = rs.getString("message");
                        
                        System.out.println("물품 ID: " + itemPk);
                        System.out.println("물품명: " + itemName);
                        System.out.println("메시지: " + message);
                        System.out.println("-------------------");
                    }
                    
                    if (!hasItems) {
                        System.out.println("대여 요청한 물품이 없습니다.");
                        return;
                    }
                    
                    // 삭제 옵션
                    System.out.println("물품을 삭제하시겠습니까? (Y/N)");
                    String deleteChoice = scanner.nextLine().toUpperCase();
                    
                    if (deleteChoice.equals("Y")) {
                        System.out.print("삭제할 물품의 ID를 입력하세요: ");
                        int deleteItemId = scanner.nextInt();
                        deleteItem(deleteItemId, userId);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("물품 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void deleteItem(int itemId, int userId) throws SQLException {
        String deleteSql = "DELETE FROM BorrowedItems WHERE borrowed_item_pk = ? AND user_fk = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("물품이 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("삭제할 수 없는 물품입니다.");
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
        throw new SQLException("사용자를 찾을 수 없습니다.");
    }
}