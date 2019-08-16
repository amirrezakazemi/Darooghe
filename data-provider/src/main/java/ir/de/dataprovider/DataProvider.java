package ir.de.dataprovider;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class DataProvider {

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

        kafkaWriter = new KafkaWriter(config.getString("data-provider.kafka.bootstrap.servers"),
                config.getString("data-provider.kafka.topic"));

        bitcoinFecher = new BitcoinFecher(config.getString("data-provider.bitcoin.wss.uri"), kafkaWriter);
        bitcoinFecher.start();
    }

    public void close() {
        bitcoinFecher.close();
        kafkaWriter.close();
    }
}
