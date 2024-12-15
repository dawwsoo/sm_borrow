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
            System.out.println("===== 회원 탈퇴 =====");
            System.out.print("비밀번호를 입력하세요: ");
            String password = scanner.nextLine();

            // 비밀번호 확인
            if (verifyPassword(password)) {
                // 사용자 삭제 (관련된 모든 데이터도 삭제)
                deleteUserData();
                System.out.println("회원 탈퇴가 완료되었습니다.");
                return true;
            } else {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("회원 탈퇴 중 오류가 발생했습니다: " + e.getMessage());
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
        // 트랜잭션 시작
        connection.setAutoCommit(false);
        
        try {
            // 관련된 모든 테이블에서 사용자 데이터 삭제
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

            // 트랜잭션 커밋
            connection.commit();
        } catch (SQLException e) {
            // 롤백
            connection.rollback();
            throw e;
        } finally {
            // 다시 자동 커밋 설정
            connection.setAutoCommit(true);
        }
    }
}