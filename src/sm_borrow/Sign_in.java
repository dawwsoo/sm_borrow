package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Sign_in {
    private Connection connection;
    private String currentUser;

    // Constructor
    public Sign_in(Connection connection) {
        try {
            this.connection = Database.connect();  // Database.java에서 연결 생성
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("데이터베이스 연결에 실패했습니다.");
        }
    }

    // 로그인 메서드
    public boolean signIn() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                currentUser = username;
                System.out.println("로그인 성공! 현재 사용자: " + currentUser);
                return true;
            } else {
                System.out.println("로그인 실패! 아이디 또는 비밀번호이 잘못되었습니다.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 현재 로그인된 사용자 반환
    public String getCurrentUser() {
        return currentUser;
    }

    // 로그아웃 메서드
    public void logout() {
        System.out.println(currentUser + "님, 로그아웃되었습니다.");
        currentUser = null;
    }
}
