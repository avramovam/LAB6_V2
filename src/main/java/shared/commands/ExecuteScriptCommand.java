package shared.commands;

import client.CommandParser;
import server.CollectionManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExecuteScriptCommand implements Command {
    private final String scriptPath;
    private static final Set<String> executingScripts = new HashSet<>();

    public ExecuteScriptCommand(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    @Override
    public String execute(CollectionManager manager, Map<String, Object> params) {
        if (executingScripts.contains(scriptPath)) {
            return "Обнаружена рекурсия! Скрипт " + scriptPath + " уже выполняется";
        }
        try {
            executingScripts.add(scriptPath);
            Path path = Paths.get(scriptPath);
            if (!Files.exists(path)) {
                return "Файл скрипта не найден: " + scriptPath;
            }

            List<String> lines = Files.readAllLines(path)
                    .stream()
                    .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("//"))
                    .collect(Collectors.toList());

            StringBuilder result = new StringBuilder();
            result.append("Начало выполнения скрипта: ").append(scriptPath).append("\n");

            for (String line : lines) {
                try {
                    Command command = CommandParser.parse(line);
                    String commandResult = command.execute(manager, params);
                    result.append("> ").append(line).append("\n")
                            .append(commandResult).append("\n\n");
                } catch (IllegalArgumentException e) {
                    result.append("Ошибка в команде '").append(line).append("': ")
                            .append(e.getMessage()).append("\n");
                }
            }

            result.append("Скрипт выполнен: ").append(scriptPath);
            return result.toString();

        } catch (IOException e) {
            return "Ошибка чтения файла скрипта: " + e.getMessage();
        } finally {
            executingScripts.remove(scriptPath);
        }
    }

    @Override
    public String getDescription() {
        return "считать и исполнить скрипт из указанного файла";
    }
}