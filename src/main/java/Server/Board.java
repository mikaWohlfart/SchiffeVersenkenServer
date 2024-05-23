package Server;

import Enum.ShipType;
import Enum.Rotation;
import Interfaces.EnemyBoard;
import Interfaces.OwnBoard;

public class Board implements OwnBoard, EnemyBoard {
    int[][] board;
    private static final int BOARD_SIZE = 8;

    public Board() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    @Override
    public boolean placeBoat(ShipType shipType, int column, int row, Rotation rotation) {
        int length = shipType.getLength();
        if (checkIfBoatCanBePlaced(length, column, row, rotation)) {
            for (int i = 0; i < length; i++) {
                if (checkIfPlaceIsUsed(column, row)) {
                    return false;
                } else {
                    //Place Boat
                    board[column][row] = 4;
                }

                if (rotation == Rotation.RIGHT) {
                    row++;
                } else {
                    column++;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean placeBomb(int column, int row) {
        if (checkIfPlaceIsUsed(column, row)) {
            boolean boatIsDestroyed = checkIfBoatIsDestroyed(column, row);
            if (boatIsDestroyed) {
                markShipAsSunk(column, row);
            }else {
                board[column][row] = 2;
            }
            return true;
        } else {
            //Place Boat
            board[column][row] = 1;
            return false;
        }
    }


    private boolean checkIfBoatIsDestroyed(int column, int row) {
        return checkDirection(column, row, -1, 0) && // nach oben
                checkDirection(column, row, 1, 0) &&  // nach unten
                checkDirection(column, row, 0, -1) && // nach links
                checkDirection(column, row, 0, 1);    // nach rechts
    }

//    public boolean isShipSunk(int x, int y) {
//        if (board[x][y] != 2) {
//            return false;
//        }
//
//        if (checkDirection(x, y, -1, 0) && // nach oben
//                checkDirection(x, y, 1, 0) &&  // nach unten
//                checkDirection(x, y, 0, -1) && // nach links
//                checkDirection(x, y, 0, 1)) {  // nach rechts
//            markShipAsSunk(x, y); // Markiere das Schiff als zerstört
//            return true;
//        }
//        return false;
//    }

    private boolean checkDirection(int x, int y, int dx, int dy) {
        int i = x + dx;
        int j = y + dy;
        while (i >= 0 && i < BOARD_SIZE && j >= 0 && j < BOARD_SIZE && board[i][j] != 0) {
            if (board[i][j] == 1) {
                return false; // Falls ein Teil des Bootes nicht getroffen wurde
            }
            i += dx;
            j += dy;
        }
        return true;
    }

    private void markShipAsSunk(int x, int y) {
        markDirection(x, y, -1, 0); // nach oben
        markDirection(x, y, 1, 0);  // nach unten
        markDirection(x, y, 0, -1); // nach links
        markDirection(x, y, 0, 1);  // nach rechts
    }

    private void markDirection(int x, int y, int dx, int dy) {
        int i = x;
        int j = y;
        while (i >= 0 && i < BOARD_SIZE && j >= 0 && j < BOARD_SIZE && board[i][j] == 2) {
            board[i][j] = 3; // Markiere als zerstört
            i += dx;
            j += dy;
        }
    }

    @Override
    public int[][] getBoard() {
        return board;
    }

    private boolean checkIfPlaceIsUsed(int column, int row) {
        if (board[column][row] == 0) {
            return false;
        }
        return true;
    }

    private boolean checkIfBoatCanBePlaced(int shipLength, int column, int row, Rotation rotation) {
        if (rotation == Rotation.RIGHT) {
            if (board.length < column + shipLength) {
                return true;
            }
            return false;
        } else {
            if (board.length < row + shipLength) {
                return true;
            }
            return false;
        }
    }

}
