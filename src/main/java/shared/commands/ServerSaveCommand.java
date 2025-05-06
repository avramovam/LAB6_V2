package shared.commands;

import server.CollectionManager;

import java.util.Map;

public class ServerSaveCommand implements Command {
    @Override
    public String execute(CollectionManager manager, Map<String, Object> args ) {
        manager.save();
        return "Коллекция сохранена (серверная команда)";
    }

    @Override
    public String getDescription() {
        return "сохранить коллекцию (только для сервера)";
    }
}