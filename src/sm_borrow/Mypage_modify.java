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
            System.out.println("===== 비밀번호 수정 =====");
            
            // 현재 비밀번호 확인
            System.out.print("현재 비밀번호를 입력하세요: ");
            String currentPassword = scanner.nextLine();
            
            if (!verifyCurrentPassword(currentPassword)) {
                System.out.println("현재 비밀번호가 일치하지 않습니다.");
                return false;
            }
            
            // 새 비밀번호 입력
            String newPassword;
            while (true) {
                System.out.print("새 비밀번호를 입력하세요: ");
                newPassword = scanner.nextLine();
                
                System.out.print("새 비밀번호를 다시 입력하세요: ");
                String confirmPassword = scanner.nextLine();
                
                if (newPassword.equals(confirmPassword)) {
                    break;
                } else {
                    System.out.println("새 비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
                }
            }
            
            // 비밀번호 업데이트
            updatePassword(newPassword);
            
            System.out.println("비밀번호가 성공적으로 변경되었습니다.");
            return true;
            
        } catch (SQLException e) {
            System.out.println("비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
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