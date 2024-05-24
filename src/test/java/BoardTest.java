import Server.Board;
import Server.PlayerHandler;
import Server.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Enum.*;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;
    PlayerHandler playerHandler;

    @BeforeEach
    public void setUp() {
        playerHandler = new PlayerHandler();
        // Initialisiere Schiffe für den Test
        List<Ship> ships = new ArrayList<>();
        String shipType = "DESTROYER";
        String coordinates = "A1";
        String rotation = "RIGHT";

        List<int[]> cti = playerHandler.coordinatesToInt(shipType, coordinates, rotation);
        for (int[] c : cti) {
            System.out.println(c[0] + " " + c[1]);
        }
        ships.add(new Ship(ShipType.valueOf(shipType), cti, Rotation.valueOf(rotation))); // Beispielschiff
        board = new Board();
        board.setShips(ships);
    }

    @Test
    public void testPlaceBombHit() {
        // Testet einen Treffer
        boolean result = board.placeBomb(0, 0);
        assertTrue(result, "Bomb should hit a ship at (1, 1)");
        // Überprüft ob das Schiff als getroffen markiert ist
        assertTrue(board.placeBomb(1, 0), "Ship should be hit at (1, 1)");
    }

    @Test
    public void testPlaceBombMiss() {
        // Testet einen Fehlschuss
        boolean result = board.placeBomb(0, 1);
        assertFalse(result, "Bomb should miss at (0, 0)");
        // Überprüft ob das Board an dieser Stelle aktualisiert wurde
        assertEquals(1, board.getBoard()[0][1], "Board should be marked with a miss at (0, 0)");
    }

    @Test
    public void testPlaceBombSink() {
        // Testet das Versenken eines Schiffs
        board.placeBomb(0, 0);
        board.placeBomb(2, 0);
        boolean result = board.placeBomb(1, 0);
        assertTrue(result, "Bomb should hit a ship at (1, 0)");
        assertEquals(3, board.getBoard()[0][0], "Ship should be destroyed after hits");
    }
}