package sm_borrow;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static Connection connection;
    private static Scanner scanner;
    private static Sign_in signIn;
    private static String currentUser;
    
    public static void main(String[] args) {

	        try {
	            // �����ͺ��̽� ���� �ʱ�ȭ
	            connection = Database.connect(); // Database Ŭ������ connect �޼��带 ���� �����մϴ�.
	            scanner = new Scanner(System.in);
	            signIn = new Sign_in(connection);
            
            while (true) {
                if (currentUser == null) {
                    // �α��� �� �޴�
                    System.out.println("===== ���л� ��ǰ �뿩 ���� =====");
                    System.out.println("1. �α���");
                    System.out.println("2. ȸ������");
                    System.out.println("0. ����");
                    
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // ���� ���� �Һ�
                    
                    switch (choice) {
                        case 1:
                            if (signIn.signIn()) {
                                currentUser = signIn.getCurrentUser();
                            }
                            break;
                        case 2:
                            Sign_up signUp = new Sign_up(connection);
                            signUp.signUp();
                            break;
                        case 0:
                            System.out.println("���α׷��� �����մϴ�.");
                            return;
                        default:
                            System.out.println("�߸��� �����Դϴ�.");
                    }
                } else {
                    // �α��� �� �޴�
                    System.out.println("===== ���л� ��ǰ �뿩 ���� =====");
                    System.out.println("1. Ȩ");
                    System.out.println("2. ä��");
                    System.out.println("3. ����������");
                    System.out.println("4. �˶�Ȯ��");
                    System.out.println("5. ��ǰ ��� (�����ֱ�)");
                    System.out.println("6. ��ǰ �뿩 ��û");
                    System.out.println("7. �α׾ƿ�");
                    System.out.println("0. ����");
                    
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // ���� ���� �Һ�
                    
                    switch (choice) {
                        case 1:
                            //showHome();
                            break;
                        case 2:
                            //showChat();
                            break;
                        case 3:
                            myPageMenu();
                            break;
                        case 4:
                            Alert alert = new Alert(connection, currentUser);
                            alert.manageAlerts();
                            break;
                        case 5:
                            Upload_L uploadL = new Upload_L(connection, currentUser);
                            uploadL.uploadItem();
                            break;
                        case 6:
                            Upload_B uploadB = new Upload_B(connection, currentUser);
                            uploadB.uploadBorrowRequest();
                            break;
                        case 7:
                            signIn.logout();
                            currentUser = null;
                            break;
                        case 0:
                            System.out.println("���α׷��� �����մϴ�.");
                            return;
                        default:
                            System.out.println("�߸��� �����Դϴ�.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("DB ���� ����: " + e.getMessage());
        }
    }
    
    
    private static void myPageMenu() {
        while (true) {
            System.out.println("===== ���������� =====");
            System.out.println("1. ��ǰ ��� Ȯ��");
            System.out.println("2. ��й�ȣ ����");
            System.out.println("3. ȸ�� Ż��");
            System.out.println("0. �ڷΰ���");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // ���� ���� �Һ�
            
            switch (choice){
            case 1:
                break;
            default:
                System.out.println("�߸��� �����Դϴ�.");
            }
        }
    }
}