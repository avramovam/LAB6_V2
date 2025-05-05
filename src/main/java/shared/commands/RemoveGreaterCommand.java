package shared.commands;

import shared.modules.Movie;
import server.CollectionManager;
import java.util.Map;

public class RemoveGreaterCommand implements Command {
    private final Movie movie;

    public RemoveGreaterCommand(Movie movie) {
        this.movie = movie;
    }

    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.removeGreater(movie);
    }
}