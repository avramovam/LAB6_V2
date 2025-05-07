package server;

public class ServerMain {

    public static void main(String[] args) {

        System.out.println("=== Запуск сервера ===");

        CollectionManager manager = new CollectionManager("FILE_NAME");
        UDPServer server = new UDPServer(12345, manager);

        // Обработка завершения работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nСервер завершает работу...");
            manager.save();
        }));

        // Основной цикл сервера
        server.start();
    }
}