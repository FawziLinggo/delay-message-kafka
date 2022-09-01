package service;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
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

//            while (true){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                ArrayList<String> topic_name_array = new ArrayList<>();
                ArrayList<Long> delay = new ArrayList<>();

                // Get data from PostgreSQL
                while (resultSet.next()){
                    int time = resultSet.getInt("delay_in_minutes");
                    String topic_name = resultSet.getString("topic_product");

                    if(time!=0){
                        long time_to_ms = time * 60000L;
                        delay.add(time_to_ms);
                    }

                    if (topic_name!=null){
                        topic_name_array.add(topic_name);
                    }
                }

            for (Object delay_time: delay) {
                for (Object topic_destination:topic_name_array) {
                    System.out.println("send msg to topic " + topic_destination + " with delay " + delay_time+" ms");
                }
            }

//            }
        } catch (SQLException e) {
            System.out.println("Error in connecting to Postgresql server");
            e.printStackTrace();
        }
    }
}
