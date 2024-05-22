package Enum;

public enum ShipType {
    BATTLESHIP("Battleship", 5),
    CRUISER("Cruiser", 4),
    DESTROYER("Destroyer", 3),
    SUBMARINE("Submarine", 2);

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