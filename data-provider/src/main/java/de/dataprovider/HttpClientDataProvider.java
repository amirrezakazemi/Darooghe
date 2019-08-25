package de.dataprovider;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class HttpClientDataProvider {

    private static final String BITCOIN_CURRENT_PRICE = "https://www.bitstamp.net/api/v2/ticker_hour/btcusd";
    private static final String ETHEREUM_CURRENT_PRICE = "https://www.bitstamp.net/api/v2/ticker_hour/ethusd";

    private String topic;

    private OkHttpClient client;
    private KafkaWriter kafkaWriter;

    public HttpClientDataProvider(KafkaWriter kafkaWriter, String topic) {
        this.kafkaWriter = kafkaWriter;
        this.topic = topic;
        client = new OkHttpClient();
    }

    public void getAndWriteToKafka(String url, String key) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        kafkaWriter.writeString(key, response.body().string(), topic);
    }

    public void start() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    getAndWriteToKafka(BITCOIN_CURRENT_PRICE, "btc-p");
                    getAndWriteToKafka(ETHEREUM_CURRENT_PRICE, "eth-p");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void close() {

    }
}
