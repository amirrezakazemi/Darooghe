package ir.darooghe;

public class Application {

    public static void main(String[] args) {
        try {
            Darooghe darooghe = new Darooghe();
            darooghe.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
