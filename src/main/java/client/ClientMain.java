package client;

public class ClientMain {
    public static void main(String[] args) {
        UDPClient client = new UDPClient("helios.cs.ifmo.ru", 12345);
        client.start();
    }
}