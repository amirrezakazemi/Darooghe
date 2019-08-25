package de.dataprovider;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class DataProvider {

    private KafkaWriter kafkaWriter;
    private WebSocketDataProvider webSocketDataProvider;
    private HttpClientDataProvider httpClientDataProvider;

    public void start() throws InterruptedException {

        File file = new File("application.conf");
        Config config;
        if (file.exists()) {
            config = ConfigFactory.parseFile(new File("application.conf"));
        } else {
            config = ConfigFactory.load();
        }

        kafkaWriter = new KafkaWriter(config.getString("data-provider.kafka.bootstrap.servers"));

        if(config.getString("data-provider.mode").equals("fake")) {
            FakeDataProvider fakeDataProvider = new FakeDataProvider(kafkaWriter,
                    config.getString("data-provider.kafka.topic.coin-prices"));
            fakeDataProvider.start();
        } else {
            webSocketDataProvider = new WebSocketDataProvider(kafkaWriter,
                    config.getString("data-provider.kafka.topic.unconfirmed-transactions"));
            webSocketDataProvider.start();

            httpClientDataProvider = new HttpClientDataProvider(kafkaWriter,
                    config.getString("data-provider.kafka.topic.coin-prices"));
            httpClientDataProvider.start();
        }
    }

    public void close() {
        webSocketDataProvider.close();
        kafkaWriter.close();
        httpClientDataProvider.close();
    }
}