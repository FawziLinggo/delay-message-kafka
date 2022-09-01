import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Tes {
    private static KafkaConsumer<String, String> consumer;
    private static TopicPartition topicPartition;
    private static final Properties props = new Properties();
    private static ArrayList<String> topic_name_array = new ArrayList<>();
    private static ArrayList<Long> delay = new ArrayList<>();

    public static void main(String[] args) {
        try {
            props.load(new FileReader("src/main/resources/tes-consumer.properties"));
            String TOPIC_NAME = (String) props.get("topic.consumer.name");
            int PARTITION = Integer.parseInt((String) props.get("partition"));
            Producer<String, String> producer = new KafkaProducer<>(props);

                // Get Array From Postgre SQL
                Tes.DatabasesConnection();
                    for (Long delay_time: delay){

                        // Change Consumer Group to each Product
                        String grup_id = "1_group_of_" + delay_time /60000 +"_minutes";
                        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, grup_id);
                        consumer = new KafkaConsumer<>(props);

                        // For
                        //for (int i=0; i<PARTITION;i++){
                        // Seek to Beginning
                        topicPartition = new TopicPartition(TOPIC_NAME, PARTITION);
                        consumer.assign(Collections.singleton(topicPartition));
                        consumer.seekToBeginning(consumer.assignment());
                        System.out.println(PARTITION);
                        printOffsets("before consumer loop", consumer, topicPartition);

                        // Start Consumer
                        startConsumer(topicPartition, delay_time,producer);
                        //}
                }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }
    private static void startConsumer(TopicPartition topicPartition, Long delay_time, Producer<String, String> producer){
        try {
            //while (true) {
                int consumerPollDurationOfSeconds = Integer.parseInt((String) props.get("Poll.Duration"));
                long timestampnow = Instant.now().toEpochMilli();
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(consumerPollDurationOfSeconds));
                for (ConsumerRecord<String, String> record : records) {
                    if ((timestampnow - record.timestamp()) >= delay_time) {
                        for (String topic_destination:topic_name_array){
                            String topic_name = topic_destination+"_"+ delay_time /60000 +"_minutes";
                            producer.send(new ProducerRecord<>(topic_name, record.value()));
                            System.out.println("send to producer");
                        }
                        System.out.println("Commit");
                        consumer.commitSync();
                    }
                    else {
                        consumer.seek(topicPartition, record.offset());
                        break;
                    }
                //}
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    static Connection conn = null;
    private static void DatabasesConnection() {
        try {
            String jdbcURL = (String) props.get("jdbcURL");
            String userDB = (String) props.get("userDB");
            String passDB = (String) props.get("passDB");
            String sql = (String) props.get("QueryDB");

            conn = DriverManager.getConnection(jdbcURL,userDB,passDB);
            System.out.println("Connected");
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void printOffsets(String message, KafkaConsumer<String, String> consumer, TopicPartition topicPartition) {
        Map<TopicPartition, OffsetAndMetadata> committed = consumer
                .committed(new HashSet<>(List.of(topicPartition)));
        OffsetAndMetadata offsetAndMetadata = committed.get(topicPartition);
        long position = consumer.position(topicPartition);
        System.out
                .printf("Offset info %s, Committed: %s, current position %s%n", message,
                        offsetAndMetadata == null ? null : offsetAndMetadata
                                .offset(), position);
    }
}
