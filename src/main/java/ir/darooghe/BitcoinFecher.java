package ir.darooghe;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BitcoinFecher {

//    private static final String LATEST_BLOCK_URL = "https://blockchain.info/latestblock";
    private static final String LATEST_BLOCK_URL = "http://api.blockcypher.com/v1/btc/main/blocks/586240";


    public static String latestBlock() throws IOException {

        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(1, TimeUnit.MINUTES);
//        client.setReadTimeout(1, TimeUnit.MINUTES);

        Request request = new Request.Builder()
//                .url("https://blockchain.info/block-height/19900?format=json")
                .url(LATEST_BLOCK_URL)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static void main(String[] args) {
        try {
            System.out.println(latestBlock());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
