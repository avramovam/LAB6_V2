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
                selector.select(1000);
                processReadyChannels(selector.selectedKeys(), channel);
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }

    private void processReadyChannels(Set<SelectionKey> keys, DatagramChannel channel) {
        Iterator<SelectionKey> iter = keys.iterator();
        while (iter.hasNext()) {
            SelectionKey key = iter.next();
            if (key.isReadable()) {
                handleClientRequest(channel);
            }
            iter.remove();
        }
    }

    private void handleClientRequest(DatagramChannel channel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            SocketAddress clientAddr = channel.receive(buffer);

            if (clientAddr != null) {
                Command command = (Command) SerializationUtils.deserialize(buffer.array());
                String response = command.execute(manager, null);
                sendResponse(channel, clientAddr, response);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки запроса: " + e.getMessage());
        }
    }

    private void sendResponse(DatagramChannel channel, SocketAddress clientAddr, String response) {
        try {
            byte[] responseData = response.getBytes();
            channel.send(ByteBuffer.wrap(responseData), clientAddr);
        } catch (IOException e) {
            System.err.println("Ошибка отправки ответа: " + e.getMessage());
        }
    }

    public void stop() {
        isRunning = false;
        manager.save();
    }
}