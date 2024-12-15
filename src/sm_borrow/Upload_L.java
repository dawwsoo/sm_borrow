package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Upload_L {
    private Connection connection;
    private Scanner scanner;
    private String currentUser;

    public Upload_L(Connection connection, String currentUser) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.currentUser = currentUser;
    }

    public void uploadItem() {
        try {
            System.out.println("===== ��ǰ ��� (�����ֱ�) =====");
            
            // ��ǰ ����
            System.out.println("��ǰ�� �����ϼ���:");
            System.out.println("1. ������");
            System.out.println("2. ���");
            System.out.println("3. ��Ʈ�� ������");
            System.out.print("����: ");
            int itemChoice = scanner.nextInt();
            
            String itemName = getItemName(itemChoice);
            
            // ���� ����
            System.out.println("������ �����ϼ���:");
            System.out.println("1. 1000��");
            System.out.println("2. 2000��");
            System.out.println("3. 3000��");
            System.out.print("����: ");
            int priceChoice = scanner.nextInt();
            
            int price = getItemPrice(priceChoice);
            int itemID = getItemID(itemName, price);
            // ����� ID ��ȸ
            int userId = getUserId(currentUser);

            // ������ ���
            String insertItemSql = "INSERT INTO LendItems (item_fk, user_fk, price) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertItemSql)) {
                pstmt.setInt(1, itemID);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, price);
                pstmt.executeUpdate();
            }

            System.out.println("��ǰ�� ���������� ��ϵǾ����ϴ�.");

        } catch (SQLException e) {
            System.out.println("��ǰ ��� �� ������ �߻��߽��ϴ�: " + e.getMessage());
        }
    }

    private String getItemName(int choice) {
        switch (choice) {
            case 1: return "������";
            case 2: return "���";
            case 3: return "��Ʈ�� ������";
            default: return "�� �� ���� ��ǰ";
        }
    }

    private int getItemPrice(int choice) {
        switch (choice) {
            case 1: return 1000;
            case 2: return 2000;
            case 3: return 3000;
            default: return 0;
        }
    }
    
    private int getItemID(String itemName, int price) throws SQLException {
        String query = "SELECT item_pk FROM Items WHERE item_name = ? AND price = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, itemName);
            pstmt.setInt(2, price);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("item_pk");
                }
            }
        }
        return -1;  // �������� �ʴ� ��� -1 ��ȯ
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