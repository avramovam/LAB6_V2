package client;

public class ClientMain {
    public static void main(String[] args) {
        UDPClient client = new UDPClient("localhost", 12345);
        client.start();
    }
}