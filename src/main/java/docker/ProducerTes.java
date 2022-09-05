package docker;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ProducerTes {
    private static Properties props = new Properties();
    public static void main(String[] args) throws IOException {

        props.load(new FileReader("src/main/resources/consumer-dcoker.properties"));
        String TOPIC_NAME = (String) props.get("topic.producer.name");


        String msg1 = "key4";
        String msg2 = "key2";
        String msg3 = "key3";

        try {
            Producer<String, String> producer = new KafkaProducer<>(props);
            producer.send(new ProducerRecord<>(TOPIC_NAME,msg1,msg1)).get();
            producer.send(new ProducerRecord<>(TOPIC_NAME,msg2,msg2)).get();
            producer.send(new ProducerRecord<>(TOPIC_NAME,msg3,msg3)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
