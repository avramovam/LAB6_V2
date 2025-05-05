package shared.modules;

/**
 * Возрастные рейтинги MPAA (LAB5)
 */
public enum MpaaRating {
    G,      // Доступно всем
    R,      // До 17 с родителями
    NC_17;  // Только с 18

    public static String getValues() {
        return "G, R, NC_17";
    }
}