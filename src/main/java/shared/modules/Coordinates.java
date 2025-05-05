package shared.modules;


import java.io.Serializable;

/**
 * Класс для хранения координат.
 * Проверяет условие: y > -177 (LAB5).
 */
public class Coordinates implements Serializable, Comparable<Coordinates> {
    private static final long serialVersionUID = 1L;

    private Double x;  // Не может быть null (LAB5)
    private long y;    // > -177 (LAB5)

    public Coordinates(Double x, long y) {
        setX(x);
        setY(y);
    }

    public int compareTo(Coordinates other) {
        int xCompare = Double.compare(this.x, other.x);
        return xCompare != 0 ? xCompare : Long.compare(this.y, other.y);
    }

    public void setX(Double x) {
        if (x == null) {
            throw new IllegalArgumentException("Координата X не может быть null");
        }
        this.x = x;
    }

    public void setY(long y) {
        if (y <= -177) {
            throw new IllegalArgumentException("Координата Y должна быть > -177");
        }
        this.y = y;
    }

    public Double getX() { return x; }
    public long getY() { return y; }

    @Override
    public String toString() {
        return String.format("(x=%.2f, y=%d)", x, y);
    }
}