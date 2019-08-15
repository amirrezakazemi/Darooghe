package ir.darooghe;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class Darooghe {

    private KafkaWriter kafkaWriter;
    private BitcoinFecher bitcoinFecher;

    public void start() throws InterruptedException {

        File file = new File("application.conf");
        Config config;
        if (file.exists()) {
            config = ConfigFactory.parseFile(new File("application.conf"));
        } else {
            config = ConfigFactory.load();
        }

        kafkaWriter = new KafkaWriter(config.getString("darooghe.kafka.bootstrap.servers"),
                config.getString("darooghe.kafka.topic"));

        bitcoinFecher = new BitcoinFecher(config.getString("darooghe.bitcoin.wss.uri"), kafkaWriter);
        bitcoinFecher.start();
    }

    public void close() {
        bitcoinFecher.close();
        kafkaWriter.close();
    }
}
