package server;

import shared.commands.*;
import shared.utils.SerializationUtils;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class UDPServer {
    private static final int BUFFER_SIZE = 65535;
    private final int port;
    private final CollectionManager manager;
    private boolean isRunning;

    public UDPServer(int port, CollectionManager manager) {
        this.port = port;
        this.manager = manager;
    }

    public void start() {
        isRunning = true;
        System.out.println("Сервер запущен на порту " + port);

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            while (isRunning) {
                if (selector.select(1000) == 0) continue;
                new Thread(this::readServerCommands).start();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isReadable()) {
                        handleClientRequest(channel);
                    }
                    iter.remove();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }

    private void readServerCommands() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.startsWith("#")) { // Серверные команды начинаются с #
                    processServerCommand(input.substring(1));
                }
            }
        }
    }

    private void processServerCommand(String command) {
        if (command.equalsIgnoreCase("save")) {
            new ServerSaveCommand().execute(manager, null);
        } else {
            System.out.println("Неизвестная серверная команда");
        }
    }

    private void handleClientRequest(DatagramChannel channel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            buffer.clear();
            SocketAddress clientAddr = channel.receive(buffer);

            if (clientAddr != null) {
                buffer.flip();
                if (buffer.remaining() == 0) return;

                byte[] receivedData = new byte[buffer.remaining()];
                buffer.get(receivedData);

                try {
                    Command command = (Command) SerializationUtils.deserialize(receivedData);

                    // Обработка команды exit
                    if (command instanceof ExitCommand) {
                        manager.save();  // Сохраняем коллекцию
                        sendResponse(channel, clientAddr, "Коллекция сохранена. Клиент отключен");
                        return;  // Не завершаем работу сервера
                    }

                    String response = command.execute(manager, null);
                    sendResponse(channel, clientAddr, response);
                } catch (Exception e) {
                    System.err.println("Ошибка обработки команды: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка обработки запроса: " + e.getMessage());
        }
    }


    private void sendResponse(DatagramChannel channel, SocketAddress clientAddr, String response) {
        try {
            byte[] responseData = SerializationUtils.serialize(response);
            ByteBuffer buffer = ByteBuffer.wrap(responseData);
            channel.send(buffer, clientAddr);
        } catch (IOException e) {
            System.err.println("Ошибка отправки ответа: " + e.getMessage());
        }
    }

}