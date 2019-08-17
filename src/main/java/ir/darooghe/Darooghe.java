package ir.darooghe;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.IOException;

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

        bitcoinFecher = new BitcoinFecher(kafkaWriter);
        Thread bitcoin_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitcoinFecher.getBitcoinCurrentPrice();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread ethereum_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitcoinFecher.getEthereumCurrentPrice();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //bitcoin_thread.start();
        //ethereum_thread.start();
        //bitcoinFecher.start(config.getString("darooghe.ethereum.wss.uri"), "bitcoin-unc");
        bitcoinFecher.start(config.getString("darooghe.ethereum.wss.uri"), "ethereum-unc");

    }

    public void close() {
        bitcoinFecher.close();
        kafkaWriter.close();
    }
}
