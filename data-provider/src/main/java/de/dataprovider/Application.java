package de.dataprovider;

public class Application {

    public static void main(String[] args) {
        try {
            DataProvider dataProvider = new DataProvider();
            dataProvider.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
