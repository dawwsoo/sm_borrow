package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Sign_in {
    private Connection connection;
    private String currentUser;

    // Constructor
    public Sign_in(Connection connection) {
        try {
            this.connection = Database.connect();  // Database.java���� ���� ����
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("�����ͺ��̽� ���ῡ �����߽��ϴ�.");
        }
    }

    // �α��� �޼���
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
                System.out.println("�α��� ����! ���� �����: " + currentUser);
                return true;
            } else {
                System.out.println("�α��� ����! ���̵� �Ǵ� ��й�ȣ�� �߸��Ǿ����ϴ�.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ���� �α��ε� ����� ��ȯ
    public String getCurrentUser() {
        return currentUser;
    }

    // �α׾ƿ� �޼���
    public void logout() {
        System.out.println(currentUser + "��, �α׾ƿ��Ǿ����ϴ�.");
        currentUser = null;
    }
}
