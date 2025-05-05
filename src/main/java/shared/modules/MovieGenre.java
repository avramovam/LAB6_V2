package shared.modules;

/**
 * Жанры фильмов (LAB5)
 */
public enum MovieGenre {
    DRAMA,
    COMEDY,
    ADVENTURE,
    HORROR,
    FANTASY;

    public static String getValues() {
        return "DRAMA, COMEDY, ADVENTURE, HORROR, FANTASY";
    }
}