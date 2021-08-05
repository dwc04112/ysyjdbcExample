package kr.ac.daegu.ysy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kr.ac.daegu.ysy.input.InputUtil;

public class DbProcess {
    private static final String DB_URL  = "jdbc:mariadb://localhost:3306/DGD";  //java에서는 db종류따라 스키마가 달라질수있다
    private static final String DB_USER = "root";
    private static final String DB_PW   = "0000";

    private final String selectedNumber;

    // 생성자를 명시적으로 지정하면 기본생성자 DbProcess()는 호출될 수 없다. (따로 명시 해야 한다.)
    public DbProcess(String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public void startProcessing() throws SQLException {         //코드를 수행하다가 문제가생기면 sqlex예외라면
                                                                //try.catch를 해야하는데 > 따로 입력이 필요없다
                                                                //빨리 디버깅 하기위해서 예외처리를 한다
        // Connection, PreparedStatement, ResultSet은 interface 객체이다.
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // selectedNumber에 따라 쿼리를 달리 한다.
        switch (this.selectedNumber){
            case "1":
                pstmt = conn.prepareStatement("select * from Student");
                rs = pstmt.executeQuery();
                /* 쿼리날린 결과를 가지고 콘솔에 출력한다. */
                while(rs.next()){
                    System.out.println(
                            rs.getInt(1)    + " | "
                                    + rs.getString(2) + " | "
                                    + rs.getInt(3)    + " | "
                                    + rs.getString(4) + " | "
                                    + rs.getString(5));
                }
                break;
            case "2": // 신규 Student를 추가 한다.
                // 이름은 뭔지 나이는 몇살인지 번호는 뭔지 이메일은 뭔지 입력을 받아서 쿼리 실행 하는 코드들..
                //id값은 primary키라서 이미 차있는경우도 있으니 id값은 입력되어있어야한다
                System.out.println("이름은?");
                String name = InputUtil.getStringFromConsole("no name");
                System.out.println("나이는?");
                int age = InputUtil.getIntFromConsole();
                System.out.println("번호는?");
                String phone = InputUtil.getStringFromConsole("no phone number");
                System.out.println("이메일은?");
                String email = InputUtil.getStringFromConsole("no email");

                // max id + 1을 가져온다, 새로운 id를 db에 insert 하기 위해
                pstmt = conn.prepareStatement("select max(id) + 1 from Student");
                rs = pstmt.executeQuery();
                if(rs.next()){
                    int maxId = rs.getInt(1);
                    System.out.println("maxId = " + maxId);
                    pstmt = conn.prepareStatement("insert into Student values (?, ?, ?, ?, ?)");
                    pstmt.setInt(1, maxId);
                    pstmt.setString(2, name);
                    pstmt.setInt(3, age);
                    pstmt.setString(4, phone);
                    pstmt.setString(5, email);
                    System.out.println("INSERT 완료");

                    int updatedRows = pstmt.executeUpdate();
                    System.out.println("updatedRows: " + updatedRows);
                }
                break;
            default :
                System.out.println("nothing to do"); break;
        }
    }
}
