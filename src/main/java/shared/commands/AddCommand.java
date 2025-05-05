package shared.commands;

import server.CollectionManager;
import shared.modules.Movie;
import java.util.Map;

/**
 * Команда добавления фильма (LAB5)
 */
public class AddCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final Movie movie;

    public AddCommand(Movie movie) {
        this.movie = movie;
    }

    @Override
    public String execute(CollectionManager collectionManager, Map<String, Object> args) {
        return collectionManager.add(movie);
    }

    @Override
    public String getDescription() {
        return "Добавить новый элемент в коллекцию";
    }
}