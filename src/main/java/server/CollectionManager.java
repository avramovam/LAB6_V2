package server;

import shared.modules.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

/**
 * Полная реализация менеджера коллекции с обработкой CSV
 */
public class CollectionManager {
    private PriorityQueue<Movie> collection = new PriorityQueue<>();
    private LocalDateTime initializationDate;
    private String csvFilePath;
    private static final String EMPTY_COLLECTION_MSG = "Коллекция пуста";
    private static final String DEFAULT_FILENAME = "collection.csv";
    private String filePath;

    public CollectionManager(String envVarName) {
        this.initializationDate = LocalDateTime.now();
        this.filePath = resolveFilePath(envVarName);
        this.collection = new PriorityQueue<>(Comparator.comparing(Movie::getName));

        initializeCollectionFile();
        loadCollection();
    }

    private String resolveFilePath(String envVarName) {
        // 1. Проверяем переменную окружения
        String envPath = System.getenv(envVarName);
        if (envPath != null && !envPath.trim().isEmpty()) {
            return envPath;
        }

        // 2. Используем файл в рабочей директории
        return Paths.get("").toAbsolutePath() + File.separator + DEFAULT_FILENAME;
    }

    private void initializeCollectionFile() {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            try {
                // Создаем директории, если нужно
                Files.createDirectories(path.getParent());

                // Создаем файл с заголовками CSV
                try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
                    writer.println("id,name,coordinates_x,coordinates_y,creationDate,oscarsCount,length,genre,mpaaRating,director_name,director_passportID,director_location_x,director_location_y,director_location_name");
                }

                System.out.println("Файл коллекции создан: " + path.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Ошибка создания файла коллекции: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    private void loadCollection() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            // Пропускаем заголовок
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Movie movie = parseCsvLine(line);
                    if (movie != null) {
                        collection.add(movie);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга строки: " + line);
                }
            }

            System.out.printf("Загружено %d элементов из %s\n", collection.size(), filePath);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла коллекции: " + e.getMessage());
        }
    }

    // =============== Основные команды ===============

    public String add(Movie movie) {
        if (!isPassportIdUnique(movie.getDirector().getPassportID())) {
            return "Ошибка: passportID должен быть уникальным";
        }

        movie.setId(generateId());
        movie.setCreationDate(LocalDateTime.now());
        collection.add(movie);
        return "Фильм добавлен с ID: " + movie.getId();
    }

    public String update(long id, Movie updatedMovie) {
        Optional<Movie> existing = collection.stream()
                .filter(m -> m.getId() == id)
                .findFirst();

        if (existing.isEmpty()) {
            return "Фильм с ID " + id + " не найден";
        }

        if (!isPassportIdUnique(updatedMovie.getDirector().getPassportID(), id)) {
            return "Ошибка: passportID должен быть уникальным";
        }

        collection.remove(existing.get());
        updatedMovie.setId(id);
        collection.add(updatedMovie);
        return "Фильм с ID " + id + " успешно обновлен";
    }

    public String removeById(int id) {
        boolean removed = collection.removeIf(m -> m.getId() == id);
        return removed ? "Фильм удален" : "Фильм с ID " + id + " не найден";
    }

    public String clear() {
        collection.clear();
        return "Коллекция очищена";
    }

    // =============== Специальные команды ===============

    public String addIfMin(Movie movie) {
        Movie min = collection.peek();
        if (min == null || movie.compareTo(min) < 0) {
            return add(movie);
        }
        return "Фильм не добавлен - не является минимальным";
    }

    public String removeGreater(Movie movie) {
        int beforeSize = collection.size();
        collection.removeIf(m -> m.compareTo(movie) > 0);
        int removedCount = beforeSize - collection.size();
        return "Удалено фильмов: " + removedCount;
    }

    public String removeHead() {
        Movie head = collection.poll();
        return head != null
                ? "Первый элемент:\n" + head
                : "Коллекция пуста";
    }

    // =============== Информационные команды ===============

    public String getInfo() {
        return String.format(
                "Тип коллекции: %s\n" +
                        "Дата инициализации: %s\n" +
                        "Количество элементов: %d",
                collection.getClass().getSimpleName(),
                initializationDate,
                collection.size()
        );
    }

    public String show() {
        if (collection.isEmpty()) {
            return "Коллекция пуста.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Содержимое коллекции ===\n");
        sb.append("Всего фильмов: ").append(collection.size()).append("\n");
        sb.append("Дата инициализации: ")
                .append(initializationDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
                .append("\n\n");

        collection.stream()
                .sorted()
                .forEach(movie -> sb.append(movie).append("\n"));

        return sb.toString();
    }

    public String minByCoordinates() {
        return collection.stream()
                .min(Comparator.comparing(Movie::getCoordinates))
                .map(Movie::toString)
                .orElse(EMPTY_COLLECTION_MSG);
    }

    public String maxById() {
        return collection.stream()
                .max(Comparator.comparingLong(Movie::getId))
                .map(Movie::toString)
                .orElse(EMPTY_COLLECTION_MSG);
    }

    public String countGreaterThanGenre(MovieGenre genre) {
        long count = collection.stream()
                .filter(m -> m.getGenre() != null)
                .filter(m -> m.getGenre().compareTo(genre) > 0)
                .count();
        return "Количество: " + count;
    }

    // =============== Работа с файлом ===============

    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
            writer.println("id,name,coordinates_x,coordinates_y,creationDate,oscarsCount,length,genre,mpaaRating,director_name,director_passportID,director_location_x,director_location_y,director_location_name");

            collection.stream()
                    .map(this::movieToCsvLine)
                    .forEach(writer::println);

        } catch (IOException e) {
            System.err.println("Ошибка сохранения файла: " + e.getMessage());
        }
    }

    private void loadFromCsv() {
        Path path = Paths.get(csvFilePath);
        if (!Files.exists(path)) {
            System.out.println("Файл не существует, создана новая коллекция");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // Пропускаем заголовок
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Movie movie = parseCsvLine(line);
                    if (movie != null) {
                        collection.add(movie);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга строки: " + line);
                    e.printStackTrace();
                }
            }

            System.out.println("Загружено фильмов: " + collection.size());
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    // =============== Вспомогательные методы ===============

    private Movie parseCsvLine(String csvLine) {
        String[] values = csvLine.split(",");
        if (values.length < 14) {
            System.err.println("Ошибка: в строке недостаточно данных - " + csvLine);
            return null;
        }

        try {
            // Парсим координаты
            Coordinates coordinates = new Coordinates(
                    Double.parseDouble(values[2]),
                    Long.parseLong(values[3])
            );

            // Парсим режиссера
            Location location = new Location(
                    Long.parseLong(values[11]),
                    Double.parseDouble(values[12]),
                    values.length > 13 && !values[13].isEmpty() ? values[13] : null
            );
            Person director = new Person(
                    values[9],
                    values[10],
                    location
            );

            // Создаем и наполняем объект Movie
            Movie movie = new Movie();
            movie.setId(Long.parseLong(values[0]));
            movie.setName(values[1]);
            movie.setCoordinates(coordinates);
            movie.setCreationDate(LocalDateTime.parse(values[4]));
            movie.setOscarsCount(Integer.parseInt(values[5]));
            movie.setLength(Integer.parseInt(values[6]));

            // Обрабатываем необязательные поля
            if (!values[7].isEmpty()) {
                movie.setGenre(MovieGenre.valueOf(values[7]));
            }
            if (!values[8].isEmpty()) {
                movie.setMpaaRating(MpaaRating.valueOf(values[8]));
            }

            movie.setDirector(director);

            return movie;
        } catch (Exception e) {
            System.err.println("Ошибка парсинга строки CSV: " + csvLine);
            e.printStackTrace();
            return null;
        }
    }

    private String movieToCsvLine(Movie movie) {
        return String.join(",",
                String.valueOf(movie.getId()),
                movie.getName(),
                String.valueOf(movie.getCoordinates().getX()),
                String.valueOf(movie.getCoordinates().getY()),
                movie.getCreationDate().toString(),
                String.valueOf(movie.getOscarsCount()),
                String.valueOf(movie.getLength()),
                movie.getGenre() != null ? movie.getGenre().name() : "",
                movie.getMpaaRating() != null ? movie.getMpaaRating().name() : "",
                movie.getDirector().getName(),
                movie.getDirector().getPassportID(),
                String.valueOf(movie.getDirector().getLocation().getX()),
                String.valueOf(movie.getDirector().getLocation().getY()),
                movie.getDirector().getLocation().getName() != null
                        ? movie.getDirector().getLocation().getName()
                        : ""
        );
    }

    private boolean isPassportIdUnique(String passportID) {
        return collection.stream()
                .noneMatch(m -> m.getDirector().getPassportID().equals(passportID));
    }

    private boolean isPassportIdUnique(String passportID, long excludeId) {
        return collection.stream()
                .filter(m -> m.getId() != excludeId)
                .noneMatch(m -> m.getDirector().getPassportID().equals(passportID));
    }

    private long generateId() {
        return collection.stream()
                .mapToLong(Movie::getId)
                .max()
                .orElse(0) + 1;
    }

    // Для тестирования
    public PriorityQueue<Movie> getCollection() {
        return collection;
    }
}