import Server.Board;
import Server.PlayerHandler;
import Server.SchiffeVersenkenServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShipAdd {
    PlayerHandler playerHandler;
    @BeforeEach
    public void setUp() {
        playerHandler = new PlayerHandler();
    }

    @Test
    public void coordinatesToInt(){
        String shipType = "DESTROYER";
        String coordinates = "A1";
        String rotation = "RIGHT";
        String rotation2 = "DOWN";
        List<int[]> cti = playerHandler.coordinatesToInt(shipType, coordinates, rotation);
        for (int[] c : cti) {
            System.out.println(c[0] + " " + c[1]);
        }

        List<int[]> cti2 = playerHandler.coordinatesToInt(shipType, coordinates, rotation2);
        for (int[] c : cti2) {
            System.out.println(c[0] + " " + c[1]);
        }
    }
    @Test
    public void testCheckIfShipPositionIsValid_noCollision() {
        String shipType = "DESTROYER";
        String coordinates = "A1";
        String rotation = "RIGHT";

        boolean test = playerHandler.checkIfShipsCollide(playerHandler.coordinatesToInt(shipType, coordinates, rotation));
        assertFalse(test, "TEST");
        System.out.println(test);
        boolean result = playerHandler.checkIfShipPositionIsValid(shipType, coordinates, rotation);

        assertTrue(result, "Ship should be placed successfully without collision");
    }

    @Test
    public void checkIfShipCollides(){
        String shipType = "DESTROYER";
        String coordinates = "A1";
        String rotation = "RIGHT";
        List<int[]> cti = playerHandler.coordinatesToInt(shipType, coordinates, rotation);
        boolean check = playerHandler.checkIfShipsCollide(cti);
        assertFalse(check, "Ship should be placed successfully without collision");
    }

    @Test
    public void testCheckIfShipPositionIsValid_withCollision() {
        // Erstes Schiff platzieren
        String firstShipType = "DESTROYER";
        String firstCoordinates = "A1";
        String firstRotation = "RIGHT";
        playerHandler.checkIfShipPositionIsValid(firstShipType, firstCoordinates, firstRotation);

        // Zweites Schiff platzieren, das kollidiert
        String secondShipType = "SUBMARINE";
        String secondCoordinates = "A1"; // Gleiche Koordinaten wie erstes Schiff
        String secondRotation = "DOWN";

        boolean result = playerHandler.checkIfShipPositionIsValid(secondShipType, secondCoordinates, secondRotation);

        assertFalse(result, "Ship should not be placed due to collision with existing ship");
    }
}
