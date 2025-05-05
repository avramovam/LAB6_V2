package shared.commands;

import server.CollectionManager;
import java.util.Map;

public class ClearCommand implements Command {
    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.clear();
    }

    @Override
    public String getDescription() {
        return "Очистить коллекцию";
    }
}