package ir.darooghe;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

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
    private KafkaWriter kafkaWriter;
    private List<WebSocketClient> webSocketClients;


    public BitcoinFecher(String serverUri, KafkaWriter kafkaWriter) {
        try {
            this.serverUri = new URI(serverUri);
        } catch (URISyntaxException e) {
            // todo: log
            System.exit(1);
        }

        this.kafkaWriter = kafkaWriter;
        webSocketClients = new ArrayList<>();
    }



    public void start() throws InterruptedException {
        WebSocketClient unconfirmedTransaction = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("New connection opened in Bitcoin fetcher for unconfirmed transactions.");
                send ("{\"op\": \"unconfirmed_sub\"}");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Bitcoin fetcher unconfirmed transactions web socket received message: " + message);
                kafkaWriter.writeString("bitcoin-unc", message);
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
