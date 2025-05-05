package shared.commands;

import server.CollectionManager;
import java.io.Serializable;
import java.util.Map;

/**
 * Базовый интерфейс для всех команд (LAB5 + LAB6)
 */
public interface Command extends Serializable {
    String execute(CollectionManager collectionManager, Map<String, Object> args);

    default String getDescription() {
        return "Описание команды";
    }
}