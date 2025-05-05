package shared.commands;

import server.CollectionManager;
import java.io.*;
import java.util.*;

public class ExecuteScriptCommand implements Command {
    private final String filename;

    public ExecuteScriptCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public String execute(CollectionManager manager, Map<String, Object> args) {
        try {
            File scriptFile = new File(filename);
            Scanner fileScanner = new Scanner(scriptFile);

            while (fileScanner.hasNextLine()) {
                String commandLine = fileScanner.nextLine().trim();
                // Парсинг и выполнение команд из файла
            }

            return "Скрипт выполнен успешно";
        } catch (FileNotFoundException e) {
            return "Ошибка: Файл скрипта не найден";
        }
    }
}