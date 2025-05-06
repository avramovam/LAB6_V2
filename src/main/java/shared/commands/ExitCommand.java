package shared.commands;

import server.CollectionManager;

import java.io.Serializable;
import java.util.Map;

public class ExitCommand implements Command, Serializable {
    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        manager.save();  // Только сохраняем коллекцию
        return "Коллекция сохранена. Клиент отключен";
    }

    @Override
    public String getDescription() {
        return "завершить клиент (с сохранением коллекции на сервере)";
    }
}