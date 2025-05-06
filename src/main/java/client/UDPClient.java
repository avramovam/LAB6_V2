package client;

import shared.commands.*;
import shared.utils.SerializationUtils;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class UDPClient {
    private static final int TIMEOUT_MS = 5000;
    private static final int BUFFER_SIZE = 65535;

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
                try {
                    // Отправляем команду exit на сервер
                    processCommand("exit");
                } catch (Exception e) {
                    System.out.println("Ошибка при завершении: " + e.getMessage());
                }
                System.out.println("Завершение работы клиента...");
                break;  // Только завершаем клиент
            }

            if (input.isEmpty()) continue;

            try {
                processCommand(input);
            } catch (ServerUnavailableException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void processCommand(String input) throws ServerUnavailableException, IOException {
        try {
            Command command = CommandParser.parse(input);
            if (command == null) return;

            String response = sendCommandWithRetry(command);
            System.out.println("Ответ сервера:\n" + response);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private String sendCommandWithRetry(Command command)
            throws ServerUnavailableException, IOException {

        byte[] data = SerializationUtils.serialize(command);
        ByteBuffer buffer = ByteBuffer.wrap(data);

        channel.send(buffer, serverAddress);
        System.out.println("Команда отправлена");

        return receiveResponse();
    }

    private String receiveResponse() throws IOException, ServerUnavailableException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
            buffer.clear();
            SocketAddress addr = channel.receive(buffer);

            if (addr != null) {
                buffer.flip();
                byte[] responseData = new byte[buffer.remaining()];
                buffer.get(responseData);
                try {
                    return (String) SerializationUtils.deserialize(responseData);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Поток прерван");
            }
        }

        throw new ServerUnavailableException("Сервер не отвечает. Попробуйте позже.");
    }

    private void closeResources() {
        try {
            if (channel != null) channel.close();
        } catch (IOException e) {
            System.err.println("Ошибка закрытия канала: " + e.getMessage());
        }
    }


class ServerUnavailableException extends Exception {
    public ServerUnavailableException(String message) {
        super(message);
    }
}
}