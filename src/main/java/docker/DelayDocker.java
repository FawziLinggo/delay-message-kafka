package docker;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DelayDocker {


    private static KafkaConsumer<String, String> consumer;
    private static TopicPartition topicPartition;
    private static Properties props = new Properties();

    public static void main(String[] args) throws InterruptedException, IOException {

        props.load(new FileReader("src/main/resources/consumer-dcoker.properties"));
        String TOPIC_NAME = (String) props.get("topic.consumer.name");
        int number_of_partitions = Integer.parseInt((String) props.get("number.of.partitions"));

        // Change Consumer Group to each Product
        Long delay = Long.parseLong((String) props.get("delay.in.ms"));
        String grup_id = "group_of_" + delay /60000 +"_minutes";
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, grup_id);

//        int PARTITION = number_of_partitions-1;
        consumer = new KafkaConsumer<>(props);


        while (true) {
            for (int i = 0; i < number_of_partitions; i++) {
                topicPartition = new TopicPartition(TOPIC_NAME, i);
                // Add Commit
                Map<TopicPartition, OffsetAndMetadata> committed = consumer.committed(new HashSet<>(List.of(topicPartition)));
                OffsetAndMetadata offsetAndMetadata = committed.get(topicPartition);
                consumer.assign(Collections.singleton(topicPartition));

                if (offsetAndMetadata == null) {
                    consumer.seekToBeginning(consumer.assignment());
                } else {
                    consumer.seek(topicPartition, offsetAndMetadata);
                }

            int consumerPollDurationOfSeconds = Integer.parseInt((String) props.get("Poll.Duration"));
            long timestampnow = Instant.now().toEpochMilli();

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(consumerPollDurationOfSeconds));
            for (ConsumerRecord<String, String> record : records) {
                if ((timestampnow - record.timestamp()) >= delay) {
                    System.out.printf("consumed: timestamp %s, key = %s, value = %s, partition id= %s, offset = %s%n",
                            record.timestamp(), record.key(), record.value(), record.partition(), record.offset());
                    consumer.commitAsync();
                } else {
//                        for (int i = 0; i < number_of_partitions; i++) {
//                            topicPartition = new TopicPartition(TOPIC_NAME, i);
//                            consumer.seek(topicPartition, record.offset());
//                        }
                    consumer.seek(topicPartition, record.offset());
                    break;
                }
            }
        }
        }
    }
}
