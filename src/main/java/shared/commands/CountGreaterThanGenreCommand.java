package shared.commands;

import shared.modules.MovieGenre;
import server.CollectionManager;
import java.util.Map;

public class CountGreaterThanGenreCommand implements Command {
    private final MovieGenre genre;

    public CountGreaterThanGenreCommand(MovieGenre genre) {
        this.genre = genre;
    }

    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        return manager.countGreaterThanGenre(genre);
    }
}