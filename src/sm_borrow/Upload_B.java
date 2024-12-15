package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Upload_B {
    private Connection connection;
    private Scanner scanner;
    private String currentUser;

    public Upload_B(Connection connection, String currentUser) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.currentUser = currentUser;
    }

    public void uploadBorrowRequest() {
        try {
            System.out.println("===== ��ǰ �뿩 ��û =====");
            
            // ��ǰ ����
            System.out.println("��ǰ�� �����ϼ���:");
            System.out.println("1. ������");
            System.out.println("2. ���");
            System.out.println("3. ��Ʈ�� ������");
            System.out.print("����: ");
            int itemChoice = scanner.nextInt();

            String itemName = getItemName(itemChoice);

            // ���Ѱ� ����
            System.out.println("���Ѱ��� �����ϼ���:");
            System.out.println("1. 1000��");
            System.out.println("2. 2000��");
            System.out.println("3. 3000��");
            System.out.print("����: ");
            int priceChoice = scanner.nextInt();
            scanner.nextLine(); 

            int maxPrice = getItemPrice(priceChoice);
            int itemID = getItemID(itemName, maxPrice);
            int userId = getUserId(currentUser);

            // �뿩 ��û ���
            registerBorrowRequest(itemID, maxPrice, userId);

            System.out.println("�뿩 ��û�� ���������� ��ϵǾ����ϴ�.");
        } catch (SQLException e) {
            System.out.println("�뿩 ��û �� ������ �߻��߽��ϴ�: " + e.getMessage());
        }
    }

    private void registerBorrowRequest(int itemId, int maxPrice, int borrowerId) throws SQLException {
        String insertBorrowRequestSql = "INSERT INTO BorrowedItems (item_fk, user_fk, max_price, message) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertBorrowRequestSql)) {
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, borrowerId);
            pstmt.setInt(3, maxPrice);
            pstmt.setString(4, "���ο� �뿩 ��û");
            pstmt.executeUpdate();

            System.out.println("�뿩 ��û�� ��ϵǾ����ϴ�.");
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
                if (rs.next()) return rs.getInt("user_pk");
            }
        }
        throw new SQLException("����ڸ� ã�� �� �����ϴ�.");
    }
}
