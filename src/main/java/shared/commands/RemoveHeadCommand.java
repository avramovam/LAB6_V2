package shared.commands;

import server.CollectionManager;
import java.util.Map;

public class RemoveHeadCommand implements Command {
    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.removeHead();
    }

    @Override
    public String getDescription() {
        return "Удалить первый элемент коллекции";
    }
}