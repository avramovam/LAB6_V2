package shared.commands;

import shared.modules.Movie;
import server.CollectionManager;
import java.util.Map;

public class AddIfMinCommand implements Command {
    private final Movie movie;

    public AddIfMinCommand(Movie movie) {
        this.movie = movie;
    }

    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.addIfMin(movie);
    }
}