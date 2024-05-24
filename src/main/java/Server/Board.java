package Server;

import Enum.Coordinates;
import Enum.Rotation;

import java.util.ArrayList;
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
        ships = new ArrayList<>();
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean placeBomb(int column, int row) {
        if (checkIfBombCanBePlaced(column, row)) {
            if (checkIfPlaceIsUsedByBoat(column, row)) {
                for (Ship ship : ships) {
                    List<int[]> coordinates = ship.getCoordinates();
                    for (int[] coordinate : coordinates) {
                        if (coordinate[0] == column && coordinate[1] == row) {
                            ship.markHit();
                            board[coordinate[0]][coordinate[1]] = 2;
                            if (ship.isShipDestroyed()) {
                                markBoard(ship.getCoordinates());
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


    private void markBoard(List<int[]> coordinates) {
        for (int[] coordinate : coordinates) {
            board[coordinate[0]][coordinate[1]] = 3;
        }
    }

    private boolean checkIfPlaceIsUsedByBoat(int column, int row) {
        for (Ship ship : ships) {
            List<int[]> coordinates = ship.getCoordinates();
            for (int[] coordinate : coordinates) {
                if (coordinate[0] == column && coordinate[1] == row) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIfBombCanBePlaced(int column, int row) {
        if (board[column][row] == 0 || board[column][row] == 4) {
            return true;
        }
        return false;
    }

    public String castToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < BOARD_SIZE; i++) {
            sb.append("\"" + i + "\"");
            sb.append(":{");
            for (int j = 0; j < BOARD_SIZE; j++) {
                sb.append("\"" + castIntToChar(j) + "\"");
                sb.append(":");
                sb.append("\"" + board[j][i] + "\"");
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    public String castToStringForAttacker() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < BOARD_SIZE; i++) {
            sb.append("\"" + i + "\"");
            sb.append(":{");
            for (int j = 0; j < BOARD_SIZE; j++) {
                sb.append("\"" + castIntToChar(j) + "\"");
                sb.append(":");
                if (board[j][i] == 4) {
                    sb.append("\"" + 0 + "\"");
                } else {
                    sb.append("\"" + board[j][i] + "\"");
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
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
