package Server;

import Interfaces.IPlayerHandler;
import Enum.PlayerCommands;
import Enum.ServerCommands;
import Enum.RPS_GAME;
import Interfaces.Methods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerHandler extends Thread implements IPlayerHandler {
    private SchiffeVersenkenServer schiffeVersenkenServer;
    private String playername;
    private Socket clientSocket;
    private PrintWriter out;
    private int playernumber;
    private boolean isRegistered;
    private RPS_GAME rps;
    private boolean rpsWon;
    Map<PlayerCommands, Methods> actions = new HashMap<>();

    public PlayerHandler(SchiffeVersenkenServer schiffeVersenkenServer, Socket socket, int playernumber) {
        this.clientSocket = socket;
        this.schiffeVersenkenServer = schiffeVersenkenServer;
        this.playernumber = playernumber;
        this.playerCommandInputHandlerIndexed();
    }

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
        playername = message[1];
        isRegistered = true;
        sendMessageToUser("Welcome " + playername);
        System.out.println("Befehl REGISTER verarbeitet.");
    }

    private void handleShipAdd(String[] message) {

        System.out.println("Befehl SHIP_ADD verarbeitet.");
    }

    private void handleRPS(String[] message) {
        rps = RPS_GAME.valueOf(message[1]);
        System.out.println("Befehl RPS verarbeitet.");
    }

    private void handleBomb(String[] message) {
        System.out.println("Befehl BOMB verarbeitet.");
    }

    private void playerCommandInputHandlerIndexed() {
        actions.put(PlayerCommands.REGISTER, (String[] message) -> handleRegister(message));
        actions.put(PlayerCommands.SHIP_ADD, (String[] message) -> handleShipAdd(message));
        actions.put(PlayerCommands.RPS, (String[] message) -> handleRPS(message));
        actions.put(PlayerCommands.BOMB, (String[] message) -> handleBomb(message));
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

    public boolean getIsRegsitered(){
        return isRegistered;
    }

    public RPS_GAME getRps() {
        return rps;
    }

    public void restartRPS(){
        rps = null;
    }

    public void setRpsWon(boolean rpsWon) {
        this.rpsWon = rpsWon;
    }
}
