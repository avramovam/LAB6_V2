package shared.commands;

import server.CollectionManager;
import java.util.Map;

/**
 * Команда вывода всех элементов (LAB5)
 */
public class ShowCommand implements Command {
    private static final long serialVersionUID = 1L;

    @Override
    public String execute(CollectionManager collectionManager, Map<String, Object> args) {
        return collectionManager.show();
    }

    @Override
    public String getDescription() {
        return "Вывести все элементы коллекции";
    }
}