package de.dataprovider;

import java.util.concurrent.ThreadLocalRandom;

public class FakeDataProvider {

    private String topic;
    private KafkaWriter kafkaWriter;

    public FakeDataProvider(KafkaWriter kafkaWriter, String topic) {
        this.kafkaWriter = kafkaWriter;
        this.topic = topic;
    }

    public void start() {
        Thread t = new Thread(() -> {
            while (true) {
                int lastPrice = 1000;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                kafkaWriter.writeString("fake-p", generatePriceRecord(nextPrice(lastPrice)), topic);
            }
        });
        t.start();
    }

    private String generatePriceRecord(int price) {
        return String.format("{\"high\": \"0\", \"last\": \"%d\", \"timestamp\": \"%d\", \"bid\": \"0\"," +
                " \"vwap\": \"0\", \"volume\": \"0\", \"low\": \"0\", \"ask\": \"0\", \"open\": \"0\"}",
                price, System.currentTimeMillis()/1000);
    }

    private int nextPrice(int lastPrice) {
        if (lastPrice == 0) {
            return lastPrice;
        }
        int randomInt = ThreadLocalRandom.current().nextInt(10);
        if (randomInt < 2) {
            return lastPrice - 1;
        } else if (randomInt > 4) {
            return lastPrice + 1;
        }
        return lastPrice;
    }
}
