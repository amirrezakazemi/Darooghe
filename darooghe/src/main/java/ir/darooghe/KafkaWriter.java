package ir.darooghe;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Closeable;
import java.util.Properties;

public class KafkaWriter implements Closeable {

    private String topic;
    private Producer<String, String> producer;

    public KafkaWriter(String bootstrapServers, String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);

        this.topic = topic;
    }

    public void writeString(String key, String record) {
        producer.send(new ProducerRecord<String, String>(topic, key, record), (m, e) -> System.out.println("sent!!!"));
        producer.flush();
    }

    @Override
    public void close() {
        producer.close();
    }
}
