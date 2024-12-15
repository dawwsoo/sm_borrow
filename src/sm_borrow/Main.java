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
	            // 데이터베이스 연결 초기화
	            connection = Database.connect(); // Database 클래스의 connect 메서드를 통해 연결합니다.
	            scanner = new Scanner(System.in);
	            signIn = new Sign_in(connection);
            
            while (true) {
                if (currentUser == null) {
                    // 로그인 전 메뉴
                    System.out.println("===== 대학생 물품 대여 서비스 =====");
                    System.out.println("1. 로그인");
                    System.out.println("2. 회원가입");
                    System.out.println("0. 종료");
                    
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // 개행 문자 소비
                    
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
                            System.out.println("프로그램을 종료합니다.");
                            return;
                        default:
                            System.out.println("잘못된 선택입니다.");
                    }
                } else {
                    // 로그인 후 메뉴
                    System.out.println("===== 대학생 물품 대여 서비스 =====");
                    System.out.println("1. 홈");
                    System.out.println("2. 채팅");
                    System.out.println("3. 마이페이지");
                    System.out.println("4. 알람확인");
                    System.out.println("5. 물품 등록 (빌려주기)");
                    System.out.println("6. 물품 대여 요청");
                    System.out.println("7. 로그아웃");
                    System.out.println("0. 종료");
                    
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // 개행 문자 소비
                    
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
                            System.out.println("프로그램을 종료합니다.");
                            return;
                        default:
                            System.out.println("잘못된 선택입니다.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("DB 연결 오류: " + e.getMessage());
        }
    }
    
    
    private static void myPageMenu() {
        while (true) {
            System.out.println("===== 마이페이지 =====");
            System.out.println("1. 물품 목록 확인");
            System.out.println("2. 비밀번호 변경");
            System.out.println("3. 회원 탈퇴");
            System.out.println("0. 뒤로가기");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // 개행 문자 소비
            
            switch (choice){
            case 1:
                break;
            default:
                System.out.println("잘못된 선택입니다.");
            }
        }
    }
}