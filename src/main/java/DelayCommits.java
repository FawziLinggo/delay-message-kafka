import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.io.FileInputStream;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class DelayCommits {

    private static KafkaConsumer<String, String> consumer;
    private static TopicPartition topicPartition;
    private static Properties props = new Properties();



    public static void main(String[] args) throws Exception {

        props.load(new FileReader("src/main/resources/consumer.properties"));
        String TOPIC_NAME = (String) props.get("topic.consumer.name");
        int PARTITION = Integer.parseInt((String) props.get("partition"));

        consumer = new KafkaConsumer<>(props);
        topicPartition = new TopicPartition(TOPIC_NAME, PARTITION);
        consumer.assign(Collections.singleton(topicPartition));
        printOffsets("before consumer loop", consumer, topicPartition);
        startConsumer();
    }

    private static void startConsumer() throws InterruptedException {
            while (true) {
                long delay = Long.parseLong((String) props.get("delay.in.ms"));
                long timestampnow = Instant.now().toEpochMilli();
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(100));
                for (ConsumerRecord<String, String> record : records) {
                    if ((timestampnow - record.timestamp()) >= delay) {
                        System.out.printf("consumed: timestamp %s, key = %s, value = %s, partition id= %s, offset = %s%n",
                                record.timestamp(), record.key(), record.value(), record.partition(), record.offset());
                        consumer.commitAsync();
                    }
                    else {
                    consumer.seek(topicPartition, record.offset());
                    break;
                    }
                }
            }
    }
    private static void printOffsets(String message, KafkaConsumer<String, String> consumer, TopicPartition topicPartition) {
        Map<TopicPartition, OffsetAndMetadata> committed = consumer
                .committed(new HashSet<>(Arrays.asList(topicPartition)));
        OffsetAndMetadata offsetAndMetadata = committed.get(topicPartition);
        long position = consumer.position(topicPartition);
        System.out
                .printf("Offset info %s, Committed: %s, current position %s%n", message,
                        offsetAndMetadata == null ? null : offsetAndMetadata
                                .offset(), position);
    }
}
