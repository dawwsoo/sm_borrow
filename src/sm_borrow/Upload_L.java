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
            System.out.println("===== 물품 등록 (빌려주기) =====");
            
            // 물품 선택
            System.out.println("물품을 선택하세요:");
            System.out.println("1. 충전기");
            System.out.println("2. 담요");
            System.out.println("3. 노트북 충전기");
            System.out.print("선택: ");
            int itemChoice = scanner.nextInt();
            
            String itemName = getItemName(itemChoice);
            
            // 가격 선택
            System.out.println("가격을 선택하세요:");
            System.out.println("1. 1000원");
            System.out.println("2. 2000원");
            System.out.println("3. 3000원");
            System.out.print("선택: ");
            int priceChoice = scanner.nextInt();
            
            int price = getItemPrice(priceChoice);
            int itemID = getItemID(itemName, price);
            // 사용자 ID 조회
            int userId = getUserId(currentUser);

            // 아이템 등록
            String insertItemSql = "INSERT INTO LendItems (item_fk, user_fk, price) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertItemSql)) {
                pstmt.setInt(1, itemID);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, price);
                pstmt.executeUpdate();
            }

            System.out.println("물품이 성공적으로 등록되었습니다.");

        } catch (SQLException e) {
            System.out.println("물품 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String getItemName(int choice) {
        switch (choice) {
            case 1: return "충전기";
            case 2: return "담요";
            case 3: return "노트북 충전기";
            default: return "알 수 없는 물품";
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
        return -1;  // 존재하지 않는 경우 -1 반환
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