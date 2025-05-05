package shared.commands;

import server.CollectionManager;
import java.util.Map;

public class RemoveByIdCommand implements Command {
    private final int id;

    public RemoveByIdCommand(int id) {
        this.id = id;
    }

    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.removeById(id);
    }

    @Override
    public String getDescription() {
        return "Удалить элемент по ID";
    }
}