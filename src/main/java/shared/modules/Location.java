package shared.modules;

import java.io.Serializable;

/**
 * Класс для локации режиссера
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private long x;
    private double y;
    private String name;  // Может быть null (LAB5)

    public Location(long x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;  // Допускается null
    }

    // Геттеры без валидации
    public long getX() { return x; }
    public double getY() { return y; }
    public String getName() { return name; }

    @Override
    public String toString() {
        if (name == null || name.isEmpty()) {
            return String.format("(x=%d, y=%.2f)", x, y);
        }
        return String.format("%s (x=%d, y=%.2f)", name, x, y);
    }
}