package de.dataprovider;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Closeable;
import java.util.Properties;

public class KafkaWriter implements Closeable {

    private Producer<String, String> producer;

    public KafkaWriter(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    public void writeString(String key, String record, String topic) {
        System.out.println(key + " : " + record);
        producer.send(new ProducerRecord<String, String>(topic, key, record),
                (m, e) -> System.out.println(String.format("message: %s\t error: %s", m, e)));
        producer.flush();
    }

    @Override
    public void close() {
        producer.close();
    }
}
