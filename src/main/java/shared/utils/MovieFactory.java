package shared.utils;

import shared.modules.*;

import java.util.Arrays;
import java.util.Scanner;

public class MovieFactory {
    private static Scanner scanner = new Scanner(System.in);

    public static Movie createMovieInteractive() {
        System.out.println("\n=== Создание нового фильма ===");

        String name = readNonEmptyString("Название фильма");
        Coordinates coordinates = readCoordinates();
        int oscars = readPositiveInt("Количество оскаров");
        int length = readPositiveInt("Длительность (минут)");
        MovieGenre genre = readEnum("Жанр", MovieGenre.class, true);
        MpaaRating mpaa = readEnum("Рейтинг MPAA", MpaaRating.class, true);
        Person director = readPerson();

        return new Movie(name, coordinates, oscars, length, genre, mpaa, director);
    }

    private static Person readPerson() {
        System.out.println("\n-- Режиссер --");
        String name = readNonEmptyString("Имя режиссера");
        String passport = readNonEmptyString("Номер паспорта");
        Location location = readLocation();
        return new Person(name, passport, location);
    }

    private static Location readLocation() {
        System.out.println("\n-- Локация режиссера --");
        long x = readLong("Координата X локации");
        double y = readDouble("Координата Y локации");
        String name = readNullableString("Название локации (можно пропустить)");
        return new Location(x, y, name);
    }

    // ========== Вспомогательные методы ==========

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Поле не может быть пустым!");
        }
    }

    private static Coordinates readCoordinates() {
        System.out.println("\n-- Координаты фильма --");
        double x = readDouble("Координата X (дробное число)");
        long y = readLong("Координата Y (целое число, > -177)", -176, Long.MAX_VALUE);
        return new Coordinates(x, y);
    }

    private static long readLong(String prompt, long min, long max) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                long value = Long.parseLong(scanner.nextLine());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Значение должно быть между %d и %d!\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число!");
            }
        }
    }


    private static String readNullableString(String prompt) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                int value = Integer.parseInt(scanner.nextLine());
                if (value > 0) {
                    return value;
                }
                System.out.println("Значение должно быть положительным!");
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число!");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число!");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите число!");
            }
        }
    }

    private static <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, boolean nullable) {
        while (true) {
            try {
                System.out.print(prompt + " (" + getEnumValues(enumClass) + ")" +
                        (nullable ? " (можно пропустить)" : "") + ": ");
                String input = scanner.nextLine().trim();

                if (nullable && input.isEmpty()) {
                    return null;
                }

                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Неверное значение! Допустимые варианты: " + getEnumValues(enumClass));
            }
        }
    }

    private static <T extends Enum<T>> String getEnumValues(Class<T> enumClass) {
        return String.join(", ", Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new));
    }
}