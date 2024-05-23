package Server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Enum.ServerCommands;

public class SchiffeVersenkenServer {
    private int PORT;
    String currentPlayerName;
    List<PlayerHandler> playerHandlers = new ArrayList<>();

    public SchiffeVersenkenServer() {
        setPORT();
        host();
        int playerCount = 1;
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server gestartet. Warte auf Verbindungen...");
            while (playerCount <= 2) {
                Socket clientSocket = serverSocket.accept();
                PlayerHandler player = new PlayerHandler(this, clientSocket, playerCount - 1);
                player.start();
                playerHandlers.add(player);
                playerCount++;
            }

            waitingForRegistration();
            waitingForRPS();
            waitingForShipToPlaces();


            //TODO
        /*
            Game plan:
            1. Start screen
                a. name
                b. start or connect
            2. Rock Paper Scissors
            3. Ship placing
            4. Defending / Attacking
            5. End screen
                a. score
                b. play again
                c. exit
         */

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitingForShipToPlaces() {

    }

    private void waitingForRPS() throws InterruptedException {
        int counter = 0;
        while (counter < 2) {
            counter = 0;
            Thread.sleep(5000);
            for (PlayerHandler p : playerHandlers) {
                if (p.getRps() != null) {
                    counter++;
                }
            }
        }
        if (playerHandlers.get(0).getRps().ordinal() == playerHandlers.get(1).getRps().ordinal()) {
            for (PlayerHandler p : playerHandlers) {
                p.sendMessageToUser(ServerCommands.TIE.toString());
                p.restartRPS();
            }
            waitingForRPS();
        } else if (playerHandlers.get(0).getRps().ordinal() == 0 && playerHandlers.get(1).getRps().ordinal() == 1) {
            playerHandlers.get(0).sendMessageToUser(ServerCommands.LOST.toString());
            playerHandlers.get(0).setRpsWon(false);
            playerHandlers.get(1).sendMessageToUser(ServerCommands.WON.toString());
            playerHandlers.get(1).setRpsWon(true);

        } else {
            playerHandlers.get(1).sendMessageToUser(ServerCommands.LOST.toString());
            playerHandlers.get(1).setRpsWon(false);
            playerHandlers.get(0).sendMessageToUser(ServerCommands.WON.toString());
            playerHandlers.get(0).setRpsWon(true);
        }


    }

    private void waitingForRegistration() throws InterruptedException {
        System.out.println("WAITING FOR TWO PLAYERS TO REGISTER");
        int counter = 0;
        while (counter < 2) {
            counter = 0;
            Thread.sleep(5000);
            for (PlayerHandler p : playerHandlers) {
                if (p.getIsRegsitered()) {
                    counter += 1;
                    System.out.println("WAITING FOR " + (2 - counter) + " PLAYERS TO REGISTER");
                }
            }
        }
        System.out.println("TWO PLAYERS ARE REGISTERED");
    }


    private void setPORT() {
        System.out.println("Bitte gebe den Port an, unter welchem du erreichbar sein möchtest:");
        Scanner sc = new Scanner(System.in);
        PORT = sc.nextInt();
    }


    public void host() {
        try {
            System.out.println("Deine IP-Adresse: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //return System.getProperty("host", "localhost");
    }

    private void startGameLobby() {

    }
}
