package ir.darooghe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.darooghe.BitcoinFecher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static ir.darooghe.BitcoinFecher.latestBlock;
import static ir.darooghe.BitcoinFecher.transactionSocket;

public class DataProvider {


    public static void main(String[] args) {

            try {
                BitcoinFecher.transactionSocket();

            }
             catch (InterruptedException e) {
                e.printStackTrace();
            }catch (URISyntaxException e) {
                e.printStackTrace();
            }

            }


}
