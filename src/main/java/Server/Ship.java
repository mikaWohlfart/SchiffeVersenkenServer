package Server;

import java.util.ArrayList;
import java.util.List;

import Enum.ShipType;
import Enum.Coordinates;
import Enum.Rotation;

public class Ship {
    private final ShipType shipType;
    private final List<int[]> coordinates;
    private List<Boolean> hits;
    private final Rotation rotation;

    public Ship(ShipType shipType, List<int[]> coordinates, Rotation rotation) {
        this.shipType = shipType;
        this.coordinates = coordinates;
        this.hits = new ArrayList<>();
        for (int i = 0; i < shipType.getLength(); i++) {
            hits.add(false); // Anfangs sind alle Teile unbeschädigt
        }
        this.rotation = rotation;
    }

    public List<int[]> getCoordinates() {
        return coordinates;
    }

    public void markHit() {
        if (hits.size() - 1 == shipType.getLength()) {
            hits.add(true);
        }
    }

    public boolean isShipDestroyed() {
        return hits.size() == shipType.getLength();
    }

    private String integerKoordinateToChar(int i) {
        switch (i) {
            case 0:
                return Coordinates.A.name();
            case 1:
                return Coordinates.B.name();
            case 2:
                return Coordinates.C.name();
            case 3:
                return Coordinates.D.name();
            case 4:
                return Coordinates.E.name();
            case 5:
                return Coordinates.F.name();
            case 6:
                return Coordinates.G.name();
            case 7:
                return Coordinates.H.name();
            case 8:
                return Coordinates.I.name();
            default:
                return Coordinates.J.name();

        }
    }

    public Rotation getRotation() {
        return rotation;
    }

    public ShipType getShipType() {
        return shipType;
    }
}
