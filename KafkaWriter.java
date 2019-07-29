package ir.darooghe;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Closeable;
import java.util.Properties;

public class KafkaWriter implements Closeable {

    private static final String KAFKA_TOPIC = "bitcoin-transactions";

    Producer<String, String> producer;

    public KafkaWriter() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }

    public void writeString(String key, String record) {
        producer.send(new ProducerRecord<String, String>(KAFKA_TOPIC, key, record), (m, e) -> System.out.println("sent!!!"));
        producer.flush();
    }

    @Override
    public void close() {
        producer.close();
    }

    public static void main(String[] args) {
        KafkaWriter kafkaWriter = new KafkaWriter();
        kafkaWriter.writeString("key", "value");
    }
}
