package shared.commands;

import server.CollectionManager;
import java.util.Map;

public class ExitCommand implements Command {
    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        manager.save();
        return "Завершение работы";
    }
}