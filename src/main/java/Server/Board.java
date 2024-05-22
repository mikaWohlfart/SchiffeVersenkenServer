package Server;

import Enum.ShipType;
import Enum.Rotation;
import Interfaces.EnemyBoard;
import Interfaces.OwnBoard;

public class Board implements OwnBoard, EnemyBoard {
    int[][] board;

    public Board() {
        board = new int[8][8];
    }

    @Override
    public boolean placeBoat(ShipType shipType, int column, int row, Rotation rotation) {
        int length = shipType.getLength();
        for (int i = 0; i < length; i++) {

            if (checkIfPlaceIsUsed(column, row)){
                return false;
            }else{
                //Place Boat
                board[column][row] = 4;
            }

            if(rotation == Rotation.RIGHT) {
                row++;
            }else {
                column++;
            }
        }
        return false;
    }

    @Override
    public boolean placeBomb(int column, int row) {
        if (checkIfPlaceIsUsed(column, row)){
            checkIfBoatIsDestroyed(column, row);
            return true;
        }else{
            //Place Boat
            board[column][row] = 4;
            return false;
        }
    }

    private void checkIfBoatIsDestroyed(int column, int row) {

    }


    public int[][] getBoard() {
        return board;
    }

    private boolean checkIfPlaceIsUsed(int column, int row) {
        if (board[column][row] == 0) {
            return false;
        }
        return true;
    }

}
