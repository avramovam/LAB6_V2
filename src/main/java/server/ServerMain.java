package server;

public class ServerMain {
    private static final String ENV_VAR_NAME = "/Users/macbook/Desktop/лабы/Java/LAB6_secondVersion/collection.csv";

    public static void main(String[] args) {
        System.out.println("=== Запуск сервера ===");
        System.out.println("Используемая переменная окружения: " + ENV_VAR_NAME);

        CollectionManager manager = new CollectionManager(ENV_VAR_NAME);
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