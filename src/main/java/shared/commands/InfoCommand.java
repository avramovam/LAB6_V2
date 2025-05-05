package shared.commands;

import server.CollectionManager;
import java.util.Map;

public class InfoCommand implements Command {
    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.getInfo();
    }

    @Override
    public String getDescription() {
        return "Вывести информацию о коллекции";
    }
}