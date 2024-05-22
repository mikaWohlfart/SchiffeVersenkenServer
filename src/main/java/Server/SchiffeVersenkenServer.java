package Server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class SchiffeVersenkenServer implements ISchiffeVersenkenServer{

    List<IPlayerHandler> playerHandler = new ArrayList<>();


    public SchiffeVersenkenServer() {
        final int PORT = 12345;
        int playerCount = 1;

        host();
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server gestartet. Warte auf Verbindungen...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Neue Verbindung von Spieler " + playerCount);
                playerHandler.add(new PlayerHandler(this, clientSocket, playerCount + ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void messageHandler(String message, String playername) {
        String[] messageInParts = message.split(" ");
        String commandSendByPlayer = messageInParts[0];
        PlayerCommands[] playerCommands = PlayerCommands.values();
        String commandFiltered = Arrays.stream(playerCommands)
                .map(Enum::toString)
                .filter(command -> command.equals(commandSendByPlayer))
                .findFirst()
                .orElse("FAILED");
        System.out.println(commandFiltered + " || ist gleich Failed?" + commandFiltered.equals("FAILED"));

        if (!(commandFiltered.equals("FAILED"))){
            commandIsValid(messageInParts);
        }else{
            for (IPlayerHandler player : playerHandler) {
                System.out.println();
                if (player.getPlayername().equals(playername)) {
                    player.sendMessageToUser(ServerCommands.UNKNOWN_COMMAND.toString());
                    break;
                }
            }
        }
    }

    private void commandIsValid(String[] messageInParts){

        Map<PlayerCommands, Runnable> actions = new HashMap<>();
        actions.put(PlayerCommands.REGISTER, () -> handleRegister());
        actions.put(PlayerCommands.SHIP_ADD, () -> handleShipAdd());
        actions.put(PlayerCommands.RPS, () -> handleRPS());
        actions.put(PlayerCommands.BOMB, () -> handleBomb());
        PlayerCommands befehl = PlayerCommands.valueOf(messageInParts[0]);
        Runnable action = actions.getOrDefault(befehl, () -> {});
        action.run();
    }

    private static void handleRegister() {
        System.out.println("Befehl REGISTER verarbeitet.");
    }

    private static void handleShipAdd() {
        System.out.println("Befehl SHIP_ADD verarbeitet.");
    }

    private static void handleRPS() {
        System.out.println("Befehl RPS verarbeitet.");
    }

    private static void handleBomb() {
        System.out.println("Befehl BOMB verarbeitet.");
    }

    public void host()
    {
        try
        {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //return System.getProperty("host", "localhost");
    }
}
