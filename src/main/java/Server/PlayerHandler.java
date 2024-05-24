package Server;

import Interfaces.IPlayerHandler;
import Enum.PlayerCommands;
import Enum.ServerCommands;
import Enum.RPS_GAME;
import Enum.Coordinates;
import Interfaces.Methods;
import Enum.ShipType;
import Enum.Rotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class PlayerHandler extends Thread implements IPlayerHandler {
    private SchiffeVersenkenServer schiffeVersenkenServer;
    private String playername;
    private Socket clientSocket;
    private PrintWriter out;
    private int playernumber;
    private boolean isRegistered;
    private RPS_GAME rps;
    private boolean rpsWon;
    private String playerStatus;
    private Board attackerBoard;
    private Board defenderBoard;
    private boolean playerAlreadyAttacked;
    private boolean attackHitted;

    Map<PlayerCommands, Methods> actions = new HashMap<>();
    List<Ship> ships = new ArrayList<>();

    public PlayerHandler(SchiffeVersenkenServer schiffeVersenkenServer, Socket socket, int playernumber, Board attackerBoard) {
        this.clientSocket = socket;
        this.schiffeVersenkenServer = schiffeVersenkenServer;
        this.playernumber = playernumber;
        this.playerCommandInputHandlerIndexed();
        this.attackerBoard = attackerBoard;
        playerAlreadyAttacked = false;
    }

    public PlayerHandler() {}

    public void run() {
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (!(this.isInterrupted())) {
                String message = null;
                while ((message = in.readLine()) != null) {
                    handlePlayerInput(message);
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handlePlayerInput(String message) {
        this.messageHandler(message);
    }

    @Override
    public void sendMessageToUser(String message) {
        out.println(message);
    }

    @Override
    public String getPlayername() {
        return playername;
    }

    public int getPlayernumber() {
        return playernumber;
    }


    private void commandIsValid(String[] messageInParts) {
        PlayerCommands befehl = PlayerCommands.valueOf(messageInParts[0]);
        actions.get(befehl).handleInput(messageInParts);
    }

    private void handleRegister(String[] message) {
        if (message[1] != null && !message[1].isEmpty()) {
            playername = message[1];
            isRegistered = true;
            sendMessageToUser(ServerCommands.REGISTERED.name());
        } else {
            sendMessageToUser(ServerCommands.DENIED.name());
        }
    }

    private void handleShipAdd(String[] message) {

        if (message != null && message.length == 4 && ships != null && !checkShipLimitReachedPerType(message[1]) && ships.size() < 10) {
            String shipType = message[1];
            String coordinates = message[2];
            String rotation = message[3];
            if (shipType != null && !shipType.isEmpty()) {
                boolean isValid = checkIfShipPositionIsValid(shipType, coordinates, rotation);
                if (isValid) {
                    sendMessageToUser(ServerCommands.PLACED.name());
                } else {
                    sendMessageToUser(ServerCommands.REJECTED.name());
                }
            }
        } else {
            sendMessageToUser(ServerCommands.REJECTED.name());
        }
    }

    private boolean checkShipLimitReachedPerType(String shipType) {
        if (shipType != null && !shipType.isEmpty()) {
            int counter = 0;
            if (shipType.equals(ShipType.BATTLESHIP.getName())) {
                for (Ship ship : ships) {
                    if (ship.getShipType() == ShipType.BATTLESHIP) {
                        counter++;
                    }
                }
                if (counter == 1) {
                    return true;
                }
            }
            counter = 0;
            if (shipType.equals(ShipType.CRUISER.getName())) {
                for (Ship ship : ships) {
                    if (ship.getShipType() == ShipType.CRUISER) {
                        counter++;
                    }
                }
                if (counter == 2) {
                    return true;
                }
            }
            counter = 0;
            if (shipType.equals(ShipType.DESTROYER.getName())) {
                for (Ship ship : ships) {
                    if (ship.getShipType() == ShipType.DESTROYER) {
                        counter++;
                    }
                }
                if (counter == 3) {
                    return true;
                }
            }
            counter = 0;
            if (shipType.equals(ShipType.SUBMARINE.getName())) {
                for (Ship ship : ships) {
                    if (ship.getShipType() == ShipType.SUBMARINE) {
                        counter++;
                    }
                }
                if (counter == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkIfShipPositionIsValid(String shipType, String coordinates, String rotation) {
        List<int[]> coordinatesInInt = coordinatesToInt(shipType, coordinates, rotation);
        Rotation rotationEnum = Rotation.valueOf(rotation);
        if (rotationEnum == Rotation.RIGHT) {
            if (coordinatesInInt.get(coordinatesInInt.size() - 1)[1] >= 10) {
                return false;
            }
        } else {
            if (coordinatesInInt.get(coordinatesInInt.size() - 1)[0] >= 10) {
                return false;
            }
        }

        if (ships != null) {
            boolean collisiondetected = checkIfShipsCollide(coordinatesInInt);
            if (!collisiondetected) {
                ships.add(new Ship(ShipType.valueOf(shipType), coordinatesInInt, Rotation.valueOf(rotation)));
                return true;
            }
        }
        return false;
    }

    public boolean checkIfShipsCollide(List<int[]> koordinatesInInt) {
        if (koordinatesInInt.size() > 1) {
            if (koordinatesInInt.get(0)[0] < koordinatesInInt.get(1)[0]) {
                if (koordinatesInInt.get(0)[0] + (koordinatesInInt.size() - 1) >= 10) {
                    return true;
                }
            } else if (koordinatesInInt.get(0)[1] < koordinatesInInt.get(1)[1]) {
                if (koordinatesInInt.get(0)[1] + (koordinatesInInt.size() - 1) >= 10) {
                    return true;
                }
            }
        }else{
            if(koordinatesInInt.get(0)[0] >= 10 || koordinatesInInt.get(0)[1] >= 10){
                return true;
            }
        }

        for (Ship ship : ships) {
            List<int[]> coordinates = ship.getCoordinates();
            for (int[] coordinate : coordinates) {
                for (int[] coordinateShipToPlace : koordinatesInInt) {
                    if (coordinateShipToPlace[0] == coordinate[0]) {
                        if (coordinateShipToPlace[1] == coordinate[1] || (coordinateShipToPlace[1] - 1) == coordinate[1] || (coordinateShipToPlace[1] + 1) == coordinate[1]) {
                            return true;
                        }
                    }
                    if (coordinateShipToPlace[1] == coordinate[1]) {
                        if (coordinateShipToPlace[0] == coordinate[0] || (coordinateShipToPlace[0] - 1) == coordinate[0] || (coordinateShipToPlace[0] + 1) == coordinate[0]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<int[]> coordinatesToInt(String shipType, String coordinates, String rotation) {
        List<int[]> coordinatesInInt = new ArrayList<>();
        ShipType shipTypeEnum = ShipType.valueOf(shipType);
        Rotation rotationEnum = Rotation.valueOf(rotation);

        if (coordinates != null && coordinates.length() >= 2) {
            String coordinateColumn = coordinates.substring(0, 1);
            String coordinateRow = coordinates.substring(1);
            int koordinateColumnInt = Coordinates.valueOf(coordinateColumn).ordinal();
            int koordinateRowInt = Integer.parseInt(coordinateRow);
            koordinateRowInt -= 1;
            System.out.println("10.... " + koordinateRowInt);
            coordinatesInInt.add(new int[]{koordinateColumnInt, koordinateRowInt});
            for (int i = 0; i < shipTypeEnum.getLength() - 1; i++) {
                if (rotationEnum == Rotation.RIGHT) {
                    koordinateColumnInt += 1;
                    coordinatesInInt.add(new int[]{koordinateColumnInt, koordinateRowInt});
                } else {
                    koordinateRowInt += 1;
                    coordinatesInInt.add(new int[]{koordinateColumnInt, koordinateRowInt});
                }
            }
        } else {
            sendMessageToUser(ServerCommands.REJECTED.name());
        }
        return coordinatesInInt;
    }

    private void handleRPS(String[] message) {
        rps = RPS_GAME.valueOf(message[1]);
    }

    private void handleBomb(String[] message) {
        if (playerStatus.equals(ServerCommands.ATTACKER.name()) && !playerAlreadyAttacked) {
            if (message[1] != null && message[1].length() == 2) {
                String attackerColumn = (message[1].substring(0, 1));
                Coordinates attackerCoordinates = Coordinates.valueOf(attackerColumn);
                int attackerRow = Integer.parseInt(message[1].substring(1));
                attackerRow--;
                attackHitted = attackerBoard.placeBomb(attackerCoordinates.ordinal(), attackerRow);
                playerAlreadyAttacked = true;
            }

        }
    }

    public synchronized void notifyServer(){
        notifyAll();
    }

    private void playerCommandInputHandlerIndexed() {
        actions.put(PlayerCommands.REGISTER, this::handleRegister);
        actions.put(PlayerCommands.SHIP_ADD, this::handleShipAdd);
        actions.put(PlayerCommands.RPS, this::handleRPS);
        actions.put(PlayerCommands.BOMB, this::handleBomb);
    }

    private void messageHandler(String message) {
        String[] messageInParts = message.split(" ");
        String commandSendByPlayer = messageInParts[0];
        PlayerCommands[] playerCommands = PlayerCommands.values();
        String commandFiltered = Arrays.stream(playerCommands)
                .map(Enum::toString)
                .filter(command -> command.equals(commandSendByPlayer))
                .findFirst()
                .orElse("FAILED");

        if (!(commandFiltered.equals("FAILED"))) {
            commandIsValid(messageInParts);
        } else {
            sendMessageToUser(ServerCommands.UNKNOWN_COMMAND.toString());
        }
    }

    public boolean getIsRegsitered() {
        return isRegistered;
    }

    public RPS_GAME getRps() {
        return rps;
    }

    public void restartRPS() {
        rps = null;
    }

    public void setRpsWon(boolean rpsWon) {
        this.rpsWon = rpsWon;
    }

    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
    }

    public String getPlayerStatus() {
        return playerStatus;
    }

    public boolean getRpsWon() {
        return rpsWon;
    }

    public Board getAttackerBoard() {
        return attackerBoard;
    }

    public Board getDefenderBoard() {
        return defenderBoard;
    }

    public void setDefenderBoard(List<Ship> ships) {
        defenderBoard = new Board(ships);
    }

    public boolean playerAlreadyAttacked() {
        return playerAlreadyAttacked;
    }

    public boolean isAttackHitted() {
        return attackHitted;
    }

    public void setAttackHitted(boolean b) {
        attackHitted = b;
    }

    public void setPlayerAlreadyAttacked(boolean b) {
        playerAlreadyAttacked = b;
    }
}
