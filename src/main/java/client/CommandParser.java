package client;

import shared.commands.*;
import shared.modules.*;
import shared.utils.MovieFactory;
import java.util.*;

/**
 * Парсер командной строки с валидацией входных данных
 */
public class CommandParser {
    private static final Scanner scanner = new Scanner(System.in);

    public static Command parse(String input) throws IllegalArgumentException {
        String[] parts = input.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        try {
            switch (command) {
                case "help":
                    return new HelpCommand();

                case "info":
                    return new InfoCommand();

                case "show":
                    return new ShowCommand();

                case "add":
                    return new AddCommand(MovieFactory.createMovieInteractive());

                case "update":
                    return parseUpdateCommand(args);

                case "remove_by_id":
                    return new RemoveByIdCommand(parseId(args));

                case "clear":
                    return new ClearCommand();

                case "save":
                    throw new IllegalArgumentException("Команда save недоступна клиенту");

                case "execute_script":
                    return new ExecuteScriptCommand(args);

                case "exit":
                    return new ExitCommand();

                case "remove_head":
                    return new RemoveHeadCommand();

                case "add_if_min":
                    return new AddIfMinCommand(MovieFactory.createMovieInteractive());

                case "remove_greater":
                    return new RemoveGreaterCommand(MovieFactory.createMovieInteractive());

                case "min_by_coordinates":
                    return new MinByCoordinatesCommand();

                case "max_by_id":
                    return new MaxByIdCommand();

                case "count_greater_than_genre":
                    return new CountGreaterThanGenreCommand(parseGenre(args));

                default:
                    throw new IllegalArgumentException("Неизвестная команда: " + command);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static UpdateCommand parseUpdateCommand(String args) {
        String[] parts = args.split("\\s+", 2);
        if (parts.length < 1) {
            throw new IllegalArgumentException("Требуется указать ID");
        }
        int id = parseId(parts[0]);
        return new UpdateCommand(id, MovieFactory.createMovieInteractive());
    }

    private static int parseId(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID должен быть числом");
        }
    }

    private static MovieGenre parseGenre(String arg) {
        try {
            return MovieGenre.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Доступные жанры: " +
                    Arrays.toString(MovieGenre.values()));
        }
    }

    public static List<Command> parseScript(List<String> lines) {
        List<Command> commands = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.trim().startsWith("//")) {
                commands.add(parse(line));
            }
        }
        return commands;
    }


}