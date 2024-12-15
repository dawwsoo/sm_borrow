package sm_borrow;
//
//
//import java.sql.*;
//import java.util.Scanner;
//
//public class Alert {
//    private Connection connection;
//    private Scanner scanner;
//    private String currentUser;
//
//    public Alert(Connection connection, String currentUser) {
//        this.connection = connection;
//        this.scanner = new Scanner(System.in);
//        this.currentUser = currentUser;
//    }
//
//    public void showAndManageAlerts() {
//        try {
//            // 사용자 ID 조회
//            int userId = getUserId(currentUser);
//            
//            // 알림 목록 조회
//            String alertsSql = "SELECT a.alert_pk, li.item_fk, i.item_name, u.username AS borrower_name " +
//                               "FROM Alerts a " +
//                               "JOIN LendItems li ON a.user_L_fk = li.user_fk " +
//                               "JOIN Items i ON li.item_fk = i.item_pk " +
//                               "JOIN BorrowedItems bi ON bi.item_fk = i.item_pk " +
//                               "JOIN Users u ON bi.user_fk = u.user_pk " +
//                               "WHERE a.user_L_fk = ? AND a.status = '대기'";
//            
//            try (PreparedStatement pstmt = connection.prepareStatement(alertsSql)) {
//                pstmt.setInt(1, userId);
//                
//                try (ResultSet rs = pstmt.executeQuery()) {
//                    if (!rs.next()) {
//                        System.out.println("현재 알림이 없습니다.");
//                        return;
//                    }
//                    
//                    // 결과를 다시 처음으로
//                    rs.beforeFirst();
//                    
//                    System.out.println("===== 알림 목록 =====");
//                    while (rs.next()) {
//                        int alertId = rs.getInt("alert_pk");
//                        String itemName = rs.getString("item_name");
//                        String borrowerName = rs.getString("borrower_name");
//                        
//                        System.out.println("알림 ID: " + alertId);
//                        System.out.println("물품: " + itemName);
//                        System.out.println("대여 요청자: " + borrowerName);
//                        
//                        // 승인/거절 선택
//                        System.out.println("1. 승인");
//                        System.out.println("2. 거절");
//                        System.out.print("선택: ");
//                        int choice = scanner.nextInt();
//                        
//                        if (choice == 1) {
//                            approveAlert(alertId, itemName, borrowerName);
//                        } else {
//                            rejectAlert(alertId);
//                        }
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("알림 관리 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    private void approveAlert(int alertId, String itemName, String borrowerName) throws SQLException {
//        // 트랜잭션 시작
//        connection.setAutoCommit(false);
//        
//        try {
//            // 1. 알림 상태를 승인으로 변경
//            String updateAlertSql = "UPDATE Alerts SET status = '승인' WHERE alert_pk = ?";
//            try (PreparedStatement pstmt = connection.prepareStatement(updateAlertSql)) {
//                pstmt.setInt(1, alertId);
//                pstmt.executeUpdate();
//            }
//            
//            // 2. 다른 동일한 알림들을 거절로 변경
//            String rejectOtherAlertsSql = "UPDATE Alerts SET status = '거절' " +
//                                          "WHERE user_L_fk = (SELECT user_L_fk FROM Alerts WHERE alert_pk = ?) " +
//                                          "AND alert_pk != ?";
//            try (PreparedStatement pstmt = connection.prepareStatement(rejectOtherAlertsSql)) {
//                pstmt.setInt(1, alertId);
//                pstmt.setInt(2, alertId);
//                pstmt.executeUpdate();
//            }
//            
//            // 3. 채팅방 생성
//            String createChatRoomSql = "INSERT INTO ChatRooms (lender_fk, borrower_fk) " +
//                                       "VALUES ((SELECT user_L_fk FROM Alerts WHERE alert_pk = ?), " +
//                                       "(SELECT user_fk FROM BorrowedItems WHERE item_fk = (SELECT item_fk FROM LendItems WHERE user_fk = (SELECT user_L_fk FROM Alerts WHERE alert_pk = ?))))";
//            try (PreparedStatement pstmt = connection.prepareStatement(createChatRoomSql)) {
//                pstmt.setInt(1, alertId);
//                pstmt.setInt(2, alertId);
//                pstmt.executeUpdate();
//            }
//            
//            // 트랜잭션 커밋
//            connection.commit();
//            
//            System.out.println("대여 요청을 승인했습니다. 채팅방이 생성되었습니다.");
//        } catch (SQLException e) {
//            // 롤백
//            connection.rollback();
//            throw e;
//        } finally {
//            // 다시 자동 커밋 설정
//            connection.setAutoCommit(true);
//        }
//    }
//
//    private void rejectAlert(int alertId) throws SQLException {
//        String updateAlertSql = "UPDATE Alerts SET status = '거절' WHERE alert_pk = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(updateAlertSql)) {
//            pstmt.setInt(1, alertId);
//            pstmt.executeUpdate();
//        }
//        
//        System.out.println("대여 요청을 거절했습니다.");
//    }
//
//    private int getUserId(String username) throws SQLException {
//        String sql = "SELECT user_pk FROM Users WHERE username = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setString(1, username);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("user_pk");
//                }
//            }
//        }
//        throw new SQLException("사용자를 찾을 수 없습니다.");
//    }
//}



import java.sql.*;
import java.util.Scanner;

public class Alert {
    private Connection connection;
    private Scanner scanner;
    private String currentUser;

    public Alert(Connection connection, String currentUser) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.currentUser = currentUser;
    }

    public void manageAlerts() {
        try {
            System.out.println("===== 알림 관리 =====");

            // 알림 목록 조회
            displayAlerts();

            // 상태 변경
            System.out.print("변경할 알림 ID를 입력하세요: ");
            int alertId = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            System.out.println("새로운 상태를 선택하세요:");
            System.out.println("1. 승인");
            System.out.println("2. 거절");
            System.out.print("선택: ");
            int statusChoice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            String newStatus = getStatus(statusChoice);

            if (updateAlertStatus(alertId, newStatus)) {
                System.out.println("알림 상태가 성공적으로 변경되었습니다.");
            } else {
                System.out.println("알림 상태 변경 중 오류가 발생했습니다.");
            }

        } catch (SQLException e) {
            System.out.println("알림 관리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void displayAlerts() throws SQLException {
        String query = "SELECT a.user_L_id, i.item_name, u.username AS borrower_name, a.status, a.created_at " +
                       "FROM Alerts a " +
                       "JOIN Items i ON a.item_fk = i.item_pk " +
                       "JOIN Users u ON a.user_B_fk = u.user_pk " +
                       "WHERE a.user_L_fk = ? AND a.status = '대기'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, getUserId(currentUser));
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("[알림 목록]");
                System.out.printf("%-10s %-20s %-15s %-10s %-20s\n", "알림 ID", "물품 이름", "빌리는 사람", "상태", "생성일자");

                while (rs.next()) {
                    System.out.printf("%-10d %-20s %-15s %-10s %-20s\n",
                            rs.getInt("alert_id"),
                            rs.getString("item_name"),
                            rs.getString("borrower_name"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"));
                }
            }
        }
    }

    private boolean updateAlertStatus(int alertId, String newStatus) throws SQLException {
        String query = "UPDATE Alerts SET status = ? WHERE alert_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, alertId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private String getStatus(int choice) {
        switch (choice) {
            case 1: return "승인";
            case 2: return "거절";
            default: throw new IllegalArgumentException("잘못된 상태 선택입니다.");
        }
    }

    private int getUserId(String username) throws SQLException {
        String query = "SELECT user_pk FROM Users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_pk");
                } else {
                    throw new SQLException("사용자를 찾을 수 없습니다.");
                }
            }
        }
    }
}
