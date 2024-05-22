package Server;

import Interfaces.IPlayerHandler;
import Interfaces.ISchiffeVersenkenServer;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class SchiffeVersenkenServer implements ISchiffeVersenkenServer {
    private int PORT;
    List<IPlayerHandler> playerHandler = new ArrayList<>();
    String currentPlayerName;
    Map<PlayerCommands, Runnable> actions = new HashMap<>();

    public SchiffeVersenkenServer() {
        setPORT();
        host();
        playerCommandInputHandlerIndexed();

        int playerCount = 1;

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server gestartet. Warte auf Verbindungen...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String currentPlayerNameExpression = in.readLine();
                String[] currentPlayerNameInArray = currentPlayerNameExpression.split(" ");
                if (playerCount < 2) {
                    if (currentPlayerNameInArray[0] != null && currentPlayerNameInArray[0].equals(PlayerCommands.REGISTER.toString())) {
                        currentPlayerName = currentPlayerNameInArray[1];
                        System.out.println("Neue Verbindung von Spieler " + currentPlayerName);
                        playerHandler.add(new PlayerHandler(this, clientSocket, currentPlayerName));
                        playerCount++;
                    } else {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println("REGISTRATION FAILED! PLEASE RESTART! USE REGISTER <yourPlayerName> INSTEAD!");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playerCommandInputHandlerIndexed() {
        actions.put(PlayerCommands.SHIP_ADD, () -> handleShipAdd());
        actions.put(PlayerCommands.RPS, () -> handleRPS());
        actions.put(PlayerCommands.BOMB, () -> handleBomb());
    }

    private void setPORT() {
        System.out.println("Bitte gebe den Port an, unter welchem du erreichbar sein möchtest:");
        Scanner sc = new Scanner(System.in);
        PORT = sc.nextInt();
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

        if (!(commandFiltered.equals("FAILED"))) {
            commandIsValid(messageInParts);
        } else {
            for (IPlayerHandler player : playerHandler) {
                System.out.println();
                if (player.getPlayername().equals(playername)) {
                    player.sendMessageToUser(ServerCommands.UNKNOWN_COMMAND.toString());
                    break;
                }
            }
        }
    }

    private void commandIsValid(String[] messageInParts) {
        PlayerCommands befehl = PlayerCommands.valueOf(messageInParts[0]);
        Runnable action = actions.getOrDefault(befehl, () -> {
        });
        action.run();
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

    public void host() {
        try {
            System.out.println("Deine Email-Adresse: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //return System.getProperty("host", "localhost");
    }
}
