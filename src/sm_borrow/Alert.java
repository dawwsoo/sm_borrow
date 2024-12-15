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
//            // ����� ID ��ȸ
//            int userId = getUserId(currentUser);
//            
//            // �˸� ��� ��ȸ
//            String alertsSql = "SELECT a.alert_pk, li.item_fk, i.item_name, u.username AS borrower_name " +
//                               "FROM Alerts a " +
//                               "JOIN LendItems li ON a.user_L_fk = li.user_fk " +
//                               "JOIN Items i ON li.item_fk = i.item_pk " +
//                               "JOIN BorrowedItems bi ON bi.item_fk = i.item_pk " +
//                               "JOIN Users u ON bi.user_fk = u.user_pk " +
//                               "WHERE a.user_L_fk = ? AND a.status = '���'";
//            
//            try (PreparedStatement pstmt = connection.prepareStatement(alertsSql)) {
//                pstmt.setInt(1, userId);
//                
//                try (ResultSet rs = pstmt.executeQuery()) {
//                    if (!rs.next()) {
//                        System.out.println("���� �˸��� �����ϴ�.");
//                        return;
//                    }
//                    
//                    // ����� �ٽ� ó������
//                    rs.beforeFirst();
//                    
//                    System.out.println("===== �˸� ��� =====");
//                    while (rs.next()) {
//                        int alertId = rs.getInt("alert_pk");
//                        String itemName = rs.getString("item_name");
//                        String borrowerName = rs.getString("borrower_name");
//                        
//                        System.out.println("�˸� ID: " + alertId);
//                        System.out.println("��ǰ: " + itemName);
//                        System.out.println("�뿩 ��û��: " + borrowerName);
//                        
//                        // ����/���� ����
//                        System.out.println("1. ����");
//                        System.out.println("2. ����");
//                        System.out.print("����: ");
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
//            System.out.println("�˸� ���� �� ������ �߻��߽��ϴ�: " + e.getMessage());
//        }
//    }
//
//    private void approveAlert(int alertId, String itemName, String borrowerName) throws SQLException {
//        // Ʈ����� ����
//        connection.setAutoCommit(false);
//        
//        try {
//            // 1. �˸� ���¸� �������� ����
//            String updateAlertSql = "UPDATE Alerts SET status = '����' WHERE alert_pk = ?";
//            try (PreparedStatement pstmt = connection.prepareStatement(updateAlertSql)) {
//                pstmt.setInt(1, alertId);
//                pstmt.executeUpdate();
//            }
//            
//            // 2. �ٸ� ������ �˸����� ������ ����
//            String rejectOtherAlertsSql = "UPDATE Alerts SET status = '����' " +
//                                          "WHERE user_L_fk = (SELECT user_L_fk FROM Alerts WHERE alert_pk = ?) " +
//                                          "AND alert_pk != ?";
//            try (PreparedStatement pstmt = connection.prepareStatement(rejectOtherAlertsSql)) {
//                pstmt.setInt(1, alertId);
//                pstmt.setInt(2, alertId);
//                pstmt.executeUpdate();
//            }
//            
//            // 3. ä�ù� ����
//            String createChatRoomSql = "INSERT INTO ChatRooms (lender_fk, borrower_fk) " +
//                                       "VALUES ((SELECT user_L_fk FROM Alerts WHERE alert_pk = ?), " +
//                                       "(SELECT user_fk FROM BorrowedItems WHERE item_fk = (SELECT item_fk FROM LendItems WHERE user_fk = (SELECT user_L_fk FROM Alerts WHERE alert_pk = ?))))";
//            try (PreparedStatement pstmt = connection.prepareStatement(createChatRoomSql)) {
//                pstmt.setInt(1, alertId);
//                pstmt.setInt(2, alertId);
//                pstmt.executeUpdate();
//            }
//            
//            // Ʈ����� Ŀ��
//            connection.commit();
//            
//            System.out.println("�뿩 ��û�� �����߽��ϴ�. ä�ù��� �����Ǿ����ϴ�.");
//        } catch (SQLException e) {
//            // �ѹ�
//            connection.rollback();
//            throw e;
//        } finally {
//            // �ٽ� �ڵ� Ŀ�� ����
//            connection.setAutoCommit(true);
//        }
//    }
//
//    private void rejectAlert(int alertId) throws SQLException {
//        String updateAlertSql = "UPDATE Alerts SET status = '����' WHERE alert_pk = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(updateAlertSql)) {
//            pstmt.setInt(1, alertId);
//            pstmt.executeUpdate();
//        }
//        
//        System.out.println("�뿩 ��û�� �����߽��ϴ�.");
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
//        throw new SQLException("����ڸ� ã�� �� �����ϴ�.");
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
            System.out.println("===== �˸� ���� =====");

            // �˸� ��� ��ȸ
            displayAlerts();

            // ���� ����
            System.out.print("������ �˸� ID�� �Է��ϼ���: ");
            int alertId = scanner.nextInt();
            scanner.nextLine(); // ���� ����

            System.out.println("���ο� ���¸� �����ϼ���:");
            System.out.println("1. ����");
            System.out.println("2. ����");
            System.out.print("����: ");
            int statusChoice = scanner.nextInt();
            scanner.nextLine(); // ���� ����

            String newStatus = getStatus(statusChoice);

            if (updateAlertStatus(alertId, newStatus)) {
                System.out.println("�˸� ���°� ���������� ����Ǿ����ϴ�.");
            } else {
                System.out.println("�˸� ���� ���� �� ������ �߻��߽��ϴ�.");
            }

        } catch (SQLException e) {
            System.out.println("�˸� ���� �� ������ �߻��߽��ϴ�: " + e.getMessage());
        }
    }

    private void displayAlerts() throws SQLException {
        String query = "SELECT a.user_L_id, i.item_name, u.username AS borrower_name, a.status, a.created_at " +
                       "FROM Alerts a " +
                       "JOIN Items i ON a.item_fk = i.item_pk " +
                       "JOIN Users u ON a.user_B_fk = u.user_pk " +
                       "WHERE a.user_L_fk = ? AND a.status = '���'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, getUserId(currentUser));
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("[�˸� ���]");
                System.out.printf("%-10s %-20s %-15s %-10s %-20s\n", "�˸� ID", "��ǰ �̸�", "������ ���", "����", "��������");

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
            case 1: return "����";
            case 2: return "����";
            default: throw new IllegalArgumentException("�߸��� ���� �����Դϴ�.");
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
                    throw new SQLException("����ڸ� ã�� �� �����ϴ�.");
                }
            }
        }
    }
}
