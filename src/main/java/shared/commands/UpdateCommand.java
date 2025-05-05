package shared.commands;

import shared.modules.Movie;
import server.CollectionManager;
import java.util.Map;

public class UpdateCommand implements Command {
    private final int id;
    private final Movie updatedMovie;

    public UpdateCommand(int id, Movie updatedMovie) {
        this.id = id;
        this.updatedMovie = updatedMovie;
    }

    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.update(id, updatedMovie);
    }

    @Override
    public String getDescription() {
        return "Обновить элемент по ID";
    }
}