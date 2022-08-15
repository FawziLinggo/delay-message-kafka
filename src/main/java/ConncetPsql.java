import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class ConncetPsql {
    private static Properties props = new Properties();

    public static void main(String[] args) throws  IOException {

        props.load(new FileReader("src/main/resources/consumer.properties"));
        String jdbcURL = (String) props.get("jdbcURL");
        String userDB = (String) props.get("userDB");
        String passDB = (String) props.get("passDB");
        String sql = (String) props.get("QueryDB");

        try {
            Connection connection = DriverManager.getConnection(jdbcURL,userDB,passDB);
            System.out.println("Connect");

            //while (true){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()){
                    int time = resultSet.getInt("time");
                    System.out.println(time);
                }
            //}
        } catch (SQLException e) {
            System.out.println("Error in connecting to Postgresql server");
            e.printStackTrace();
        }
    }
}
