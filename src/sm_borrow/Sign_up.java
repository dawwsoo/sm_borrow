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
            System.out.println("===== 회원가입 =====");
            
            // 이메일 입력 및 검증
            String username;
            while (true) {
                System.out.print("숙명 이메일을 입력하세요 (예: example@sookmyung.ac.kr): ");
                username = scanner.nextLine();
                
                // 숙명 이메일 형식 검증
                if (!isValidSookmyungEmail(username)) {
                    System.out.println("잘못된 이메일 형식입니다. 숙명 이메일을 입력해주세요.");
                    continue;
                }
                
                // 이메일 중복 확인
                if (isUsernameTaken(username)) {
                    System.out.println("이미 존재하는 이메일입니다.");
                    continue;
                }
                
                break;
            }

            // 비밀번호 입력
            String password;
            while (true) {
                System.out.print("비밀번호를 입력하세요: ");
                password = scanner.nextLine();
                System.out.print("비밀번호를 다시 입력하세요: ");
                String confirmPassword = scanner.nextLine();
                
                if (password.equals(confirmPassword)) {
                    break;
                } else {
                    System.out.println("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
                }
            }

            // 데이터베이스에 사용자 추가
            String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password); // 실제 운영 시 비밀번호는 암호화해야 함
                pstmt.executeUpdate();
            }

            System.out.println("회원가입이 완료되었습니다.");
            return true;

        } catch (SQLException e) {
            System.out.println("회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return false;
        }
    }

    // 숙명 이메일 형식 검증
    private boolean isValidSookmyungEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@sookmyung\\.ac\\.kr$", email);
    }

    // 이메일 중복 확인
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