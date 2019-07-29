package ir.darooghe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

public class BitcoinFecher {
    private static final String BLOCKCHAIN_URL = "https://api.blockcypher.com/v1/btc/main";
    private static final String TRANSACTION_URL = "https://api.cryptoapis.io/v1/bc/btc/mainnet/txs/txid";

    private static OkHttpClient client = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String latestBlock() throws IOException {
//        client.setConnectTimeout(1, TimeUnit.MINUTES);
//        client.setReadTimeout(1, TimeUnit.MINUTES);

        Request request = new Request.Builder()
                .url(BLOCKCHAIN_URL)
                .build();
        Response response = client.newCall(request).execute();
        String chainInfo = response.body().string();

        System.out.println("Chain Info: " + chainInfo);

        Map<String, Object> chainMap = objectMapper.readValue(chainInfo, new TypeReference<Map<String,Object>>(){});
        String latestBlockUrl = (String) chainMap.get("latest_url");

        System.out.println(latestBlockUrl);


        request = new Request.Builder()
                .url(latestBlockUrl + "?index=0&limit=1000")
                .build();

        response = client.newCall(request).execute();
        String latestBlock = response.body().string();
        System.out.println(latestBlock);
        return  latestBlock;
    }

    public static String transaction(String hash) throws IOException {
        Request request = new Request.Builder()
                .url(TRANSACTION_URL + "/" +  hash)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-API-Key", "841f13837b2e5d82e40dece4afaff42c2a09738e")
                .build();

        Response response = client.newCall(request).execute();
        return  response.body().string();
    }

    public static void main(String[] args) {
        /*
        try {
            //System.out.println(latestBlock());
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
    }


}
