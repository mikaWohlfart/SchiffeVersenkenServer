package Enum;

public enum ShipType {
    BATTLESHIP("Battleship", 4),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2),
    SUBMARINE("Submarine", 1);

    private final String name; // Name of the ship
    private final int length;  // Length of the ship

    // Constructor to initialize enum constants with name and length
    ShipType(String name, int length) {
        this.name = name;
        this.length = length;
    }

    // Getter methods to access ship name and length
    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }
}