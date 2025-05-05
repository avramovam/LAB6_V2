package client;

import shared.commands.*;
import shared.utils.SerializationUtils;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class UDPClient {
    private static final int TIMEOUT_MS = 5000;
    private static final int RETRY_DELAY_MS = 3000;

    private final String host;
    private final int port;
    private DatagramChannel channel;
    private InetSocketAddress serverAddress;

    public UDPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            initializeConnection();
            runCommandLoop();
        } catch (IOException e) {
            System.err.println("Критическая ошибка клиента: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void initializeConnection() throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.serverAddress = new InetSocketAddress(host, port);

        System.out.printf("=== Клиент запущен ===\n" +
                        "Сервер: %s:%d\n" +
                        "Таймаут ожидания: %d мс\n" +
                        "Для выхода введите 'exit'\n",
                host, port, TIMEOUT_MS);
    }

    private void runCommandLoop() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Завершение работы клиента...");
                break;
            }

            if (input.isEmpty()) continue;

            try {
                processCommand(input);
            } catch (ServerUnavailableException e) {
                System.out.println(e.getMessage());
                System.out.println("Попробуйте позже или проверьте соединение");
            }
        }
    }

    private void processCommand(String input) throws ServerUnavailableException, IOException {
        try {
            Command command = CommandParser.parse(input);
            String response = sendCommandWithRetry(command);
            System.out.println("Сервер: \n" + response);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private String sendCommandWithRetry(Command command)
            throws ServerUnavailableException, IOException {

        byte[] data = SerializationUtils.serialize(command);
        ByteBuffer buffer = ByteBuffer.wrap(data);

        System.out.print("Отправка команды... ");
        channel.send(buffer, serverAddress);

        try {
            String response = receiveResponse();
            System.out.print("✓ ");
            return response;
        } catch (SocketTimeoutException e) {
            System.out.println();
            throw new ServerUnavailableException("Сервер временно недоступен");
        }
    }

    private String receiveResponse() throws IOException, SocketTimeoutException {
        ByteBuffer buffer = ByteBuffer.allocate(65535);
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
            if (channel.receive(buffer) != null) {
                buffer.flip();
                return new String(buffer.array(), 0, buffer.limit());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Поток был прерван");
            }
        }
        throw new SocketTimeoutException();
    }

    private void closeResources() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new UDPClient("localhost", 12345).start();
    }
}

class ServerUnavailableException extends Exception {
    public ServerUnavailableException(String message) {
        super(message);
    }
}