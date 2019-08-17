package ir.darooghe;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

public class BitcoinFecher {
//    private static final String BLOCKCHAIN_URL = "https://api.blockcypher.com/v1/btc/main";
//    private static final String TRANSACTION_URL = "https://api.cryptoapis.io/v1/bc/btc/mainnet/txs/txid";
//    private static final String WEBSOCKET_WSS = "wss://socket.blockcypher.com/v1/btc/main";
//    private static WebSocketConnection webSocketConnection;

    //    private static OkHttpClient client = new OkHttpClient();
//    private static ObjectMapper objectMapper = new ObjectMapper();

    private URI serverUri;
    private String key;
    //private URI url;
    private KafkaWriter kafkaWriter;
    private List<WebSocketClient> webSocketClients;


    public BitcoinFecher(KafkaWriter kafkaWriter) {
        /*try {
            this.serverUri = new URI(serverUri);
        } catch (URISyntaxException e) {
            // todo: log
            System.exit(1);
        }*/

        this.kafkaWriter = kafkaWriter;
        webSocketClients = new ArrayList<>();
    }

    private static final String BITCOIN_CURRENT_PRICE = "https://www.bitstamp.net/api/v2/ticker_hour/btcusd";
    private static final String ETHEREUM_CURRENT_PRICE = "https://www.bitstamp.net/api/v2/ticker_hour/ethusd";
    private static OkHttpClient client = new OkHttpClient();

    public void getEthereumCurrentPrice() throws IOException {
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Request request = new Request.Builder()
                    .url(ETHEREUM_CURRENT_PRICE)
                    .build();
            Response response = client.newCall(request).execute();
            kafkaWriter.writeString("ethereum-price", response.body().string());
        }
    }
    public void getBitcoinCurrentPrice() throws IOException {
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Request request = new Request.Builder()
                    .url(BITCOIN_CURRENT_PRICE)
                    .build();
            Response response = client.newCall(request).execute();
            kafkaWriter.writeString("bitcoin-price", response.body().string());
        }
    }
    public void start(String url, String key) throws InterruptedException {
        try {
            this.serverUri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.key = key;
        System.out.println("well im here");
        WebSocketClient unconfirmedTransaction = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("New connection opened in Bitcoin fetcher for unconfirmed transactions.");
                if (key.equals("bitcoin-unc"))
                    send ("{\"op\": \"unconfirmed_sub\"}");
                else if (key.equals("ethereum-unc"))
                    send("{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"subscribe\",\"params\":[\"transaction\"]}");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Bitcoin fetcher unconfirmed transactions web socket received message: " + message);
                kafkaWriter.writeString(key, message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Bitcoin fetcher unconfirmed transactions web socket client closed with exit code "
                        + code + " additional info: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("an error occurred:" + ex);
            }
        };

        System.out.println(String.format("Connecting to %s ...", serverUri));
        if(unconfirmedTransaction.connectBlocking()) {
            System.out.println("Connected");
            webSocketClients.add(unconfirmedTransaction);
            unconfirmedTransaction.onOpen(null);
        }
    }


    public void close() {
        // todo : log
        for (WebSocketClient client : webSocketClients) {
            client.close();
        }
    }





//    public static String latestBlock() throws IOException {
////        client.setConnectTimeout(1, TimeUnit.MINUTES);
////        client.setReadTimeout(1, TimeUnit.MINUTES);
//
//        Request request = new Request.Builder()
//                .url(BLOCKCHAIN_URL)
//                .build();
//        Response response = client.newCall(request).execute();
//        String chainInfo = response.body().string();
//
//        System.out.println("Chain Info: " + chainInfo);
//
//        Map<String, Object> chainMap = objectMapper.readValue(chainInfo, new TypeReference<Map<String,Object>>(){});
//        String latestBlockUrl = (String) chainMap.get("latest_url");
//
//        System.out.println(latestBlockUrl);
//
//
//        request = new Request.Builder()
//                .url(latestBlockUrl + "?index=0&limit=1000")
//                .build();
//
//        response = client.newCall(request).execute();
//        String latestBlock = response.body().string();
//        System.out.println(latestBlock);
//        return  latestBlock;
//    }
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




}
