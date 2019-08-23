package ir.de.dataprovider;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class WebSocketDataProvider {
    private KafkaWriter kafkaWriter;
    private List<WebSocketClient> webSocketClients;
    private String topic;


    public WebSocketDataProvider(KafkaWriter kafkaWriter, String topic) {
        this.kafkaWriter = kafkaWriter;
        this.topic = topic;
        webSocketClients = new ArrayList<>();
    }

    public void start() throws InterruptedException {
        WebSocketClient ethUncTrxWSC = ethUncTrxWSC();
        System.out.println("Connecting to Ethereum WSS...");
        if(ethUncTrxWSC.connectBlocking()) {
            System.out.println("Connected");
            webSocketClients.add(ethUncTrxWSC);
            ethUncTrxWSC.onOpen(null);
        }
    }

    public void close() {
        // todo : log
        for (WebSocketClient client : webSocketClients) {
            client.close();
        }
    }

    private WebSocketClient ethUncTrxWSC() {
        URI serverUri = null;
        try {
            serverUri = new URI("wss://ws.web3api.io?x-api-key=UAK4da6d177bc88c50c9cdfc23449932600");
        } catch (URISyntaxException e) {
            // todo: log
            System.exit(1);
        }
        return new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("New connection opened for Ethereum unconfirmed transactions.");
                send ("{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"subscribe\",\"params\":[\"transaction\"]}");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Ethereum unconfirmed transactions web socket received message: " + message);
                kafkaWriter.writeString("eth-unc", message, topic);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Ethereum unconfirmed transactions web socket connection closed with exit code "
                        + code + " additional info: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("an error occurred:" + ex);
            }
        };
    }

}
