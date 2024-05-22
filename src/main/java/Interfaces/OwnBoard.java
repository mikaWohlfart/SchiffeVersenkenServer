package Interfaces;
import Enum.ShipType;
import Enum.Rotation;

public interface OwnBoard {
    boolean placeBoat(ShipType shipType, int column, int row, Rotation rotation);
}
