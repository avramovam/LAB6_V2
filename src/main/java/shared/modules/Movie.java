package shared.modules;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс для хранения данных о фильме.
 * Реализует Comparable для сортировки по названию (требование LAB5).
 */
public class Movie implements Serializable, Comparable<Movie> {
    private static final long serialVersionUID = 1L;

    private Long id;                        // Автогенерация (LAB5)
    private String name;                    // Не может быть null (LAB5)
    private Coordinates coordinates;        // Не может быть null (LAB5)
    private LocalDateTime creationDate;     // Автогенерация (LAB5)
    private int oscarsCount;                // > 0 (LAB5)
    private Integer length;                 // Не может быть null, > 0 (LAB5)
    private MovieGenre genre;               // Может быть null (LAB5)
    private MpaaRating mpaaRating;          // Может быть null (LAB5)
    private Person director;                // Не может быть null (LAB5)

    // Конструктор
    public Movie(String name, Coordinates coordinates, int oscarsCount,
                 Integer length, MovieGenre genre, MpaaRating mpaaRating,
                 Person director) {
        setName(name);
        setCoordinates(coordinates);
        setOscarsCount(oscarsCount);
        setLength(length);
        setGenre(genre);
        setMpaaRating(mpaaRating);
        setDirector(director);
    }

    public Movie() {}

    public void setOscarsCount(int oscarsCount) {
        if (oscarsCount <= 0) {
            throw new IllegalArgumentException("Число оскаров должно быть положительным");
        }
        this.oscarsCount = oscarsCount;
    }

    public void setLength(Integer length) {
        if (length == null || length <= 0) {
            throw new IllegalArgumentException("Длина должна быть положительным числом");
        }
        this.length = length;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getDirector() {
        return director;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(MpaaRating mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public Integer getLength() {
        return length;
    }


    public int getOscarsCount() {
        return oscarsCount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название фильма не может быть пустым");
        }
        this.name = name;
    }

    // ... аналогичные сеттеры для остальных полей ...

    //---------------- Сортировка по умолчанию (LAB5) ----------------
    @Override
    public int compareTo(Movie other) {
        return this.name.compareTo(other.name);
    }

    //---------------- Для вывода в CSV (LAB5) ----------------
    public String toCsv() {
        return String.format("%d,%s,%f,%d,%s,%d,%d,%s,%s,%s,%s,%d,%f,%s",
                id,
                name,
                coordinates.getX(),
                coordinates.getY(),
                creationDate.toString(),
                oscarsCount,
                length,
                genre != null ? genre.name() : "",
                mpaaRating != null ? mpaaRating.name() : "",
                director.getName(),
                director.getPassportID(),
                director.getLocation().getX(),
                director.getLocation().getY(),
                director.getLocation().getName()
        );
    }

    @Override
    public String toString() {
        return String.format(
                "=== Фильм ID: %d ===\n" +
                        "Название: %s\n" +
                        "Координаты: %s\n" +
                        "Дата создания: %s\n" +
                        "Оскаров: %d\n" +
                        "Длительность: %d мин.\n" +
                        "Жанр: %s\n" +
                        "Рейтинг MPAA: %s\n" +
                        "--- Режиссер ---\n" +
                        "Имя: %s\n" +
                        "Паспорт: %s\n" +
                        "Локация: %s\n" +
                        "=====================",
                id,
                name,
                coordinates,
                creationDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                oscarsCount,
                length,
                genre != null ? genre : "не указан",
                mpaaRating != null ? mpaaRating : "не указан",
                director.getName(),
                director.getPassportID(),
                director.getLocation()
        );
    }
}