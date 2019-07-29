package ir.darooghe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ir.darooghe.BitcoinFecher.latestBlock;
import static ir.darooghe.BitcoinFecher.transaction;

public class DataProvider {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        KafkaWriter kafkaWriter = new KafkaWriter();

        int currentBlockHeight = 0;

            try {


                while(true) {
                    System.out.println("Sleep for 10 seconds...");
                    Thread.sleep(10000);

                    String latestBlock = latestBlock();
                    Map<String, Object> blockMap = objectMapper.readValue(latestBlock, new TypeReference<Map<String,Object>>(){});

                    int blockHeight = (int) blockMap.get("height");
                    if (blockHeight > currentBlockHeight) {
                        currentBlockHeight = blockHeight;
                        List<String> txHashes = (List<String>) blockMap.get("txids");
                        for (String txHash : txHashes) {
                            Thread.sleep(1000);
                            kafkaWriter.writeString("bitcoin", transaction(txHash));
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }






}
