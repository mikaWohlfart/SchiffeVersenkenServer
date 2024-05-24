package Server;

import Enum.Coordinates;
import Enum.Rotation;

import java.util.List;

public class Board {
    int[][] board;
    private static final int BOARD_SIZE = 10;
    private List<Ship> ships;

    public Board(List<Ship> ships) {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        this.ships = ships;
        for (Ship ship : ships) {
            List<int[]> coordinates = ship.getCoordinates();
            for (int i = 0; i < coordinates.size(); i++) {
                for (int k = 0; k < BOARD_SIZE; k++) {
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        if (coordinates.get(i)[0] == k && coordinates.get(i)[1] == j) {
                            board[k][j] = 4;
                        }
                    }
                }

            }
        }
    }

    public Board() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }


    public boolean placeBomb(int column, int row) {
        if (checkIfBombCanBePlaced(column, row)) {
            if (checkIfPlaceIsUsedByBoat(column, row)) {
                for (Ship ship : ships) {
                    List<int[]> coordinates = ship.getCoordinates();
                    for (int[] coordinate : coordinates) {
                        if (coordinate[0] == column && coordinate[1] == row) {
                            ship.markHit();
                            if (ship.isShipDestroyed()) {
                                markBoard(ship.getCoordinates().size(), column, row, ship.getRotation());
                            }
                        }
                    }
                }
                return true;
            } else {
                board[column][row] = 1;
                return false;
            }
        }
        return false;
    }


    private void markBoard(int size, int column, int row, Rotation rotation) {
        for (int i = 0; i < size; i++) {
            if (rotation == Rotation.RIGHT) {
                board[column][row] = 3;
                column += 1;
            }
        }
    }

    private boolean checkIfPlaceIsUsedByBoat(int column, int row) {
        return board[column][row] == 4;
    }

    private boolean checkIfBombCanBePlaced(int column, int row) {
        if (board[column][row] == 0) {
            return true;
        }
        return false;
    }

    public String castToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < BOARD_SIZE; i++) {
            sb.append(i);
            sb.append(":{");
            for (int j = 0; j < BOARD_SIZE; j++) {
                sb.append(castIntToChar(j));
                sb.append(":");
                sb.append(board[j][i]);
                sb.append(",");
            }
            sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private String castIntToChar(int j) {
        switch (j) {
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
}
