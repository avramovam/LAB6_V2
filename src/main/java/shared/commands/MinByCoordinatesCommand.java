package shared.commands;

import server.CollectionManager;
import java.util.Map;

public class MinByCoordinatesCommand implements Command {
    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.minByCoordinates();
    }
}