package shared.commands;

import server.CollectionManager;

import java.util.Map;

public class HelpCommand implements Command {
    @Override
    public String execute(CollectionManager collectionManager, Map<String, Object> args) {
        return """
            ===== Справка по доступным командам =====
            help : вывести справку по доступным командам
            info : вывести информацию о коллекции (тип, дата инициализации, количество элементов)
            show : вывести все элементы коллекции
            add : добавить новый элемент в коллекцию
            update id : обновить элемент с указанным id
            remove_by_id id : удалить элемент по id
            clear : очистить коллекцию
            save : сохранить коллекцию в файл (только сервер)
            execute_script file_name : выполнить скрипт из файла
            exit : завершить программу
            remove_head : вывести и удалить первый элемент
            add_if_min : добавить элемент, если он минимальный
            remove_greater : удалить элементы, превышающие заданный
            min_by_coordinates : найти элемент с минимальными координатами
            max_by_id : найти элемент с максимальным id
            count_greater_than_genre genre : подсчитать элементы с жанром больше заданного
            
            Формат ввода:
            - Для составных типов вводите по одному полю
            - Enum-ы: используйте значения из списка
            - Пустая строка = null для необязательных полей
            =========================================
            """;
    }

    @Override
    public String getDescription() {
        return "вывести справку по доступным командам";
    }
}