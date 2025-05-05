package server;

public class ServerMain {
    private static final String ENV_VAR_NAME = "LAB5_COLLECTION";

    public static void main(String[] args) {
        System.out.println("=== Запуск сервера ===");
        System.out.println("Используемая переменная окружения: " + ENV_VAR_NAME);

        CollectionManager manager = new CollectionManager(ENV_VAR_NAME);
        UDPServer server = new UDPServer(12345, manager);

        // Обработка завершения работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nЗавершение работы сервера...");
            manager.save();
            server.stop();
        }));

        server.start();
    }
}