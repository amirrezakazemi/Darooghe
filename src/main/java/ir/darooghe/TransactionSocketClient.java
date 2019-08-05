package ir.darooghe;

import kafka.utils.Json;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class TransactionSocketClient extends WebSocketClient {
    ArrayList<String> transactions;
    private static final KafkaWriter kafkaWriter = new KafkaWriter();
    public TransactionSocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
        transactions = new ArrayList<>();

    }

    public TransactionSocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("new connection opened");
        send("{\"op\": \"ping\"}");
        send ("{\"op\": \"unconfirmed_sub\"}");

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);

    }

    @Override
    public void onMessage(String message){
        System.out.println("received message: " + message);
        try {
            if(!message.equals("{\"op\":\"pong\"}"))
                receiveMessage(message);
        }catch (InterruptedException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    public void receiveMessage(String message) throws InterruptedException {
        Thread.sleep(1000);
        kafkaWriter.writeString("bitcoin", message);
    }

}