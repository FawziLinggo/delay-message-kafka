package docker;

import Props.DelayProperties;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DelayDocker {

    final static Logger logger = Logger.getLogger(DelayDocker.class.getName());
    private static final Properties props = new Properties();

    public static void main(String[] args) throws InterruptedException, IOException {
        ParameterTool params = ParameterTool.fromArgs(args);
        Properties props = new DelayProperties(params.getRequired("config.delay.path")).build();

//        String TOPIC_NAME = (String) props.get("topic.consumer.name");
//        int number_of_partitions = Integer.parseInt((String) props.get("number.of.partitions"));
//        long delay = Long.parseLong((String) props.get("delay.in.ms"));

        long delay = Long.parseLong(System.getenv("DELAY_MS"));
        String TOPIC_NAME = System.getenv("TOPIC_CONSUMER_NAME");
        int number_of_partitions = Integer.parseInt(System.getenv("NUBER_OF_PARTITIONS"));



        // Change Consumer Group to each Product
        String grup_id = "group_of_" + delay /60000 +"_minutes";
        String TOPIC_NAME_PRODUCER = TOPIC_NAME + "_delay_"+delay /60000 +"_minutes";
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, grup_id);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);



        while (true) {
            for (int i = 0; i < number_of_partitions; i++) {
                TopicPartition topicPartition = new TopicPartition(TOPIC_NAME, i);

                // Add Commit
                Map<TopicPartition, OffsetAndMetadata> committed = consumer.committed(new HashSet<>(List.of(topicPartition)));
                OffsetAndMetadata offsetAndMetadata = committed.get(topicPartition);
                consumer.assign(Collections.singleton(topicPartition));

                // Check Offset and Metadata
                if (offsetAndMetadata == null) {
                    consumer.seekToBeginning(consumer.assignment());
                    logger.warn(String.format("Offset and Metadata null ") + offsetAndMetadata);
                } else {
                    consumer.seek(topicPartition, offsetAndMetadata);
                }

                int consumerPollDurationOfSeconds = Integer.parseInt((String) props.get("Poll.Duration"));
                long timestampnow = Instant.now().toEpochMilli();
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(consumerPollDurationOfSeconds));
                try {
                    for (ConsumerRecord<String, String> record : records) {
                        if ((timestampnow - record.timestamp()) >= delay) {
                            producer.send(new ProducerRecord<>(TOPIC_NAME_PRODUCER, record.value()));
                            consumer.commitAsync();
                            logger.info(String.format("successfully sent message. "+ delay /60000
                                    +" minutes delay. commit(partition : "+ record.partition()
                                    + ", offset : "+record.offset()+")"));
                        } else {
                            consumer.seek(topicPartition, record.offset());
                        }
                    }
                } catch (Exception e) {
                    logger.error(String.format("Error : %s", e.getMessage()));
                }
            }
        }
    }
}
