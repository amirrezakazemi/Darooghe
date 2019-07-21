package ir.darooghe;

import java.io.IOException;

public class DataProvider {

    public static void main(String[] args) {
        KafkaWriter kafkaWriter = new KafkaWriter();
        try {
            kafkaWriter.writeString("bitcoin", BitcoinFecher.latestBlock());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
