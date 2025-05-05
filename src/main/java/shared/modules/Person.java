package shared.modules;

import java.io.Serializable;

/**
 * Класс для данных режиссера.
 * Проверяет уникальность passportID (LAB5).
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;           // Не может быть null (LAB5)
    private String passportID;      // Уникальный, не null (LAB5)
    private Location location;      // Не может быть null (LAB5)

    public Person(String name, String passportID, Location location) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (passportID == null || passportID.trim().isEmpty()) {
            throw new IllegalArgumentException("Паспорт не может быть пустым");
        }
        if (location == null) {
            throw new IllegalArgumentException("Локация не может быть null");
        }

        this.name = name;
        this.passportID = passportID;
        this.location = location;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя режиссера не может быть пустым");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPassportID() {
        return passportID;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("%s (Паспорт: %s)", name, passportID);
    }
}