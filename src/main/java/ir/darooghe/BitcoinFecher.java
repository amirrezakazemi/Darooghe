package ir.darooghe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.java_websocket.drafts.*;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

public class BitcoinFecher {
    private static final String BLOCKCHAIN_URL = "https://api.blockcypher.com/v1/btc/main";
    private static final String TRANSACTION_URL = "https://api.cryptoapis.io/v1/bc/btc/mainnet/txs/txid";
    private static final String WEBSOCKET_WSS = "wss://ws.blockchain.info/inv";
//    private static final String WEBSOCKET_WSS = "wss://socket.blockcypher.com/v1/btc/main";


    static TransactionSocketClient socketClient;
   // private static WebSocketConnection webSocketConnection;

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
    /*
    public static String transaction(String hash) throws IOException {
        Request request = new Request.Builder()
                .url(TRANSACTION_URL + "/" +  hash)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-API-Key", "bfca1c0fba9b74c2a3c735a4f845a51b4a828eee")
                .build();

        Response response = client.newCall(request).execute();
        return  response.body().string();
    }

     */
    public static void transactionSocket() throws InterruptedException, URISyntaxException {

        socketClient = new TransactionSocketClient(new URI(WEBSOCKET_WSS), new Draft_6455());
        socketClient.connectBlocking();
        Thread.sleep(5000);
        socketClient.onOpen(new ServerHandshake() {
            @Override
            public short getHttpStatus() {
                return 0;
            }

            @Override
            public String getHttpStatusMessage() {
                return null;
            }

            @Override
            public Iterator<String> iterateHttpFields() {
                return null;
            }

            @Override
            public String getFieldValue(String name) {
                return null;
            }

            @Override
            public boolean hasFieldValue(String name) {
                return false;
            }

            @Override
            public byte[] getContent() {
                return new byte[0];
            }
        });




    }

    public static void main(String[] args) {

        try {
            BitcoinFecher.transactionSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
